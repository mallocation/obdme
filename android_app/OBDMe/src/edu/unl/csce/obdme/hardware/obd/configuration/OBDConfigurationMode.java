package edu.unl.csce.obdme.hardware.obd.configuration;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class OBDConfigurationMode.
 */
public class OBDConfigurationMode {

	/** The configured mode hex. */
	private String modeHex;

	/** The configured pid list. */
	private ConcurrentHashMap<String, OBDConfigurationPID> configuredPIDList;

	/**
	 * Instantiates a new oBD configuration mode.
	 *
	 * @param modeHex the mode hex
	 */
	public OBDConfigurationMode(String modeHex) {
		this.modeHex = modeHex;

		//Create a new hash map that contains the PIDS
		configuredPIDList = new ConcurrentHashMap<String, OBDConfigurationPID>();
	}

	/**
	 * Sets the mode hex.
	 *
	 * @param modeName the new mode hex
	 */
	public void setModeHex(String modeName) {
		this.modeHex = modeName;
	}

	/**
	 * Gets the mode hex.
	 *
	 * @return the mode hex
	 */
	public String getModeHex() {
		return modeHex;
	}

	/**
	 * Put pid.
	 *
	 * @param pidHex the pid hex
	 */
	public void putPID(String pidHex) {
		this.configuredPIDList.put(pidHex, new OBDConfigurationPID(pidHex));
	}

	/**
	 * Put pid.
	 *
	 * @param pidObject the pid object
	 */
	public void putPID(OBDConfigurationPID pidObject) {
		this.configuredPIDList.put(pidObject.getPidHex(), pidObject);
	}
	
	/**
	 * Gets the pID.
	 *
	 * @param pidHex the pid hex
	 * @return the pID
	 */
	public OBDConfigurationPID getPID(String pidHex) {
		return this.configuredPIDList.get(pidHex);
	}

	/**
	 * Contains pid.
	 *
	 * @param hex the hex
	 * @return true, if successful
	 */
	public boolean containsPID(String hex) {
		if (this.configuredPIDList.containsKey(hex)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Pid key set.
	 *
	 * @return the sets the
	 */
	public Set<String> pidKeySet() {
		return configuredPIDList.keySet();
	}

	/**
	 * Removes the pid.
	 *
	 * @param pidHex the pid hex
	 */
	public void removePID(String pidHex) {
		this.configuredPIDList.remove(pidHex);
	}

}
