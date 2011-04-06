package edu.unl.csce.obdme.collector;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import edu.unl.csce.obdme.OBDMe;
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothServiceRequestMaxRetriesException;
import edu.unl.csce.obdme.hardware.elm.ELMDeviceNoDataException;
import edu.unl.csce.obdme.hardware.elm.ELMException;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.elm.ELMUnableToConnectException;
import edu.unl.csce.obdme.hardware.obd.OBDResponse;
import edu.unl.csce.obdme.utilities.AppSettings;
import edu.unl.csce.obdme.utilities.UnitConversion;

/**
 * The Class DataCollector.
 */
public class DataCollector {

	/** The app handler. */
	private Handler appHandler;

	/** The collector thread. */
	private DataCollectorThread collectorThread;

	/** The writer thread. */
	private DatabaseWriterThread writerThread;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The message state. */
	private int messageState;

	/** The context. */
	private Context context;	

	/** The current data. */
	private ConcurrentHashMap<String, String> currentData;

	/** The current data queue. */
	private ConcurrentLinkedQueue<HashMap<String, String>> currentDataQueue;

	/** The app settings. */
	private AppSettings appSettings;

	/** The location collector. */
	private LocationCollector locationCollector;

	/** The acceleration collector. */
	private AccelerationCollector accelerationCollector;

	/** The Constant STATE_CHANGE. */
	public static final int STATE_CHANGE = 738264738;

	/** The Constant COLLECTOR_NONE. */
	public static final int COLLECTOR_NONE = 0;

	/** The Constant COLLECTOR_POLLING. */
	public static final int COLLECTOR_POLLING = 1;

	/** The Constant COLLECTOR_PAUSED. */
	public static final int COLLECTOR_PAUSED = 2;

	/** The Constant COLLECTOR_FAILED. */
	public static final int COLLECTOR_FAILED = 3;

	/** The Constant DATA_CHANGE. */
	public static final int DATA_CHANGE = 113513131;

	/** The Constant COLLECTOR_NEW_DATA. */
	public static final int COLLECTOR_NEW_DATA = 0;

