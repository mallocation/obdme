package edu.unl.csce.obdme.obd;

/**
 * The Enumerator MODES.
 */
public enum MODES {
	
	/** The current data mode. */
	CURRENT_DATA("01", 1, "Show current data"),
	
	/** The freeze frame data mode. */
	FREEZE_DATA("02", 2, "Show freeze frame data"),
	
	/** The stored DTC code mode. */
	STORED_DTC("03", 3, "Show stored Diagnostic Trouble Codes"),
	
	/** The clear DTC mode. */
	CLEAR_DTC("04", 4, "Clear Diagnostic Trouble Codes and stored values"),
	
	/** The oxygen test results mode. */
	OXY_TEST_RESULTS("05", 5, "Test results, oxygen sensor monitoring"),
	
	/** The the other test results mode. */
	OTHER_TEST_RESULTS("06", 6, "Test results, other component/system monitoring"),
	
	/** The show pending DTC codes mode. */
	SHOW_PEND_DTC("07", 7, "Show pending Diagnostic Trouble Codes"),
	
	/** The control OBD component/system mode. */
	CTRL_OBD("08", 8, "Control operation of on-board component/system"),
	
	/** The vehicle information request mode. */
	REQ_VECH_INFO("09", 9, "Request vehicle information"),
	
	/** The permanent DTC code mode */
	PERM_DTC("0A", 10, " Permanent DTC's");
	
	/** The hex string of the mode. */
	private final String mode;
	
	/** The integer value of the mode. */
	private final int value;
	
	/** The plain English description of the mode. */
	private final String description;
	
	/**
	 * Instantiates a new OBD modes enumeration.
	 *
	 * @param mode hex string of the mode
	 * @param value the integer value of the mode
	 * @param description the plain English description of the mode
	 */
	private MODES(String mode, int value, String description) {
		this.mode = mode;
		this.value = value;
		this.description = description;
	}

	/**
	 * Gets the mode hex string
	 *
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Gets the mode integer value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the plain English description of the mode
	 */
	public String getDescription() {
		return description;
	}
}
