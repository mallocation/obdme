package edu.unl.csce.obdme.setupwizard;

import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothAndroidService;
import edu.unl.csce.obdme.bluetooth.BluetoothDiscovery;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.hardware.elm.ELMAutoConnectPoller;
import edu.unl.csce.obdme.hardware.elm.ELMCheckHardwarePoller;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.elm.ELMIgnitionPoller;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class OBDMeSetupWizardBluetooth.
 */
public class SetupWizardBluetooth extends Activity {

	/** The Constant REQUEST_ENABLE_BT. */
	private static final int REQUEST_ENABLE_BT = 0;

	/** The Constant REQUEST_CONNECT_DEVICE. */
	private static final int REQUEST_CONNECT_DEVICE = 1;

	/** The Constant MESSAGE_STATE_CHANGE. */
	public static final int MESSAGE_STATE_CHANGE = 1;

	/** The setup state. */
	private int SETUP_STATE = 0;

	/** The bluetooth adapter. */
	private BluetoothAdapter bluetoothAdapter = null;

	/** The bluetooth service. */
	private BluetoothService bluetoothService = null;

	/** The preferences. */
	private SharedPreferences prefs;

	/** The connect dialog. */
	private ProgressDialog connectDialog;
	
	private ELMCheckHardwarePoller chPoller;
	
	private ProgressDialog chDialog;
	