	/**
	 * Instantiates a new data collector.
	 *
	 * @param context the context
	 * @param handler the handler
	 */
	public DataCollector(Context context, Handler handler) {
		this.appHandler = handler;
		this.context = context;
		this.elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		this.appSettings = ((OBDMeApplication)context.getApplicationContext()).getApplicationSettings();

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
			"Initializing the DataCollector.");
		}

		currentData = new ConcurrentHashMap<String, String>();
		currentDataQueue = new ConcurrentLinkedQueue<HashMap<String, String>>();
		messageState = COLLECTOR_NONE;
	}

	/**
	 * Instantiates a new data collector.
	 *
	 * @param context the context
	 */
	public DataCollector(Context context) {
		this.context = context;
		this.elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		this.appSettings = ((OBDMeApplication)context.getApplicationContext()).getApplicationSettings();

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
			"Initializing the DataCollector.");
		}

		currentData = new ConcurrentHashMap<String, String>();
		currentDataQueue = new ConcurrentLinkedQueue<HashMap<String, String>>();

		messageState = COLLECTOR_NONE;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	private synchronized void setState(int state) {
		if(messageState != state) {
			messageState = state;
			appHandler.obtainMessage(STATE_CHANGE, state, -1).sendToTarget();
		}
	}

	/**
	 * Sets the data change.
	 *
	 * @param state the new data change
	 */
	private synchronized void setDataChange(int state) {
		appHandler.obtainMessage(DATA_CHANGE, state, -1).sendToTarget();
	}

	/**
	 * Gets the poller state.
	 *
	 * @return the poller state
	 */
	public synchronized int getPollerState() {
		return messageState;
	}

	/**
	 * Sets the app handler.
	 *
	 * @param appHandler the new app handler
	 */
	public void setAppHandler(Handler appHandler) {
		this.appHandler = appHandler;
	}

	/**
	 * Start collecting.
	 */
	public synchronized void startCollecting() {
		if (collectorThread != null) {
			collectorThread.cancel();
			collectorThread = null;
		}

		if (locationCollector == null) {
			if(appSettings.isGpsEnabled()) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
					"Location Collection Enabled.  Starting the location collector.");
				}
				locationCollector = new LocationCollector(this.context);
				locationCollector.run();
				locationCollector.requestLocationUpdates();
			}
		}

		if(appSettings.isAccelEnabled()) {
			if (accelerationCollector == null) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
					"Acceleration Collection Enabled.  Starting the acceleration collector.");
				}
				accelerationCollector = new AccelerationCollector(this.context);
				accelerationCollector.run();
				accelerationCollector.requestAccelerometerUpdates();
			}
		}

		collectorThread = new DataCollectorThread();
		collectorThread.start();

		setState(COLLECTOR_POLLING);
	}

	/**
	 * Batch database write.
	 */
	public synchronized void batchDatabaseWrite() {
		if (writerThread != null) {
			writerThread = null;
		}
		writerThread = new DatabaseWriterThread(context, currentDataQueue);
		writerThread.start();
	}

	/**
	 * Pause polling.
	 */
	public synchronized void pausePolling() {
		if (collectorThread != null) {
			collectorThread.pause();
			setState(COLLECTOR_PAUSED);
		}
	}

	/**
	 * Settings update.
	 */
	public synchronized void settingsUpdate() {
		if (collectorThread != null) {
			collectorThread.setAppSettingsUpdate(true);
		}
	}

	/**
	 * Failed polling.
	 */
	private synchronized void failedPolling() {
		if (collectorThread != null) {
			collectorThread.pause();
		}
		setState(COLLECTOR_FAILED);
	}

	/**
	 * Put current data.
	 *
	 * @param key the key
	 * @param value the value
	 */
	protected synchronized void putCurrentData(String key, String value) {
		this.currentData.put(key, value);
	}

	/**
	 * Gets the current data.
	 *
	 * @param modeHex the mode hex
	 * @param pidHex the pid hex
	 * @return the current data
	 */
	public synchronized String getCurrentData(String modeHex, String pidHex) {

		//Try to get the current data for modeHex and PidHex 
		try {
			return currentData.get((modeHex + pidHex).toString());
		} 

		//On failure return the uninit string
		catch (Exception e) {
			return "----";
		}
	}

	/**
	 * Gets the current data.
	 *
	 * @param modeHexPidHex the mode hex pid hex
	 * @return the current data
	 */
	public synchronized String getCurrentData(String modeHexPidHex) {

		//Try to get the current data for modeHexPidHex 
		try {
			return currentData.get(modeHexPidHex);
		} 

		//On failure return the uninit string
		catch (Exception e) {
			return "----";
		}
	}

	/**
	 * Put current data set.
	 *
	 * @param currentDataSet the current data set
	 */
	protected synchronized void putCurrentDataSet(HashMap<String, String> currentDataSet) {
		this.currentDataQueue.add(currentDataSet);

		//The the amount of collected data we have exceeds the threshold
		if (currentDataQueue.size() > context.getResources()
				.getInteger(R.integer.collector_system_memory_threshold)) {

			//If there isn't a database writing thread
			if (writerThread == null) {

				//Batch DB write
				batchDatabaseWrite();
			}

			//If there is a database writer thread and
			//it's not currently running
			if (writerThread != null) {
				if (!writerThread.isAlive()) {

					//Batch DB write
					batchDatabaseWrite();
				}
			}
		}
	}

	/**
	 * Stop.
	 */
	public synchronized void stop() {
		if (collectorThread != null) {
			collectorThread.cancel();
			while(collectorThread.isAlive()){
				//Wait
			}
			collectorThread = null;
		}
		setState(COLLECTOR_NONE);
	}

	/**
	 * The Class DataCollectorThread.
	 */
	private class DataCollectorThread extends Thread {

		/** The pollable enabled pids. */
		private HashMap<String, List<String>> pollableEnabledPIDS;

		/** The continue polling. */
		private boolean continuePolling;

		/** The collection paused. */
		private boolean collectionPaused;

		/** The app settings update. */
		private boolean appSettingsUpdate = false;


		/**
		 * Instantiates a new data collector thread.
		 */
		public DataCollectorThread() {

			//Set the thread name
			setName("Data Collector");

			//We are assuming that we want to start polling immediately
			this.continuePolling = true;
			this.collectionPaused = false;

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Getting a list of enabled pollable PIDS.");
			}

			//Save the list of pollable and enabled PIDS.
			this.pollableEnabledPIDS = elmFramework.getObdFramework().getCollectedPIDList();

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Initializing the Data Collector Thread.");
			}

		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {

			//If logging is enabled, proint a nice message to the user
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Started Polling");
			}

			//If we want to continue polling
			while(continuePolling) {

				//Check for settings updates
				checkForSettingsUpdates();

				//If the collection is paused, Loop for a while
				while(collectionPaused) {
					SystemClock.sleep(2000);
				}

				//Create a hash map to store the polling data in (this is for one complete data set
				HashMap<String, String> currentDataForQueue = new HashMap<String, String>();

				//Get OPD Data
				getOBDData(currentDataForQueue);

				//Get GPS Data
				getGPSData(currentDataForQueue);

				//Get Accelerometer Data
				getAccelerometerData(currentDataForQueue);

				//Record the timestamp
				currentDataForQueue.put("timestamp", new Date().toString());

				//Record the VIN
				currentDataForQueue.put("vin", appSettings.getAccountVIN());

				//If the user does not want to upload data, then dont collect it.
				if (appSettings.getDataUpload() != OBDMe.DATA_USAGE_NEVER) {
					//Add the current collection to the queue
					putCurrentDataSet(currentDataForQueue);
				}

				//Tell the GUI app that we have new data to update the screen with
				setDataChange(COLLECTOR_NEW_DATA);

				//If the sleep frequency was defined
				int frequency;
				if ((frequency = appSettings.getCollectionFrequency()) > 0) {
					//Sleep for the defined frequency
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
								"Sleeping the collector for " + frequency + " seconds.");
					}
					SystemClock.sleep(frequency);
				}

			}


		}

		/**
		 * Cancel.
		 */
		public synchronized void cancel(){
			continuePolling = false;
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Cancel Polling");
			}
		}

		/**
		 * Pause.
		 */
		public synchronized void pause(){
			collectionPaused = true;
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Pausing Polling");
			}
		}

		/**
		 * Checks if is continue polling.
		 *
		 * @return true, if is continue polling
		 */
		@SuppressWarnings("unused")
		public synchronized boolean isContinuePolling() {
			return continuePolling;
		}

		/**
		 * Sets the continue polling.
		 *
		 * @param continuePolling the new continue polling
		 */
		@SuppressWarnings("unused")
		public synchronized void setContinuePolling(boolean continuePolling) {
			this.continuePolling = continuePolling;
		}

		/**
		 * Checks if is collection paused.
		 *
		 * @return true, if is collection paused
		 */
		@SuppressWarnings("unused")
		public synchronized boolean isCollectionPaused() {
			return collectionPaused;
		}

		/**
		 * Sets the collection paused.
		 *
		 * @param collectionPaused the new collection paused
		 */
		@SuppressWarnings("unused")
		public synchronized void setCollectionPaused(boolean collectionPaused) {
			this.collectionPaused = collectionPaused;
		}

		/**
		 * Checks if is app settings update.
		 *
		 * @return true, if is app settings update
		 */
		public synchronized boolean isAppSettingsUpdate() {
			return appSettingsUpdate;
		}

		/**
		 * Sets the app settings update.
		 *
		 * @param appSettingsUpdate the new app settings update
		 */
		public synchronized void setAppSettingsUpdate(boolean appSettingsUpdate) {
			this.appSettingsUpdate = appSettingsUpdate;
		}

		/**
		 * Gets the gPS data.
		 *
		 * @param currentDataForQueue the current data for queue
		 * @return the gPS data
		 */
		private void getGPSData(HashMap<String, String> currentDataForQueue) {
			//Read location data if enabled
			if(appSettings.isGpsEnabled()) {

				//If the user changed the settings mid collection, and the collector hasn't started
				if (locationCollector == null) {
					if(appSettings.isGpsEnabled()) {
						if(context.getResources().getBoolean(R.bool.debug)) {
							Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
							"Location Collection Enabled.  Starting the location collector.");
						}
						locationCollector = new LocationCollector(context);
						locationCollector.run();
						locationCollector.requestLocationUpdates();
					}
				}
				
				//Otherwise, the collector has already been started
				else {
					//Get the current location
					Location currentLocation = locationCollector.getCurrentLocation();

					//If there is a location to be collected
					if (currentLocation != null) {
//						if(context.getResources().getBoolean(R.bool.debug)) {
//							Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
//									"gps_accuracy " + Float.toString(locationCollector.getCurrentLocation().getAccuracy()) + ", " +
//									"gps_bearing " + Float.toString(locationCollector.getCurrentLocation().getBearing()) + ", " +
//									"gps_altitude " + Double.toString(locationCollector.getCurrentLocation().getAltitude()) + ", " +
//									"gps_latitude " + Double.toString(locationCollector.getCurrentLocation().getLatitude()) + ", " +
//									"gps_longitude " + Double.toString(locationCollector.getCurrentLocation().getLongitude()) + ", ");
//						}
						//Save the location information
						currentDataForQueue.put("gps_accuracy", Float.toString(locationCollector.getCurrentLocation().getAccuracy()));
						currentDataForQueue.put("gps_bearing", Float.toString(locationCollector.getCurrentLocation().getBearing()));
						currentDataForQueue.put("gps_altitude", Double.toString(locationCollector.getCurrentLocation().getAltitude()));
						currentDataForQueue.put("gps_latitude", Double.toString(locationCollector.getCurrentLocation().getLatitude()));
						currentDataForQueue.put("gps_longitude", Double.toString(locationCollector.getCurrentLocation().getLongitude()));
					}
				}
			}
			
			//Otherwise location collection isn't enabled
			else {
				
				//If location collector is running, destroy it
				if (locationCollector != null) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
						"Location Collection Disabled.  Stopping the location collector.");
					}
					locationCollector.stop();
					locationCollector = null;
				}
			}
		}

		//		I make ugly comments.

		/**
		 * Gets the accelerometer data.
		 *
		 * @param currentDataForQueue the current data for queue
		 * @return the accelerometer data
		 */
		private void getAccelerometerData(HashMap<String, String> currentDataForQueue) {

			//Read accelerometer data if enabled
			if(appSettings.isAccelEnabled()) {

				//If the user changed the settings mid collection, and the collector hasn't started
				if (accelerationCollector == null) {
					if(appSettings.isAccelEnabled()) {
						if(context.getResources().getBoolean(R.bool.debug)) {
							Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
							"Acceleration Collection Enabled.  Starting the acceleration collector.");
						}
						accelerationCollector = new AccelerationCollector(context);
						accelerationCollector.run();
						accelerationCollector.requestAccelerometerUpdates();
					}
				}

				//Otherwise the collector has already been started
				else {

//					if(context.getResources().getBoolean(R.bool.debug)) {
//						Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
//								"linear_accel_x " + Float.toString(accelerationCollector.getLinearAcceleration().getLinearXAccel()) + ", " +
//								"linear_accel_y " + Float.toString(accelerationCollector.getLinearAcceleration().getLinearYAccel()) + ", " +
//								"linear_accel_z " + Float.toString(accelerationCollector.getLinearAcceleration().getLinearZAccel())
//						);
//					}
//
//					if(context.getResources().getBoolean(R.bool.debug)) {
//						Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
//								"accel_x " + Float.toString(accelerationCollector.getAcceleration().getXAccel()) + ", " +
//								"accel_y " + Float.toString(accelerationCollector.getAcceleration().getYAccel()) + ", " +
//								"accel_z " + Float.toString(accelerationCollector.getAcceleration().getZAccel())
//						);
//					}

					//Save the acceleration information
					currentDataForQueue.put("accel_x", Float.toString(accelerationCollector.getAcceleration().getXAccel()));
					currentDataForQueue.put("accel_y", Float.toString(accelerationCollector.getAcceleration().getYAccel()));
					currentDataForQueue.put("accel_z", Float.toString(accelerationCollector.getAcceleration().getZAccel()));

					//Save the linear acceleration information
					currentDataForQueue.put("linear_accel_x", Float.toString(accelerationCollector.getLinearAcceleration().getLinearXAccel()));
					currentDataForQueue.put("linear_accel_y", Float.toString(accelerationCollector.getLinearAcceleration().getLinearYAccel()));
					currentDataForQueue.put("linear_accel_z", Float.toString(accelerationCollector.getLinearAcceleration().getLinearZAccel()));
				}
			}
			
			//Otherwise acceleration collection isn't enabled
			else {
				
				//If acceleration collector is running, destroy it 
				if (accelerationCollector != null) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
						"Acceleration Collection Disabled.  Stopping the acceleration collector.");
					}
					accelerationCollector.stop();
					accelerationCollector = null;
				}
			}
		}

		/**
		 * Gets the oBD data.
		 *
		 * @param currentDataForQueue the current data for queue
		 * @return the oBD data
		 */
		private void getOBDData(HashMap<String, String> currentDataForQueue) {
			//For all the pollable and enabled PIDS
			for (String modeHex : pollableEnabledPIDS.keySet()) {
				for (Iterator<String> pidIrt = pollableEnabledPIDS.get(modeHex).iterator(); pidIrt.hasNext(); ) {
					String currentPID = pidIrt.next();
					try {

						//Make the request for the current pollable and enabled mode and PID
						OBDResponse response = elmFramework.sendOBDRequest(elmFramework.getConfiguredPID(modeHex, currentPID));

						//If the response isn't null
						if (response != null) {
							try {

								//If the return value is a double
								if (response.getProcessedResponse() instanceof Double) {

									//Get the processed double
									Double responseDouble = (Double)response.getProcessedResponse();

									//Put the processed response into the current data for the queue (we do not want to convert to english units, they are always metric
									currentDataForQueue.put((modeHex+currentPID).toString(), elmFramework.getConfiguredPID(
											modeHex, currentPID).getDecimalFormat().format(responseDouble));

									//If we need to convert to English units
									if(appSettings.isEnglishUnits()) {
										responseDouble = UnitConversion.convertToEnglish(elmFramework.getConfiguredPID(modeHex, currentPID)
												.getPidUnit(), responseDouble);
									}

									//Add the user configured unit value (Metric or English) to the current data hash map
									putCurrentData((modeHex+currentPID).toString(), elmFramework.getConfiguredPID(modeHex, currentPID)
											.getDecimalFormat().format(responseDouble));
								}

								//If the return value is a string
								else if (response.getProcessedResponse() instanceof String) {

									//Put the string in the current data for the queue 
									putCurrentData((modeHex+currentPID).toString(), (String)response.getProcessedResponse());

									//Put the string in the current data hash map
									currentDataForQueue.put((modeHex+currentPID).toString(), (String)response.getProcessedResponse());
								}

								//If the return value is a List
								else if (response.getProcessedResponse() instanceof List) {
									@SuppressWarnings("unchecked")
									//Not nice but we ALWAYS return a list of strings... 
									//So we can make some assumptions here and ignore the warnings
									List<String> resultsList = (List<String>) response.getProcessedResponse();

									//Store the list
									putCurrentData((modeHex+currentPID).toString(), resultsList.toString());
									currentDataForQueue.put((modeHex+currentPID).toString(), resultsList.toString());
								}

							} catch (Exception e) {
								if(context.getResources().getBoolean(R.bool.debug)) {
									Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
											"Error converting the result: " + e.getMessage());
								}
							}
						}

						//If we are exceeding the maximum tries on a request, reestablish collection
					} catch (BluetoothServiceRequestMaxRetriesException bsrmre) {
						if(context.getResources().getBoolean(R.bool.debug)) {
							Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
									"Bluetooth Service Request Max Retries Exception: " + bsrmre.getMessage() 
									+ ".  Stopping the collector.");
						}

						//Set the state to failed
						failedPolling();
					} 

					//If the ELM is unable to connect with the ECU, reestablish collection
					catch (ELMUnableToConnectException euce) {
						if(context.getResources().getBoolean(R.bool.debug)) {
							Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
									"ELM Unable to connect exception: " + euce.getMessage() 
									+ ".  Stopping the collector.");
						}

						//Set the state to failed
						failedPolling();

					} 

					//If the ELM is not getting any data from the ECU, reestablish collection
					catch (ELMDeviceNoDataException edne) {
						if(context.getResources().getBoolean(R.bool.debug)) {
							Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
									"ELM no data exception: " + edne.getMessage() 
									+ ".  Stopping the collector.");
						}

						//Set the state to failed
						failedPolling();

					}

					//Some general ELM exception that we don't particularly care about
					catch (ELMException elme) {
						if(context.getResources().getBoolean(R.bool.debug)) {
							Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
									"General ELM Exception: " + elme.getMessage());
						}
					}

					//Who the fuck knows what caused this...
					catch (Exception e) {
						if(context.getResources().getBoolean(R.bool.debug)) {
							Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
									"There was a general exception", e);
						}
					}


				}
			}
		}

		/**
		 * Check for settings updates.
		 */
		private void checkForSettingsUpdates() {

			if(isAppSettingsUpdate()) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
					"Collected PIDs changed, updating the local data collector list.");
				}

				//Update the list
				this.pollableEnabledPIDS = elmFramework.getObdFramework().getCollectedPIDList();

				//Set the update flag to false
				setAppSettingsUpdate(false);
			}
		}
	};
}
