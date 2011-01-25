package edu.unl.csce.obdme.hardware.elm;

import android.content.Context;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.hardware.obd.OBDFramework;
import edu.unl.csce.obdme.hardware.obd.OBDPID;
import edu.unl.csce.obdme.hardware.obd.OBDParserException;
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

	private Context context;

	/**
	 * Instantiates a new ELM Framework.
	 *
	 * @param context the context
	 * @param bluetoothService the bluetooth service
	 * @param autoStart the auto start
	 */
	public ELMFramework(Context context, BluetoothService bluetoothService, boolean autoStart) {
		this.bluetoothService = bluetoothService;
		this.context = context;
		this.setConnectionInit(false);
		this.setHardwareVerified(false);
		this.setIgnitionOn(false);
		this.setProtocolSet(false);

		//Create a new OBD Framework
		this.obdFramework = new OBDFramework(context, this);

		//Automatically find protocols
		if(autoStart) {
			this.autoSearchProtocols();
		}
	}

	/**
	 * Send OBD request.
	 *
	 * @param request the request
	 * @return the oBD response
	 * @throws OBDParserException 
	 */
	public OBDResponse sendOBDRequest(OBDRequest request) throws ELMException {

		OBDResponse response = null;
		
		//Try to automatically auto search for protocols if someone else
		//failed to do so appropriately
		if(isProtocolSet()) {
			autoSearchProtocols();
		}

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
	 * Initializes the connection.
	 *
	 * @return true, if successful
	 */
	public boolean initConnection() {

		//If the connection is not already initialized
		if (!this.isConnectionInit()) {

			//Establish an incrementer so that we know whether or not all
			//the initialization steps completed successfully
			int connectionInitIncr = 0;

			//Reset the ELM to defaults
			bluetoothService.clearResponseQueue();
			bluetoothService.write("ATD");
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
				connectionInitIncr++;
			}

			//Turn off echo to eliminate useless data transfer
			bluetoothService.clearResponseQueue();
			bluetoothService.write("ATE0");
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
				connectionInitIncr++;
			}

			//If all the steps completed successfully
			if (connectionInitIncr == 2) {
				this.setConnectionInit(true);
			}
			else {
				this.setConnectionInit(false);
			}
		}

		//Return isConnectionInit
		return this.isConnectionInit();
	}

	/**
	 * Verify hardware version.
	 *
	 * @return true, if successful
	 */
	public boolean verifyHardwareVersion() {

		//If the connection has been initialized
		if(this.isConnectionInit()) {

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
		}

		//Otherwise, try to initialize the connection
		else {

			this.initConnection();

			//If the connection has been initialized
			if(this.isConnectionInit()) {

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
			}
		}

		//Return isHardwareVerified
		return this.isHardwareVerified();
	}

	/**
	 * Check vehicle ignition.
	 *
	 * @return true, if successful
	 */
	public boolean checkVehicleIgnition() {

		//If the device hardware has been verified
		if(this.isHardwareVerified()) {
			bluetoothService.clearResponseQueue();
			bluetoothService.write("ATIGN");
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_ON)) {
				this.setIgnitionOn(true);
			}
			else {
				this.setIgnitionOn(false);
			}
		}

		//If the device hardware is not verified, try to verify it
		else {

			//Run the verify routine
			this.verifyHardwareVersion();

			//If the device hardware has been verified
			if(this.isHardwareVerified()) {

				//Request the ignition status
				bluetoothService.clearResponseQueue();
				bluetoothService.write("ATIGN");

				//If the ignition is on
				if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_ON)) {
					this.setIgnitionOn(true);
				}
				else {
					this.setIgnitionOn(false);
				}
			}
		}

		//Return isIgnitionOn
		return this.isIgnitionOn();

	}

	/**
	 * Auto search protocols.
	 *
	 * @return true, if successful
	 */
	public boolean autoSearchProtocols() {

		//If the vehicle ignition is on
		if(this.isIgnitionOn()) {

			//Send the autosearch protocol to the ELM device
			bluetoothService.clearResponseQueue();
			bluetoothService.write("ATSP0");

			//If the response was OK, then success
			if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
				this.setProtocolSet(true);
			}
			else {
				this.setProtocolSet(false);
			}
		}

		//Otherwise, try to check if the vehicle ignition is on
		else {
			this.checkVehicleIgnition();

			//If the vehicle ignoition is on
			if(this.isIgnitionOn()) {

				//Send the autosearch protocol to the ELM device
				bluetoothService.clearResponseQueue();
				bluetoothService.write("ATSP0");

				//If the response was OK, then success
				if(bluetoothService.getResponseFromQueue().contains(ELM_DEVICE_OK)) {
					this.setProtocolSet(true);
				}
				else {
					this.setProtocolSet(false);
				}
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
