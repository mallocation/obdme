package edu.unl.csce.obdme.hardware.obd;

/**
 * The Class OBDRequest.
 */
public class OBDRequest {
	
	/** The mode hex. */
	private String modeHex;
	
	/** The pid hex. */
	private String pidHex;
	
	/** The return length. */
	private int returnLength;
	
	/** The request pid. */
	private OBDPID requestPID;
	
	/**
	 * Instantiates a new oBD request.
	 *
	 * @param pid the pid
	 */
	public OBDRequest (OBDPID pid) {
		this.modeHex = pid.getParentMode();
		this.pidHex = pid.getPidHex();
		this.returnLength = pid.getPidReturn();	
		this.requestPID = pid;
	}
	
	/**
	 * Instantiates a new oBD request.
	 *
	 * @param mode the mode
	 * @param pid the pid
	 * @param returnSize the return size
	 */
	public OBDRequest (String mode, String pid, int returnSize) {
		this.modeHex = mode;
		this.pidHex = pid;
		this.returnLength = returnSize;	
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
	 * @param mode the new mode
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
	 * @param pid the new pid
	 */
	public void setPid(String pid) {
		this.pidHex = pid;
	}

	/**
	 * Sets the return length.
	 *
	 * @param returnLength the new return length
	 */
	public void setReturnLength(int returnLength) {
		this.returnLength = returnLength;
	}

	/**
	 * Gets the return length.
	 *
	 * @return the return length
	 */
	public int getReturnLength() {
		return returnLength;
	}

	/**
	 * Sets the request pid.
	 *
	 * @param requestPID the new request pid
	 */
	public void setRequestPID(OBDPID requestPID) {
		this.requestPID = requestPID;
	}

	/**
	 * Gets the request pid.
	 *
	 * @return the request pid
	 */
	public OBDPID getRequestPID() {
		return requestPID;
	}

}
