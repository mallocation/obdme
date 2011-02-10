package edu.unl.csce.obdme.hardware.obd;

import java.util.List;

import edu.unl.csce.obdme.hardware.elm.ELMException;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;

/**
 * The Class OBDValidator.
 */
public abstract class OBDValidator {

	/**
	 * Validate.
	 *
	 * @param elmFramework the elm framework
	 * @return true, if successful
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked" })
	public static boolean validate(ELMFramework elmFramework) throws Exception {

		//See if the valid PID query for Mode 1 PID 00 is supported
		if (elmFramework.queryConfiguredPID("01", "00")) {
			OBDResponse result = elmFramework.sendOBDRequest(elmFramework.getConfiguredPID("01", "00"));
			if(result.getProcessedResponse() instanceof List){

				//For the bit string response, change the supported status
				for (String supportedPID : (List<String>) result.getProcessedResponse()) {
					if (elmFramework.queryConfiguredPID("01", supportedPID)) {
						elmFramework.getObdFramework().getConfiguredProtocol().get("01")
						.getPID(supportedPID).setSupported(true);
					}
				}
			}
		}

		//See if the valid PID query for Mode 1 PID 20 is supported
		if (elmFramework.queryConfiguredPID("01", "20")) {
			OBDResponse result = elmFramework.sendOBDRequest(elmFramework.getConfiguredPID("01", "20"));
			if(result.getProcessedResponse() instanceof List){
				//For the bit string response, change the supported status
				for (String supportedPID : (List<String>) result.getProcessedResponse()) {
					if (elmFramework.queryConfiguredPID("01", supportedPID)) {
						elmFramework.getObdFramework().getConfiguredProtocol().get("01")
						.getPID(supportedPID).setSupported(true);
					}
				}
			}
		}

		//See if the valid PID query for Mode 1 PID 40 is supported
		if (elmFramework.queryConfiguredPID("01", "40")) {
			OBDResponse result = elmFramework.sendOBDRequest(elmFramework.getConfiguredPID("01", "40"));
			if(result.getProcessedResponse() instanceof List){
				//For the bit string response, change the supported status
				for (String supportedPID : (List<String>) result.getProcessedResponse()) {
					if (elmFramework.queryConfiguredPID("01", supportedPID)) {
						elmFramework.getObdFramework().getConfiguredProtocol().get("01")
						.getPID(supportedPID).setSupported(true);
					}
				}
			}
		}

		//See if the valid PID query for Mode 9 PID 00 is supported
		if (elmFramework.queryConfiguredPID("09", "00")) {
			OBDResponse result = elmFramework.sendOBDRequest(elmFramework.getConfiguredPID("09", "00"));
			if(result.getProcessedResponse() instanceof List){
				//For the bit string response, change the supported status
				for (String supportedPID : (List<String>) result.getProcessedResponse()) {
					if (elmFramework.queryConfiguredPID("09", supportedPID)) {
						elmFramework.getObdFramework().getConfiguredProtocol().get("09")
						.getPID(supportedPID).setSupported(true);
					}
				}
			}
		}
		
		//Free up all the unsupported PID's to free up memory space
		//removeUnsupportedPIDS(elmFramework);
		
		return true;
	}

	/**
	 * Removes the unsupported pids.
	 *
	 * @param elmFramework the elm framework
	 */
	public static void removeUnsupportedPIDS(ELMFramework elmFramework) {

		//For all the modes that exist in the configured protocol
		for ( String currentMode : elmFramework.getObdFramework()
				.getConfiguredProtocol().keySet() ) {

			//For all the pids that exist in the configured protocol
			for ( String currentPID : elmFramework.getObdFramework()
					.getConfiguredProtocol().get(currentMode).pidKeySet()) {

				//If it's not supported
				if (!elmFramework.getObdFramework().getConfiguredProtocol().get(currentMode)
						.getPID(currentPID).isSupported()) {

					//Remove it
					elmFramework.getObdFramework().getConfiguredProtocol().get(currentMode)
					.removePID(currentPID);
				}
			}
		}
	}
}