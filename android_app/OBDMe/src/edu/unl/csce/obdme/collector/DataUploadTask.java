package edu.unl.csce.obdme.collector;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import edu.unl.csce.obdme.OBDMe;
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.api.entities.graph.inertial.VehicleAcceleration;
import edu.unl.csce.obdme.api.entities.graph.spatial.VehicleLocation;
import edu.unl.csce.obdme.api.entities.graph.statistics.StatDataPoint;
import edu.unl.csce.obdme.api.entities.graph.statistics.StatDataset;
import edu.unl.csce.obdme.api.entities.graph.statistics.VehicleStatPush;
import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;
import edu.unl.csce.obdme.database.OBDMeDatabaseHelper;
import edu.unl.csce.obdme.utilities.AppSettings;

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

	/** The data upload handler. */
	private Handler dataUploadHandler;

	/** The app settings. */
	private AppSettings appSettings;

	/**
	 * Instantiates a new data upload task.
	 *
	 * @param context the context
	 * @param dataUploadHander the data upload hander
	 */
	public DataUploadTask(Context context, Handler dataUploadHander) {
		this.context = context;
		this.dataUploadHandler = dataUploadHander;
		this.webFramework = ((OBDMeApplication)context.getApplicationContext()).getWebFramework();
		this.appSettings = ((OBDMeApplication)context.getApplicationContext()).getApplicationSettings();

		//Set this thread to be low priority
		Thread t = Thread.currentThread();
		t.setPriority(Thread.MIN_PRIORITY);
		
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
			"Initializing the Data Uploader Task.");
		}
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		//If the user does not want to upload data, then dont
		if (appSettings.getDataUpload() != OBDMe.DATA_USAGE_NEVER) {

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
				
				// TODO ok by inserting this return statement? should probably exit this thread if we cannot open the database.
				return;
			}

			//If logging is enabled, print a nice message that we are starting a graph push
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datacollector),
				"Starting a vehicle data push");
			}
			
			//Get a VIN to upload by selecting the top-most entry
			Cursor queryVinToUpload = this.sqldb.query(OBDMeDatabaseHelper.TABLE_NAME, 
										null, null, null, null, null, null, Integer.toString(1));
			Cursor queryVehicleDataToUpload = null;			
			String vinToUpload = null;
			
			if (queryVinToUpload.getCount() > 0 && queryVinToUpload.moveToFirst()) {
				//Get the top-most VIN
				vinToUpload = queryVinToUpload.getString(queryVinToUpload.getColumnIndex("vin"));
				//Close the query
				queryVinToUpload.close();
				//run a new query for entries matching the VIN
				queryVehicleDataToUpload = this.sqldb.query(OBDMeDatabaseHelper.TABLE_NAME,
						null, String.format("vin='%s'", vinToUpload), null,null, null, null, Integer.toString(context.getResources().getInteger(R.integer.uploader_setcount_max)));
			} else {
				queryVinToUpload.close();
				this.sqldb.close();
				return;
			}
			
			StringBuffer sb = new StringBuffer();
			// TODO - do we really want to check to ensure there are the minimum number of rows here?
			// Otherwise, we may end up with rows that never make it to the server.			
			if (queryVehicleDataToUpload.getCount() > 0) {
				
				VehicleStatPush statPush = new VehicleStatPush(vinToUpload);
				queryVehicleDataToUpload.moveToFirst();
				
				do {
					StatDataset dataset = new StatDataset();
					dataset.email = appSettings.getAccountUsername();
				
					//traverse the columns
					for (int i=0; i<queryVehicleDataToUpload.getColumnCount(); i++) {
						
						// If the cell is not empty
						if (!queryVehicleDataToUpload.isNull(i)) {
							
							//Get the column name
							String columnName = queryVehicleDataToUpload.getColumnName(i);
							
							//Keep track of id's to delete upon completion
							if (columnName.equals(OBDMeDatabaseHelper.TABLE_KEY)) {
								sb.append(Integer.toString(queryVehicleDataToUpload.getInt(i)) + ",");
							}
							
							//if the column contains PID data
							if (columnName.contains("data_")) {
								String modeHex = columnName.substring(5,7);
								String pidHex = columnName.substring(7);
								dataset.datapoints.add(new StatDataPoint(modeHex, pidHex, removeChar(queryVehicleDataToUpload.getString(i), ',')));
							}
						
							//otherwise this is the timestamp of the collection
							else if (columnName.equals("timestamp")) {
								dataset.timestamp = new Date(queryVehicleDataToUpload.getString(i));
							}
							
							//If the current key is the GPS Accuracy (Location Services enabled)
							else if (columnName.equals("gps_accuracy")) {
								if(dataset.location == null) {
									dataset.location = new VehicleLocation();
								}
								dataset.location.accuracy = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the GPS Bearing (Location Services enabled)
							else if (columnName.equals("gps_bearing")) {
								if(dataset.location == null) {
									dataset.location = new VehicleLocation();
								}
								dataset.location.bearing = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the GPS Altitude (Location Services enabled)
							else if (columnName.equals("gps_altitude")) {
								if(dataset.location == null) {
									dataset.location = new VehicleLocation();
								}
								dataset.location.altitude = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the GPS Latitude (Location Services enabled)
							else if (columnName.equals("gps_latitude")) {
								if(dataset.location == null) {
									dataset.location = new VehicleLocation();
								}
								dataset.location.latitude = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the GPS Longitude (Location Services enabled)
							else if (columnName.equals("gps_longitude")) {
								if(dataset.location == null) {
									dataset.location = new VehicleLocation();
								}
								dataset.location.longitude = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the acceleration in the x direction (Acceleration Services enabled)
							else if (columnName.equals("accel_x")) {
								if(dataset.acceleration == null) {
									dataset.acceleration = new VehicleAcceleration();
								}
								dataset.acceleration.accel_x = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the acceleration in the y direction (Acceleration Services enabled)
							else if (columnName.equals("accel_y")) {
								if(dataset.acceleration == null) {
									dataset.acceleration = new VehicleAcceleration();
								}
								dataset.acceleration.accel_y = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the acceleration in the z direction (Acceleration Services enabled)
							else if (columnName.equals("accel_z")) {
								if(dataset.acceleration == null) {
									dataset.acceleration = new VehicleAcceleration();
								}
								dataset.acceleration.accel_z = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the linear acceleration in the x direction (Acceleration Services enabled)
							else if (columnName.equals("linear_accel_x")) {
								if(dataset.acceleration == null) {
									dataset.acceleration = new VehicleAcceleration();
								}
								dataset.acceleration.linear_accel_x = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the linear acceleration in the y direction (Acceleration Services enabled)
							else if (columnName.equals("linear_accel_y")) {
								if(dataset.acceleration == null) {
									dataset.acceleration = new VehicleAcceleration();
								}
								dataset.acceleration.linear_accel_y = queryVehicleDataToUpload.getString(i);
							}
							
							//If the current key is the linear acceleration in the z direction (Acceleration Services enabled)
							else if (columnName.equals("linear_accel_z")) {
								if(dataset.acceleration == null) {
									dataset.acceleration = new VehicleAcceleration();
								}
								dataset.acceleration.linear_accel_z = queryVehicleDataToUpload.getString(i);
							}
						}
					}
					
					//if there are any datapoints, send 'er off
					if (dataset.datapoints.size() > 0) {
						statPush.statSets.add(dataset);
					}
					
				} while (queryVehicleDataToUpload.moveToNext());
				
				//done with the query results, so close it up
				queryVehicleDataToUpload.close();
				
				//remove the trailing comma from the id's to remove.
				sb.deleteCharAt(sb.length() - 1);
				
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
					"Pushing a graph to the web service");
				}
				
				boolean bDataPushSuccessful = false;
				
				try {
					//send the vehicle data off to persistent storage
					bDataPushSuccessful = webFramework.getStatisticsService().logVehicleStatistics(vinToUpload, statPush);
				} catch (ObdmeException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
						"ObdmeException pushing data to web service.", e);
					}
					bDataPushSuccessful = false;
				} catch (CommException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
						"CommException pushing data to web service.", e);
					}
					bDataPushSuccessful = false;
				}
				
				if (bDataPushSuccessful) {
					//successful data submission, remove the rows from the database.
					int affectedRows = sqldb.delete(OBDMeDatabaseHelper.TABLE_NAME, OBDMeDatabaseHelper.TABLE_KEY + " in (" + sb.toString() + ")", null);
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
								"Datasets removed from the database: " + affectedRows);
					}					
				}
			}
			
			//done with the database.
			sqldb.close();
			
			

			
			/*** BEGIN ORIGINAL DATA UPLOAD CODE ***/			
//			//Get the first rows from the database 
//			Cursor queryResults = this.sqldb.query(OBDMeDatabaseHelper.TABLE_NAME,
//					null, null, null, null, null, null, 
//					Integer.toString(context.getResources().getInteger(R.integer.uploader_setcount_max)));
//			
//			
//
//
//			StringBuffer sb = new StringBuffer();
//
//			//If we are ready to upload (requirements met)
//			if(checkIfReadyForUpload(queryResults)) {
//				
//				//Make a new graph push object
//				GraphPush graphPush = new GraphPush();
//
//				//Move to the first row of the query
//				queryResults.moveToFirst();
//
//				do {
//
//					//Make a new graph entry
//					GraphEntry graphEntry = new GraphEntry();
//
//					//For all the columns
//					for (int i = 0; i < queryResults.getColumnCount(); i++) {
//
//						//If the cell is not empty
//						if (!queryResults.isNull(i)) {
//
//							//Get the column name
//							String columnName = queryResults.getColumnName(i);
//
//							if (columnName.equals(OBDMeDatabaseHelper.TABLE_KEY)) {
//								sb.append(Integer.toString(queryResults.getInt(i)) + ",");
//							}
//
//							//If the column contains PID data
//							if (columnName.contains("data_")) {
//								String modeHex = columnName.substring(5,7);
//								String pidHex = columnName.substring(7);
//								graphEntry.getPoints().add(new GraphPoint(modeHex, pidHex, queryResults.getString(i)));
//							}
//
//							//Otherwise if the column is the timestamp
//							else if (columnName.equals("timestamp")) {
//								graphEntry.setTimestamp(new Date(queryResults.getString(i)));
//							}
//
//							//Otherwise if the column is the VIN
//							else if (columnName.equals("vin")) {
//								graphEntry.setVIN(queryResults.getString(i));
//							}
//						}	
//					}
//
//					//Add the graph entry to the graph push
//					graphPush.getEntries().add(graphEntry);
//
//					//While there are still rows, continue
//				} while(queryResults.moveToNext());
//
//				sb.deleteCharAt(sb.length()-1);
//
//				if(context.getResources().getBoolean(R.bool.debug)) {
//					Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
//					"Pushing a graph to the web service");
//				}
//
//				//Send the graph push to the vehicle graph service
//				// TODO: get this fixed (below)
//				//webFramework.getVehicleGraphService().pushVehicleGraphData(graphPush, dataUploadHandler);	
//
//				//int affectedRows = sqldb.delete(OBDMeDatabaseHelper.TABLE_NAME, OBDMeDatabaseHelper.TABLE_KEY + " in (" + sb.toString() + ")", null);
//
////				if(context.getResources().getBoolean(R.bool.debug)) {
////					Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
////							"Datasets removed from the database: " + affectedRows);
////				}
//			}
//			//Close the connection, we're done.
//			queryResults.close();
//			sqldb.close();
			
			/*** END ORIGINAL DATA UPLOAD CODE ***/
		}
		else {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_datauploaderthread),
				"Skipping upload, the user has specified that they do not want to upload data.");
			}
		}
	}
	
	public static String removeChar(String s, char c) {

		   String r = "";

		   for (int i = 0; i < s.length(); i ++) {
		      if (s.charAt(i) != c) r += s.charAt(i);
		   }

		   return r;
		}
}
