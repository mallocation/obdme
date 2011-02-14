package edu.unl.csce.obdme.hardware.obd;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class OBDMode.
 */
public class OBDMode {

	/** The mode hex. */
	private String modeHex;

	/** The mode value. */
	private int modeValue;

	/** The mode description. */
	private String modeDescription;

	/** The pid list. */
	private ConcurrentHashMap<String, OBDPID> pidList;


	/**
	 * Instantiates a new oBD mode.
	 *
	 * @param modeHex the mode hex
	 * @param modeDescription the mode description
	 */
	public OBDMode(String modeHex, String modeDescription) {
		this.modeHex = modeHex;
		this.modeValue = Integer.parseInt(modeHex,16);
		this.modeDescription = modeDescription;

		//Create a new hash map that contains the PIDS
		pidList = new ConcurrentHashMap<String, OBDPID>();
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
	 * Sets the mode description.
	 *
	 * @param modeDescription the new mode description
	 */
	public void setModeDescription(String modeDescription) {
		this.modeDescription = modeDescription;
	}


	/**
	 * Gets the mode description.
	 *
	 * @return the mode description
	 */
	public String getModeDescription() {
		return modeDescription;
	}

	/**
	 * Put pid.
	 *
	 * @param hex the hex
	 * @param pidObj the pid obj
	 */
	public void putPID(String hex, OBDPID pidObj) {
		this.pidList.put(hex, pidObj);
	}

	/**
	 * Gets the pID.
	 *
	 * @param pidHex the pid hex
	 * @return the pID
	 */
	public OBDPID getPID(String pidHex) {
		return this.pidList.get(pidHex);
	}

	/**
	 * Contains pid.
	 *
	 * @param hex the hex
	 * @return true, if successful
	 */
	public boolean containsPID(String hex) {
		if (this.pidList.containsKey(hex)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Sets the mode value.
	 *
	 * @param modeValue the new mode value
	 */
	public void setModeValue(int modeValue) {
		this.modeValue = modeValue;
	}


	/**
	 * Gets the mode value.
	 *
	 * @return the mode value
	 */
	public int getModeValue() {
		return modeValue;
	}

	/**
	 * Pid key set.
	 *
	 * @return the sets the
	 */
	public Set<String> pidKeySet() {
		return pidList.keySet();
	}

	/**
	 * Removes the pid.
	 *
	 * @param pidHex the pid hex
	 */
	public void removePID(String pidHex) {
		this.pidList.remove(pidHex);
	}

}
