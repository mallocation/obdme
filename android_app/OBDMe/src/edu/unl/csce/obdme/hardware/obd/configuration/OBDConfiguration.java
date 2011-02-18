package edu.unl.csce.obdme.hardware.obd.configuration;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The Class OBDConfiguration.
 */
public class OBDConfiguration {

	/** The configured mode list. */
	private ConcurrentHashMap<String, OBDConfigurationMode> configuredModeList;

	/** The VIN. */
	private String VIN;


	/**
	 * Instantiates a new oBD configuration.
	 *
	 * @param vin the vin
	 */
	public OBDConfiguration(String vin) {

		this.VIN = vin;

		//Create a new hash map that contains the Modes
		configuredModeList = new ConcurrentHashMap<String, OBDConfigurationMode>();
	}

	/**
	 * Put mode.
	 *
	 * @param modeHex the mode hex
	 */
	public void putMode(String modeHex) {
		this.configuredModeList.put(modeHex, new OBDConfigurationMode(modeHex));
	}


	/**
	 * Gets the mode.
	 *
	 * @param modeHex the mode hex
	 * @return the mode
	 */
	public OBDConfigurationMode getMode(String modeHex) {
		return this.configuredModeList.get(modeHex);
	}

	/**
	 * Contains mode.
	 *
	 * @param modeHex the mode hex
	 * @return true, if successful
	 */
	public boolean containsMode(String modeHex) {
		if (this.configuredModeList.containsKey(modeHex)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Query supported pid.
	 *
	 * @param modeHex the mode hex
	 * @param pidHex the pid hex
	 * @return true, if successful
	 */
	public boolean querySupportedPID(String modeHex, String pidHex) {
		
		//If the configuration contains the mode
		if (this.configuredModeList.containsKey(modeHex)) {
			
			//and contains the PID
			if (this.configuredModeList.get(modeHex).containsPID(pidHex)) {
				
				//Return true
				return true;
			}
		}
		
		//Otherwise, it's not supported
		return false;
	}
	
	

	/**
	 * Mode key set.
	 *
	 * @return the sets the
	 */
	public Set<String> modeKeySet() {
		return configuredModeList.keySet();
	}

	/**
	 * Removes the mode.
	 *
	 * @param modeHex the mode hex
	 */
	public void removeMode(String modeHex) {
		this.configuredModeList.remove(modeHex);
	}

	/**
	 * Sets the vIN.
	 *
	 * @param vin the new vIN
	 */
	public void setVIN(String vin) {
		VIN = vin;
	}

	/**
	 * Gets the vIN.
	 *
	 * @return the vIN
	 */
	public String getVIN() {
		return VIN;
	}

}
