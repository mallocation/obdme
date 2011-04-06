package edu.unl.csce.obdme;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.collector.DataCollector;
import edu.unl.csce.obdme.collector.DataUploader;
import edu.unl.csce.obdme.database.OBDMeDatabaseHelper;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.utilities.AppSettings;

/**
 * The Class OBDMeApplication.
 */
public class OBDMeApplication extends Application {

	/** The instance. */
	private static OBDMeApplication instance = null;

	/** The bluetooth service. */
	private BluetoothService bluetoothService = null;

	/** The elm framework. */
	private ELMFramework elmFramework = null;

	/** The web framework. */
	private ObdMeService webFramework = null;

	/** The data collector. */
	private DataCollector dataCollector;

	/** The data uploader. */
	private DataUploader dataUploader;

	/** The application settings. */
	private AppSettings applicationSettings;

	/**
	 * Gets the single instance of OBDMeApplication.
	 *
	 * @return single instance of OBDMeApplication
	 */
	public static OBDMeApplication getInstance() {
		checkInstance();
		return instance;
	}

	/**
	 * Gets the bluetooth service.
	 *
	 * @return the bluetooth service
	 */
	public BluetoothService getBluetoothService() {
		if (bluetoothService == null) {
			checkInstance();
			bluetoothService = new BluetoothService(getApplicationContext());
		}
		return bluetoothService;
	}

	/**
	 * Gets the eLM framework.
	 *
	 * @return the eLM framework
	 */
	public ELMFramework getELMFramework() {
		if (elmFramework == null) {
			checkInstance();
			elmFramework = new ELMFramework(getApplicationContext());
		}
		return elmFramework;
	}

	/**
	 * Gets the data collector.
	 *
	 * @return the data collector
	 */
	public DataCollector getDataCollector() {
		if (dataCollector == null) {
			checkInstance();
			dataCollector = new DataCollector(getApplicationContext());
		}
		return dataCollector;
	}

	/**
	 * Gets the web framework.
	 *
	 * @return the web framework
	 */
	public ObdMeService getWebFramework() {
		if (webFramework == null) {
			checkInstance();
			webFramework = new ObdMeService(getString(R.string.webservice_apikey));
		}
		return webFramework;
	}
	
	/**
	 * Gets the application settings.
	 *
	 * @return the application settings
	 */
	public AppSettings getApplicationSettings() {
		if (applicationSettings == null) {
			checkInstance();
			applicationSettings = new AppSettings(getApplicationContext());
		}
		return applicationSettings;
	}
	
	/**
	 * Gets the data uploader.
	 *
	 * @return the data uploader
	 */
	public DataUploader getDataUploader() {
		if (dataUploader == null) {
			checkInstance();
			dataUploader = new DataUploader(getApplicationContext(), getWebFramework());
		}
		return dataUploader;
	}

	/**
	 * Check instance.
	 */
	private static void checkInstance() {
		if (instance == null)
			throw new IllegalStateException("Application not created yet!");
	}
	
	/**
	 * Destroy application.
	 */
	public void destroyApplication() {
		this.dataUploader = null;
		this.webFramework = null;
		this.dataCollector = null;
		this.elmFramework = null;
		this.bluetoothService = null;
		android.os.Process.killProcess(android.os.Process.myPid());
		
	}

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	public void onCreate() {
		
		SQLiteDatabase sqldb = null;
		
		if (getResources().getBoolean(R.bool.debug_database_fresh)) {
			try {
				OBDMeDatabaseHelper dbh = new OBDMeDatabaseHelper(this);
				sqldb = dbh.getWritableDatabase();
			} catch (Exception e) {

				//Failed... Print of the error.
				if(getResources().getBoolean(R.bool.debug)) {
					Log.e(getResources().getString(R.string.debug_tag_databasewriter),
							"Could not get writable database: " + e.getMessage());
				}
			}
			
			if (sqldb != null) {
				if(getResources().getBoolean(R.bool.debug)) {
					Log.d(getResources().getString(R.string.debug_tag_databasewriter),
					"Cleaning all rows from the database.");
				}
				sqldb.execSQL("DELETE FROM " + OBDMeDatabaseHelper.TABLE_NAME + ";");
				sqldb.close();
			}
		}
		
		
		super.onCreate();
		//provide an instance for our static accessors
		instance = this;
		getApplicationSettings();
		getBluetoothService();
		getELMFramework();
		getDataUploader();
	}

}
