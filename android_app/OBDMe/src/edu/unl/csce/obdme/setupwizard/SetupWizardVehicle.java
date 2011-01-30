package edu.unl.csce.obdme.setupwizard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.hardware.elm.ELMAutoConnectPoller;
import edu.unl.csce.obdme.hardware.elm.ELMException;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.elm.ELMIgnitionPoller;
import edu.unl.csce.obdme.hardware.obd.OBDResponse;

/**
 * The Class SetupWizardVehicle.
 */
public class SetupWizardVehicle extends Activity {

	/** The bluetooth service. */
	private BluetoothService bluetoothService;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The ign poller. */
	private ELMIgnitionPoller ignPoller;

	/** The ignition dialog. */
	private ProgressDialog ignitionDialog;

	/** The ign poller. */
	private ELMAutoConnectPoller acPoller;

	/** The ignition dialog. */
	private ProgressDialog acDialog;

	/** The prefs. */
	private SharedPreferences prefs;

	/** The SETU p_ state. */
	private int SETUP_STATE = 0;
	
	/** The Constant SETUP_VEHICLE_RESULT_OK. */
	public static final int SETUP_VEHICLE_RESULT_OK = 10;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizard_vehicle),
		"Starting the OBDMe Vehicle Setup Wizard Activity.");

		prefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), 0);

		setContentView(R.layout.setupwizard_vehicle);

		bluetoothService = ((OBDMeApplication)getApplication()).getBluetoothService();
		bluetoothService.setAppHandler(bluetoothHandler);

		elmFramework = ((OBDMeApplication)getApplication()).getELMFramework();

		//Setup the button on-click listener
		Button next = (Button) findViewById(R.id.setupwizard_vehicle_button);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				switch(SETUP_STATE) {
				case 0:
					startIgnitionPollerThread();
					break;

				case 1:
					Intent intent = new Intent(view.getContext(), SetupWizardComplete.class);
					startActivityForResult(intent, SetupWizardComplete.SETUP_COMPLETE_RESULT_OK);
					break;

				}
			}
		});

	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		
		//The user acknowledged the complete screen
		case SetupWizardComplete.SETUP_COMPLETE_RESULT_OK:
			if (resultCode == Activity.RESULT_OK) {
				
				//Backprop result ok
				setResult(Activity.RESULT_OK);
				
				//Finish
				finish();
			}
			break;
		}
    }

	/**
	 * Update view.
	 */
	public void updateView() {

		Button button = (Button) findViewById(R.id.setupwizard_vehicle_button);
		TextView text = (TextView) findViewById(R.id.setupwizard_vehicle_text);

		//Switch on the current state of the setup
		switch(SETUP_STATE) {

		//Post Bluetooth is supported
		case 1:
			text.setText(R.string.setupwizard_vehicle_body_text_confirm);
			button.setText(R.string.setupwizard_vehicle_button_confirm_text);
			TextView vinTitleText = (TextView) findViewById(R.id.setupwizard_vehicle_vin_title);
			vinTitleText.setVisibility(View.VISIBLE);
			TextView vinText = (TextView) findViewById(R.id.setupwizard_vehicle_vin);
			vinText.setVisibility(View.VISIBLE);
			break;


		}
	}
	/**
	 * Start ignition poller thread.
	 */
	private void startIgnitionPollerThread() {
		ignPoller = new ELMIgnitionPoller(getApplicationContext(), ignitionHandler, elmFramework);
		ignPoller.startPolling();
	}

	/**
	 * Start auto connect poller thread.
	 */
	private void startAutoConnectPollerThread() {
		acPoller = new ELMAutoConnectPoller(getApplicationContext(), acHandler, elmFramework);
		acPoller.startPolling();
	}

	/**
	 * Gets the VIN.
	 *
	 * @return the VIN
	 */
	private void getVIN() {

		TextView vinText = (TextView) findViewById(R.id.setupwizard_vehicle_vin);

		if(elmFramework.getObdFramework().isPIDSupported("09", "02")) {
			try {
				OBDResponse response = elmFramework.sendOBDRequest(elmFramework.getConfiguredPID("09", "02"));
				if (response != null) {
					String vinResult = (String)response.getProcessedResponse();
					vinText.setText(vinResult);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(getResources().getString(R.string.prefs_account_vin), vinResult);
					editor.commit();
					SETUP_STATE = 1;
					updateView();
				}
			} catch (ELMException elme) {
				if(getResources().getBoolean(R.bool.debug)) {
					Log.e(getResources().getString(R.string.debug_tag_setupwizard_vehicle),
							elme.getMessage());
				}
			}
		}

	}

	/** The ignition handler. */
	private final Handler ignitionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case ELMIgnitionPoller.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_setupwizard_vehicle),
						"MESSAGE_STATE_CHANGE: " + msg.arg1);

				//Get the new state of the BT service
				switch (msg.arg1) {

				case ELMIgnitionPoller.IGNITION_NONE:
					break;

				case ELMIgnitionPoller.IGNITION_POLLING:
					//This is an intermediate step between starting the polling and
					//actually receiving a result.

					break;

				case ELMIgnitionPoller.IGNITION_OFF:
					//Show the connecting dialog box
					if(ignitionDialog == null){
						ignitionDialog = ProgressDialog.show(SetupWizardVehicle.this, "", getResources().getString(R.string.setupwizard_vehicle_dialog_ignition), true);
					}
					break;

				case ELMIgnitionPoller.IGNITION_ON:
					if(ignPoller != null){
						ignPoller.stop();
						ignPoller = null;
					}

					//Dismiss connecting dialog.  Update the view and change the state to select a new device
					if(ignitionDialog != null){
						ignitionDialog.dismiss();
						ignitionDialog = null;
					}
					startAutoConnectPollerThread();
					break;
				}
			}
		}
	};

	/** The ac handler. */
	private final Handler acHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case ELMIgnitionPoller.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_setupwizard_vehicle),
						"MESSAGE_STATE_CHANGE: " + msg.arg1);

				//Get the new state of the BT service
				switch (msg.arg1) {

				case ELMAutoConnectPoller.AUTO_CONNECT_NONE:
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_POLLING:
					//Show the connecting dialog box
					if(acDialog == null){
						acDialog = ProgressDialog.show(SetupWizardVehicle.this, "", getResources().getString(R.string.setupwizard_vehicle_dialog_autoconnect), true);
					}
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_FAILED:
					break;

				case ELMAutoConnectPoller.AUTO_CONNECT_COMPLETE:

					if(acPoller != null){
						acPoller.stop();
						acPoller = null;
					}

					//Dismiss connecting dialog.  Update the view and change the state to select a new device
					if(acDialog != null){
						acDialog.dismiss();
						acDialog = null;
					}

					getVIN();
					break;
				}
			}
		}
	};

	/** The bluetooth handler. */
	private final Handler bluetoothHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case BluetoothService.MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_setupwizard_vehicle),
						"MESSAGE_STATE_CHANGE: " + msg.arg1);

				//Get the new state of the BT service
				switch (msg.arg1) {

				case BluetoothService.STATE_CONNECTED:
					break;

				case BluetoothService.STATE_FAILED:
					break;

				case BluetoothService.STATE_CONNECTING:
					break;

				case BluetoothService.STATE_NONE:
					break;
				}
				break;
			}
		}
	};


}
