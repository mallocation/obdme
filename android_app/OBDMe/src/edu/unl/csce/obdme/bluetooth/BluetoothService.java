package edu.unl.csce.obdme.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.unl.csce.obdme.OBDMe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * The Class BluetoothService.
 */
public class BluetoothService {

	/** The Constant ASCII_COMMAND_PROMPT. */
	private static final char ASCII_COMMAND_PROMPT = '>';

	/** The Constant DEBUG_TAG. */
	private static final String DEBUG_TAG = "BluetoothService";

	/** The Constant DEBUG. */
	private static final boolean DEBUG = true;

	/** The Constant NAME. */
	private static final String NAME = "BluetoothService";

	static UUID UUID_RFCOMM_GENERIC = new UUID(0x0000110100001000L,0x800000805F9B34FBL);

	/** The bluetooth adapter. */
	private final BluetoothAdapter bluetoothAdapter;

	/** The app handler. */
	private final Handler appHandler;

	/** The comm connect thread. */
	private CommunicationConnectThread commConnectThread;

	/** The comm connected thread. */
	private CommunicationConnectedThread commConnectedThread;

	/** The m state. */
	private int messageState;

	/** The Constant STATE_NONE. */
	public static final int STATE_NONE = 0;

	/** The Constant STATE_LISTEN. */
	public static final int STATE_LISTEN = 1;

	/** The Constant STATE_CONNECTING. */
	public static final int STATE_CONNECTING = 2;

	/** The Constant STATE_CONNECTED. */
	public static final int STATE_CONNECTED = 3;

	public static final int STATE_FAILED = 4;

