package edu.unl.csce.obdme.bluetooth;

import java.util.Set;

import edu.unl.csce.obdme.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

// TODO: Auto-generated Javadoc
/**
 * The Class BluetoothDiscovery.
 */
public class BluetoothDiscovery extends Activity {
	
	/** The Constant DEBUG_TAG. */
	private static final String DEBUG_TAG = "DeviceListActivity";
	
	/** The Constant DEBUG. */
	private static final boolean DEBUG = true;

	/** The EXTR a_ devic e_ address. */
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	/** The bluetooth adapter. */
	private BluetoothAdapter bluetoothAdapter;
	
	/** The paired devices array adapter. */
	private ArrayAdapter<String> pairedDevicesArrayAdapter;
	
	/** The new devices array adapter. */
	private ArrayAdapter<String> newDevicesArrayAdapter;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.bluetooth_discovery);

		setResult(Activity.RESULT_CANCELED);

		Button scanButton = (Button) findViewById(R.id.btdiscovery_button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});

		pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		newDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		ListView pairedListView = (ListView) findViewById(R.id.btdiscovery_paireddevices);
		pairedListView.setAdapter(pairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);

		ListView newDevicesListView = (ListView) findViewById(R.id.btdiscovery_newdevices);
		newDevicesListView.setAdapter(newDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(broadcastReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(broadcastReceiver, filter);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

		if (pairedDevices.size() > 0) {
			findViewById(R.id.btdiscovery_title_paireddevices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		} else {
			String noDevices = getResources().getText(R.string.btdiscovery_nonepaired).toString();
			pairedDevicesArrayAdapter.add(noDevices);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (bluetoothAdapter != null) {
			bluetoothAdapter.cancelDiscovery();
		}

		this.unregisterReceiver(broadcastReceiver);
	}

	/**
	 * Start device discover with the BluetoothAdapter.
	 */
	private void doDiscovery() {
		if (DEBUG) Log.d(DEBUG_TAG, "Executing Bluetooth device discovery");

		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.btdiscovery_scanning);
		newDevicesArrayAdapter.clear();

		findViewById(R.id.btdiscovery_title_newdevices).setVisibility(View.VISIBLE);

		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}

		bluetoothAdapter.startDiscovery();
	}

	/** The m device click listener. */
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			bluetoothAdapter.cancelDiscovery();

			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);

			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};

	/** The broadcast receiver. */
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					newDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			} 
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.btdiscovery_selectdevice);
				findViewById(R.id.btdiscovery_button_scan).setVisibility(View.VISIBLE);
				if (newDevicesArrayAdapter.getCount() == 0) {
					String noDevices = getResources().getText(R.string.btdiscovery_nonefound).toString();
					newDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

}