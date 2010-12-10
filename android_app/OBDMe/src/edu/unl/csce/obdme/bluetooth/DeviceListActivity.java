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

public class DeviceListActivity extends Activity {
    private static final String DEBUG_TAG = "DeviceListActivity";
    private static final boolean DEBUG = true;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;
    private ArrayAdapter<String> newDevicesArrayAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        setResult(Activity.RESULT_CANCELED);

        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        newDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(newDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
            	pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter != null) {
        	bluetoothAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(broadcastReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (DEBUG) Log.d(DEBUG_TAG, "Executing Bluetooth device discovery");

        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        if (bluetoothAdapter.isDiscovering()) {
        	bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }

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
                setTitle(R.string.select_device);
                if (newDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    newDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}