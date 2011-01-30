package edu.unl.csce.obdme.hardware.elm;

import android.content.Context;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.hardware.obd.OBDFramework;
import edu.unl.csce.obdme.hardware.obd.OBDPID;
import edu.unl.csce.obdme.hardware.obd.OBDRequest;
import edu.unl.csce.obdme.hardware.obd.OBDResponse;
import edu.unl.csce.obdme.hardware.obd.OBDResponseLengthException;

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

	/**
	 * Instantiates a new ELM Framework.
	 *
	 * @param context the context
	 * @param bluetoothService the bluetooth service
	 */
	public ELMFramework(Context context, BluetoothService bluetoothService) {
		this.bluetoothService = bluetoothService;
		this.context = context;
		this.setConnectionInit(false);
		this.setHardwareVerified(false);
		this.setIgnitionOn(false);
		this.setProtocolSet(false);

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

		OBDResponse response = null;

		try {
			//If the protocol is set
			if(this.isProtocolSet()) {
				//Send the formatted request to the bluetooth service
				bluetoothService.write(request.toString());

				//Parse the response from the bluetooth service
				response = new OBDResponse(context, request, bluetoothService.getResponseFromQueue());

			}

		} catch (ELMDeviceNoDataException dnde) {
			//This most likely means that the PID is not supported.
			//For safetey's sake, we are going to disable it.
			getObdFramework().getConfiguredProtocol().get(request.getMode())
			.getPID(request.getPid()).setSupported(false);

		} catch (ELMUnableToConnectException utce) {
			//Whoopsies... The ELM327 is not connected to an ECU.
			//TODO

		} catch (OBDResponseLengthException orle) {
			//Hmmm... There seems to be
			//a) A problem with our connection (bad data transfer)
			//b) Or we got something we didn't ask for

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

		OBDRequest request = new OBDRequest(pid);
		OBDResponse response = null;

		bluetoothService.write(request.toString());

		//Parse the response from the bluetooth service
		response = new OBDResponse(context, request, bluetoothService.getResponseFromQueue());

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


		//If all the steps completed successfully
		if (connectionInitIncr == 3) {
			this.setConnectionInit(true);
		}
		else {
			this.setConnectionInit(false);
		}

		//Return isConnectionInit
		return this.isConnectionInit();
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
		bluetoothService.clearResponseQueue();
		bluetoothService.write("ATI");

		//If it matches our device identifier, the hardware is verified
		if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_IDENTIFIER)) {
			this.setHardwareVerified(true);
		}
		else {
			this.setHardwareVerified(false);
		}

		//Return isHardwareVerified
		return this.isHardwareVerified();
	}

	/**
	 * Check vehicle ignition.
	 *
	 * @return true, if successful
	 */
	public synchronized boolean checkVehicleIgnition() {

		bluetoothService.write("ATIGN");
		if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_ON)) {
			this.setIgnitionOn(true);
		}
		else {
			this.setIgnitionOn(false);
		}

		//Return isIgnitionOn
		return this.isIgnitionOn();

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

			//If the response was OK, then success
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
				this.setProtocolSet(true);
			}
			else {
				this.setProtocolSet(false);
			}
		}
		return this.isProtocolSet();
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
	private void setHardwareVerified(boolean hardwareVerified) {
		this.hardwareVerified = hardwareVerified;
	}

	/**
	 * Sets the ignition on.
	 *
	 * @param ignitionOn the ignitionOn to set
	 */
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
