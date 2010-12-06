package edu.unl.csce.obdme.obd;

import java.util.List;

/**
 * The Enumerator PIDS.
 */
public enum PIDS {
	
	/** The supported PID's. */
	SUPPORTED_PIDS(			"00",	0,	4,	"%",		"PIDs supported [01 - 20]"),
	
	/** The monitor status. */
	MONITOR_STATUS(			"01",	1,	4,	"",			"Monitor status since DTCs cleared"),
	
	/** The freeze DTC command. */
	FREEZE_DTC(				"02",	2,	2,	"",			"Freeze DTC"),
	
	/** The fuel status. */
	FUEL_STATUS(			"03",	3,	2,	"",			"Fuel system status"),
	
	/** The engine load. */
	ENGINE_LOAD(			"04",	4,	1,	"%",		"Calculated engine load value"),
	
	/** The engine coolant temperature. */
	ENGINE_COOLANT_TEMP(	"05",	5,	1,	"C",		"Engine coolant temperature"),
	
	/** The short term fuel % (bank 1). */
	SHORT_FUEL_BANK1(		"06",	6,	1,	"%",		"Short term fuel % trim—Bank 1"),
	
	/** The long term fuel % (bank 1). */
	LONG_FUEL_BANK1(		"07",	7,	1,	"%",		"Long term fuel % trim—Bank 1"),
	
	/** The short term fuel % (bank 2). */
	SHORT_FUEL_BANK2(		"08",	8,	1,	"%",		"Short term fuel % trim—Bank 2"),
	
	/** The long term fuel % (bank 2). */
	LONG_FUEL_BANK2(		"09",	9,	1,	"%",		"Long term fuel % trim—Bank 2"),
	
	/** The fuel pressure. */
	FUEL_PREASSURE(			"0A",	10,	1,	"kPaG",		"Fuel pressure"),
	
	/** The intake manifold pressure. */
	INTK_MAN_PRESSURE(		"0B",	11,	1,	"kPa",		"Intake manifold absolute pressure"),
	
	/** The engine RPM's */
	ENGINE_RPM(				"0C",	12,	2,	"RPM",		"Engine RPM"),
	
	/** The vehicle speed. */
	VECH_SPEED(				"0D",	13,	1,	"Km/Hr",	"Vehicle speed"),
	
	/** The timing advance. */
	TIMING_ADV(				"0E",	14,	1,	"Degrees",	"Timing advance"),
	
	/** The intake air temperature. */
	INTK_AIR_TEMP(			"0F",	15,	1,	"C",		"Intake air temperature"),
	
	/** The MAF air flwo rate. */
	MAF_AIR_FLOW(			"10",	16,	2,	"gm/sec",	"MAF air flow rate"),
	
	/** The throttle posistion. */
	THROTTLE_POS(			"11",	17,	1,	"%",		"Throttle position"),
	
	/** The commanded secondary air status. */
	CMD_SEC_AIR_STATUS(		"12",	18,	1,	"",			"Commanded secondary air status"),
	
	/** The oxygen sensors present. */
	OXY_SENS_PRES(			"13",	19,	1,	"",			"Oxygen sensors present"),
	
	/** Bank 1, Sensor 1:Oxygen sensor voltage,Short term fuel trim. */
	OXY_SENS_BNK1_SNS1(		"14",	20,	2,	"",			"Bank 1, Sensor 1:Oxygen sensor voltage,Short term fuel trim"),
	
	/** Bank 1, Sensor 2:Oxygen sensor voltage,Short term fuel trim. */
	OXY_SENS_BNK1_SNS2(		"15",	21,	2,	"",			"Bank 1, Sensor 2:Oxygen sensor voltage,Short term fuel trim"),
	
	/** Bank 1, Sensor 3:Oxygen sensor voltage,Short term fuel trim. */
	OXY_SENS_BNK1_SNS3(		"16",	22,	2,	"",			"Bank 1, Sensor 3:Oxygen sensor voltage,Short term fuel trim"),
	
	/** Bank 1, Sensor 4:Oxygen sensor voltage,Short term fuel trim. */
	OXY_SENS_BNK1_SNS4(		"17",	23,	2,	"",			"Bank 1, Sensor 4:Oxygen sensor voltage,Short term fuel trim"),
	
	/** Bank 2, Sensor 1:Oxygen sensor voltage,Short term fuel trim. */
	OXY_SENS_BNK2_SNS1(		"18",	24,	2,	"",			"Bank 2, Sensor 1:Oxygen sensor voltage,Short term fuel trim"),
	
	/** Bank 2, Sensor 2:Oxygen sensor voltage,Short term fuel trim. */
	OXY_SENS_BNK2_SNS2(		"19",	25,	2,	"",			"Bank 2, Sensor 2:Oxygen sensor voltage,Short term fuel trim"),
	
	/** Bank 2, Sensor 3:Oxygen sensor voltage,Short term fuel trim. */
	OXY_SENS_BNK2_SNS3(		"1A",	26,	2,	"",			"Bank 2, Sensor 3:Oxygen sensor voltage,Short term fuel trim"),
	
