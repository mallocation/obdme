package edu.unl.csce.obdme.collector;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.database.OBDMeDatabaseHelper;

/**
 * The Class DatabaseWriterThread.
 */
public class DatabaseWriterThread extends Thread {

	/** The sqldb. */
	private SQLiteDatabase sqldb;

	/** The context. */
	private Context context;

	/** The current data queue. */
	private ConcurrentLinkedQueue<HashMap<String, String>> currentDataQueue;

	/**
	 * Instantiates a new database writer thread.
	 *
	 * @param context the context
	 * @param currentDataQueue the current data queue
	 */
	public DatabaseWriterThread(Context context, ConcurrentLinkedQueue<HashMap<String, String>> currentDataQueue) {

		this.currentDataQueue = currentDataQueue;
		this.context = context;

		//Set the thread name
		setName("Database Writer");

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

		//If logging is enabled, print a nice message that we are starting a local DB push
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
			"Executing batch database write");
		}

		//For all the items in the current data queue
		for (int i = 0 ; i < currentDataQueue.size(); i++ ) {

			//Instanciate a new content values object to store the table row data into
			ContentValues cv = new ContentValues();

			//Get the oldest set from the collector queue
			HashMap<String, String> currentSet = currentDataQueue.poll();

			//For all the values that were recorded
			for (String currentKey : currentSet.keySet()) {

				//If the current key is the timestamp
				if (currentKey.equals("timestamp")) {
					cv.put("timestamp", currentSet.get(currentKey));
				}

				//Otherwise it is a PID data item
				else {
					//add them to the table
					cv.put("data_" + currentKey, currentSet.get(currentKey));
				}
			}

			//Load the content values into a new table row
			long tableRow = sqldb.insert(OBDMeDatabaseHelper.TABLE_NAME, null, cv);

			//If debugging is enabled, print row number to the developer
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
						"Inserted data into table row: " + tableRow);
			}

		}

		//Close the connection, we're done.
		sqldb.close();

	}

}