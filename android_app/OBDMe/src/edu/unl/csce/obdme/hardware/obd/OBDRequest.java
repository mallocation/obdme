package edu.unl.csce.obdme.hardware.obd;

/**
 * The Class OBDRequest.
 */
public class OBDRequest {
	
	/** The mode. */
	private String modeHex;
	
	/** The pid. */
	private String pidHex;
	
	/** The return length. */
	private int returnLength;
	
	/**
	 * Instantiates a new OBD request.
	 *
	 * @param pid the pid
	 */
	public OBDRequest (OBDPID pid) {
		this.setMode(pid.getParentMode().getModeHex());
		this.setPid(pid.getPidHex());
		this.setReturnLength(pid.getPidReturn());	
	}
	
	public OBDRequest (String mode, String pid, int returnSize) {
		this.setMode(mode);
		this.setPid(pid);
		this.setReturnLength(returnSize);	
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return modeHex + pidHex;
	}

	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public String getMode() {
		return modeHex;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.modeHex = mode;
	}

	/**
	 * Gets the pid.
	 *
	 * @return the pid
	 */
	public String getPid() {
		return pidHex;
	}

	/**
	 * Sets the pid.
	 *
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pidHex = pid;
	}

	/**
	 * Sets the return length.
	 *
	 * @param returnLength the returnLength to set
	 */
	public void setReturnLength(int returnLength) {
		this.returnLength = returnLength;
	}

	/**
	 * Gets the return length.
	 *
	 * @return the returnLength
	 */
	public int getReturnLength() {
		return returnLength;
	}

}
