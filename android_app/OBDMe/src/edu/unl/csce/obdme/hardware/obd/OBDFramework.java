package edu.unl.csce.obdme.hardware.obd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import edu.unl.csce.obdme.hardware.elm.ELMException;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;

/**
 * The Class OBDFramework.
 */
public class OBDFramework {

	/** The configured protocol. */
	private ConcurrentHashMap<String, OBDMode> configuredProtocol;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/**
	 * Instantiates a new OBD framework.
	 *
	 * @param context the context
	 * @param parentELMFramework the parent elm framework
	 */
	public OBDFramework(Context context, ELMFramework parentELMFramework) {
		this.elmFramework = parentELMFramework;
		this.configuredProtocol = OBDConfigurationManager.parseOBDFullProtocol(context);

	}
	
	/**
	 * Enable all pids.
	 */
	public void enableAllPIDS() {
		for (String modeHex : configuredProtocol.keySet()) {
			for(String pidHex : configuredProtocol.get(modeHex).pidKeySet()) {
				configuredProtocol.get(modeHex).getPID(pidHex).setEnabled(true);
				
			}
		}
	}

	/**
	 * Gets the enabled pid list.
	 *
	 * @return the enabled pid list
	 */
	public HashMap<String, List<String>> getEnabledPIDList() {

		HashMap<String, List<String>> enabledPIDS = new HashMap<String, List<String>>();

		//For all the modes that exist in the configured protocol
		for ( String currentMode : configuredProtocol.keySet() ) {
			
			enabledPIDS.put(currentMode, new ArrayList<String>());

			//For all the pids that exist in the configured protocol
			for ( String currentPID : configuredProtocol.get(currentMode).pidKeySet()) {

				//If the PID is enabled
				if (configuredProtocol.get(currentMode).getPID(currentPID).isSupported() && 
						configuredProtocol.get(currentMode).getPID(currentPID).isEnabled()) {

					//Add the PID hex to the list
					enabledPIDS.get(currentMode).add(currentPID);
				}
			}
		}

		return enabledPIDS;
	}

	/**
	 * Gets the pollable pid list.
	 *
	 * @return the pollable pid list
	 */
	public HashMap<String, List<String>> getPollablePIDList() {

		HashMap<String, List<String>> pollablePIDS = new HashMap<String, List<String>>();

		//For all the modes that exist in the configured protocol
		for ( String currentMode : configuredProtocol.keySet() ) {
			
			pollablePIDS.put(currentMode, new ArrayList<String>());

			//For all the pids that exist in the configured protocol
			for ( String currentPID : configuredProtocol.get(currentMode).pidKeySet()) {

				//If the PID is enabled
				if (configuredProtocol.get(currentMode).getPID(currentPID).isSupported() && 
						configuredProtocol.get(currentMode).getPID(currentPID).isPollable()) {

					pollablePIDS.get(currentMode).add(currentPID);
				}
			}
		}

		return pollablePIDS;
	}

	/**
	 * Gets the enabled pollable pid list.
	 *
	 * @return the enabled pollable pid list
	 */
	public HashMap<String, List<String>> getEnabledPollablePIDList() {

		HashMap<String, List<String>> enabledPollablePIDS = new HashMap<String, List<String>>();

		//For all the modes that exist in the configured protocol
		for ( String currentMode : configuredProtocol.keySet() ) {
			
			enabledPollablePIDS.put(currentMode, new ArrayList<String>());

			//For all the pids that exist in the configured protocol
			for ( String currentPID : configuredProtocol.get(currentMode).pidKeySet()) {

				//If the PID is enabled
				if (configuredProtocol.get(currentMode).getPID(currentPID).isSupported() && 
						configuredProtocol.get(currentMode).getPID(currentPID).isEnabled() &&
						configuredProtocol.get(currentMode).getPID(currentPID).isPollable()) {

					//Add the PID hex to the list
					enabledPollablePIDS.get(currentMode).add(currentPID);
				}
			}
		}

		return enabledPollablePIDS;
	}



	/**
	 * Query valid pids.
	 *
	 * @return true, if successful
	 * @throws ELMException the eLM exception
	 */
	public boolean queryValidPIDS() throws ELMException {

		return OBDValidator.validate(this.elmFramework);

	}

	/**
	 * Removes the mode.
	 *
	 * @param modeHex the mode hex
	 */
	public void removeMode(String modeHex) {
		if(this.configuredProtocol.contains(modeHex)) {
			this.configuredProtocol.remove(modeHex);
		}
	}

	/**
	 * Checks if is pID supported.
	 *
	 * @param mode the mode
	 * @param pid the pid
	 * @return true, if is pID supported
	 */
	public boolean isPIDSupported(String mode, String pid) {

		//If the configured protocol contains the mode
		if (configuredProtocol.containsKey(mode)) {

			//If the configured protocol contains the pid
			if (configuredProtocol.get(mode).containsPID(pid)) {

				//Return if the configured protocol supports the pid
				return configuredProtocol.get(mode).getPID(pid).isSupported();
			}

			//If it's not listed, its not supported as far as we're concerned
			else {
				return false;
			}
		}

		//If the mode is not listed, the pid is not supported as far as we're concerned.
		else {
			return false;
		}
	}

	/**
	 * Gets the configured protocol.
	 *
	 * @return the configuredProtocol
	 */
	public ConcurrentHashMap<String, OBDMode> getConfiguredProtocol() {
		return configuredProtocol;
	}

	/**
	 * Sets the configured protocol.
	 *
	 * @param configuredProtocol the configuredProtocol to set
	 */
	public void setConfiguredProtocol(ConcurrentHashMap<String, OBDMode> configuredProtocol) {
		this.configuredProtocol = configuredProtocol;
	}

}

