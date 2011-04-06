package edu.unl.csce.obdme.database;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.hardware.obd.OBDMode;
import edu.unl.csce.obdme.hardware.obd.configuration.OBDConfigurationManager;

/**
 * The Class OBDMeDatabaseHelper.
 */
public class OBDMeDatabaseHelper extends SQLiteOpenHelper {
	
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "obdme.db";
    
    /** The Constant DATABASE_VERSION. */
    private static final int DATABASE_VERSION = 3;
    
    /** The Constant TABLE_NAME. */
    public static final String TABLE_NAME = "loggeddata";
    
    /** The Constant TABLE_KEY. */
    public static final String TABLE_KEY = "dataset";
		
	/** The context. */
	private Context context;

    /**
     * Instantiates a new oBD me database helper.
     *
     * @param context the context
     */
    public OBDMeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	StringBuffer sb = new StringBuffer();
    	ConcurrentHashMap<String, OBDMode> protocol = OBDConfigurationManager.readModeAndPIDOBDProtocol(context);
    	
    	sb.append("CREATE TABLE " + TABLE_NAME + " ( " + TABLE_KEY + " INTEGER PRIMARY KEY");
    	
    	//Setup the timestamp column
    	sb.append(" , timestamp" + " TEXT");
    	
    	//Setup the vin column
    	sb.append(" , vin" + " TEXT");
    	
    	//Setup the location columns
    	sb.append(" , gps_accuracy" + " TEXT");
    	sb.append(" , gps_bearing" + " TEXT");
    	sb.append(" , gps_altitude" + " TEXT");
    	sb.append(" , gps_latitude" + " TEXT");
    	sb.append(" , gps_longitude" + " TEXT");
    	
    	//Setup the acceleration columns
    	sb.append(" , accel_x" + " TEXT");
    	sb.append(" , accel_y" + " TEXT");
    	sb.append(" , accel_z" + " TEXT");
    	sb.append(" , linear_accel_x" + " TEXT");
    	sb.append(" , linear_accel_y" + " TEXT");
    	sb.append(" , linear_accel_z" + " TEXT");
    	
    	//For all the modes that exist in the full protocol
		for ( String currentMode : protocol.keySet() ) {
			//For all the pids that exist in the full protocol
			for ( String currentPID : protocol.get(currentMode).pidKeySet()) {
				sb.append(" , data_" + currentMode + currentPID + " TEXT");
			}
		}
		
		//End our transaction
		sb.append(");");
		
		//Execute the query
		db.execSQL(sb.toString());
    }

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_database_helper),
			"Upgrading the database from " + oldVersion + " to " + newVersion);
		}
		
		//Update for location services
		if (oldVersion == 1 && newVersion == 2) {
			String alterStatement = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN ";
			db.execSQL(alterStatement + "gps_accuracy TEXT;");
			db.execSQL(alterStatement + "gps_bearing TEXT;");
			db.execSQL(alterStatement + "gps_altitude TEXT;");
			db.execSQL(alterStatement + "gps_latitude TEXT;");
			db.execSQL(alterStatement + "gps_longitude TEXT;");
		}
		
		//Update for acceleration services
		else if (oldVersion == 2 && newVersion == 3) {
			String alterStatement = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN ";
			db.execSQL(alterStatement + "accel_x TEXT;");
			db.execSQL(alterStatement + "accel_y TEXT;");
			db.execSQL(alterStatement + "accel_z TEXT;");
			db.execSQL(alterStatement + "linear_accel_x TEXT;");
			db.execSQL(alterStatement + "linear_accel_y TEXT;");
			db.execSQL(alterStatement + "linear_accel_z TEXT;");
		}
		
	}
}