	/** Bank 2, Sensor 4:Oxygen sensor voltage,Short term fuel trim. */
	OXY_SENS_BNK2_SNS4(		"1B",	27,	2,	"",			"Bank 2, Sensor 4:Oxygen sensor voltage,Short term fuel trim"),
	
	/** The OBD standards this vehicle conforms to. */
	OBD_STANDARDS(			"1C",	28,	1,	"",			"OBD standards this vehicle conforms to"),
	
	/** The Oxygen sensors present. */
	OXY_SENS_PRES2(			"1D",	29,	1,	"",			"Oxygen sensors present"),
	
	/** The Auxiliary input status. */
	AUX_STATUS(				"1E",	30,	1,	"",			"Auxiliary input status"),
	
	/** The Run time since engine start. */
	RUN_TIME(				"1F",	31,	2,	"Seconds",	"Run time since engine start");
	
	/** The hex string of the PID. */
	private final String pid;
	
	/** The PID integer value. */
	private final int value;
	
	/** The number of bytes returned from the command. */
	private final int returnSize;
	
	/** The units of the data returned. */
	private final String unit;
	
	/** The plain english description of the PID. */
	private final String description;

	/**
	 * Instantiates a new PID enumerator.
	 *
	 * @param pid the pid
	 * @param value the value
	 * @param returnSize the return size
	 * @param unit the unit
	 * @param description the description
	 */
	private PIDS(String pid, int value, int returnSize, String unit, String description) {
		this.pid = pid;
		this.value = value;
		this.returnSize = returnSize;
		this.unit = unit;
		this.description = description;
	}
	
	/**
	 * Evaluates the command result based on the PID.
	 *
	 * @param bytes the bytes
	 * @return the double
	 * @throws Exception the exception
	 */
	double evalResult(List<Integer> bytes) throws Exception{
		double A = bytes.get(0);
		
        switch(this) {
        	case ENGINE_LOAD:
        		return A*100/255;
        		
        	case ENGINE_COOLANT_TEMP:
        		return A-40;
        		
        	case SHORT_FUEL_BANK1:
        		return .7812*(A-128);
        		
        	case LONG_FUEL_BANK1:
        		return .7812*(A-128);
        		
        	case SHORT_FUEL_BANK2:
        		return .7812*(A-128);
        		
        	case LONG_FUEL_BANK2:
        		return .7812*(A-128);
        		
        	case FUEL_PREASSURE:
        		return 3*A;
        		
        	case INTK_MAN_PRESSURE:
        		return A;
        		
            case ENGINE_RPM:   
            	if (bytes.size() > 1) {
            		double B = bytes.get(1);
            		return .25*(A*256+B);
            	}
            	else {
            		throw new Exception("Not enough bytes given to perform calculation");
            	}
            	
            case VECH_SPEED:
            	return A;
            	
            case TIMING_ADV:
        		return (.5*A)-64;
        		
            case INTK_AIR_TEMP:
            	return A-40;
            	
            case MAF_AIR_FLOW:
            	if (bytes.size() > 1) {
            		double B = bytes.get(1);
            		return .25*(A*256+B);
            	}
            	else {
            		throw new Exception("Not enough bytes given to perform calculation");
            	}
            	
            case THROTTLE_POS:
            	return .3922*A;
        }
        throw new AssertionError("Unknown enumeration: " + this);
    }
	
	/**
	 * Gets the status of a PID.  This is for none numeric PID results.
	 *
	 * @param bytes the bytes
	 * @return the status
	 * @throws Exception the exception
	 */
	String getStatus(List<Integer> bytes) throws Exception{
		String A = Integer.toBinaryString(bytes.get(0));
		
        switch(this) {
        
        	case FUEL_STATUS:
        		if (A.charAt(0) == '1') {
        			return "Open loop due to insufficient engine temperature";
        		}
        		if (A.charAt(1) == '1') {
        			return "Closed loop, using oxygen sensor feedback to determine fuel mix";
        		}
        		if (A.charAt(2) == '1') {
        			return "Open loop due to engine load OR fuel cut due to deacceleration ";
        		}
        		if (A.charAt(3) == '1') {
        			return "Open loop due to system failure";
        		}
        		if (A.charAt(4) == '1') {
        			return "Closed loop, using at least one oxygen sensor but there is a fault in the feedback system";
        		}
        		
        	case CMD_SEC_AIR_STATUS:
        		if (A.charAt(0) == '1') {
        			return "Upstream of catalytic converter";
        		}
        		if (A.charAt(1) == '1') {
        			return "Downstream of catalytic converter";
        		}
        		if (A.charAt(2) == '1') {
        			return "From the outside atmosphere or off";
        		}
        }
        throw new AssertionError("Unknown enumeration: " + this);
    }

	/**
	 * Gets the PID hex string.
	 *
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}


	/**
	 * Gets the PID integer value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return value;
	}


	/**
	 * Gets the number of bytes returned by the PID request.
	 *
	 * @return the returnSize
	 */
	public int getReturnSize() {
		return returnSize;
	}

	/**
	 * Gets the plain English description of the PID.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the units of the returned result.
	 *
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
}