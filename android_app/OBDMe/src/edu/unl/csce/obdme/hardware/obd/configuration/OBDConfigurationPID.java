package edu.unl.csce.obdme.hardware.obd.configuration;


/**
 * The Class OBDConfigurationPID.
 */
public class OBDConfigurationPID {


	/** The pid hex. */
	private String pidHex;

	/** The enabled. */
	private boolean enabled;

	/**
	 * Instantiates a new oBD configuration pid.
	 *
	 * @param pidHex the pid hex
	 */
	public OBDConfigurationPID(String pidHex) {
		this.pidHex = pidHex;
		this.enabled = false;
	}

	/**
	 * Instantiates a new oBD configuration pid.
	 *
	 * @param pidHex the pid hex
	 * @param enabled the enabled
	 */
	public OBDConfigurationPID(String pidHex, String enabled) {
		this.pidHex = pidHex;

		if (enabled.toString().equals("true")) {
			this.enabled = true;
		}

		else {
			this.enabled = false;
		}

	}

	/**
	 * Instantiates a new oBD configuration pid.
	 *
	 * @param pidHex the pid hex
	 * @param enabled the enabled
	 */
	public OBDConfigurationPID(String pidHex, boolean enabled) {
		this.pidHex = pidHex;
		this.enabled = enabled;
	}


	/**
	 * Gets the pid hex.
	 *
	 * @return the pid hex
	 */
	public String getPidHex() {
		return pidHex;
	}

	/**
	 * Sets the pid hex.
	 *
	 * @param pidHex the new pid hex
	 */
	public void setPidHex(String pidHex) {
		this.pidHex = pidHex;
	}

	/**
	 * Sets the enabled.
	 *
	 * @param enabled the new enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

}
