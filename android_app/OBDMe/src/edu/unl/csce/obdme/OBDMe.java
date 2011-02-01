package edu.unl.csce.obdme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.collector.DataCollector;
import edu.unl.csce.obdme.hardware.elm.ELMAutoConnectPoller;
import edu.unl.csce.obdme.hardware.elm.ELMCheckHardwarePoller;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.elm.ELMIgnitionPoller;
import edu.unl.csce.obdme.settingsmenu.SettingsMenu;

/**
 * The Class OBDMe.
 */
public class OBDMe extends Activity {

	/** The bluetooth service. */
	private BluetoothService bluetoothService;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The busy dialog. */
	private ProgressDialog busyDialog;

	/** The hardware poller. */
	private ELMCheckHardwarePoller hardwarePoller;

	/** The ignition poller. */
	private ELMIgnitionPoller ignitionPoller;

	/** The auto connect poller. */
	private ELMAutoConnectPoller autoConnectPoller;

	/** The collector thread. */
	private DataCollector collectorThread;

	/** The Constant REQUEST_ENABLE_BT. */
	private static final int REQUEST_ENABLE_BT = 0;

	/** The Constant MESSAGE_STATE_CHANGE. */
	public static final int MESSAGE_STATE_CHANGE = 0;

	/** Modes of Operation. */
	public static final int BASIC_USER_MODE = 0;

	/** The Constant ENTHUSIAST_USER_MODE. */
	public static final int ENTHUSIAST_USER_MODE = 1;

	/** The Constant MECHANIC_MODE. */
	public static final int MECHANIC_MODE = 2;

	/** Shared Preferences. */
	private SharedPreferences sharedPrefs;

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		bluetoothService = ((OBDMeApplication)getApplication()).getBluetoothService();
		elmFramework = ((OBDMeApplication)getApplication()).getELMFramework();
		
		sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), MODE_PRIVATE);

		checkBluetoothEnabled();

		

		/* Depending on the mode (user or mechanic) display appropriate data
		 * to the user and also display data according to which orientation
		 * the screen is in (landscape or portrait).
		 */
		if( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == BASIC_USER_MODE ){
			setContentView(R.layout.basicuser_mode_portrait);
		} else if ( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == ENTHUSIAST_USER_MODE ) {
			//setContentView(R.layout.enthusiastuser_mode);	
		} else if ( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == MECHANIC_MODE ) {
			setContentView(R.layout.mechanic_mode);
			mechanicMode();
		} else {
			// First run, so set it to BASIC_USER_MODE
			SharedPreferences.Editor prefEditor = sharedPrefs.edit();
			prefEditor.putInt(getResources().getString(R.string.prefs_mode), BASIC_USER_MODE);
			prefEditor.commit();
			setContentView(R.layout.basicuser_mode_portrait);
		}


		//setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	public void onStart() {
		super.onStart();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	public synchronized void onResume() {
		super.onResume();
		if( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == BASIC_USER_MODE ){
			setContentView(R.layout.basicuser_mode_portrait);
        } else if ( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == ENTHUSIAST_USER_MODE ) {
			//setContentView(R.layout.enthusiastuser_mode);
		} else if ( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == MECHANIC_MODE ) {
			setContentView(R.layout.mechanic_mode);
			mechanicMode();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	public synchronized void onPause() {
		super.onPause();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	public void onStop() {
		super.onStop();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	public void onDestroy() {
		super.onDestroy();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
            return true;
		case R.id.settings:
        	startActivity(new Intent(this, SettingsMenu.class));
            return true;
        }

        return false;
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == BASIC_USER_MODE ){
				setContentView(R.layout.basicuser_mode_landscape);
			}
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			if( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == BASIC_USER_MODE ){
				setContentView(R.layout.basicuser_mode_portrait);
			}
		}
	}

	/**
	 * Check bluetooth enabled.
	 */
	public void checkBluetoothEnabled() {

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		//If Bluetooth is not enabled
		if (!bluetoothAdapter.isEnabled()) {

			//Send intent to OS to prompt user to enable BT
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

		//Bluetooth is already enabled, update view
		else {
			startBluetoothConnectionThread();
		}
	}

	/**
	 * Start bluetooth connection thread.
	 */
	private void startBluetoothConnectionThread() {
		if (bluetoothService == null) {
			bluetoothService = new BluetoothService(getApplicationContext());
			autoConnectPoller.startPolling();
		}
		else if(bluetoothService != null) {
			bluetoothService.setAppHandler(bluetoothHandler);
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			String device = sharedPrefs.getString(getString(R.string.prefs_bluetooth_device), null);
			BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device);

			switch (bluetoothService.getState()) {
			case BluetoothService.STATE_NONE:
				bluetoothService.connect(bluetoothDevice);
				break;

			case BluetoothService.STATE_CONNECTED:
				startHardwareVerifierThread();
				break;

			case BluetoothService.STATE_CONNECTING:
				if(busyDialog == null){
					busyDialog = ProgressDialog.show(OBDMe.this, "", getResources().getString(R.string.setupwizard_bluetooth_dialog_hardware), true);
				}
				else if (busyDialog != null) {
					busyDialog.setMessage(getResources().getString(R.string.setupwizard_bluetooth_dialog_connecting));
					if (!busyDialog.isShowing()) {
						busyDialog.show();
					}
				}
				break;

			case BluetoothService.STATE_FAILED:
				bluetoothService.connect(bluetoothDevice);
				break;
			}
		}
	}

	/**
	 * Start hardware verifier thread.
	 */
	private void startHardwareVerifierThread() {
		hardwarePoller = new ELMCheckHardwarePoller(getApplicationContext(), hardwareHandler, elmFramework, 500);
		hardwarePoller.startPolling();
	}

	/**
	 * Start ignition poller thread.
	 */
	private void startIgnitionPollerThread() {
		ignitionPoller = new ELMIgnitionPoller(getApplicationContext(), ignitionHandler, elmFramework, 500);
		ignitionPoller.startPolling();
	}

	/**
	 * Start auto connect poller thread.
	 */
	private void startAutoConnectPollerThread() {
		autoConnectPoller = new ELMAutoConnectPoller(getApplicationContext(), autoConnectHandler, elmFramework, 500);
		autoConnectPoller.startPolling();
	}

	/**
	 * Start data collector thread.
	 */
	private void startDataCollectorThread() {
		collectorThread = new DataCollector(getApplicationContext(), collectorHandler, elmFramework);
		collectorThread.startCollecting();
	}

	/** The bluetooth handler. */
	private final Handler bluetoothHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case BluetoothService.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_setupwizard_vehicle),
						"MESSAGE_STATE_CHANGE: " + msg.arg1);

				switch (msg.arg1) {

				case BluetoothService.STATE_CONNECTED:
					startHardwareVerifierThread();
					break;

				case BluetoothService.STATE_FAILED:
					//TODO Make this nice for the user
					break;

				case BluetoothService.STATE_CONNECTING:
					if(busyDialog == null){
						busyDialog = ProgressDialog.show(OBDMe.this, "", getResources().getString(R.string.setupwizard_bluetooth_dialog_connecting), true);
					}
					else if (busyDialog != null) {
						busyDialog.setMessage(getResources().getString(R.string.setupwizard_bluetooth_dialog_connecting));
						if (!busyDialog.isShowing()) {
							busyDialog.show();
						}
					}
					break;

				case BluetoothService.STATE_NONE:
					break;
				}
				break;
			}
		}
	};

	/** The hardware handler. */
	private final Handler hardwareHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case ELMCheckHardwarePoller.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
						"Check Harware State Change: " + msg.arg1);

				switch (msg.arg1) {

				case ELMCheckHardwarePoller.CHECK_HARDWARE_NONE:
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_POLLING:
					//Show the connecting dialog box
					if(busyDialog == null){
						busyDialog = ProgressDialog.show(OBDMe.this, "", getResources().getString(R.string.setupwizard_bluetooth_dialog_hardware), true);
					}
					else if (busyDialog != null) {
						busyDialog.setMessage(getResources().getString(R.string.setupwizard_bluetooth_dialog_hardware));
						if (!busyDialog.isShowing()) {
							busyDialog.show();
						}
					}
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_VERIFIED:
					if(hardwarePoller != null){
						hardwarePoller.stop();
						hardwarePoller = null;
					}

					startIgnitionPollerThread();
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_FAILED:
					if(hardwarePoller != null){
						hardwarePoller.stop();
						hardwarePoller = null;
					}

					if (busyDialog != null) {
						busyDialog.dismiss();
						busyDialog = null;
					}

					//TODO Make this nice...

					break;
				}
			}
		}
	};

	/** The ignition handler. */
	private final Handler ignitionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case ELMIgnitionPoller.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_obdme),
						"MESSAGE_STATE_CHANGE: " + msg.arg1);

				switch (msg.arg1) {

				case ELMIgnitionPoller.IGNITION_NONE:
					break;

				case ELMIgnitionPoller.IGNITION_POLLING:
					//This is an intermediate step between starting the polling and
					//actually receiving a result.

					break;

				case ELMIgnitionPoller.IGNITION_OFF:
					if(ignitionPoller != null){
						ignitionPoller.setPollingInterval(2000);
					}

					if(busyDialog == null){
						busyDialog = ProgressDialog.show(OBDMe.this, "", getResources().getString(R.string.setupwizard_vehicle_dialog_ignition), true);
					}
					else if (busyDialog != null) {
						busyDialog.setMessage(getResources().getString(R.string.setupwizard_vehicle_dialog_ignition));
						if (!busyDialog.isShowing()) {
							busyDialog.show();
						}
					}
					break;

				case ELMIgnitionPoller.IGNITION_ON:
					if(ignitionPoller != null){
						ignitionPoller.stop();
						ignitionPoller = null;
					}

					startAutoConnectPollerThread();
					break;
				}
			}
		}
	};

	/** The auto connect handler. */
	private final Handler autoConnectHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case ELMAutoConnectPoller.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_obdme),
						"MESSAGE_STATE_CHANGE: " + msg.arg1);

				switch (msg.arg1) {

				case ELMAutoConnectPoller.AUTO_CONNECT_NONE:
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_POLLING:
					//Show the connecting dialog box
					if(busyDialog == null){
						busyDialog = ProgressDialog.show(OBDMe.this, "", getResources().getString(R.string.setupwizard_vehicle_dialog_autoconnect), true);
					}
					else if (busyDialog != null) {
						busyDialog.setMessage(getResources().getString(R.string.setupwizard_vehicle_dialog_autoconnect));
						if (!busyDialog.isShowing()) {
							busyDialog.show();
						}
					}
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_FAILED:
					if(autoConnectPoller != null){
						autoConnectPoller.setPollingInterval(2000);
					}
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_COMPLETE:

					if(autoConnectPoller != null){
						autoConnectPoller.stop();
						autoConnectPoller = null;
					}

					//Dismiss connecting dialog.  Update the view and change the state to select a new device
					if(busyDialog != null){
						busyDialog.dismiss();
						busyDialog = null;
					}

					startDataCollectorThread();

					break;
				}
			}
		}
	};

	/** The collector handler. */
	private final Handler collectorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case DataCollector.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_obdme),
						"MESSAGE_STATE_CHANGE: " + msg.arg1);

				switch (msg.arg1) {

				case DataCollector.COLLECTOR_NONE:

					break;

				case DataCollector.COLLECTOR_PAUSED:

					break;

				case DataCollector.COLLECTOR_POLLING:

					break;

				}

			case DataCollector.MESSAGE_DATA_CHANGE:

				switch (msg.arg1) {

				case DataCollector.COLLECTOR_NEW_DATA:
					
					//TODO: TESTING
					TextView lowerText = (TextView) findViewById(R.id.basicusermode_portrait_lower_value);
					lowerText.setText(collectorThread.getCurrentData("01", "0C"));
					break;

				}


			}
		}
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(getResources().getBoolean(R.bool.debug)) Log.d(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
				"onActivityResult " + resultCode);

		switch (requestCode) {

		//Action on request for user to enable BT
		case REQUEST_ENABLE_BT:

			//Android OS enabled BT, update the view
			if (resultCode == Activity.RESULT_OK) {
				startBluetoothConnectionThread();

			} else {
				//Show alert dialog, the app must exit.  This is not recoverable
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getResources().getString(R.string.obdme_main_bluetooth_notenabled_error))
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						OBDMe.this.finish();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

			}
			break;

		}
	}
	
	public void mechanicMode(){
		// Set up real-time data list view
		ListView dataList = (ListView) findViewById(R.id.mechanicmode_realtimedata_list);
		String[] dataItems = { "test 1", "test 2", "test 3" };
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.device_name, dataItems);
		dataList.setAdapter(dataAdapter);
		
		// Set up error codes list view
		ListView errorList = (ListView) findViewById(R.id.mechanicmode_errorcodes_list);
		String[] errorItems = { "error code 1", "error code 2" };
		ArrayAdapter<String> errorAdapter = new ArrayAdapter<String>(this, R.layout.device_name, errorItems);
		errorList.setAdapter(errorAdapter);
	}
}