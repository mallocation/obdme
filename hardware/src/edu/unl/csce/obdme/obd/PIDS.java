package edu.unl.csce.obdme.obd;

public enum PIDS {
	SUPPORTED_PIDS("00", 0, 4, true, "PIDs supported [01 - 20]"),
	MONITOR_STATUS("01", 1, 4, true, "Monitor status since DTCs cleared"),
	FREEZE_DTC("02", 2, 2, true, "Freeze DTC"),
	FUEL_STATUS("03", 3, 2, true, "Fuel system status"),
	ENGINE_LOAD("04", 4, 1, true, "Calculated engine load value"),
	ENGINE_COOLENT_TEMP("05", 5, 1, true, "Engine coolant temperature"),
	SHORT_FUEL_BANK1("06", 6, 1, true, "Short term fuel % trim—Bank 1"),
	LONG_FUEL_BANK1("07", 7, 1, true, "Long term fuel % trim—Bank 1"),
	SHORT_FUEL_BANK2("08", 8, 1, true, "Short term fuel % trim—Bank 2"),
	LONG_FUEL_BANK2("09", 9, 1, true, "Long term fuel % trim—Bank 2"),
	FUEL_PREASSURE("0A", 10, 1, true, "Fuel pressure"),
	INTK_MAN_PREASSURE("0B", 11, 1, true, "Intake manifold absolute pressure"),
	ENGINE_RPM("0C", 12, 2, true, "Engine RPM"),
	VECH_SPEED("0D", 13, 1, true, "Vehicle speed"),
	TIMING_ADV("0E", 14, 1, true, "Timing advance"),
	INTK_AIR_TEMP("0F", 15, 1, true, "Intake air temperature"),
	MAF_AIR_FLOW("10", 16, 2, true, "MAF air flow rate"),
	THROTTLE_POS("11", 17, 1, true, "Throttle position"),
	CMD_SEC_AIR_STATUS("12", 18, 1, true, "Commanded secondary air status"),
	OXY_SENS_PRES("13", 19, 1, true, "Oxygen sensors present");
	
	private final String pid;
	private final int value;
	private final int returnSize;
	private boolean supported;
	private final String description;

	
	private PIDS(String pid, int value, int returnSize, boolean supported, String description) {
		this.pid = pid;
		this.value = value;
		this.returnSize = returnSize;
		this.setSupported(supported);
		this.description = description;
	}


	/**
	 * @param supported the supported to set
	 */
	public void setSupported(boolean supported) {
		this.supported = supported;
	}


	/**
	 * @return the supported
	 */
	public boolean isSupported() {
		return supported;
	}


	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}


	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}


	/**
	 * @return the returnSize
	 */
	public int getReturnSize() {
		return returnSize;
	}
}