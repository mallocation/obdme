package edu.unl.csce.obdme.hardware;

/**
 * The Class OBDRequest.
 */
public class OBDRequest {
	
	/** The mode. */
	private String mode;
	
	/** The pid. */
	private String pid;
	
	/**
	 * Instantiates a new OBD request.
	 *
	 * @param modeHex the mode hex
	 * @param pidHex the pid hex
	 */
	public OBDRequest (String modeHex, String pidHex) {
		this.setMode(modeHex);
		this.setPid(pidHex);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return mode + pid;
	}

	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Gets the pid.
	 *
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * Sets the pid.
	 *
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

}