	private ELMFramework elmFramework;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
		"Starting the OBDMe Setup Wizard Bluetooth Activity.");

		//Load the App Pref DB
		prefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), 0);

		setContentView(R.layout.setupwizard_bluetooth);
		
		//Intent intent = new Intent(getBaseContext(), BluetoothAndroidService.class);
		//startService(intent);

		//Setup the button on-click listener
		Button next = (Button) findViewById(R.id.setupwizard_bluetooth_button);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {


				//Switch on the current state of the setup
				switch(SETUP_STATE) {

				//Check that if Bluetooth is available on the device
				case 0:
					checkBluetoothAvailability();
					break;

					//Check that Bluetooth is Enabled.  If not, ask the user to enable it
				case 1:
					checkBluetoothEnabled();
					break;

					//Prompt the user to select the device from the list of devices
				case 2:
					selectBluetoothDevice();
					break;

					//Attempt to connect to the device.  On failure, return to state 2
				case 3:
					connectBluetoothDevice();
					break;

					//Check the hardware version of the device.  This will verify that it is an ELM327
					//and that it is version 1.4 or greater
				case 4:
					checkHardwareVersion();
					break;

					//Bluetooth setup is complete.  Proceed.
				case 5:
					Intent intent = new Intent(view.getContext(), SetupWizardVehicle.class);
					startActivity(intent);
					break;

					//Prompt the user to select the device from the list of devices
				case 6:
					selectBluetoothDevice();
					break;
					
					//Prompt the user to select the device from the list of devices
				case 7:
					selectBluetoothDevice();
					break;
				}

			}

		});

	}

	public void updateView() {

		Button button = (Button) findViewById(R.id.setupwizard_bluetooth_button);
		TextView text = (TextView) findViewById(R.id.setupwizard_bluetooth_text);

		//Switch on the current state of the setup
		switch(SETUP_STATE) {

		//Post Bluetooth is supported
		case 1:
			ImageView supportedImage = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_supported_image);
			supportedImage.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
			text.setText(R.string.setupwizard_bluetooth_text_enable);
			button.setText(R.string.setupwizard_bluetooth_button_enable_text);
			break;

			//State 1 was successful, set View for state 2
		case 2:
			ImageView enabledImage = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_enabled_image);
			enabledImage.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
			text.setText(R.string.setupwizard_bluetooth_text_select);
			button.setText(R.string.setupwizard_bluetooth_button_select_text);
			break;

			//State 2 was successful, set View for state 3
		case 3:
			ImageView selectedImage = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_paired_image);
			selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
			text.setText(R.string.setupwizard_bluetooth_text_connect);
			button.setText(R.string.setupwizard_bluetooth_button_connect_text);
			break;

			//State 3 was successful, set View for state 4
		case 4:
			ImageView connectedImage = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_connected_image);
			connectedImage.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
			text.setText(R.string.setupwizard_bluetooth_text_verify);
			button.setText(R.string.setupwizard_bluetooth_button_verify_text);
			break;

			//Bluetooth setup is complete.  Proceed.
		case 5:
			ImageView verifiedImage = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_verify_image);
			verifiedImage.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
			text.setText(R.string.setupwizard_bluetooth_text_next);
			button.setText(R.string.setupwizard_bluetooth_button_next_text);
			break;

		case 6:
			ImageView selectedImage2 = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_paired_image);
			selectedImage2.setImageDrawable(getResources().getDrawable(R.drawable.blue_r_arrow));
			text.setText(R.string.setupwizard_bluetooth_text_connect_error);
			button.setText(R.string.setupwizard_bluetooth_button_select_text);
			break;
			
		case 7:
			ImageView selectedImage3 = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_paired_image);
			selectedImage3.setImageDrawable(getResources().getDrawable(R.drawable.blue_r_arrow));
			text.setText(R.string.setupwizard_bluetooth_text_verify_error);
			button.setText(R.string.setupwizard_bluetooth_button_select_text);
			break;

		}
	}

	/**
	 * Check Bluetooth availability.
	 *
	 * @return true, if successful
	 */
	public void checkBluetoothAvailability() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		//If no BT adapter
		if (bluetoothAdapter == null) {

			//Set the red X
			ImageView image = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_supported_image);
			image.setImageDrawable(getResources().getDrawable(R.drawable.red_x));

			//Show alert dialog, the app must exit.  This is not recoverable
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getResources().getString(R.string.setupwizard_bluetooth_error_notsupported))
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					SetupWizardBluetooth.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

		//Bluetooth supported
		else {
			SETUP_STATE = 1;
			updateView();
		}
	}


	/**
	 * Check Bluetooth enabled.
	 */
	public void checkBluetoothEnabled() {

		//If Bluetooth is not enabled
		if (!bluetoothAdapter.isEnabled()) {

			//Send intent to OS to prompt user to enable BT
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

		//Bluetooth is already enabled, update view
		else {
			SETUP_STATE = 2;
			updateView();
		}
	}

	/**
	 * Pair Bluetooth device.
	 */
	public void selectBluetoothDevice() {

		//Start the discovery and selector intent
		Intent serverIntent = new Intent(this, BluetoothDiscovery.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	/**
	 * Connect Bluetooth device.
	 */
	public void connectBluetoothDevice() {

		//Start the Bluetooth service for communication with the device
		bluetoothService = ((OBDMeApplication)getApplication()).getBluetoothService();
		bluetoothService.setAppHandler(messageHandler);
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(prefs.getString(getResources().getString(R.string.prefs_bluetooth_device), null)); 

		//Request a connection to device
		bluetoothService.connect(device);
	}

	protected void checkHardwareVersion() {
		elmFramework = ((OBDMeApplication)getApplication()).getELMFramework();
		chPoller = new ELMCheckHardwarePoller(getApplicationContext(), chHandler, elmFramework);
		chPoller.startPolling();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(getResources().getBoolean(R.bool.debug)) Log.d(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
				"onActivityResult " + resultCode);

		switch (requestCode) {

		//Action on request for user to select a device
		case REQUEST_CONNECT_DEVICE:

			//If a device was selected
			if (resultCode == Activity.RESULT_OK) {

				//Get the address of the device
				String address = data.getExtras()
				.getString(BluetoothDiscovery.EXTRA_DEVICE_ADDRESS);

				//Save the device address to prefs for later
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(getResources().getString(R.string.prefs_bluetooth_device), address);
				editor.commit();

				//Update the view
				SETUP_STATE = 3;
				updateView();

				if(getResources().getBoolean(R.bool.debug)) Log.d(getResources().getString(R.string.debug_tag_setupwizard_bluetooth), 
				"Device Connected.");
			}
			break;

			//Action on request for user to enable BT
		case REQUEST_ENABLE_BT:

			//Android OS enabled BT, update the view
			if (resultCode == Activity.RESULT_OK) {
				SETUP_STATE = 2;
				updateView();

				//Android OS failed to enable BT, update the view with red x and a TOAST message
			} else {
				ImageView image = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_enabled_image);
				image.setImageDrawable(getResources().getDrawable(R.drawable.red_x));
				if(getResources().getBoolean(R.bool.debug)) Log.d(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
						getResources().getString(R.string.setupwizard_bluetooth_error_notenabled));
				Toast.makeText(this, R.string.setupwizard_bluetooth_error_notenabled, getResources().getInteger(R.integer.setupwizard_toast_time)).show();
			}

		}
	}
	
	private final Handler chHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case ELMIgnitionPoller.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
						"Check Harware State Change: " + msg.arg1);

				//Get the new state of the BT service
				switch (msg.arg1) {

				case ELMCheckHardwarePoller.CHECK_HARDWARE_NONE:
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_POLLING:
					//Show the connecting dialog box
					if(chDialog == null){
						chDialog = ProgressDialog.show(SetupWizardBluetooth.this, "", getResources().getString(R.string.setupwizard_bluetooth_dialog_hardware), true);
					}
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_VERIFIED:
					if(chPoller != null){
						chPoller.stop();
						chPoller = null;
					}
					
					SETUP_STATE = 5;
					updateView();
					
					//Dismiss connecting dialog.  Update the view and change the state to select a new device
					if(chDialog != null){
						chDialog.dismiss();
						chDialog = null;
					}
					break;

				case ELMCheckHardwarePoller.CHECK_HARDWARE_FAILED:
					if(chPoller != null){
						chPoller.stop();
						chPoller = null;
					}
					
					ImageView verifyImage = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_verify_image);
					ImageView connectedImage = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_connected_image);
					verifyImage.setImageDrawable(getResources().getDrawable(R.drawable.red_x));
					connectedImage.setImageDrawable(getResources().getDrawable(R.drawable.grey_tick));
					SETUP_STATE = 7;
					updateView();
					
					//Dismiss connecting dialog.  Update the view and change the state to select a new device
					if(chDialog != null){
						chDialog.dismiss();
						chDialog = null;
					}
					break;
				}
			}
		}
	};

	/** The message handler. */
	private final Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
						"Bluetooth State Change: " + msg.arg1);

				ImageView image = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_connected_image);

				//Get the new state of the BT service
				switch (msg.arg1) {

				//The service is holding a valid connection with a device
				case BluetoothService.STATE_CONNECTED:

					//Dismiss connecting dialog, update view
					connectDialog.dismiss();
					SETUP_STATE = 4;
					updateView();
					break;

					//Connection to the device failed
				case BluetoothService.STATE_FAILED:

					//Dismiss connecting dialog.  Update the view and change the state to select a new device
					connectDialog.dismiss();
					image.setImageDrawable(getResources().getDrawable(R.drawable.red_x));
					SETUP_STATE = 6;
					updateView();
					break;

					//The service is currently trying to establish a connection with the device
				case BluetoothService.STATE_CONNECTING:

					//Show the connecting dialog box
					connectDialog = ProgressDialog.show(SetupWizardBluetooth.this, "", getResources().getString(R.string.setupwizard_bluetooth_dialog_connecting), true);
					break;

					//The service currently doesn't have a state.  This is possible on initial start, failure, or lost connection.
				case BluetoothService.STATE_NONE:

					//Dismiss connecting dialog.  Update the view and change the state to select a new device
					connectDialog.dismiss();
					image.setImageDrawable(getResources().getDrawable(R.drawable.red_x));
					SETUP_STATE = 6;
					updateView();
					break;
				}
				break;
			}
		}
	};
}
