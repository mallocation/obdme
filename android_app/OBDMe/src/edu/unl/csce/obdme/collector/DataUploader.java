package edu.unl.csce.obdme.collector;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.api.entities.graph.vehicles.GraphEntry;
import edu.unl.csce.obdme.api.entities.graph.vehicles.GraphPoint;
import edu.unl.csce.obdme.api.entities.graph.vehicles.GraphPush;
import edu.unl.csce.obdme.database.OBDMeDatabaseHelper;

/**
 * The Class DataUploader.
 */
public class DataUploader extends Thread {

	/** The Constant STATE_CHANGE. */
	public static final int STATE_CHANGE = 113513131;
	
	/** The Constant UPLOADER_NONE. */
	public static final int UPLOADER_NONE = 0;
	
	/** The Constant UPLOADER_WAITING. */
	public static final int UPLOADER_WAITING = 1;
	
	/** The Constant UPLOADER_UPLOADING. */
	public static final int UPLOADER_UPLOADING = 2;

	/** The Constant COLLECTOR_NEW_DATA. */
	public static final int COLLECTOR_NEW_DATA = 0;
	
	/** The message state. */
	private int messageState;
	
	/** The web framework. */
	private ObdMeService webFramework;
	
	/** The context. */
	private Context context;
	
	/** The shared prefs. */
	private SharedPreferences sharedPrefs;
	
	/** The app handler. */
	private Handler appHandler;
	
	/** The upload timer. */
	private Timer uploadTimer;
	
	/** The timer delay. */
	private int timerDelay;


	/**
	 * Instantiates a new data uploader.
	 *
	 * @param context the context
	 * @param handler the handler
	 * @param webFramework the web framework
	 */
	public DataUploader(Context context, Handler handler, ObdMeService webFramework) {
		this.webFramework = webFramework;
		this.appHandler = handler;
		this.context = context;

		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

		this.timerDelay = context.getResources().getInteger(R.integer.uploader_timer_interval);

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
			"Initializing the Data Uploader with a Handler");
		}

		this.uploadTimer = new Timer();
		this.uploadTimer.schedule(new UploaderThread(context), this.timerDelay);

		messageState = UPLOADER_WAITING;
	}

	/**
	 * Instantiates a new data uploader.
	 *
	 * @param context the context
	 * @param webFramework the web framework
	 */
	public DataUploader(Context context, ObdMeService webFramework) {
		messageState = UPLOADER_NONE;
		this.webFramework = webFramework;
		this.context = context;

		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
			"Initializing the Data Uploader without a Handler");
		}

		this.uploadTimer = new Timer();
		this.uploadTimer.schedule(new UploaderThread(context), this.timerDelay);

		messageState = UPLOADER_WAITING;
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
	 * Gets the uploader state.
	 *
	 * @return the uploader state
	 */
	public synchronized int getUploaderState() {
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
	 * Stop timer.
	 */
	public synchronized void stopTimer() {
		setState(UPLOADER_NONE);
		this.uploadTimer.cancel();
	}

	/**
	 * Resume timer.
	 */
	public synchronized void resumeTimer() {
		this.uploadTimer.schedule(new UploaderThread(context), this.timerDelay);
	}

	/**
	 * Resume timer.
	 *
	 * @param delay the delay
	 */
	public synchronized void resumeTimer(int delay) {
		this.timerDelay = delay;
		this.uploadTimer.schedule(new UploaderThread(context), this.timerDelay);
	}

	/**
	 * The Class UploaderThread.
	 */
	private class UploaderThread extends TimerTask {

		/** The sqldb. */
		private SQLiteDatabase sqldb;

		/**
		 * Instantiates a new uploader thread.
		 *
		 * @param context the context
		 */
		public UploaderThread(Context context) {
			//Set the thread name
			setName("Data Uploader Task");

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Initializing the Data Uploader Task.");
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

			//If logging is enabled, print a nice message that we are starting a graph push
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Starting a graph push");
			}

			//Get the first rows from the database 
			Cursor queryResults = this.sqldb.query(OBDMeDatabaseHelper.TABLE_NAME,
					null, null, null, null, null, null, 
					Integer.toString(context.getResources().getInteger(R.integer.uploader_setcount_3g)));

			//If we are ready to upload (requirements met)
			if(checkIfReadyForUpload(queryResults)) {

				//Make a new graph push object
				GraphPush graphPush = new GraphPush();

				//Move to the first row of the query
				queryResults.moveToFirst();

				do {

					//Make a new graph entry
					GraphEntry graphEntry = new GraphEntry();

					//For all the columns
					for (int i = 1; i < queryResults.getColumnCount(); i++) {
						
						//If the cell is not empty
						if (!queryResults.isNull(i)) {
							
							//Get the column name
							String columnName = queryResults.getColumnName(i);
							
							//If the column contains PID data
							if (columnName.substring(0, 5).equals("data_")) {
								String modeHex = columnName.substring(5,7);
								String pidHex = columnName.substring(7);
								graphEntry.getPoints().add(new GraphPoint(modeHex, pidHex, queryResults.getString(i)));
							}
							
							//Otherwise if the column is the timestamp
							else if (columnName.equals("timestamp")) {
								graphEntry.setTimestamp(new Date(queryResults.getString(i)));
							}
							
							//Otherwise if the column is the VIN
							else if (columnName.equals("vin")) {
								graphEntry.setVIN(queryResults.getString(i));
							}
						}	
					}
					
					//Add the graph entry to the graph push
					graphPush.getEntries().add(graphEntry);
					
					//While there are still rows, continue
				} while(queryResults.moveToNext());

				
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
					"Pushing a graph to the web service");
				}
				
				//Send the graph push to the vehicle graph service
				webFramework.getVehicleGraphService().pushVehicleGraphData(graphPush, uploadHandler);	
				
				//Close the connection, we're done.
				queryResults.close();
				sqldb.close();
			}
		}

		/**
		 * Check if ready for upload.
		 *
		 * @param queryResults the query results
		 * @return true, if successful
		 */
		private boolean checkIfReadyForUpload(Cursor queryResults) {

			//Get a connectivity manager object to check whether wifi is connected
			ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			//If the wifi is not connected and the user has indicated that they only want to upload on wifi
			if (!wifi.isConnected() && sharedPrefs.getInt(context.getString(R.string.prefs_dataupload), 0) == 0) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
					"User settings indicate wifi only upload but no wifi available. Quit.");
				}

				//Return false
				return false;
			}

			//If the query results are less that our minimum upload amount (for batch processing)
			if (queryResults.getCount() < context.getResources().getInteger(R.integer.uploader_setcount_3g)){

				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
							"There is not enough data in the database to batch upload. Quit.");
				}

				//Return false
				return false;
			}

			//Otherwise, we are ready to upload!
			return true;
		}

		/** The upload handler. */
		private final Handler uploadHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {

				//Messsage from BT service indicating a connection state change
				case 0:
					if(msg.obj != null) {

					} else {

					}
					break;



				}
			}
		};

	};

}
