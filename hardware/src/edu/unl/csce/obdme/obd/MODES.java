package edu.unl.csce.obdme.obd;

public enum MODES {
	CURRENT_DATA("01", 1, "Show current data"),
	FREEZE_DATA("02", 2, "Show freeze frame data"),
	STORED_DTC("03", 3, "Show stored Diagnostic Trouble Codes"),
	CLEAR_DTC("04", 4, "Clear Diagnostic Trouble Codes and stored values"),
	OXY_TEST_RESULTS("05", 5, "Test results, oxygen sensor monitoring"),
	OTHER_TEST_RESULTS("06", 6, "Test results, other component/system monitoring"),
	SHOW_PEND_DTC("07", 7, "Show pending Diagnostic Trouble Codes"),
	CTRL_OBD("08", 8, "Control operation of on-board component/system"),
	REQ_VECH_INFO("09", 9, "Request vehicle information"),
	PERM_DTC("0A", 10, " Permanent DTC's");
	
	private final String mode;
	private final int value;
	private final String description;
	
	private MODES(String mode, int value, String description) {
		this.mode = mode;
		this.value = value;
		this.description = description;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
