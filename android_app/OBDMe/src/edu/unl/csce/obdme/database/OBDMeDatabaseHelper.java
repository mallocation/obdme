package edu.unl.csce.obdme.database;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.unl.csce.obdme.hardware.obd.OBDConfigurationManager;
import edu.unl.csce.obdme.hardware.obd.OBDMode;

/**
 * The Class OBDMeDatabaseHelper.
 */
public class OBDMeDatabaseHelper extends SQLiteOpenHelper {
	
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "obdme.db";
    
    /** The Constant DATABASE_VERSION. */
    private static final int DATABASE_VERSION = 1;
    
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
    	ConcurrentHashMap<String, OBDMode> protocol = OBDConfigurationManager.parseOBDCommands(context);
    	
    	sb.append("CREATE TABLE " + TABLE_NAME + " ( " + TABLE_KEY + " INTEGER PRIMARY KEY");
    	
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
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
