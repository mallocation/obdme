package edu.unl.csce.obdme.utilities;

import edu.unl.csce.obdme.OBDMe;
import edu.unl.csce.obdme.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * The Class AppSettings.
 */
public class AppSettings {
	
	/** The first run. */
	private boolean firstRun;
	
	/** The bluetooth device address. */
	private String bluetoothDeviceAddress;
	
	/** The account uid. */
	private long accountUID;
	
	/** The account username. */
	private String accountUsername;
	
	/** The account password. */
	private String accountPassword;
	
	/** The account vin. */
	private String accountVIN;
	
	/** The display mode. */
	private int displayMode;
	
	/** The data upload. */
	private int dataUpload;
	
	/** The gps enabled. */
	private boolean gpsEnabled;
	
	/** The collection frequency. */
	private int collectionFrequency;
	
	/** The english units. */
	private boolean englishUnits;
	
	/** The account vehicle alias. */
	private String accountVehicleAlias;
	
	/** The context. */
	private Context context;
	
	/** The shared prefs. */
	private SharedPreferences sharedPrefs;

	/** The accel enabled. */
	private boolean accelEnabled;
	
	/**
	 * Instantiates a new app settings.
	 *
	 * @param context the context
	 */
	public AppSettings(Context context) {
		this.context = context;
		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		loadPersistentPrefs();
	}

	/**
	 * Load persistent prefs.
	 */
	public void loadPersistentPrefs() {
		
		if (sharedPrefs.contains(context.getResources().getString(R.string.prefs_firstrun))){
			firstRun = false;
		}
		else {
			firstRun = true;
		}
		
		//Account Settings
		accountUID = sharedPrefs.getLong(context.getResources().getString(R.string.prefs_account_uid), -1);
		accountUsername = sharedPrefs.getString(context.getResources().getString(R.string.prefs_account_username), null);
		accountPassword = sharedPrefs.getString(context.getResources().getString(R.string.prefs_account_password), null);
		accountVIN = sharedPrefs.getString(context.getResources().getString(R.string.prefs_account_vin), null);
		accountVehicleAlias = sharedPrefs.getString(context.getResources().getString(R.string.prefs_account_vehicle_alias), "My Awesome Car");
		
		//Device Settings
		bluetoothDeviceAddress = sharedPrefs.getString(context.getResources().getString(R.string.prefs_bluetooth_device), null);
		
		//Application Settings
		displayMode = sharedPrefs.getInt(context.getResources().getString(R.string.prefs_mode), OBDMe.BASIC_USER_MODE);
		dataUpload = sharedPrefs.getInt(context.getResources().getString(R.string.prefs_dataupload), OBDMe.DATA_USAGE_WIFI_AND_NETWORK);
		collectionFrequency = sharedPrefs.getInt(context.getResources().getString(R.string.prefs_frequency), 1000);
		gpsEnabled = sharedPrefs.getBoolean(context.getResources().getString(R.string.prefs_gps), false);
		accelEnabled = sharedPrefs.getBoolean(context.getResources().getString(R.string.prefs_accel), false);
		englishUnits = sharedPrefs.getBoolean(context.getResources().getString(R.string.prefs_englishunits), true);
		
	}
	
	/**
	 * Commit unsaved changes.
	 */
	public void commitUnsavedChanges() {
		SharedPreferences.Editor editor = sharedPrefs.edit();
				
		//Account Settings
		editor.putLong(context.getResources().getString(R.string.prefs_account_uid), accountUID);
		editor.putString(context.getResources().getString(R.string.prefs_account_username), accountUsername);
		editor.putString(context.getResources().getString(R.string.prefs_account_password), accountPassword);
		editor.putString(context.getResources().getString(R.string.prefs_account_vin), accountVIN);
		editor.putString(context.getResources().getString(R.string.prefs_account_vehicle_alias), accountVehicleAlias);

		//Device Settings
		editor.putString(context.getResources().getString(R.string.prefs_bluetooth_device), bluetoothDeviceAddress);
		
		//Application Settings
		editor.putInt(context.getResources().getString(R.string.prefs_mode), displayMode);
		editor.putInt(context.getResources().getString(R.string.prefs_dataupload), dataUpload);
		editor.putInt(context.getResources().getString(R.string.prefs_frequency), collectionFrequency);
		editor.putBoolean(context.getResources().getString(R.string.prefs_firstrun), firstRun);
		editor.putBoolean(context.getResources().getString(R.string.prefs_gps), gpsEnabled);
		editor.putBoolean(context.getResources().getString(R.string.prefs_accel), accelEnabled);
		editor.putBoolean(context.getResources().getString(R.string.prefs_englishunits), englishUnits);

		editor.commit();
	}
	
