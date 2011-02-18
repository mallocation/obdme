package edu.unl.csce.obdme.hardware.obd.configuration;


/**
 * The Class OBDConfigurationPID.
 */
public class OBDConfigurationPID {


	/** The pid hex. */
	private String pidHex;

	/** The supported. */
	private boolean supported;

	/** The displayed. */
	private boolean displayed;

	/** The collected. */
	private boolean collected;

	/**
	 * Instantiates a new oBD configuration pid.
	 *
	 * @param pidHex the pid hex
	 */
	public OBDConfigurationPID(String pidHex) {
		this.pidHex = pidHex;
		this.supported = false;
	}

	/**
	 * Instantiates a new oBD configuration pid.
	 *
	 * @param pidHex the pid hex
	 * @param supported the supported
	 */
	public OBDConfigurationPID(String pidHex, String supported) {
		this.pidHex = pidHex;

		if (supported.toString().equals("true")) {
			this.supported = true;
		}

		else {
			this.supported = false;
		}

	}

	/**
	 * Instantiates a new oBD configuration pid.
	 *
	 * @param pidHex the pid hex
	 * @param supported the supported
	 * @param collected the collected
	 * @param displayed the displayed
	 */
	public OBDConfigurationPID(String pidHex, String supported, String collected, String displayed) {
		this.pidHex = pidHex;

		//Check if the PID is supported
		if (supported.toString().equals("true")) {
			this.supported = true;
			
			//If the PID is collected
			if(collected.toString().equals("true")) {
				this.collected = true;
			}
			else {
				this.collected = false;
			}

			//If the PID is displayed
			if(displayed.toString().equals("true")) {
				this.displayed = true;
			}
			else {
				this.displayed = false;
			}
		}
		
		//Otherwise, the PID is not supported so it cannot be collected or displayed
		else {
			this.supported = false;
			this.collected = false;
			this.displayed = false;
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
		this.supported = enabled;
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
	 * Sets the supported.
	 *
	 * @param supported the new supported
	 */
	public void setSupported(boolean supported) {
		this.supported = supported;
	}

	/**
	 * Checks if is supported.
	 *
	 * @return true, if is supported
	 */
	public boolean isSupported() {
		return supported;
	}

	/**
	 * Sets the displayed.
	 *
	 * @param displayed the new displayed
	 */
	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	/**
	 * Checks if is displayed.
	 *
	 * @return true, if is displayed
	 */
	public boolean isDisplayed() {
		return displayed;
	}

	/**
	 * Sets the collected.
	 *
	 * @param collected the new collected
	 */
	public void setCollected(boolean collected) {
		this.collected = collected;
	}

	/**
	 * Checks if is collected.
	 *
	 * @return true, if is collected
	 */
	public boolean isCollected() {
		return collected;
	}


}
