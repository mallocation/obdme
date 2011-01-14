package edu.unl.csce.obdme.setupwizard;

import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.bluetooth.OBDMeBluetoothDiscovery;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class OBDMeSetupWizardBluetooth.
 */
public class OBDMeSetupWizardBluetooth extends Activity {

	/** The Constant REQUEST_ENABLE_BT. */
	private static final int REQUEST_ENABLE_BT = 0;
	
	/** The Constant REQUEST_CONNECT_DEVICE. */
	private static final int REQUEST_CONNECT_DEVICE = 1;
	
    /** The Constant MESSAGE_STATE_CHANGE. */
    public static final int MESSAGE_STATE_CHANGE = 1;
    
    /** The Constant MESSAGE_READ. */
    public static final int MESSAGE_READ = 2;
    
    /** The Constant MESSAGE_WRITE. */
    public static final int MESSAGE_WRITE = 3;
    
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
				"Starting the OBDMe Setup Wizard Bluetooth Activity.");
		
		prefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), 0);

		setContentView(R.layout.setupwizard_bluetooth);

		Button next = (Button) findViewById(R.id.setupwizard_bluetooth_button);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Button button = (Button) findViewById(R.id.setupwizard_bluetooth_button);
				TextView text = (TextView) findViewById(R.id.setupwizard_bluetooth_text);
				switch(SETUP_STATE) {
				case 0:
					if (checkBluetoothAvailability()) {
						text.setText(R.string.setupwizard_bluetooth_text_enable);
						button.setText(R.string.setupwizard_bluetooth_button_enable_text);
						SETUP_STATE = 1;
					}
					break;
				case 1:
					checkBluetoothEnabled();
					break;
				case 2:
					pairBluetoothDevice();
					break;
				case 3:
					connectBluetoothDevice();
					break;
				case 4:
					Intent intent = new Intent(view.getContext(), OBDMeSetupWizardComplete.class);
					finish();
					startActivity(intent);
					break;
				}

			}

		});

	}

	/**
	 * Check Bluetooth availability.
	 *
	 * @return true, if successful
	 */
	public boolean checkBluetoothAvailability() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (bluetoothAdapter == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getResources().getString(R.string.setupwizard_bluetooth_error_notsupported))
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					OBDMeSetupWizardBluetooth.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
		else {
			ImageView image = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_supported_image);
			image.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
			return true;
		}
	}


	/**
	 * Check bluetooth enabled.
	 */
	public void checkBluetoothEnabled() {
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		else {
			ImageView image = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_enabled_image);
			image.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
			Button button = (Button) findViewById(R.id.setupwizard_bluetooth_button);
			TextView text = (TextView) findViewById(R.id.setupwizard_bluetooth_text);
			text.setText(R.string.setupwizard_bluetooth_text_select);
			button.setText(R.string.setupwizard_bluetooth_button_select_text);
			SETUP_STATE = 2;
		}
	}

	/**
	 * Pair bluetooth device.
	 */
	public void pairBluetoothDevice() {
		Intent serverIntent = new Intent(this, OBDMeBluetoothDiscovery.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	/**
	 * Connect bluetooth device.
	 */
	public void connectBluetoothDevice() {
		bluetoothService = new BluetoothService(this, messageHandler);
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(prefs.getString(getResources().getString(R.string.prefs_bluetooth_device), null)); 
		bluetoothService.connect(device);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(getResources().getBoolean(R.bool.debug)) Log.d(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
				"onActivityResult " + resultCode);

		Button button = (Button) findViewById(R.id.setupwizard_bluetooth_button);
		TextView text = (TextView) findViewById(R.id.setupwizard_bluetooth_text);
		ImageView image = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_paired_image);

		switch (requestCode) {

		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras()
				.getString(OBDMeBluetoothDiscovery.EXTRA_DEVICE_ADDRESS);
				image.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
				text.setText(R.string.setupwizard_bluetooth_text_connect);
				button.setText(R.string.setupwizard_bluetooth_button_connect_text);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(getResources().getString(R.string.prefs_bluetooth_device), address);
				editor.commit();
				SETUP_STATE = 3;
				
				//if(getResources().getBoolean(R.bool.debug)) Log.d(getResources().getString(R.string.debug_tag_setupwizrd_bluetooth), 
				//		"Device Connected.");
			}
			break;

		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				image.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
				text.setText(R.string.setupwizard_bluetooth_text_select);
				button.setText(R.string.setupwizard_bluetooth_button_select_text);
				SETUP_STATE = 2;

			} else {
				if(getResources().getBoolean(R.bool.debug)) Log.d(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
						getResources().getString(R.string.setupwizard_bluetooth_error_notenabled));
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}

		}
	}

	/** The message handler. */
	private final Handler messageHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if(getResources().getBoolean(R.bool.debug)) Log.i(getResources().getString(R.string.debug_tag_setupwizard_bluetooth),
						"MESSAGE_STATE_CHANGE: " + msg.arg1);
				
				Button button = (Button) findViewById(R.id.setupwizard_bluetooth_button);
				TextView text = (TextView) findViewById(R.id.setupwizard_bluetooth_text);
				ImageView image = (ImageView) findViewById(R.id.setupwizard_bluetooth_list_connected_image);
				
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					image.setImageDrawable(getResources().getDrawable(R.drawable.green_tick));
					text.setText(R.string.setupwizard_bluetooth_text_connect);
					button.setText(R.string.setupwizard_complete_button_next_text);
					connectDialog.dismiss();
					SETUP_STATE = 4;
					break;
				case BluetoothService.STATE_FAILED:
					image.setImageDrawable(getResources().getDrawable(R.drawable.red_x));
					connectDialog.dismiss();
					break;
				case BluetoothService.STATE_CONNECTING:
					connectDialog = ProgressDialog.show(OBDMeSetupWizardBluetooth.this, "", getResources().getString(R.string.setupwizard_bluetooth_dialog_connecting), true);
					break;
				case BluetoothService.STATE_NONE:
					connectDialog.dismiss();
					break;
				}
				break;

			case MESSAGE_WRITE:
				
				break;
			case MESSAGE_READ:
				
				break;
			}
		}
	};
}
