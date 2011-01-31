package edu.unl.csce.obdme.collector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.database.OBDMeDatabaseHelper;
import edu.unl.csce.obdme.hardware.elm.ELMException;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.obd.OBDResponse;

/**
 * The Class DataCollector.
 */
public class DataCollector {

	/** The app handler. */
	private final Handler appHandler;

	/** The dc thread. */
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

	/** The Constant MESSAGE_STATE_CHANGE. */
	public static final int MESSAGE_STATE_CHANGE = 0;

	/** The Constant COLLECTOR_NONE. */
	public static final int COLLECTOR_NONE = 0;

	/** The Constant COLLECTOR_POLLING. */
	public static final int COLLECTOR_POLLING = 1;

	/** The Constant COLLECTOR_PAUSED. */
	public static final int COLLECTOR_PAUSED = 2;

	/** The Constant MESSAGE_DATA_CHANGE. */
	public static final int MESSAGE_DATA_CHANGE = 1;

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
			appHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
		}
	}

	/**
	 * Sets the data change.
	 *
	 * @param state the new data change
	 */
	private synchronized void setDataChange(int state) {
		appHandler.obtainMessage(MESSAGE_DATA_CHANGE, state, -1).sendToTarget();
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public synchronized int getPollerState() {
		return messageState;
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
			writerThread.cancel();
			writerThread = null;
		}

		writerThread = new DatabaseWriterThread();
		writerThread.start();
	}

	/**
	 * Pause polling.
	 */
	public synchronized void pausePolling() {
		if (collectorThread != null) {
			collectorThread.pause();
		}
		setState(COLLECTOR_PAUSED);
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
		try {
			return currentData.get((modeHex + pidHex).toString());
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Put current data set.
	 *
	 * @param currentDataSet the current data set
	 */
	protected synchronized void putCurrentDataSet(HashMap<String, String> currentDataSet) {
		this.currentDataQueue.add(currentDataSet);

		if (this.currentDataQueue.size() > 10) {
			batchDatabaseWrite();
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
			this.continuePolling = true;
			this.collectionPaused = false;

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Getting a list of enabled pollable PIDS.");
			}

			this.pollableEnabledPIDS = elmFramework.getObdFramework().getEnabledPollablePIDList();

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Initializing the Data Collector Thread.");
			}

		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {


			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Started Polling");
			}

			while(continuePolling) {

				HashMap<String, String> currentDataForQueue = new HashMap<String, String>();

				for (String modeHex : pollableEnabledPIDS.keySet()) {
					for (Iterator<String> pidIrt = pollableEnabledPIDS.get(modeHex).iterator(); pidIrt.hasNext(); ) {
						String currentPID = pidIrt.next();
						try {

							//If the collection is paused, Loop for a while
							while(collectionPaused) {
								//Loop-de-loop
								try {
									Thread.sleep(1000);
								} catch(Exception e) {
									if(context.getResources().getBoolean(R.bool.debug)) {
										Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
												"Interrupted exception while the thread was paused: " + e.getMessage());
									}
								}
							}

							//Make the request for the current pollable and enabled mode and PID
							OBDResponse response = elmFramework.sendOBDRequest(elmFramework.getConfiguredPID(modeHex, currentPID));

							if (response != null) {
								try {
									//If the result was a double, round it and convert it to a string
									if (elmFramework.getConfiguredPID(modeHex, currentPID).getPidReturnObject().equals("java.lang.Double")) {
										Double responseDouble = (Double)response.getProcessedResponse();
										responseDouble = (double) Math.round(responseDouble);
										putCurrentData((modeHex+currentPID).toString(), Double.toString(responseDouble));
										currentDataForQueue.put((modeHex+currentPID).toString(), Double.toString(responseDouble));
									}

									//If the result was a string
									else if (elmFramework.getConfiguredPID(modeHex, currentPID).getPidReturnObject().equals("java.lang.String")) {
										putCurrentData((modeHex+currentPID).toString(), (String)response.getProcessedResponse());
										currentDataForQueue.put((modeHex+currentPID).toString(), (String)response.getProcessedResponse());
									}

									//If the result was an array list of strings
									else if (elmFramework.getConfiguredPID(modeHex, currentPID).getPidReturnObject().equals("java.util.ArrayList")) {
										@SuppressWarnings("unchecked")
										List<String> resultsList = (List<String>) response.getProcessedResponse();
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

						} catch (ELMException elme) {
							if(context.getResources().getBoolean(R.bool.debug)) {
								Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
										"General ELM Exception: " + elme.getMessage());
							}
						}


					}
				}

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
			collectionPaused = false;
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
				"Pausing Polling");
			}
		}

		/**
		 * Checks if is continue polling.
		 *
		 * @return the continuePolling
		 */
		@SuppressWarnings("unused")
		public synchronized boolean isContinuePolling() {
			return continuePolling;
		}

		/**
		 * Sets the continue polling.
		 *
		 * @param continuePolling the continuePolling to set
		 */
		@SuppressWarnings("unused")
		public synchronized void setContinuePolling(boolean continuePolling) {
			this.continuePolling = continuePolling;
		}

		/**
		 * Checks if is collection paused.
		 *
		 * @return the collectionPaused
		 */
		@SuppressWarnings("unused")
		public synchronized boolean isCollectionPaused() {
			return collectionPaused;
		}

		/**
		 * Sets the collection paused.
		 *
		 * @param collectionPaused the collectionPaused to set
		 */
		@SuppressWarnings("unused")
		public synchronized void setCollectionPaused(boolean collectionPaused) {
			this.collectionPaused = collectionPaused;
		}

	};

	/**
	 * The Class DatabaseWriterThread.
	 */
	private class DatabaseWriterThread extends Thread {

		/** The sqldb. */
		private SQLiteDatabase sqldb;

		/**
		 * Instantiates a new database writer thread.
		 */
		public DatabaseWriterThread() {

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Initializing the Database Writer Thread.");
			}

			//Try to get a writable database handle
			try {
				OBDMeDatabaseHelper dbh = new OBDMeDatabaseHelper(context);
				this.sqldb = dbh.getWritableDatabase();
			} catch (Exception e) {

				//Failed... Print of the error.
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
							"Could not get writable database: " + e.getMessage());
				}
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Executing batch database write");
			}

			//Create the new SQL statement string buffer, this will hold the batch inserts
			StringBuffer sbSQLStatement = new StringBuffer();

			//For all the items in the current data queue
			for (int i = 0 ; i < currentDataQueue.size(); i++ ) {

				//Create new string buffers for both the insert statement, columns, and values
				StringBuffer sbInsert = new StringBuffer();
				StringBuffer sbColumns = new StringBuffer();
				StringBuffer sbValues = new StringBuffer();

				//Start the initial insert statement
				sbInsert.append("INSERT INTO " + OBDMeDatabaseHelper.TABLE_NAME + " ");

				//Start the initial column declaration
				sbColumns.append("(" + OBDMeDatabaseHelper.TABLE_KEY);

				//Start the initial values declaration
				sbValues.append("(NULL");

				//Get the current set from the dataQueue
				HashMap<String, String> currentSet = currentDataQueue.poll();

				//For all the mode and pid values that were recorded
				for (String currentKey : currentSet.keySet()) {

					//Add the column and value
					sbColumns.append(",data_" + currentKey);
					sbValues.append(",'" +currentSet.get(currentKey) + "'");
				}

				//End the column and values section
				sbColumns.append(") VALUES ");
				sbValues.append(");");

				//FOR DEBUG!
				if(context.getResources().getBoolean(R.bool.debug)) {
					String sqlInsert = sbInsert.toString()+sbColumns.toString()+sbValues.toString();
					Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
							"SQL INSERT: " + sqlInsert.toString());
				}

				//Add this statement to the group of statements
				sbSQLStatement.append(sbInsert.toString()+sbColumns.toString()+sbValues.toString());

			}

			//Execute the sql
			try {

				sqldb.execSQL(sbSQLStatement.toString());

			} catch (SQLException sqle) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_datacollector),
							"Error executing database query: " + sqle.getMessage());
				}
			}

			//Close the connection, we're done.
			sqldb.close();

		}

		/**
		 * Cancel.
		 */
		public void cancel(){
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
				"Cancel Polling");
			}
		}

	};
}
