package edu.unl.csce.obdme.collector;

import java.util.Date;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.api.entities.graph.vehicles.GraphEntry;
import edu.unl.csce.obdme.api.entities.graph.vehicles.GraphPoint;
import edu.unl.csce.obdme.api.entities.graph.vehicles.GraphPush;
import edu.unl.csce.obdme.database.OBDMeDatabaseHelper;

/**
 * The Class DataUploadTask.
 */
public class DataUploadTask implements Runnable {

	/** The sqldb. */
	private SQLiteDatabase sqldb;

	/** The context. */
	private Context context;

	/** The web framework. */
	private ObdMeService webFramework;

	private Handler dataUploadHandler;

	/**
	 * Instantiates a new data upload task.
	 *
	 * @param context the context
	 * @param webFramework the web framework
	 * @param dataUploadHander 
	 */
	public DataUploadTask(Context context, ObdMeService webFramework, Handler dataUploadHander) {

		this.context = context;
		this.dataUploadHandler = dataUploadHander;
		this.webFramework = webFramework;

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
			"Initializing the Data Uploader Task.");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		//Try to get a writable database handle
		try {
			OBDMeDatabaseHelper dbh = new OBDMeDatabaseHelper(context);
			this.sqldb = dbh.getWritableDatabase();
		} catch (Exception e) {

			//Failed... Print of the error.
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_datauploaderthread),
						"Could not get writable database: " + e.getMessage());
			}
		}

		//If logging is enabled, print a nice message that we are starting a graph push
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
			"Starting a graph push");
		}

		//Get the first rows from the database 
		Cursor queryResults = this.sqldb.query(OBDMeDatabaseHelper.TABLE_NAME,
				null, null, null, null, null, null, 
				Integer.toString(context.getResources().getInteger(R.integer.uploader_setcount_max)));

		StringBuffer sb = new StringBuffer();

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
				for (int i = 0; i < queryResults.getColumnCount(); i++) {

					//If the cell is not empty
					if (!queryResults.isNull(i)) {

						//Get the column name
						String columnName = queryResults.getColumnName(i);

						if (columnName.equals(OBDMeDatabaseHelper.TABLE_KEY)) {
							sb.append(Integer.toString(queryResults.getInt(i)) + ",");
						}

						//If the column contains PID data
						if (columnName.contains("data_")) {
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

			sb.deleteCharAt(sb.length()-1);

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
				"Pushing a graph to the web service");
			}

			//Send the graph push to the vehicle graph service
			webFramework.getVehicleGraphService().pushVehicleGraphData(graphPush, dataUploadHandler);	

			int affectedRows = sqldb.delete(OBDMeDatabaseHelper.TABLE_NAME, OBDMeDatabaseHelper.TABLE_KEY + " in (" + sb.toString() + ")", null);
			
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
						"Datasets removed from the database: " + affectedRows);
			}

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

		//If the query results are less that our minimum upload amount (for batch processing)
		if (queryResults.getCount() < context.getResources().getInteger(R.integer.uploader_setcount_min)){

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
						"There is not enough data in the database to batch upload. Quit.");
			}

			//Return false
			return false;
		}

		//Otherwise, we are ready to upload!
		return true;
	}
}
