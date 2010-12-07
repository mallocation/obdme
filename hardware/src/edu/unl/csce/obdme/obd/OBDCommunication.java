package edu.unl.csce.obdme.obd;

import java.util.EnumMap;

import java.util.List;

import org.apache.log4j.Logger;

import edu.unl.csce.obdme.bluetooth.CommunicationInterface;


/**
 * The Class OBDCommunication.
 */
public class OBDCommunication {
	
	/** The serial interface to the ELM327. */
	private CommunicationInterface serialInterface;
	
	/** The log. */
	private Logger log;
	
	/** The helper. */
	private OBDDataHelpers helper;
	
	/** The valid PIDS. */
	private EnumMap<PIDS, Boolean> validPIDS;
	
	/**
	 * Instantiates a new OBD communication.
	 *
	 * @param serialInterface the serial interface (bluetooth)
	 */
	public OBDCommunication(CommunicationInterface serialInterface) {
		//Save a reference to the serial interface object
		this.serialInterface = serialInterface;
		
		//Instantiate the data helper
		this.helper = new OBDDataHelpers();
		
		this.validPIDS = new EnumMap<PIDS, Boolean>(PIDS.class);
						
		//Initialize the logger for this instance
		log = Logger.getLogger(OBDCommunication.class);
		
		try {
			getSupportedPIDS();
		} catch (Exception e) {
			log.error("OBD Communication constructor could not get valid PIDS.");
		}
		
	}

	/**
	 * Gets the supported PIDS from the on board ECU.
	 *
	 * @return the supported PIDs
	 * @throws Exception the exception thrown by called methods
	 */
	public void getSupportedPIDS() throws Exception {
		
		//Send the command and receive the response
		String receivedData = this.serialInterface.sendOBDCommand(MODES.CURRENT_DATA.getMode() + 
				PIDS.SUPPORTED_PIDS.getPid());
		
		List<Integer> responseValues = helper.stringToIntArray(receivedData);
		
		//Check the header info on the response
		responseValues = helper.processHeader(responseValues, MODES.CURRENT_DATA, PIDS.SUPPORTED_PIDS);
		
		//Make sure that our response size is correct
		if (responseValues.size() > PIDS.SUPPORTED_PIDS.getReturnSize()) {
			log.warn("Result was not expected size, trimming results to size.");
			responseValues = responseValues.subList(0, 4);
		}
		
		String encodedBits = helper.makeBitString(responseValues);
		
		log.info("Supported PID's bit string: " + encodedBits);
		
		for (PIDS pid : PIDS.values()) {
			if (encodedBits.charAt(pid.getValue()) == '1') {
				log.info("Setting " + pid.toString() + " as a supported PID");
				this.validPIDS.put(pid, true);
			}
			else {
				log.info("Setting " + pid.toString() + " as a unsupported PID");
				this.validPIDS.put(pid, false);
			}
		}
		
	}
	
	/**
	 * Read the PID sensor/property value using the ELM327
	 *
	 * @param mode the mode enumeration
	 * @param pid the PID enumeration
	 * @return the string value of the result
	 * @throws Exception the exception thrown by called methods
	 */
	public String readPIDValue(MODES mode, PIDS pid) throws Exception {
		
		//Send command and wait for response
		String receivedData = this.serialInterface.sendOBDCommand(mode.getMode() + pid.getPid());
		
		List<Integer> responseValues = helper.stringToIntArray(receivedData);
		
		//Check the header info on the response
		responseValues = helper.processHeader(responseValues, mode, pid);
		
		if (responseValues.size() > pid.getReturnSize()) {
			log.warn("Result was not expected size, trimming results to size.");
			responseValues = responseValues.subList(0, pid.getReturnSize());
		}
		
		return Double.toString(pid.evalResult(responseValues));
	}	
	
	/**
	 * Gets the valid PIDS enumerator map.
	 *
	 * @return the valid PID's enumerator map
	 */
	public EnumMap<PIDS, Boolean> getValidPIDS() {
		return validPIDS;
	}

}
