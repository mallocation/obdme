package edu.unl.csce.obdme.collector;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothServiceRequestMaxRetriesException;
import edu.unl.csce.obdme.hardware.elm.ELMDeviceNoDataException;
import edu.unl.csce.obdme.hardware.elm.ELMException;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.elm.ELMUnableToConnectException;
import edu.unl.csce.obdme.hardware.obd.OBDResponse;
import edu.unl.csce.obdme.utils.UnitConversion;

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

	/** The shared prefs. */
	private SharedPreferences sharedPrefs;

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
	 * @param elmFramework the elm framework
	 */
	public DataCollector(Context context, Handler handler, ELMFramework elmFramework) {
		messageState = COLLECTOR_NONE;
		appHandler = handler;
		this.elmFramework = elmFramework;
		this.context = context;

		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
			"Initializing the DataCollector.");
		}

		currentData = new ConcurrentHashMap<String, String>();
		currentDataQueue = new ConcurrentLinkedQueue<HashMap<String, String>>();

	}

	/**
	 * Instantiates a new data collector.
	 *
	 * @param context the context
	 * @param elmFramework the elm framework
	 */
	public DataCollector(Context context, ELMFramework elmFramework) {
		messageState = COLLECTOR_NONE;
		this.elmFramework = elmFramework;
		this.context = context;

		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
			"Initializing the DataCollector.");
		}

		currentData = new ConcurrentHashMap<String, String>();
		currentDataQueue = new ConcurrentLinkedQueue<HashMap<String, String>>();

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

				//Create a hash map to store the polling data in (this is for one complete data set
				HashMap<String, String> currentDataForQueue = new HashMap<String, String>();

				//For all the pollable and enabled PIDS
				for (String modeHex : pollableEnabledPIDS.keySet()) {
					for (Iterator<String> pidIrt = pollableEnabledPIDS.get(modeHex).iterator(); pidIrt.hasNext(); ) {
						String currentPID = pidIrt.next();
						try {

							//If the collection is paused, Loop for a while
							while(collectionPaused) {
								//Loop-de-loop
								SystemClock.sleep(2000);
								//wEEEEEEEEEEEEEEEEE.
							}

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
										if(sharedPrefs.getBoolean(context.getResources().getString(R.string.prefs_englishunits), false)) {
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
							e.printStackTrace();
						}


					}
				}
				
				//Record the timestamp
				currentDataForQueue.put("timestamp", new Date().toString());
				
				//Record the VIN
				currentDataForQueue.put("vin", sharedPrefs.getString(context.getResources().getString(R.string.prefs_account_vin), null));

				//Add the current collection to the queue
				putCurrentDataSet(currentDataForQueue);

				//Tell the GUI app that we have new data to update the screen with
				setDataChange(COLLECTOR_NEW_DATA);

			}


		}

		/**
		 * Cancel.
		 */
		public synchronized void cancel(){
			continuePolling = false;
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
				"Cancel Polling");
			}
		}

		/**
		 * Pause.
		 */
		public synchronized void pause(){
			collectionPaused = true;
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
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

	};
}
