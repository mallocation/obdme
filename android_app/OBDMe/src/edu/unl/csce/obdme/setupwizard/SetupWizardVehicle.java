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

public class SetupWizardVehicle extends Activity {

	private BluetoothService bluetoothService = null;
	private ELMFramework elmFramework = null;
	private ELMIgnitionPoller ignPoller = null;
	private ProgressDialog ignitionDialog;
	private SharedPreferences prefs;

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
				ignPoller = new ELMIgnitionPoller(getApplicationContext(), ignitionHandler, elmFramework);
				ignPoller.startPolling();
			}
		});

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
					ignitionDialog.dismiss();
					break;
				}
			}
		}
	};

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