	/**
	 * Checks if is first run.
	 *
	 * @return true, if is first run
	 */
	public boolean isFirstRun() {
		return firstRun;
	}

	/**
	 * Sets the first run.
	 *
	 * @param firstRun the new first run
	 */
	public void setFirstRun(boolean firstRun) {
		this.firstRun = firstRun;
	}

	/**
	 * Gets the account uid.
	 *
	 * @return the account uid
	 */
	public long getAccountUID() {
		return accountUID;
	}

	/**
	 * Sets the account uid.
	 *
	 * @param accountUID the new account uid
	 */
	public void setAccountUID(long accountUID) {
		this.accountUID = accountUID;
	}

	/**
	 * Gets the account username.
	 *
	 * @return the account username
	 */
	public String getAccountUsername() {
		return accountUsername;
	}

	/**
	 * Sets the account username.
	 *
	 * @param accountUsername the new account username
	 */
	public void setAccountUsername(String accountUsername) {
		this.accountUsername = accountUsername;
	}

	/**
	 * Gets the account password.
	 *
	 * @return the account password
	 */
	public String getAccountPassword() {
		return accountPassword;
	}

	/**
	 * Sets the account password.
	 *
	 * @param accountPassword the new account password
	 */
	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}

	/**
	 * Gets the account vin.
	 *
	 * @return the account vin
	 */
	public String getAccountVIN() {
		return accountVIN;
	}

	/**
	 * Sets the account vin.
	 *
	 * @param accountVIN the new account vin
	 */
	public void setAccountVIN(String accountVIN) {
		this.accountVIN = accountVIN;
	}

	/**
	 * Gets the display mode.
	 *
	 * @return the display mode
	 */
	public int getDisplayMode() {
		return displayMode;
	}

	/**
	 * Sets the display mode.
	 *
	 * @param displayMode the new display mode
	 */
	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	/**
	 * Gets the data upload.
	 *
	 * @return the data upload
	 */
	public int getDataUpload() {
		return dataUpload;
	}

	/**
	 * Sets the data upload.
	 *
	 * @param dataUpload the new data upload
	 */
	public void setDataUpload(int dataUpload) {
		this.dataUpload = dataUpload;
	}

	/**
	 * Checks if is gps enabled.
	 *
	 * @return true, if is gps enabled
	 */
	public boolean isGpsEnabled() {
		return gpsEnabled;
	}

	/**
	 * Sets the gps enabled.
	 *
	 * @param gpsEnabled the new gps enabled
	 */
	public void setGpsEnabled(boolean gpsEnabled) {
		this.gpsEnabled = gpsEnabled;
	}

	/**
	 * Gets the collection frequency.
	 *
	 * @return the collection frequency
	 */
	public int getCollectionFrequency() {
		return collectionFrequency;
	}

	/**
	 * Sets the collection frequency.
	 *
	 * @param collectionFrequency the new collection frequency
	 */
	public void setCollectionFrequency(int collectionFrequency) {
		this.collectionFrequency = collectionFrequency;
	}

	/**
	 * Sets the bluetooth device address.
	 *
	 * @param bluetoothDeviceAddress the new bluetooth device address
	 */
	public void setBluetoothDeviceAddress(String bluetoothDeviceAddress) {
		this.bluetoothDeviceAddress = bluetoothDeviceAddress;
	}

	/**
	 * Gets the bluetooth device address.
	 *
	 * @return the bluetooth device address
	 */
	public String getBluetoothDeviceAddress() {
		return bluetoothDeviceAddress;
	}

	/**
	 * Sets the english units.
	 *
	 * @param englishUnits the new english units
	 */
	public void setEnglishUnits(boolean englishUnits) {
		this.englishUnits = englishUnits;		
	}

	/**
	 * Checks if is english units.
	 *
	 * @return true, if is english units
	 */
	public boolean isEnglishUnits() {
		return englishUnits;
	}

	/**
	 * Sets the account vehicle alias.
	 *
	 * @param accountVehicleAlias the new account vehicle alias
	 */
	public void setAccountVehicleAlias(String accountVehicleAlias) {
		this.accountVehicleAlias = accountVehicleAlias;
	}

	/**
	 * Gets the account vehicle alias.
	 *
	 * @return the account vehicle alias
	 */
	public String getAccountVehicleAlias() {
		return accountVehicleAlias;
	}

	/**
	 * Checks if is accel enabled.
	 *
	 * @return true, if is accel enabled
	 */
	public boolean isAccelEnabled() {
		return accelEnabled;
	}

	/**
	 * Sets the accel enabled.
	 *
	 * @param accelEnabled the new accel enabled
	 */
	public void setAccelEnabled(boolean accelEnabled) {
		this.accelEnabled = accelEnabled;
	}
	

}
