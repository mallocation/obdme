package edu.unl.csce.obdme.bluetooth;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.unl.csce.obdme.OBDMe;
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

	/** The comm connect thread. */
	private BluetoothConnectThread bluetoothConnectThread;

	/** The comm connected thread. */
	private BluetoothConnectedThread bluetoothConnectedThread;

	/** The m state. */
	private int messageState;

	/** The context. */
	private Context context;

	/** The Constant MESSAGE_STATE_CHANGE. */
	public static final int MESSAGE_STATE_CHANGE = 0;

	/** The Constant STATE_NONE. */
	public static final int STATE_NONE = 0;

	/** The Constant STATE_LISTEN. */
	public static final int STATE_LISTEN = 1;

	/** The Constant STATE_CONNECTING. */
	public static final int STATE_CONNECTING = 2;

	/** The Constant STATE_CONNECTED. */
	public static final int STATE_CONNECTED = 3;

	/** The Constant STATE_FAILED. */
	public static final int STATE_FAILED = 4;

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
	private synchronized void setState(int state) {
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
					"New Bluetooth State ->" + state);
		}

		messageState = state;

		appHandler.obtainMessage(OBDMe.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
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
		setState(STATE_LISTEN);
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
		bluetoothConnectThread = new BluetoothConnectThread(device);
		bluetoothConnectThread.start();
		setState(STATE_CONNECTING);
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

		// Send the name of the connected device back to the UI Activity
		Message msg = appHandler.obtainMessage(OBDMe.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(OBDMe.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		appHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads.
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
	 */
	public String getResponseFromQueue() {
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
	 * Connection failed.
	 */
	private void connectionFailed() {
		setState(STATE_FAILED);
	}


	/**
	 * Connection lost.
	 */
	private void connectionLost() {
		setState(STATE_LISTEN);

		Message msg = appHandler.obtainMessage(OBDMe.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(OBDMe.TOAST, "Connection to the OBD device was lost");
		msg.setData(bundle);
		appHandler.sendMessage(msg);
	}

	/**
	 * The Class CommunicationConnectThread.
	 */
	private class BluetoothConnectThread extends Thread {

		/** The bluetooth socket. */
		private final BluetoothSocket bluetoothSocket;

		/** The bluetooth device. */
		private final BluetoothDevice bluetoothDevice;

		/**
		 * Instantiates a new communication connect thread.
		 *
		 * @param device the device
		 */
		public BluetoothConnectThread(BluetoothDevice device) {
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
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
				"Running communication connect thread");
			}

			setName("ConnectThread");

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

				connectionFailed();
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
	 * The Class ConnectionConnectedThread.
	 */
	private class BluetoothConnectedThread extends Thread {

		/** The bluetooth socket. */
		private final BluetoothSocket bluetoothSocket;

		/** The input stream. */
		private final InputStream inputStream;

		/** The output stream. */
		private final OutputStream outputStream;
		
		/** The response queue. */
		private ConcurrentLinkedQueue<String> responseQueue;

		/**
		 * Instantiates a new connection connected thread.
		 *
		 * @param socket the socket
		 */
		public BluetoothConnectedThread(BluetoothSocket socket) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_service_bluetooth),
				"Creating connected thread");
			}

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
						if (inputStream.available() > 0) {

							//Append the read byte to the buffer
							currentChar = (char)this.inputStream.read();
							recievedData.append(currentChar);
						}
					} while (currentChar != ASCII_COMMAND_PROMPT); //Continue until we reach a prompt character

					String recievedString = recievedData.toString();
					recievedString = recievedString.trim();

					responseQueue.add(recievedString);

				} catch (IOException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_service_bluetooth),
						"Disconnected");
					}
					connectionLost();
					break;
				}
			}
		}

		/**
		 * Gets the response from queue.
		 *
		 * @return the response from queue
		 */
		public String getResponseFromQueue() {
			while(responseQueue.isEmpty()){
				//Do nothing, we want to block
			}
			return responseQueue.poll();
		}
		
		/**
		 * Clear response queue.
		 */
		public void clearResponseQueue() {
			responseQueue.clear();
		}

		/**
		 * Write to the connected OutStream.
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
					"Exception during write");
				}
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