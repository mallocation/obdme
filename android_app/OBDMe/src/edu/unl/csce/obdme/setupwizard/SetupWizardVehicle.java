package edu.unl.csce.obdme.setupwizard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.elm.ELMIgnitionPoller;
import edu.unl.csce.obdme.hardware.obd.OBDException;
import edu.unl.csce.obdme.hardware.obd.OBDRequest;
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
	
	/** The prefs. */
	private SharedPreferences prefs;
	
	/** The SETU p_ state. */
	private int SETUP_STATE = 0;


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

				}
			}
		});

	}

	/**
	 * Start ignition poller thread.
	 */
	private void startIgnitionPollerThread() {
		ignPoller = new ELMIgnitionPoller(getApplicationContext(), ignitionHandler, elmFramework);
		ignPoller.startPolling();
	}

	/**
	 * Gets the vIN.
	 *
	 * @return the vIN
	 */
	private void getVIN() {

		elmFramework.autoSearchProtocols();

		//elmFramework.getObdFramework().queryValidPIDS();
		//SEARCHING...
		//		014 
		//		0: 49 02 01 31 48 47 
		//		1: 46 41 31 36 35 33 37 
		//		2: 4C 30 37 37 32 35 39 
		//if(elmFramework.getObdFramework().isPIDSupported("09", "02")) {
		OBDRequest request = new OBDRequest("09", "02", 25);
		OBDResponse response;
		try {
			response = elmFramework.sendOBDRequest(request);
		} catch (OBDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//}

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
					ignitionDialog = ProgressDialog.show(SetupWizardVehicle.this, "", getResources().getString(R.string.setupwizard_vehicle_dialog_ignition), true);
					break;

				case ELMIgnitionPoller.IGNITION_ON:

					//Dismiss connecting dialog.  Update the view and change the state to select a new device
					if(ignitionDialog != null){
						ignitionDialog.dismiss();
						ignitionDialog = null;
					}
					if(ignPoller != null){
						ignPoller.stop();
						ignPoller = null;
						getVIN();
					}
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
