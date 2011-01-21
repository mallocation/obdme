package edu.unl.csce.obdme.hardware;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
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
	private HashMap<String, OBDPID> pidList;
	
	
	/**
	 * Instantiates a new OBD mode.
	 *
	 * @param modeHex the mode hex
	 * @param modeDescription the mode description
	 */
	public OBDMode(String modeHex, String modeDescription) {
		this.setModeHex(modeHex);
		this.setModeValue(Integer.parseInt(modeHex,16));
		this.setModeDescription(modeDescription);
		
		//Create a new hash map that contains the PIDS
		pidList = new HashMap<String, OBDPID>();
	}


	/**
	 * Sets the mode hex.
	 *
	 * @param modeName the modeName to set
	 */
	public void setModeHex(String modeName) {
		this.modeHex = modeName;
	}


	/**
	 * Gets the mode hex.
	 *
	 * @return the modeName
	 */
	public String getModeHex() {
		return modeHex;
	}

	/**
	 * Sets the mode description.
	 *
	 * @param modeDescription the modeDescription to set
	 */
	public void setModeDescription(String modeDescription) {
		this.modeDescription = modeDescription;
	}


	/**
	 * Gets the mode description.
	 *
	 * @return the modeDescription
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
	 * @param modeValue the modeValue to set
	 */
	public void setModeValue(int modeValue) {
		this.modeValue = modeValue;
	}


	/**
	 * Gets the mode value.
	 *
	 * @return the modeValue
	 */
	public int getModeValue() {
		return modeValue;
	}

}
