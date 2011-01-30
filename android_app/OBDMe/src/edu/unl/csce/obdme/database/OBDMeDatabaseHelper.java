package edu.unl.csce.obdme.database;

import java.util.HashMap;

import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.obd.OBDMode;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The Class OBDMeDatabaseHelper.
 */
public class OBDMeDatabaseHelper extends SQLiteOpenHelper {
	
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "obdme.db";
    
    /** The Constant DATABASE_VERSION. */
    private static final int DATABASE_VERSION = 1;
    
    /** The Constant TABLE_NAME. */
    private static final String TABLE_NAME = "loggeddata";
    
    /** The Constant TABLE_KEY. */
    private static final String TABLE_KEY = "dataset";
	
	/** The elm framework. */
	private ELMFramework elmFramework;
	
	/** The context. */
	private Context context;

    /**
     * Instantiates a new oBD me database helper.
     *
     * @param context the context
     * @param elmFramework the elm framework
     */
    public OBDMeDatabaseHelper(Context context, ELMFramework elmFramework) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.elmFramework = elmFramework;
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	StringBuffer sb = new StringBuffer();
    	HashMap<String, OBDMode> protocol = elmFramework.getObdFramework().parseOBDCommands(context);
    	
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
