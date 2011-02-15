package edu.unl.csce.obdme;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		bluetoothService = ((OBDMeApplication)getApplication()).getBluetoothService();
		elmFramework = ((OBDMeApplication)getApplication()).getELMFramework();
		sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), MODE_PRIVATE);

		checkBluetoothEnabled();

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
			this.theUI = new ConnectionStatusPortrait(getApplicationContext());
			setContentView(((ConnectionStatusPortrait) this.theUI).getRoot());
			break;

			//If the orientation is landscape mode:
		case Configuration.ORIENTATION_LANDSCAPE:

			this.theUI = new ConnectionStatusLandscape(getApplicationContext());
			setContentView(((ConnectionStatusLandscape) this.theUI).getRoot());
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
			switch(sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1)) {

			case BASIC_USER_MODE:
				((BasicUserModePortrait) theUI).updateValues(collectorThread);
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
				((BasicUserModeLandscape) theUI).updateValues(collectorThread);
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
		//setConfiguredView();
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
			this.finish();
			return true;
		case R.id.menu_options_prefs:
			startActivity(new Intent(this, edu.unl.csce.obdme.settingsmenu.RootPreferences.class));
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
		setConfiguredView();
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

		setConnectionStatusView();
		
		bluetoothService.setAppHandler(eventHandler);
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
			break;

		case BluetoothService.STATE_FAILED:
			collectorThread.pausePolling();
			bluetoothService.connect(bluetoothDevice);
			break;
		}
	}

	/**
	 * Start hardware verifier thread.
	 */
	private void startHardwareVerifierThread() {
		hardwarePoller = new ELMCheckHardwarePoller(getApplicationContext(), eventHandler, elmFramework, 500);
		hardwarePoller.startPolling();
	}

	/**
	 * Start ignition poller thread.
	 */
	private void startIgnitionPollerThread() {
		ignitionPoller = new ELMIgnitionPoller(getApplicationContext(), eventHandler, elmFramework, 500);
		ignitionPoller.startPolling();
	}

	/**
	 * Start auto connect poller thread.
	 */
	private void startAutoConnectPollerThread() {
		autoConnectPoller = new ELMAutoConnectPoller(getApplicationContext(), eventHandler, elmFramework, 500);
		autoConnectPoller.startPolling();
	}

	/**
	 * Start data collector thread.
	 */
	private void startDataCollectorThread() {
		collectorThread = new DataCollector(getApplicationContext(), eventHandler, elmFramework);
		collectorThread.startCollecting();
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
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).finishWirelessAnimation();
					}
					startHardwareVerifierThread();
					break;

				case BluetoothService.STATE_FAILED:
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).setConnectionFailed();
						((ConnectionStatusPortrait) theUI).finishWirelessAnimation();
					}
					if(collectorThread != null) {
						collectorThread.pausePolling();
					}
					BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					String device = sharedPrefs.getString(getString(R.string.prefs_bluetooth_device), null);
					BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device);
					bluetoothService.connect(bluetoothDevice, 10000);
					break;

				case BluetoothService.STATE_CONNECTING:
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).startWirelessAnimation();
					}
					break;

				case BluetoothService.STATE_NONE:
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
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).startDongleAnimation();
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
					if(ignitionPoller != null){
						ignitionPoller.setPollingInterval(2000);
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
					if(autoConnectPoller != null){
						autoConnectPoller.setPollingInterval(2000);
					}
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_COMPLETE:
					if(theUI instanceof ConnectionStatusPortrait) {
						((ConnectionStatusPortrait) theUI).finishDongleAnimation();
					}
					setConfiguredView();
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

				}
				break;

			case DataCollector.DATA_CHANGE:

				switch (msg.arg1) {

				case DataCollector.COLLECTOR_NEW_DATA:
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

		}
	}
}