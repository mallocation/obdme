package edu.unl.csce.obdme.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import edu.unl.csce.obdme.bluetooth.BluetoothAndroidService.IncomingHandler;

public class BluetoothAndroidService extends Service {


	private static final char ASCII_COMMAND_PROMPT = '>';
	static UUID UUID_RFCOMM_GENERIC = new UUID(0x0000110100001000L,0x800000805F9B34FBL);
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothConnectThread bluetoothConnectThread;
	private BluetoothConnectedThread bluetoothConnectedThread;

	/** The m state. */
	private int bluetoothState;
	ArrayList<Messenger> bluetoothClients = new ArrayList<Messenger>();
	final Messenger messenger = new Messenger(new IncomingHandler());

	public static final int BLUETOOTH_SERVICE_REGISTER_CLIENT = 0;
	public static final int BLUETOOTH_SERVICE_UNREGISTER_CLIENT = 1;
	public static final int BLUETOOTH_SERVICE_START = 2;
	public static final int BLUETOOTH_SERVICE_CONNECT = 3;
	public static final int BLUETOOTH_SERVICE_STOP = 4;
	public static final int BLUETOOTH_SERVICE_READ = 5;
	public static final int BLUETOOTH_SERVICE_WRITE = 6;
	public static final int BLUETOOTH_SERVICE_READ_RESPONSE = 7;
	public static final int BLUETOOTH_SERVICE_STATE_CHANGE = 8;

	public static final int BLUETOOTH_STATE_NONE = 0;
	public static final int BLUETOOTH_STATE_LISTENING = 1;
	public static final int BLUETOOTH_STATE_CONNECTING = 2;
	public static final int BLUETOOTH_STATE_CONNECTED = 3;
	public static final int BLUETOOTH_STATE_FAILED = 4;


	@Override
	public void onCreate() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		bluetoothState = BLUETOOTH_STATE_NONE;
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BLUETOOTH_SERVICE_REGISTER_CLIENT:
				bluetoothClients.add(msg.replyTo);
				break;
			case BLUETOOTH_SERVICE_UNREGISTER_CLIENT:
				bluetoothClients.remove(msg.replyTo);
				break;
			case BLUETOOTH_SERVICE_START:
				start();
				break;
			case BLUETOOTH_SERVICE_CONNECT:
				BluetoothDevice device = (BluetoothDevice) msg.obj;
				connect(device);
				break;
			case BLUETOOTH_SERVICE_STOP:
				stop();
				break;
			case BLUETOOTH_SERVICE_READ:
				String response = getResponseFromQueue();
				for (int i = bluetoothClients.size() - 1; i >= 0; i--) {
					try {
						bluetoothClients.get(i).send(Message.obtain(null, BLUETOOTH_SERVICE_READ_RESPONSE, response));
					} catch (RemoteException e) {
						// The client is dead.  Remove it from the list;
						// we are going through the list from back to front
						// so this is safe to do inside the loop.
						bluetoothClients.remove(i);
					}
				}
				break;
			case BLUETOOTH_SERVICE_WRITE:
				String message = (String) msg.obj;
				write(message);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private synchronized void setState(int state) {
		//if (DEBUG) Log.d(DEBUG_TAG, "setState() " + messageState + " -> " + state);
		bluetoothState = state;

		for (int i = bluetoothClients.size() - 1; i >= 0; i--) {
			try {
				bluetoothClients.get(i).send(Message.obtain(null,
						BLUETOOTH_SERVICE_STATE_CHANGE, bluetoothState, 0));
			} catch (RemoteException e) {
				// The client is dead.  Remove it from the list;
				// we are going through the list from back to front
				// so this is safe to do inside the loop.
				bluetoothClients.remove(i);
			}
		}
	}

	public synchronized void start() {

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
		setState(BLUETOOTH_STATE_LISTENING);
	}

