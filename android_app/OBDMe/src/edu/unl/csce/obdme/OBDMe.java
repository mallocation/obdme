package edu.unl.csce.obdme;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.unl.csce.obdme.bluetooth.BluetoothDiscovery;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.settingsmenu.SettingsMenu;

// TODO: Auto-generated Javadoc
/**
 * The Class OBDMe.
 */
public class OBDMe extends Activity {

	/** The Constant DEBUG_TAG. */
	private static final String DEBUG_TAG = "OBDMe";
	
	/** The Constant DEBUG. */
	private static final boolean DEBUG = true;
	
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

	/** The Constant REQUEST_CONNECT_DEVICE. */
	private static final int REQUEST_CONNECT_DEVICE = 1;
	
	/** The Constant REQUEST_ENABLE_BT. */
	private static final int REQUEST_ENABLE_BT = 2;
	
	/** Modes of Operation */
	public static final int USER_MODE = 0;
	public static final int MECHANIC_MODE = 1;

	/** The Constant DEVICE_NAME. */
	public static final String DEVICE_NAME = "device_name";
	
	/** The Constant TOAST. */
	public static final String TOAST = "toast";

	/** The title bar. */
	private TextView titleBar;
	
    /** The m connected device name. */
    private String mConnectedDeviceName = null;
    
    // Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    
    /** The bluetooth adapter. */
    private BluetoothAdapter bluetoothAdapter = null;
    
    /** The bluetooth service. */
    private BluetoothService bluetoothService = null;
    
    /** Shared Preferences */
    private SharedPreferences sharedPrefs;

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(DEBUG) Log.e(DEBUG_TAG, "+++ OBDMe CREATE +++");

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		titleBar = (TextView) findViewById(R.id.title_left_text);
		titleBar.setText(R.string.app_name);
		titleBar = (TextView) findViewById(R.id.title_right_text);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (bluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available.  OBDMe will now exit.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), MODE_PRIVATE);
		
		/* Depending on the mode (user or mechanic) display appropriate data
		 * to the user and also display data according to which orientation
		 * the screen is in (landscape or portrait).
		 */
		if( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == USER_MODE ){
			
		} else if ( sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), -1) == MECHANIC_MODE ) {
			
		} else {
			// First run, so set it to USER_MODE
			SharedPreferences.Editor prefEditor = sharedPrefs.edit();
			prefEditor.putInt(getResources().getString(R.string.prefs_mode), USER_MODE);
    		prefEditor.commit();
    		
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	public void onStart() {
		super.onStart();
		if(DEBUG) Log.e(DEBUG_TAG, "++ OBDME ON START ++");


		if (!bluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			if (bluetoothService == null) setupOBDConnection();
		}
	}
	
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    public synchronized void onResume() {
        super.onResume();
        if(DEBUG) Log.e(DEBUG_TAG, "+ ON RESUME +");

        if (bluetoothService != null) {

        	if (bluetoothService.getState() == BluetoothService.STATE_NONE) {

        		bluetoothService.start();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    public synchronized void onPause() {
        super.onPause();
        if(DEBUG) Log.e(DEBUG_TAG, "- ON PAUSE -");
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    public void onStop() {
        super.onStop();
        if(DEBUG) Log.e(DEBUG_TAG, "-- ON STOP --");
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    public void onDestroy() {
        super.onDestroy();

        if (bluetoothService != null) bluetoothService.stop();
        if(DEBUG) Log.e(DEBUG_TAG, "--- OBDME ON DESTROY ---");
    }
    
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            bluetoothService.write(message);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(DEBUG) Log.i(DEBUG_TAG, "END onEditorAction");
            return true;
        }
    };
	
	/**
	 * Setup obd connection.
	 */
    
    private void setupOBDConnection() {
        Log.d(DEBUG_TAG, "Setting up OBD Connection");

        // Initialize the BluetoothChatService to perform bluetooth connections
        //bluetoothService = new BluetoothService(this, messageHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

	// The Handler that gets information back from the BluetoothChatService
	/** The m handler. */
	private final Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if(DEBUG) Log.i(DEBUG_TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					titleBar.setText(R.string.title_connected_to);
					titleBar.append(mConnectedDeviceName);
					break;
				case BluetoothService.STATE_CONNECTING:
					titleBar.setText(R.string.title_connecting);
					break;
				case BluetoothService.STATE_LISTEN:
					
				case BluetoothService.STATE_NONE:
					Intent myIntent = new Intent();
					titleBar.setText(R.string.title_not_connected);
					break;
				}
				break;
				
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add(">" + writeMessage);
                break;
            case MESSAGE_READ:
               String readMessage = (String) msg.obj;
                mConversationArrayAdapter.add(readMessage);
                break;
			case MESSAGE_DEVICE_NAME:
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to "
						+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(DEBUG) Log.d(DEBUG_TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras()
				.getString(BluetoothDiscovery.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
				bluetoothService.connect(device);
				if(DEBUG) Log.d(DEBUG_TAG, "Back in the activity result area.");
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				setupOBDConnection();
			} else {
				Log.d(DEBUG_TAG, "Bluetooth must be enable to use the OBDMe Applciation");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
            Intent serverIntent = new Intent(this, BluetoothDiscovery.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
		case R.id.settings:
        	startActivity(new Intent(this, SettingsMenu.class));
            return true;
        }

        return false;
    }
}