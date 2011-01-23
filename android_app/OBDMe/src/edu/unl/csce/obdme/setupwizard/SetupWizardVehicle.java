package edu.unl.csce.obdme.setupwizard;

import java.util.regex.Pattern;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothService;

public class SetupWizardVehicle extends Activity {
	
	/** The bluetooth adapter. */
	private BluetoothAdapter bluetoothAdapter = null;

	/** The bluetooth service. */
	private BluetoothService bluetoothService = null;

	private SharedPreferences prefs;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizard_account),
				"Starting the OBDMe Vehicle Setup Wizard Activity.");

		prefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), 0);

		setContentView(R.layout.setupwizard_vehicle);

	}
}
