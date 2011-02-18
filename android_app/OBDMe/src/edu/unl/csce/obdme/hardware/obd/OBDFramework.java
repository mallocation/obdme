package edu.unl.csce.obdme.hardware.obd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.SharedPreferences;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.obd.configuration.OBDConfigurationManager;

/**
 * The Class OBDFramework.
 */
public class OBDFramework {

	/** The configured protocol. */
	private ConcurrentHashMap<String, OBDMode> configuredProtocol;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The saved configuration. */
	private boolean savedConfiguration = false;

	/**
	 * Instantiates a new oBD framework.
	 *
	 * @param context the context
	 * @param parentELMFramework the parent elm framework
	 */
	public OBDFramework(Context context, ELMFramework parentELMFramework) {
		
		//Save a refference to the ELMFramework
		this.elmFramework = parentELMFramework;

		//Initalize a temporary shared preferences object
		SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.prefs_tag), 0);
		
		//If the VIN preference is saved (from some previous setup) and we are not debugging the setup wizard...
		if (prefs.contains(context.getString(R.string.prefs_account_vin)) && 
				!context.getResources().getBoolean(R.bool.debug_setupwizard)) {
			
			//Check if the configuration file still exists
			if (OBDConfigurationManager.checkForSavedProtocol(context, prefs.getString(
					context.getString(R.string.prefs_account_vin), null))){
				
				//Load the protocol constructed from the saved configuration
				this.configuredProtocol = OBDConfigurationManager.parseSavedOBDProtocol(context, OBDConfigurationManager.
						parseSavedConfiguration(context,  prefs.getString(context.getString(R.string.prefs_account_vin), null)));
				
				//Set that this is a saved configuration
				this.savedConfiguration = true;
			}
		}
		
		//There is no VIN saved
		else {
			
			//Set the configuration as false and load the whole protocol
			this.savedConfiguration = false;
			this.configuredProtocol = OBDConfigurationManager.parseFullOBDProtocol(context);
		}

	}

	/**
	 * Collect all pids.
	 */
	public void collectAllPIDS() {
		for (String modeHex : configuredProtocol.keySet()) {
			for(String pidHex : configuredProtocol.get(modeHex).pidKeySet()) {
				configuredProtocol.get(modeHex).getPID(pidHex).setCollected(true);

			}
		}
	}
	
	/**
	 * Display all pids.
	 */
	public void displayAllPIDS() {
		for (String modeHex : configuredProtocol.keySet()) {
			for(String pidHex : configuredProtocol.get(modeHex).pidKeySet()) {
				configuredProtocol.get(modeHex).getPID(pidHex).setDisplayed(true);
			}
		}
	}

	/**
	 * Gets the supported pollable pid list.
	 *
	 * @return the supported pollable pid list
	 */
	public synchronized HashMap<String, List<String>> getSupportedPollablePIDList() {

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
	 * Gets the collected pid list.
	 *
	 * @return the collected pid list
	 */
	public synchronized HashMap<String, List<String>> getCollectedPIDList() {

		HashMap<String, List<String>> enabledPollablePIDS = new HashMap<String, List<String>>();

		//For all the modes that exist in the configured protocol
		for ( String currentMode : configuredProtocol.keySet() ) {

			enabledPollablePIDS.put(currentMode, new ArrayList<String>());

			//For all the pids that exist in the configured protocol
			for ( String currentPID : configuredProtocol.get(currentMode).pidKeySet()) {

				//If the PID is enabled
				if (configuredProtocol.get(currentMode).getPID(currentPID).isSupported() && 
						configuredProtocol.get(currentMode).getPID(currentPID).isPollable() && 
						configuredProtocol.get(currentMode).getPID(currentPID).isCollected()) {

					//Add the PID hex to the list
					enabledPollablePIDS.get(currentMode).add(currentPID);
				}
			}
		}

		return enabledPollablePIDS;
	}
	
	/**
	 * Gets the displayed pid list.
	 *
	 * @return the displayed pid list
	 */
	public synchronized HashMap<String, List<String>> getDisplayedPIDList() {

		HashMap<String, List<String>> enabledPollablePIDS = new HashMap<String, List<String>>();

		//For all the modes that exist in the configured protocol
		for ( String currentMode : configuredProtocol.keySet() ) {

			enabledPollablePIDS.put(currentMode, new ArrayList<String>());

			//For all the pids that exist in the configured protocol
			for ( String currentPID : configuredProtocol.get(currentMode).pidKeySet()) {

				//If the PID is enabled
				if (configuredProtocol.get(currentMode).getPID(currentPID).isSupported() && 
						configuredProtocol.get(currentMode).getPID(currentPID).isCollected() &&
						configuredProtocol.get(currentMode).getPID(currentPID).isPollable() &&
						configuredProtocol.get(currentMode).getPID(currentPID).isDisplayed()) {

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
	 * @throws Exception the exception
	 */
	public boolean queryValidPIDS() throws Exception {

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
	 * @return the configured protocol
	 */
	public ConcurrentHashMap<String, OBDMode> getConfiguredProtocol() {
		return configuredProtocol;
	}

	/**
	 * Sets the configured protocol.
	 *
	 * @param configuredProtocol the configured protocol
	 */
	public void setConfiguredProtocol(ConcurrentHashMap<String, OBDMode> configuredProtocol) {
		this.configuredProtocol = configuredProtocol;
	}

	/**
	 * Sets the saved configuration.
	 *
	 * @param savedConfiguration the new saved configuration
	 */
	public void setSavedConfiguration(boolean savedConfiguration) {
		this.savedConfiguration = savedConfiguration;
	}

	/**
	 * Checks if is saved configuration.
	 *
	 * @return true, if is saved configuration
	 */
	public boolean isSavedConfiguration() {
		return savedConfiguration;
	}

}