	/**
	 * Instantiates a new bluetooth service.
	 *
	 * @param context the context
	 * @param handler the handler
	 */
	public BluetoothService(Context context, Handler handler) {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		messageState = STATE_NONE;
		appHandler = handler;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	private synchronized void setState(int state) {
		if (DEBUG) Log.d(DEBUG_TAG, "setState() " + messageState + " -> " + state);
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
		if (DEBUG) Log.d(DEBUG_TAG, "Starting");

		// Cancel any thread attempting to make a connection
		if (commConnectThread != null) {
			commConnectThread.cancel();
			commConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (commConnectedThread != null) {
			commConnectedThread.cancel();
			commConnectedThread = null;
		}
		setState(STATE_LISTEN);
	}

	/**
	 * Connect.
	 *
	 * @param device the device
	 */
	public synchronized void connect(BluetoothDevice device) {
		if (DEBUG) Log.d(DEBUG_TAG, "Connecting to: " + device);

		// Cancel any thread attempting to make a connection
		if (messageState == STATE_CONNECTING) {
			if (commConnectThread != null) {
				commConnectThread.cancel();
				commConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (commConnectedThread != null) {
			commConnectedThread.cancel();
			commConnectedThread = null;
		}

		// Start the thread to connect with the given device
		commConnectThread = new CommunicationConnectThread(device);
		commConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Connected.
	 *
	 * @param socket the socket
	 * @param device the device
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		if (DEBUG) Log.d(DEBUG_TAG, "Connected");

		// Cancel the thread that completed the connection
		if (commConnectThread != null) {
			commConnectThread.cancel();
			commConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (commConnectedThread != null) {
			commConnectedThread.cancel();
			commConnectedThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		commConnectedThread = new CommunicationConnectedThread(socket);
		commConnectedThread.start();

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
		if (DEBUG) Log.d(DEBUG_TAG, "Stopping");
		if (commConnectThread != null) {
			commConnectThread.cancel();
			commConnectThread = null;
		}
		if (commConnectThread != null) {
			commConnectThread.cancel();
			commConnectThread = null;
		}
		setState(STATE_NONE);
	}

	public void write(String command) {
		// Create temporary object
		CommunicationConnectedThread thread;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (messageState != STATE_CONNECTED);
			thread = commConnectedThread;
		}
		// Perform the write unsynchronized
		thread.write(command);
	}

	public String getResponseFromQueue(boolean block) {
		// Create temporary object
		CommunicationConnectedThread thread;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (messageState != STATE_CONNECTED);
			thread = commConnectedThread;
		}
		// Perform the write unsynchronized
		return thread.getResponseFromQueue(block);
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
	private class CommunicationConnectThread extends Thread {

		/** The bluetooth socket. */
		private final BluetoothSocket bluetoothSocket;

		/** The bluetooth device. */
		private final BluetoothDevice bluetoothDevice;

		/**
		 * Instantiates a new communication connect thread.
		 *
		 * @param device the device
		 */
		public CommunicationConnectThread(BluetoothDevice device) {
			if (DEBUG) Log.d(DEBUG_TAG, "Creating communication connect thread");
			bluetoothDevice = device;
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(UUID_RFCOMM_GENERIC);
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "IOException trying to create RFCOMM socket", e);
			}
			finally{
				try {
					Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
					tmp = (BluetoothSocket) m.invoke(device, 1);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			bluetoothSocket = tmp;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			Log.i(DEBUG_TAG, "Running communication connect thread");
			setName("ConnectThread");

			if (DEBUG) Log.d(DEBUG_TAG, "Canceling device discovery");
			bluetoothAdapter.cancelDiscovery();

			try {

				if (DEBUG) Log.d(DEBUG_TAG, "Attempting to make connection to device.");
				bluetoothSocket.connect();

			} catch (IOException ioe) {
				if (DEBUG) Log.e(DEBUG_TAG, "IOException while trying to connect to the device.", ioe);
				connectionFailed();
				setState(STATE_FAILED);

				try {
					bluetoothSocket.close();
				} catch (IOException e2) {
					Log.e(DEBUG_TAG, "Unable to close socket during connection failure", e2);
					setState(STATE_FAILED);
				}

				BluetoothService.this.start();
				return;
			}

			synchronized (BluetoothService.this) {
				commConnectThread = null;
			}

			if (DEBUG) Log.d(DEBUG_TAG, "Connection to the device was successful.");
			connected(bluetoothSocket, bluetoothDevice);
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "close() of connect socket failed", e);
			}
		}

	}


	/**
	 * The Class ConnectionConnectedThread.
	 */
	private class CommunicationConnectedThread extends Thread {

		/** The bluetooth socket. */
		private final BluetoothSocket bluetoothSocket;

		/** The input stream. */
		private final InputStream inputStream;

		/** The output stream. */
		private final OutputStream outputStream;

		/** The echo command. */
		private boolean echoCommand = true;

		/** The last command. */
		private String lastCommand = "";

		private ConcurrentLinkedQueue<String> responseQueue;

		/**
		 * Instantiates a new connection connected thread.
		 *
		 * @param socket the socket
		 */
		public CommunicationConnectedThread(BluetoothSocket socket) {
			Log.d(DEBUG_TAG, "Creating connected thread");
			bluetoothSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException ioe) {
				Log.e(DEBUG_TAG, "Temporary sockets were not created", ioe);
			}

			inputStream = tmpIn;
			outputStream = tmpOut;

			responseQueue = new ConcurrentLinkedQueue<String>();
		}

		@Override
		public void run() {
			Log.i(DEBUG_TAG, "BEGIN mConnectedThread");

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
					Log.e(DEBUG_TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}
		}

		public String getResponseFromQueue(boolean block) {

			if (block) {
				while(responseQueue.isEmpty()) {
					//Wait
				}
				return responseQueue.poll();
			}

			return responseQueue.poll();
		}

		/**
		 * Write to the connected OutStream.
		 * @param buffer  The bytes to write
		 */
		public void write(String command) {
			try {

				byte[] commandByteArray = new String(command + "\r").getBytes("ASCII");
				outputStream.write(commandByteArray);

				this.lastCommand = command + "\r";

			} catch (IOException e) {
				Log.e(DEBUG_TAG, "Exception during write", e);
			}
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException ioe) {
				Log.e(DEBUG_TAG, "IOException when closing connected socket", ioe);
			}
		}
	}
}