package edu.unl.csce.obdme.hardware.elm;

import android.content.Context;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.bluetooth.BluetoothServiceRequestTimeoutException;
import edu.unl.csce.obdme.hardware.obd.OBDFramework;
import edu.unl.csce.obdme.hardware.obd.OBDPID;
import edu.unl.csce.obdme.hardware.obd.OBDRequest;
import edu.unl.csce.obdme.hardware.obd.OBDResponse;

/**
 * The Class ELMFramework.
 */
public class ELMFramework {

	/** The Constant ELM_DEVICE_IDENTIFIER. */
	private static final String ELM_DEVICE_IDENTIFIER = "ELM327 v1.4";

	/** The Constant ELM_DEVICE_OK. */
	private static final String ELM_DEVICE_OK = "OK";

	/** The Constant ELM_DEVICE_ON. */
	private static final String ELM_DEVICE_ON = "ON";

	/** The Constant ELM_DEVICE_OFF. */
	@SuppressWarnings("unused")
	private static final String ELM_DEVICE_OFF = "OFF";

	/** The connection init. */
	private boolean connectionInit;

	/** The hardware verified. */
	private boolean hardwareVerified;

	/** The ignition on. */
	private boolean ignitionOn;

	/** The protocol set. */
	private boolean protocolSet;

	/** The bluetooth service. */
	private BluetoothService bluetoothService;

	/** The obd framework. */
	private OBDFramework obdFramework;

	/** The context. */
	private Context context;

	/** The MAXIMU m_ reques t_ retries. */
	private int MAXIMUM_REQUEST_RETRIES;

	/**
	 * Instantiates a new ELM Framework.
	 *
	 * @param context the context
	 * @param bluetoothService the bluetooth service
	 */
	public ELMFramework(Context context, BluetoothService bluetoothService) {

		//Save a refference to our services
		this.bluetoothService = bluetoothService;
		this.context = context;

		//Set the initial state of the ELMFramework
		this.connectionInit = false;
		this.hardwareVerified = false;
		this.ignitionOn = false;
		this.protocolSet = false;

		//Set the ELMFramework constants from the configs
		MAXIMUM_REQUEST_RETRIES = context.getResources().getInteger(R.integer.bluetooth_response_maxretries);

		//Create a new OBD Framework
		this.obdFramework = new OBDFramework(context, this);
	}

	/**
	 * Send OBD request.
	 *
	 * @param request the request
	 * @return the oBD response
	 * @throws ELMException the eLM exception
	 */
	public synchronized OBDResponse sendOBDRequest(OBDRequest request) throws ELMException {


		//Initalize the response
		OBDResponse response = null;

		//Initialize the number of retries
		int retries = 0;

		try {
			//Write the request 
			do {
				//Send the formatted request to the bluetooth service
				bluetoothService.write(request.toString());

				//Parse the response from the bluetooth service
				try {
					response = new OBDResponse(context, request, bluetoothService.getResponseFromQueue());
				} catch (BluetoothServiceRequestTimeoutException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
						"Bluetooth Timeout Exception waiting for response from the device: " );
					}

					retries++;
				}

			} while (response != null && retries <= MAXIMUM_REQUEST_RETRIES);

