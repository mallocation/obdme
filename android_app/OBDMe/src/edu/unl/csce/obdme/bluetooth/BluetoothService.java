package edu.unl.csce.obdme.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import edu.unl.csce.obdme.OBDMe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
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

	/** The Constant DEVICE_IDENTIFIER. */
	private static final String DEVICE_IDENTIFIER = "ELM327 v1.4";

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
		//commConnectedThread.start();
		
		if (DEBUG) Log.d(DEBUG_TAG, commConnectedThread.sendCommand("ATZ"));
		
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

	public String sendCommand(String command) {
		// Create temporary object
		CommunicationConnectedThread thread;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (messageState != STATE_CONNECTED);
			thread = commConnectedThread;
		}
		// Perform the write unsynchronized
		return thread.sendCommand(command);
	}


	/**
	 * Connection failed.
	 */
	private void connectionFailed() {
		setState(STATE_LISTEN);

		// Send a failure message back to the Activity
		Message msg = appHandler.obtainMessage(OBDMe.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(OBDMe.TOAST, "Unable to connect to OBD device");
		msg.setData(bundle);
		appHandler.sendMessage(msg);
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

				try {
					bluetoothSocket.close();
				} catch (IOException e2) {
					Log.e(DEBUG_TAG, "Unable to close socket during connection failure", e2);
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
		}

		private synchronized String receiveResponse() {

			Log.d(DEBUG_TAG, "Waiting for response from the device.");

			//Initialize the buffers
			StringBuffer recievedData = new StringBuffer();
			char currentChar = ' ';

			try {
				do {

					//If there are bytes available, read them
					if (this.inputStream.available() > 0) {

						//Append the read byte to the buffer
						currentChar = (char)this.inputStream.read();
						recievedData.append(currentChar);
					}
				} while (currentChar != ASCII_COMMAND_PROMPT); //Continue until we reach a prompt character

			} catch (IOException ioe) {
				Log.e(DEBUG_TAG, "Exception when receiving data from the device:", ioe);
			}

			String recievedString = recievedData.toString();

			//Remove extra characters and trim the string
			recievedString = recievedString.replace("\r", "");
			recievedString = recievedString.replace(">", "");
			recievedString = recievedString.trim();

			Log.d(DEBUG_TAG, "Received string from device: " + recievedString);

			//Return the received data
			return recievedString;

		}
		public String sendCommand(String command) {
			try {
				//Send the bytes and flush the output stream
				if (DEBUG) Log.d(DEBUG_TAG, "Generating command byte array");
				byte[] commandByteArray = new String(command + "\r").getBytes("ASCII");

				//Flush the output stream
				if (DEBUG) Log.d(DEBUG_TAG, "Writing bytes to the output stream.");
				this.outputStream.write(commandByteArray);
				this.outputStream.flush();

			} catch (UnsupportedEncodingException uee) {
				Log.e(DEBUG_TAG, "UnsupportedEncodingException", uee);
			} catch (IOException ioe) {
				Log.e(DEBUG_TAG, "IOException", ioe);
			}

			//Set the last command and return the response
			return receiveResponse();
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

		private String byteArrayToHexString(byte[] byteArray) {

			//Initialize the string buffer
			StringBuffer stringBuffer = new StringBuffer(byteArray.length * 2);

			//Start constructing the byte array
			stringBuffer.append("[ ");

			//For all the bytes in the array
			for (int i = 0; i < byteArray.length; i++) {

				//Convert the byte to an integer
				int v = byteArray[i] & 0xff;

				//Left shift
				if (v < 16) {
					stringBuffer.append('0');
				}

				//Add the hex string representation of the byte 
				stringBuffer.append("0x" + Integer.toHexString(v).toUpperCase() + " ");
			}

			//Close the byte array string
			stringBuffer.append("]");

			//Convert the string buffer to a string a return it
			return stringBuffer.toString();
		}
	}
}