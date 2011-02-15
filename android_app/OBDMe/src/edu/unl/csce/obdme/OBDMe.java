package edu.unl.csce.obdme;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.collector.DataCollector;
import edu.unl.csce.obdme.hardware.elm.ELMAutoConnectPoller;
import edu.unl.csce.obdme.hardware.elm.ELMCheckHardwarePoller;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.elm.ELMIgnitionPoller;
import edu.unl.csce.obdme.settingsmenu.OBDMeRootPreferences;

/**
 * The Class OBDMe.
 */
public class OBDMe extends Activity {

	/** The bluetooth service. */
	private BluetoothService bluetoothService;

	/** The elm framework. */
	private ELMFramework elmFramework;

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

	/** The Constant BASIC_USER_MODE. */
	public static final int BASIC_USER_MODE = 0;

	/** The Constant ENTHUSIAST_USER_MODE. */
	public static final int ADVANCED_USER_MODE = 1;

	/** The shared prefs. */
	private SharedPreferences sharedPrefs;

	/** The ui. */
	private Object theUI;

	/** The gesture detector. */
	private GestureDetector gestureDetector;

	/** The gesture listener. */
	View.OnTouchListener gestureListener;

	/** The connection status. */
	private boolean connectionStatus;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Get all the singletons from the application context
		bluetoothService = ((OBDMeApplication)getApplication()).getBluetoothService();
		collectorThread = ((OBDMeApplication)getApplication()).getDataCollector();
		elmFramework = ((OBDMeApplication)getApplication()).getELMFramework();
		
		//Get the shared preferences
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		//If the bluetooth thread is already connected (from the setup wizard)
		if (this.bluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
			
			//Dont show the connection status screen
			this.connectionStatus = false;
		}
		
		//Otherwise, we need to set up a connection with the device
		else {
			this.connectionStatus = true;
		}

		//If we need to set up a connection
		if (this.connectionStatus) {
			
			//Start the connection routine
			checkBluetoothEnabled();
		}
		
		//Otherwise set the configured data view
		else {
			setConfiguredView();
		}

