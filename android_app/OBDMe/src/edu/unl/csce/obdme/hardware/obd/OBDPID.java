package edu.unl.csce.obdme.hardware.obd;

/**
 * The Class OBDPID.
 */
public class OBDPID {
	
	/** The pid hex. */
	private String pidHex;
	
	/** The pid value. */
	private int pidValue;
	
	/** The pid return length. */
	private int pidReturn;
	
	/** The pid unit. */
	private String pidUnit;
	
	/** The pid eval. */
	private String pidEval;
	
	/** The pid name. */
	private String pidName;
	
	/** The supported. */
	private boolean supported;
	
	/** The parent mode. */
	private OBDMode parentMode;

	/**
	 * Instantiates a new OBDPID.
	 *
	 * @param pidHex the pid hex
	 * @param pidReturn the pid return
	 * @param pidUnit the pid unit
	 * @param pidEval the pid eval
	 * @param pidName the pid name
	 * @param parent the parent
	 */
	public OBDPID(String pidHex, int pidReturn, String pidUnit, String pidEval, String pidName, 
			OBDMode parent) {
		this.setPidHex(pidHex);
		this.setPidValue(Integer.parseInt(pidHex,16));
		this.setPidReturn(pidReturn);
		this.setPidUnit(pidUnit);
		this.setPidEval(pidEval);
		this.setPidName(pidName);
		this.setParentMode(parent);
		this.setSupported(false);
	}
	
	/**
	 * Instantiates a new OBDPID.
	 *
	 * @param pidHex the pid hex
	 * @param pidReturn the pid return
	 * @param pidUnit the pid unit
	 * @param pidEval the pid eval
	 * @param pidName the pid name
	 * @param parent the parent
	 * @param supported the supported
	 */
	public OBDPID(String pidHex, int pidReturn, String pidUnit, String pidEval, String pidName, 
			OBDMode parent, boolean supported) {
		this.setPidHex(pidHex);
		this.setPidValue(Integer.parseInt(pidHex,16));	
		this.setPidReturn(pidReturn);
		this.setPidUnit(pidUnit);
		this.setPidEval(pidEval);
		this.setPidName(pidName);
		this.setParentMode(parent);
		this.setSupported(supported);
	}

	/**
	 * Gets the pid hex.
	 *
	 * @return the pidHex
	 */
	public String getPidHex() {
		return pidHex;
	}

	/**
	 * Sets the pid hex.
	 *
	 * @param pidHex the pidHex to set
	 */
	public void setPidHex(String pidHex) {
		this.pidHex = pidHex;
	}

	/**
	 * Gets the pid unit.
	 *
	 * @return the pidUnit
	 */
	public String getPidUnit() {
		return pidUnit;
	}

	/**
	 * Sets the pid unit.
	 *
	 * @param pidUnit the pidUnit to set
	 */
	public void setPidUnit(String pidUnit) {
		this.pidUnit = pidUnit;
	}

	/**
	 * Gets the pid eval.
	 *
	 * @return the pidEval
	 */
	public String getPidEval() {
		return pidEval;
	}

	/**
	 * Sets the pid eval.
	 *
	 * @param pidEval the pidEval to set
	 */
	public void setPidEval(String pidEval) {
		this.pidEval = pidEval;
	}

	/**
	 * Gets the pid name.
	 *
	 * @return the pidName
	 */
	public String getPidName() {
		return pidName;
	}

	/**
	 * Sets the pid name.
	 *
	 * @param pidName the pidName to set
	 */
	public void setPidName(String pidName) {
		this.pidName = pidName;
	}

	/**
	 * Sets the pid return.
	 *
	 * @param pidReturn the pidReturn to set
	 */
	public void setPidReturn(int pidReturn) {
		this.pidReturn = pidReturn;
	}

	/**
	 * Gets the pid return.
	 *
	 * @return the pidReturn
	 */
	public int getPidReturn() {
		return pidReturn;
	}

	/**
	 * Sets the supported.
	 *
	 * @param supported the supported to set
	 */
	public void setSupported(boolean supported) {
		this.supported = supported;
	}

	/**
	 * Checks if is supported.
	 *
	 * @return the supported
	 */
	public boolean isSupported() {
		return supported;
	}

	/**
	 * Sets the pid value.
	 *
	 * @param pidValue the pidValue to set
	 */
	public void setPidValue(int pidValue) {
		this.pidValue = pidValue;
	}

	/**
	 * Gets the pid value.
	 *
	 * @return the pidValue
	 */
	public int getPidValue() {
		return pidValue;
	}

	/**
	 * Sets the parent mode.
	 *
	 * @param parentMode the parentMode to set
	 */
	public void setParentMode(OBDMode parentMode) {
		this.parentMode = parentMode;
	}

	/**
	 * Gets the parent mode.
	 *
	 * @return the parentMode
	 */
	public OBDMode getParentMode() {
		return parentMode;
	}

}