	public synchronized void connect(BluetoothDevice device) {
		//if (DEBUG) Log.d(DEBUG_TAG, "Connecting to: " + device);

		// Cancel any thread attempting to make a connection
		if (bluetoothState == BLUETOOTH_STATE_CONNECTING) {
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
		setState(BLUETOOTH_STATE_CONNECTING);
	}

	/**
	 * Connected.
	 *
	 * @param socket the socket
	 * @param device the device
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		//if (DEBUG) Log.d(DEBUG_TAG, "Connected");

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

		setState(BLUETOOTH_STATE_CONNECTED);
	}

	public synchronized void stop() {
		//if (DEBUG) Log.d(DEBUG_TAG, "Stopping");
		if (bluetoothConnectThread != null) {
			bluetoothConnectThread.cancel();
			bluetoothConnectThread = null;
		}
		if (bluetoothConnectThread != null) {
			bluetoothConnectThread.cancel();
			bluetoothConnectThread = null;
		}
		setState(BLUETOOTH_STATE_NONE);
	}

	public void write(String command) {
		// Create temporary object
		BluetoothConnectedThread thread;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (bluetoothState != BLUETOOTH_STATE_CONNECTED);
			thread = bluetoothConnectedThread;
		}
		// Perform the write unsynchronized
		thread.write(command);
	}

	public String getResponseFromQueue() {
		// Create temporary object
		BluetoothConnectedThread thread;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (bluetoothState != BLUETOOTH_STATE_CONNECTED);
			thread = bluetoothConnectedThread;
		}
		
		return thread.getResponseFromQueue(true);

	}

	private void connectionFailed() {
		setState(BLUETOOTH_STATE_FAILED);
	}

	private void connectionLost() {
		setState(BLUETOOTH_STATE_LISTENING);
	}

	private class BluetoothConnectThread extends Thread {
		private final BluetoothSocket bluetoothSocket;
		private final BluetoothDevice bluetoothDevice;

		public BluetoothConnectThread(BluetoothDevice device) {
			//if (DEBUG) Log.d(DEBUG_TAG, "Creating communication connect thread");
			bluetoothDevice = device;
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(UUID_RFCOMM_GENERIC);
			} catch (IOException e) {
				//Log.e(DEBUG_TAG, "IOException trying to create RFCOMM socket", e);
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
			//Log.i(DEBUG_TAG, "Running communication connect thread");
			setName("ConnectThread");

			//if (DEBUG) Log.d(DEBUG_TAG, "Canceling device discovery");
			bluetoothAdapter.cancelDiscovery();

			try {

				//if (DEBUG) Log.d(DEBUG_TAG, "Attempting to make connection to device.");
				bluetoothSocket.connect();

			} catch (IOException ioe) {
				//if (DEBUG) Log.e(DEBUG_TAG, "IOException while trying to connect to the device.", ioe);
				connectionFailed();
				setState(BLUETOOTH_STATE_FAILED);

				try {
					bluetoothSocket.close();
				} catch (IOException e2) {
					//Log.e(DEBUG_TAG, "Unable to close socket during connection failure", e2);
					setState(BLUETOOTH_STATE_FAILED);
				}

				BluetoothAndroidService.this.start();
				return;
			}

			synchronized (BluetoothAndroidService.this) {
				bluetoothConnectThread = null;
			}

			//if (DEBUG) Log.d(DEBUG_TAG, "Connection to the device was successful.");
			connected(bluetoothSocket, bluetoothDevice);
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				//Log.e(DEBUG_TAG, "close() of connect socket failed", e);
			}
		}

	}

	private class BluetoothConnectedThread extends Thread {

		private final BluetoothSocket bluetoothSocket;
		private final InputStream inputStream;
		private final OutputStream outputStream;
		private boolean echoCommand = true;
		private String lastCommand = "";
		private ConcurrentLinkedQueue<String> responseQueue;

		public BluetoothConnectedThread(BluetoothSocket socket) {
			//Log.d(DEBUG_TAG, "Creating connected thread");
			bluetoothSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException ioe) {
				//Log.e(DEBUG_TAG, "Temporary sockets were not created", ioe);
			}

			inputStream = tmpIn;
			outputStream = tmpOut;
			responseQueue = new ConcurrentLinkedQueue<String>();
		}

		@Override
		public void run() {
			//Log.i(DEBUG_TAG, "BEGIN mConnectedThread");

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
					//Log.e(DEBUG_TAG, "disconnected", e);
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

		public void write(String command) {
			try {

				byte[] commandByteArray = new String(command + "\r").getBytes("ASCII");
				outputStream.write(commandByteArray);

				this.lastCommand = command + "\r";

			} catch (IOException e) {
				//Log.e(DEBUG_TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException ioe) {
				//	Log.e(DEBUG_TAG, "IOException when closing connected socket", ioe);
			}
		}
	}


}