			//If we reached our maximum number of retries... 
			if(retries <= MAXIMUM_REQUEST_RETRIES) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
							"Maximum request retries reached for Mode " + request.getMode() + " PID " + request.getPid() 
							+ ".  There is most likely something wrong with the bluetooth connection.");
				}
			}

		} catch (ELMDeviceNoDataException dnde) {
			//This most likely means that the PID is not supported.
			//For safetey's sake, we are going to disable it.
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
						"ELM Device is indicating no data for Mode " + request.getMode() + " PID " + request.getPid() 
						+ ".  Disabling this PID for now");
			}
			getObdFramework().getConfiguredProtocol().get(request.getMode())
			.getPID(request.getPid()).setEnabled(false);

		} catch (ELMUnableToConnectException utce) {

			//Unable to connect exception
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
				"ELM Device is indicating that it is unable to connect.  The car is most likely off.");
			}

		} catch (ELMException elme) {

			//General ELM Exception, do not take any actions.
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
						"ELM Device is indicating no data for Mode " + request.getMode() + " PID " + request.getPid() 
						+ ".  Not taking any actions");
			}
		}

		return response;
	}

	/**
	 * Send obd request.
	 *
	 * @param pid the pid
	 * @return the oBD response
	 * @throws ELMException the eLM exception
	 */
	public synchronized OBDResponse sendOBDRequest(OBDPID pid) throws ELMException {

		//Build the OBD REquest from the supplied PID object
		OBDRequest request = new OBDRequest(pid);

		//Initalize the response
		OBDResponse response = null;

		//Initialize the number of retries
		int retries = 0;

		try {
			//Write the request 
			do {
				//Send the formatted request to the bluetooth service
				bluetoothService.write(request.toString());

				//Parse the response from the bluetooth service
				try {
					response = new OBDResponse(context, request, bluetoothService.getResponseFromQueue());
				} catch (BluetoothServiceRequestTimeoutException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
								"Bluetooth Timeout Exception waiting for response from the device for Mode " 
								+ request.getMode() + " PID " + request.getPid() + "." );
					}

					retries++;
				}

			} while (response != null && retries <= MAXIMUM_REQUEST_RETRIES);

			//If we reached our maximum number of retries... 
			if(retries <= MAXIMUM_REQUEST_RETRIES) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
							"Maximum request retries reached for Mode " + request.getMode() + " PID " + request.getPid() 
							+ ".  There is most likely something wrong with the bluetooth connection.");
				}
			}

		} catch (ELMDeviceNoDataException dnde) {
			//This most likely means that the PID is not supported.
			//For safetey's sake, we are going to disable it.
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
						"ELM Device is indicating no data for Mode " + request.getMode() + " PID " + request.getPid() 
						+ ".  Disabling this PID for now");
			}
			getObdFramework().getConfiguredProtocol().get(request.getMode())
			.getPID(request.getPid()).setEnabled(false);

		} catch (ELMUnableToConnectException utce) {

			//Unable to connect exception
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
				"ELM Device is indicating that it is unable to connect.  The car is most likely off.");
			}

		} catch (ELMException elme) {

			//General ELM Exception, do not take any actions.
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
						"ELM Device is indicating no data for Mode " + request.getMode() + " PID " + request.getPid() 
						+ ".  Not taking any actions");
			}
		}

		return response;
	}

	/**
	 * Initializes the connection.
	 *
	 * @return true, if successful
	 */
	public synchronized boolean initConnection() {

		//If the connection is not already initialized

		//Establish an incrementer so that we know whether or not all
		//the initialization steps completed successfully
		int connectionInitIncr = 0;

		try {
			//Reset the ELM to defaults
			bluetoothService.write("ATD");

			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
				connectionInitIncr++;
			}

			//Turn off echo to eliminate useless data transfer
			bluetoothService.write("ATE0");
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
				connectionInitIncr++;
			}

			//Turn off echo to eliminate useless data transfer
			bluetoothService.write("ATS0");
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
				connectionInitIncr++;
			}
		} catch (BluetoothServiceRequestTimeoutException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
						"Bluetooth Timeout Exception waiting for response from the device for" +
						" Connection initialization." );
			}
		}


		//If all the steps completed successfully
		if (connectionInitIncr == 3) {
			this.connectionInit = true;
		}
		else {
			this.connectionInit = false;
		}

		//Return isConnectionInit
		return this.connectionInit;
	}

	/**
	 * Verify hardware version.
	 *
	 * @return true, if successful
	 */
	public synchronized boolean verifyHardwareVersion() {

		while(!this.isConnectionInit()) {
			this.initConnection();
		}
		//Request the hardware version
		bluetoothService.write("ATI");

		//If it matches our device identifier, the hardware is verified
		try {
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_IDENTIFIER)) {
				this.hardwareVerified = true;
			}
			else {
				this.hardwareVerified = false;
			}
		} catch (BluetoothServiceRequestTimeoutException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
						"Bluetooth Timeout Exception waiting for response from the device during" +
						"hardware verification." );
			}
		}

		//Return isHardwareVerified
		return this.hardwareVerified;
	}

	/**
	 * Check vehicle ignition.
	 *
	 * @return true, if successful
	 */
	public synchronized boolean checkVehicleIgnition() {

		//Write the ignition command
		bluetoothService.write("ATIGN");
		try {
			
			//If the response was ok
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_ON)) {
				
				//Set the ignition to on
				this.ignitionOn = true;
			}
			
			//Otherwise
			else {
				
				//Set the ignition to off
				this.ignitionOn = false;
			}
		} catch (BluetoothServiceRequestTimeoutException e) {
			
			//If our request timed out
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
						"Bluetooth Timeout Exception waiting for response from the device during" +
						"vehicle ignition check." );
			}
		}

		//Return isIgnitionOn
		return this.ignitionOn;

	}

	/**
	 * Auto search protocols.
	 *
	 * @return true, if successful
	 */
	public synchronized boolean autoSearchProtocols() {

		if(!this.isProtocolSet()) {
			//Send the autosearch protocol to the ELM device
			bluetoothService.write("ATSP0");

			try {
				//If the response was OK, then success
				if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
					this.protocolSet = true;
				}
				
				//Otherwise
				else {
					
					//Assume the protocol was not set
					this.protocolSet = false;
				}
			} catch (BluetoothServiceRequestTimeoutException e) {
				
				//If there was a connection timeout during our request
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.e(context.getResources().getString(R.string.debug_tag_elmframework),
							"Bluetooth Timeout Exception waiting for response from the device during" +
							"protocol auto search." );
				}
			}
		}
		
		return this.protocolSet;
	}

	/**
	 * Gets the configured pid.
	 *
	 * @param mode the mode
	 * @param pid the pid
	 * @return the configured pid
	 */
	public OBDPID getConfiguredPID(String mode, String pid) {

		return getObdFramework().getConfiguredProtocol().get(mode).getPID(pid);	

	}

	/**
	 * Query configured pid.
	 *
	 * @param mode the mode
	 * @param pid the pid
	 * @return true, if successful
	 */
	public boolean queryConfiguredPID(String mode, String pid) {

		if (obdFramework.getConfiguredProtocol().containsKey(mode)) {
			if(obdFramework.getConfiguredProtocol().get(mode).containsPID(pid)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Checks if is connection init.
	 *
	 * @return the connectionInit
	 */
	public boolean isConnectionInit() {
		return connectionInit;
	}

	/**
	 * Sets the connection init.
	 *
	 * @param connectionInit the connectionInit to set
	 */
	@SuppressWarnings("unused")
	private void setConnectionInit(boolean connectionInit) {
		this.connectionInit = connectionInit;
	}

	/**
	 * Checks if is hardware verified.
	 *
	 * @return the hardwareVerified
	 */
	public boolean isHardwareVerified() {
		return hardwareVerified;
	}

	/**
	 * Sets the hardware verified.
	 *
	 * @param hardwareVerified the hardwareVerified to set
	 */
	@SuppressWarnings("unused")
	private void setHardwareVerified(boolean hardwareVerified) {
		this.hardwareVerified = hardwareVerified;
	}

	/**
	 * Sets the ignition on.
	 *
	 * @param ignitionOn the ignitionOn to set
	 */
	@SuppressWarnings("unused")
	private void setIgnitionOn(boolean ignitionOn) {
		this.ignitionOn = ignitionOn;
	}

	/**
	 * Checks if is ignition on.
	 *
	 * @return the ignitionOn
	 */
	public boolean isIgnitionOn() {
		return ignitionOn;
	}

	/**
	 * Sets the protocol set.
	 *
	 * @param protocolSet the protocolSet to set
	 */
	@SuppressWarnings("unused")
	private void setProtocolSet(boolean protocolSet) {
		this.protocolSet = protocolSet;
	}

	/**
	 * Checks if is protocol set.
	 *
	 * @return the protocolSet
	 */
	public boolean isProtocolSet() {
		return protocolSet;
	}

	/**
	 * Sets the obd framework.
	 *
	 * @param obdFramework the obdFramework to set
	 */
	public void setObdFramework(OBDFramework obdFramework) {
		this.obdFramework = obdFramework;
	}

	/**
	 * Gets the obd framework.
	 *
	 * @return the obdFramework
	 */
	public OBDFramework getObdFramework() {
		return obdFramework;
	}

}