		//Set up a new gesture detector for swipes
		gestureDetector = new GestureDetector(new FlingGestureDetector());

	}

	/**
	 * Sets the configured view.
	 */
	private void setConfiguredView() {

		//Switch on the screen orientation
		switch(getResources().getConfiguration().orientation) {

		//If the orientation is portrait mode:
		case Configuration.ORIENTATION_PORTRAIT:

			//Switch on the user preferred mode
			switch(this.sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1)) {

			case OBDMe.BASIC_USER_MODE:
				this.theUI = new BasicUserModePortrait(this);
				setContentView(((BasicUserModePortrait) this.theUI).getFlipper());
				break;

			case OBDMe.ADVANCED_USER_MODE:
				break;

				//If it doesn't exist or is invalid, reset
			default:
				SharedPreferences.Editor prefEditor = this.sharedPrefs.edit();
				prefEditor.putInt(getResources().getString(R.string.prefs_mode), OBDMe.BASIC_USER_MODE);
				prefEditor.commit();
				this.theUI = new BasicUserModePortrait(this);
				setContentView(((BasicUserModePortrait) this.theUI).getFlipper());
				break;

			}
			break;

			//If the orientation is landscape mode:
		case Configuration.ORIENTATION_LANDSCAPE:

			//Switch on the user preferred mode
			switch(this.sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1)) {

			case OBDMe.BASIC_USER_MODE:
				this.theUI = new BasicUserModeLandscape(this);
				setContentView(((BasicUserModeLandscape) this.theUI).getFlipper());
				break;

			case OBDMe.ADVANCED_USER_MODE:
				break;

				//If it doesn't exist or is invalid, reset
			default:
				SharedPreferences.Editor prefEditor = this.sharedPrefs.edit();
				prefEditor.putInt(getResources().getString(R.string.prefs_mode), OBDMe.BASIC_USER_MODE);
				prefEditor.commit();
				this.theUI = new BasicUserModeLandscape(this);
				setContentView(((BasicUserModeLandscape) this.theUI).getFlipper());
				break;

			}
			break;
		}

	}

	/**
	 * Sets the connection status view.
	 */
	private void setConnectionStatusView() {
		//Switch on the screen orientation
		switch(getResources().getConfiguration().orientation) {

		//If the orientation is portrait mode:
		case Configuration.ORIENTATION_PORTRAIT:
			
			//If this is a lanscape instance of the connection status screen
			if(theUI instanceof ConnectionStatusLandscape) {
				
				//Save the state of the status screen
				HashMap<String, Boolean> currentState = ((ConnectionStatusLandscape) theUI).packageState();
				
				//Pass that state to a new portrait connection status screen
				theUI = new ConnectionStatusPortrait(getApplicationContext());
				setContentView(((ConnectionStatusPortrait) theUI).getRoot());
				((ConnectionStatusPortrait) theUI).setState(currentState);
			}
			
			//Otherwise, this is an intitial screen create
			else {
				theUI = new ConnectionStatusPortrait(getApplicationContext());
				setContentView(((ConnectionStatusPortrait) theUI).getRoot());
			}
			break;

			//If the orientation is landscape mode:
		case Configuration.ORIENTATION_LANDSCAPE:
			if(this.theUI instanceof ConnectionStatusPortrait) {
				
				//Save the state of the status screen
				HashMap<String, Boolean> currentState = ((ConnectionStatusPortrait) theUI).packageState();
				
				//Pass that state to a new landscape connection status screen
				theUI = new ConnectionStatusLandscape(getApplicationContext());
				setContentView(((ConnectionStatusLandscape) theUI).getRoot());
				((ConnectionStatusLandscape) theUI).setState(currentState);
			}
			else {
				theUI = new ConnectionStatusLandscape(getApplicationContext());
				setContentView(((ConnectionStatusLandscape) theUI).getRoot());
			}
			break;
		}
	}


	/**
	 * Update configured view data.
	 */
	private void updateConfiguredViewData() {

		//Switch on the screen orientation
		switch(getResources().getConfiguration().orientation) {

		//If the orientation is portrait mode:
		case Configuration.ORIENTATION_PORTRAIT:

			//Switch on the user preferred mode
			switch(this.sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1)) {

			case BASIC_USER_MODE:
				if (theUI instanceof BasicUserModePortrait){
					((BasicUserModePortrait) theUI).updateValues(collectorThread);
				}
				break;

			case ADVANCED_USER_MODE:
				break;
				
			}
			break;

			//If the orientation is landscape mode:
		case Configuration.ORIENTATION_LANDSCAPE:

			//Switch on the user preferred mode
			switch(sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1)) {

			case BASIC_USER_MODE:
				if (theUI instanceof BasicUserModeLandscape){
					((BasicUserModeLandscape) theUI).updateValues(collectorThread);
				}
				break;

			case ADVANCED_USER_MODE:
				break;
				
			}
			break;
		}

	}

	/**
	 * The Class FlingGestureDetector.
	 */
	class FlingGestureDetector extends SimpleOnGestureListener {

		/* (non-Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > 250)
					return false;

				//If the User flinged the screen right
				if(e1.getX() - e2.getX() > 120 && Math.abs(velocityX) > 200) {

					//And we're in basic user interface mode:
					if( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == BASIC_USER_MODE ){

						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
							((BasicUserModePortrait) theUI).flingRight();
						}
						else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							((BasicUserModeLandscape) theUI).flingRight();
						}
					}

				}  else if (e2.getX() - e1.getX() > 120 && Math.abs(velocityX) > 200) {
					//And we're in basic user interface mode:
					if( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == BASIC_USER_MODE ){

						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
							((BasicUserModePortrait) theUI).flingLeft();
						}
						else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							((BasicUserModeLandscape) theUI).flingLeft();
						}
					}
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
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
		//Set the configured view
		//If the bluetooth service is not connected to a device, set the connection status view
		if (this.connectionStatus) {
			if (this.bluetoothService.getState() == BluetoothService.STATE_NONE){
				startBluetoothConnectionThread();
			}
		}

		//Otherwise, set the data view
		else {
			setConfiguredView();
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
		case R.id.menu_options_start:
			return true;
		case R.id.menu_options_pause:
			return true;
		case R.id.menu_options_quit:
			
			//Start the quit routine
			
			//Stop the data collector
			collectorThread.pausePolling();
			
			//Write all of the in memory data to the database
			collectorThread.batchDatabaseWrite();
			
			//Set the bluetooth state to none
			bluetoothService.setState(BluetoothService.STATE_NONE);
			
			//Set the connection status to true (show the connecting screen on startup)
			this.connectionStatus = true;
			
			//Finish this activity
			this.finish();
			return true;
		case R.id.menu_options_prefs:
			startActivityForResult(new Intent(this, edu.unl.csce.obdme.settingsmenu.OBDMeRootPreferences.class), OBDMeRootPreferences.USER_QUIT_SETTINGS);
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

		//Set the configured view
		//If the bluetooth service is not connected to a device, set the connection status view
		if (this.connectionStatus) {
			setConnectionStatusView();
		}

		//Otherwise, set the data view
		else {
			setConfiguredView();
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

		//Set the connection status view
		setConnectionStatusView();
		this.connectionStatus = true;

		//Set up the bluetooth service to communicate with this thread
		this.bluetoothService.setAppHandler(this.eventHandler);

		//Get the bluetooth device from shared prefs
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		String device = this.sharedPrefs.getString(getString(R.string.prefs_bluetooth_device), null);
		BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device);

		switch (bluetoothService.getState()) {
		case BluetoothService.STATE_NONE:

			//Attempt to connect
			this.bluetoothService.connect(bluetoothDevice);
			break;

		case BluetoothService.STATE_CONNECTED:

			//We're already connected, try to verify the hardware
			startHardwareVerifierThread();
			break;

		case BluetoothService.STATE_CONNECTING:
			break;

		case BluetoothService.STATE_FAILED:
			
			//If the bluetooth status is failed initially, pause the data collector if it exists
			if (this.collectorThread != null) {
				this.collectorThread.pausePolling();
			}
			
			//Attempt to reconnect to the device
			this.bluetoothService.connect(bluetoothDevice);
			break;
		}
	}

	/**
	 * Start hardware verifier thread.
	 */
	private void startHardwareVerifierThread() {
		this.hardwarePoller = new ELMCheckHardwarePoller(getApplicationContext(), eventHandler, elmFramework, 500);
		this.hardwarePoller.startPolling();
	}

	/**
	 * Start ignition poller thread.
	 */
	private void startIgnitionPollerThread() {
		this.ignitionPoller = new ELMIgnitionPoller(getApplicationContext(), eventHandler, elmFramework, 500);
		this.ignitionPoller.startPolling();
	}

	/**
	 * Start auto connect poller thread.
	 */
	private void startAutoConnectPollerThread() {
		this.autoConnectPoller = new ELMAutoConnectPoller(getApplicationContext(), eventHandler, elmFramework, 500);
		this.autoConnectPoller.startPolling();
	}

	/**
	 * Start data collector thread.
	 */
	private void startDataCollectorThread() {
		this.collectorThread.setAppHandler(eventHandler);
		this.collectorThread.startCollecting();
	}

	/** The event handler. */
	private final Handler eventHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothService.STATE_CHANGE:

				if(getResources().getBoolean(R.bool.debug)) {
					Log.d(getResources().getString(R.string.debug_tag_obdme),
					"Bluetooth State Change");
				}

				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:

					//Ok, we're connected, finish the wireless animation
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).finishWirelessAnimation();
					}
					else if(theUI instanceof ConnectionStatusLandscape) {
						((ConnectionStatusLandscape) theUI).finishWirelessAnimation();
					}

					//Start the hardware verifier thread
					startHardwareVerifierThread();
					break;

				case BluetoothService.STATE_FAILED:

					//If the connection status is a portrait instance
					if(theUI instanceof ConnectionStatusPortrait) {

						//If the wireless is already animating (initial connection attempt)
						if (((ConnectionStatusPortrait) theUI).isWirelessAnimating()) {
							((ConnectionStatusPortrait) theUI).setConnectionFailed();
							((ConnectionStatusPortrait) theUI).finishWirelessAnimation();
						}

						//Otherwise this is a connection retry
						else {
							((ConnectionStatusPortrait) theUI).startWirelessAnimation();
						}
					}

					//Else if the connection status is a landscape instance
					else if(theUI instanceof ConnectionStatusLandscape) {

						//If the wireless is already animating (initial connection attempt)
						if (((ConnectionStatusLandscape) theUI).isWirelessAnimating()) {
							((ConnectionStatusLandscape) theUI).setConnectionFailed();
							((ConnectionStatusLandscape) theUI).finishWirelessAnimation();
						}

						//Otherwise this is a connection retry
						else {
							((ConnectionStatusLandscape) theUI).startWirelessAnimation();
						}
					}

					//Pause the collection if it exists
					if(collectorThread != null) {
						collectorThread.pausePolling();
					}

					//Reconnect
					BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					String device = sharedPrefs.getString(getString(R.string.prefs_bluetooth_device), null);
					BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device);
					bluetoothService.connect(bluetoothDevice, 5000);
					break;

				case BluetoothService.STATE_CONNECTING:

					//Update the connection status
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).startWirelessAnimation();
					}
					else if(theUI instanceof ConnectionStatusLandscape) {
						((ConnectionStatusLandscape) theUI).startWirelessAnimation();
					}
					break;

				case BluetoothService.STATE_NONE:

					//The connection status screen should be shown
					connectionStatus = true;
					break;
				}
				break;

			case ELMCheckHardwarePoller.STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) {
					Log.d(getResources().getString(R.string.debug_tag_obdme),
					"Check Hardware Poller State Change");
				}

				switch (msg.arg1) {

				case ELMCheckHardwarePoller.CHECK_HARDWARE_NONE:
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_POLLING:

					//Update the connection status
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).startDongleAnimation();
					}
					else if(theUI instanceof ConnectionStatusLandscape) {
						((ConnectionStatusLandscape) theUI).startDongleAnimation();
					}
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_VERIFIED:

					//We got what we needed, kill the hardware verifier thread
					if(hardwarePoller != null){
						hardwarePoller.stop();
						hardwarePoller = null;
					}

					//Start the ignition poller
					startIgnitionPollerThread();
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_FAILED:

					break;
				}
				break;

			case ELMIgnitionPoller.STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) {
					Log.d(getResources().getString(R.string.debug_tag_obdme),
					"Ignition Poller State Change");
				}

				switch (msg.arg1) {

				case ELMIgnitionPoller.IGNITION_NONE:
					break;

				case ELMIgnitionPoller.IGNITION_POLLING:
					//This is an intermediate step between starting the polling and
					//actually receiving a result.

					break;

				case ELMIgnitionPoller.IGNITION_OFF:

					//Update the polling interval so it is much less frequent
					if(ignitionPoller != null){
						ignitionPoller.setPollingInterval(2000);
					}

					break;

				case ELMIgnitionPoller.IGNITION_ON:

					//Kill the ignition poller, we got what we needed
					if(ignitionPoller != null){
						ignitionPoller.stop();
						ignitionPoller = null;
					}

					//Start the auto connector
					startAutoConnectPollerThread();
					break;
				}
				break;

			case ELMAutoConnectPoller.STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) {
					Log.d(getResources().getString(R.string.debug_tag_obdme),
					"Auto Connect Poller State Change");
				}

				switch (msg.arg1) {

				case ELMAutoConnectPoller.AUTO_CONNECT_NONE:
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_POLLING:

					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_FAILED:

					//Update the polling interval so it is much less frequent
					if(autoConnectPoller != null){
						autoConnectPoller.setPollingInterval(2000);
					}
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_COMPLETE:

					//Update the UI
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).finishDongleAnimation();
					}
					else if(theUI instanceof ConnectionStatusLandscape) {
						((ConnectionStatusLandscape) theUI).finishDongleAnimation();
					}

					//Set connection status to true (dont show the connecting screen)
					connectionStatus = false;

					//Set the configured data view
					setConfiguredView();

					//Start the data collector
					startDataCollectorThread();

					break;
				}
				break;

			case DataCollector.STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) {
					Log.d(getResources().getString(R.string.debug_tag_obdme),
					"Data Collector State Change");
				}

				switch (msg.arg1) {

				case DataCollector.COLLECTOR_NONE:

					break;

				case DataCollector.COLLECTOR_PAUSED:

					break;

				case DataCollector.COLLECTOR_POLLING:

					break;

				case DataCollector.COLLECTOR_FAILED:

					//The collector failed, set the connection status
					connectionStatus = false;

					//Set up the connection status screen
					setConnectionStatusView();

					//Update the bluetooth state to start the establishment routine
					bluetoothService.setState(BluetoothService.STATE_FAILED);
					break;

				}
				break;

			case DataCollector.DATA_CHANGE:

				switch (msg.arg1) {

				case DataCollector.COLLECTOR_NEW_DATA:

					//On new data, update the values for the current view
					updateConfiguredViewData();
					break;

				}
				break;

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

			//The user just left the properties screen
		case OBDMeRootPreferences.USER_QUIT_SETTINGS:

			//Set the configured view
			//If the bluetooth service is not connected to a device, set the connection status view
			if (this.connectionStatus) {
				setConnectionStatusView();
			}

			//Otherwise, set the data view
			else {
				setConfiguredView();
			}
		}


	}
}