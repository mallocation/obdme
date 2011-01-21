package edu.unl.csce.obdme.hardware;

import java.util.HashMap;

/**
 * The Class OBDValidator.
 */
public abstract class OBDValidator {

	/**
	 * Validate.
	 *
	 * @param configuredProtocol the configured protocol
	 * @param elmFramework the elm framework
	 * @return the hash map
	 */
	public static HashMap<String, OBDMode> validate(HashMap<String, OBDMode> configuredProtocol, ELMFramework elmFramework) {

		if (configuredProtocol.containsKey("01")) {
			if(configuredProtocol.get("01").containsPID("00")) {
				elmFramework.sendOBDRequest(configuredProtocol.get("01").getPID("00").generateRequest());
			}
			if(configuredProtocol.get("01").containsPID("00")) {

			}
			if(configuredProtocol.get("01").containsPID("00")) {

			}
		}


		return configuredProtocol;

	}
}