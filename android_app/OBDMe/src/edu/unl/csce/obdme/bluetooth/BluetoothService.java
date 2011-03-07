package edu.unl.csce.obdme.bluetooth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import edu.unl.csce.obdme.R;

/**
 * The Class BluetoothService.
 */
public class BluetoothService {

	/** The Constant ASCII_COMMAND_PROMPT. */
	private static final char ASCII_COMMAND_PROMPT = '>';

	/** The UUI d_ rfcom m_ generic. */
	static UUID UUID_RFCOMM_GENERIC = new UUID(0x0000110100001000L,0x800000805F9B34FBL);

	/** The bluetooth adapter. */
	private final BluetoothAdapter bluetoothAdapter;

	/** The app handler. */
	private Handler appHandler;

	/** The bluetooth connect thread. */
	private BluetoothConnectThread bluetoothConnectThread;

	/** The bluetooth connected thread. */
	private BluetoothConnectedThread bluetoothConnectedThread;

	/** The message state. */
	private int messageState;

	/** The context. */
	private Context context;

	/** The Constant STATE_CHANGE. */
	public static final int STATE_CHANGE = 1536616135;

	/** The Constant STATE_NONE. */
	public static final int STATE_NONE = 0;

	/** The Constant STATE_CONNECTING. */
	public static final int STATE_CONNECTING = 1;

	/** The Constant STATE_CONNECTED. */
	public static final int STATE_CONNECTED = 2;

	/** The Constant STATE_FAILED. */
	public static final int STATE_FAILED = 3;

	/**
	 * Instantiates a new bluetooth service.
	 *
	 * @param context the context
	 */
	public BluetoothService(Context context) {
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.messageState = STATE_NONE;
		this.context = context;
	}

	/**
	 * Sets the app handler.
	 *
	 * @param appHandler the new app handler
	 */
	public void setAppHandler(Handler appHandler) {
		this.appHandler = appHandler;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public synchronized void setState(int state) {
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"New Bluetooth State ->" + state);
		}

