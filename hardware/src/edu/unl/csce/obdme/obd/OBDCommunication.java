package edu.unl.csce.obdme.obd;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.unl.csce.obdme.elm.CommunicationInterface;
import edu.unl.csce.obdme.elm.ELM327CommandSet;
import edu.unl.csce.obdme.elm.OBDCommands;

public class OBDCommunication {
	
	/** The serial interface to the ELM327. */
	protected CommunicationInterface serialInterface;
	
	protected Logger log;
	
	protected OBDDataHelpers helper;
	
	protected PIDS pids;
	
	protected MODES modes;
	
	public OBDCommunication(CommunicationInterface serialInterface) {
		//Save a reference to the serial interface object
		this.serialInterface = serialInterface;
		
		//Instantiate the data helper
		this.helper = new OBDDataHelpers();
				
		//Initialize the logger for this instance
		log = Logger.getLogger(ELM327CommandSet.class);
		
	}
	
	public void getSupportedPIDS() throws Exception {
		
		OBDCommands commands = new OBDCommands(this.serialInterface);

		//Send the command
		this.serialInterface.sendOBDCommand(MODES.CURRENT_DATA.getMode() + PIDS.SUPPORTED_PIDS.getPid());
		
		//Check if we got what we expected in return
		String receivedData = this.serialInterface.recieveResponse();
		
		List<Integer> responseValues = helper.stringToIntArray(receivedData);
		
		//Check the header info on the response
		responseValues = helper.processHeader(responseValues, MODES.CURRENT_DATA.getValue(), 
				PIDS.SUPPORTED_PIDS.getValue());
		
		//Make sure that our response size is correct
		if (responseValues.size() > PIDS.SUPPORTED_PIDS.getReturnSize()) {
			log.warn("Result was not expected size, trimming results to size.");
			responseValues = responseValues.subList(0, 4);
		}
		
		Iterator<Integer> responseValueItr = responseValues.iterator();
		StringBuffer encodedBits = new StringBuffer();
		
		while (responseValueItr.hasNext()) {
			encodedBits.append(Integer.toBinaryString(responseValueItr.next()));
		}
		
		log.info("Supported PID's bit string: " + encodedBits.toString());
		
		
		
	}

}