		if (this.messageState != state) {
			messageState = state;
			appHandler.obtainMessage(STATE_CHANGE, state, -1).sendToTarget();
		}
	}


	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public synchronized int getState() {
		return messageState;
	}


	/**
	 * Start.
	 */
	public synchronized void start() {
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
			"Starting the bluetooth service");
		}

		// Cancel any thread attempting to make a connection
		if (bluetoothConnectThread != null) {
			bluetoothConnectThread.cancel();
			bluetoothConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (bluetoothConnectedThread != null) {
			bluetoothConnectedThread.cancel();
			bluetoothConnectedThread = null;
		}
		setState(STATE_NONE);
	}

	/**
	 * Connect.
	 *
	 * @param device the device
	 */
	public synchronized void connect(BluetoothDevice device) {
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"Connecting to: " + device);
		}

		// Cancel any thread attempting to make a connection
		if (messageState == STATE_CONNECTING) {
			if (bluetoothConnectThread != null) {
				bluetoothConnectThread.cancel();
				bluetoothConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (bluetoothConnectedThread != null) {
			bluetoothConnectedThread.cancel();
			bluetoothConnectedThread = null;
		}

		// Start the thread to connect with the given device
		bluetoothConnectThread = new BluetoothConnectThread(device, 0);
		bluetoothConnectThread.start();
	}

	/**
	 * Connect.
	 *
	 * @param device the device
	 * @param sleepTime the sleep time
	 */
	public synchronized void connect(BluetoothDevice device, int sleepTime) {

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"Connecting to: " + device);
		}

		// Cancel any thread attempting to make a connection
		if (messageState == STATE_CONNECTING) {
			if (bluetoothConnectThread != null) {
				bluetoothConnectThread.cancel();
				bluetoothConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (bluetoothConnectedThread != null) {
			bluetoothConnectedThread.cancel();
			bluetoothConnectedThread = null;
		}

		// Start the thread to connect with the given device
		bluetoothConnectThread = new BluetoothConnectThread(device, sleepTime);
		bluetoothConnectThread.start();
	}

	/**
	 * Connected.
	 *
	 * @param socket the socket
	 * @param device the device
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
			"Connected");
		}

		// Cancel the thread that completed the connection
		if (bluetoothConnectThread != null) {
			bluetoothConnectThread.cancel();
			bluetoothConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (bluetoothConnectedThread != null) {
			bluetoothConnectedThread.cancel();
			bluetoothConnectedThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		bluetoothConnectedThread = new BluetoothConnectedThread(socket);
		bluetoothConnectedThread.start();

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop.
	 */
	public synchronized void stop() {
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
			"Stopping the bluetooth service");
		}

		if (bluetoothConnectThread != null) {
			bluetoothConnectThread.cancel();
			bluetoothConnectThread = null;
		}
		if (bluetoothConnectThread != null) {
			bluetoothConnectThread.cancel();
			bluetoothConnectThread = null;
		}
		setState(STATE_NONE);
	}

	/**
	 * Write.
	 *
	 * @param command the command
	 */
	public void write(String command) {
		// Create temporary object
		BluetoothConnectedThread thread;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (messageState != STATE_CONNECTED);
			thread = bluetoothConnectedThread;
		}
		// Perform the write unsynchronized
		thread.write(command);
	}

	/**
	 * Gets the response from queue.
	 *
	 * @return the response from queue
	 * @throws BluetoothServiceRequestTimeoutException the bluetooth service request timeout exception
	 */
	public String getResponseFromQueue() throws BluetoothServiceRequestTimeoutException {
		// Create temporary object
		BluetoothConnectedThread thread;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (messageState != STATE_CONNECTED);
			thread = bluetoothConnectedThread;
		}
		// Perform the write unsynchronized
		return thread.getResponseFromQueue();
	}

	/**
	 * Clear response queue.
	 */
	public void clearResponseQueue() {
		// Create temporary object
		BluetoothConnectedThread thread;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (messageState != STATE_CONNECTED);
			thread = bluetoothConnectedThread;
		}

		thread.clearResponseQueue();
	}

	/**
	 * The Class BluetoothConnectThread.
	 */
	private class BluetoothConnectThread extends Thread {

		/** The bluetooth socket. */
		private final BluetoothSocket bluetoothSocket;

		/** The bluetooth device. */
		private final BluetoothDevice bluetoothDevice;
		
		/** The sleep time. */
		private int sleepTime = 0;

		/**
		 * Instantiates a new bluetooth connect thread.
		 *
		 * @param device the device
		 * @param sleepTime the sleep time
		 */
		public BluetoothConnectThread(BluetoothDevice device, int sleepTime) {

			this.sleepTime = sleepTime;
			
			//Set the thread name
			setName("Bluetooth Connect");

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
				"Creating communication connect thread");
			}

			bluetoothDevice = device;
			BluetoothSocket tmp = null;

			try {
				tmp = device.createRfcommSocketToServiceRecord(UUID_RFCOMM_GENERIC);
			} catch (IOException e) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"IOException trying to create RFCOMM socket");
				}
			}
			finally{
				try {
					Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
					tmp = (BluetoothSocket) m.invoke(device, 1);
				} catch (SecurityException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
						"Security exception trying to create RFCOMM socket");
					}
				} catch (IllegalArgumentException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
						"Illegal argument exception trying to create RFCOMM socket");
					}
				} catch (NoSuchMethodException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
						"No such method exception trying to create RFCOMM socket");
					}
				} catch (IllegalAccessException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
						"Illegal access exception trying to create RFCOMM socket");
					}
				} catch (InvocationTargetException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
						"Invocation target exception trying to create RFCOMM socket");
					}
				}
			}
			bluetoothSocket = tmp;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			
			if (sleepTime != 0) {
				SystemClock.sleep(sleepTime);
			}
			
			setState(STATE_CONNECTING);
			
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
				"Running communication connect thread");
			}
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
				"Canceling device discovery");
			}

			bluetoothAdapter.cancelDiscovery();

			try {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"Attempting to make connection to device.");
				}

				bluetoothSocket.connect();

			} catch (IOException ioe) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"IOException while trying to connect to the device.");
				}

				setState(STATE_FAILED);

				try {
					bluetoothSocket.close();
				} catch (IOException e2) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
						"Unable to close socket during connection failure");
					}

					setState(STATE_FAILED);
				}

				BluetoothService.this.start();
				return;
			}

			synchronized (BluetoothService.this) {
				bluetoothConnectThread = null;
			}

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
				"Connection to the device was successful.");
			}

			connected(bluetoothSocket, bluetoothDevice);
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"close() of connect socket failed.");
				}
			}
		}

	}


	/**
	 * The Class BluetoothConnectedThread.
	 */
	private class BluetoothConnectedThread extends Thread {

		/** The bluetooth socket. */
		private final BluetoothSocket bluetoothSocket;

		/** The input stream. */
		private final InputStream inputStream;
		
		/** The buffered input stream. */
		private final BufferedInputStream bufferedInputStream;

		/** The output stream. */
		private final OutputStream outputStream;

		/** The response queue. */
		private ConcurrentLinkedQueue<String> responseQueue;

		/** The timeout. */
		private int timeout;

		/**
		 * Instantiates a new bluetooth connected thread.
		 *
		 * @param socket the socket
		 */
		public BluetoothConnectedThread(BluetoothSocket socket) {

			//Set the thread name
			setName("Bluetooth Connected");

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
				"Creating connected thread");
			}

			timeout = context.getResources().getInteger(R.integer.bluetooth_response_timeout);
			bluetoothSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException ioe) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"Temporary sockets were not created");
				}
			}

			inputStream = tmpIn;
			outputStream = tmpOut;
			bufferedInputStream = new BufferedInputStream(inputStream);
			
			responseQueue = new ConcurrentLinkedQueue<String>();
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
				"Running Connected Thread");
			}

			// Keep listening to the InputStream while connected
			while (true) {
				try {

					StringBuffer recievedData = new StringBuffer();
					char currentChar = ' ';

					do {

						//If there are bytes available, read them
						if (bufferedInputStream.available() > 0) {

							//Append the read byte to the buffer
							currentChar = (char)this.bufferedInputStream.read();
							recievedData.append(currentChar);
						}
					} while (currentChar != ASCII_COMMAND_PROMPT); //Continue until we reach a prompt character

					responseQueue.add(recievedData.toString());

				} catch (IOException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
						"Disconnected");
					}
					setState(STATE_FAILED);
					break;
				}
			}
		}

		/**
		 * Gets the response from queue.
		 *
		 * @return the response from queue
		 * @throws BluetoothServiceRequestTimeoutException the bluetooth service request timeout exception
		 */
		public String getResponseFromQueue() throws BluetoothServiceRequestTimeoutException {

			int timeWaiting = 0; 

			//While the response queue is empty
			while(responseQueue.isEmpty()){

				//Sleep for 10 milliseconds
				SystemClock.sleep(10);

				//Increase the amount of time we've spent waiting
				timeWaiting += 10;

				//If the time waiting exceeds the request timeout limit, throw a BluetoothServiceRequestTimeoutException
				if (timeWaiting >= timeout) {
					throw new BluetoothServiceRequestTimeoutException("The device took too long to respond.");
				}

			}

			//Otherwise, we have a response.  Return it.
			return responseQueue.poll();
		}

		/**
		 * Clear response queue.
		 */
		public void clearResponseQueue() {
			responseQueue.clear();
		}

		/**
		 * Write.
		 *
		 * @param command the command
		 */
		public void write(String command) {
			try {

				byte[] commandByteArray = new String(command + "\r").getBytes("ASCII");
				outputStream.write(commandByteArray);

			} catch (IOException e) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
							"Exception during write", e);
				}
				setState(BluetoothService.STATE_FAILED);
			}
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException ioe) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"IOException when closing connected socket");
				}
			}
		}
	}
}