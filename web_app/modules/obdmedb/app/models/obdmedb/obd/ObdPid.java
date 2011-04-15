package models.obdmedb.obd;


public class ObdPid implements Comparable<ObdPid>{
	
	private String mode;
	
	private String pid;
	
	private String pidTitle;
	
	private ObdPid(String mode, String pid, String pidTitle) {
		this.mode = mode;
		this.pid = pid;
		this.pidTitle = pidTitle;
	}
	
	public String getMode() {
		return this.mode;
	}
	
	public String getPid() {
		return this.pid;
	}
	
	public String getPidTitle() {
		return this.pidTitle;
	}
	
	@Override
	public int compareTo(ObdPid that) {
		return this.pidTitle.compareTo(that.getPidTitle());
	}
	
	public static final ObdPid CALCULATED_ENGINE_LOAD_VALUE = new ObdPid("01", "04", "Calculated Engine Load Value");
	public static final ObdPid ENGINE_COOLANT_TEMPERATURE = new ObdPid("01", "05", "Engine Coolant Temperature");
	public static final ObdPid SHORT_TERM_FUEL_TRIM = new ObdPid("01", "06", "Short term fuel trim");
	public static final ObdPid LONG_TERM_FUEL_TRIM = new ObdPid("01", "07", "Long term fuel trim");
	public static final ObdPid INTAKE_MANIFOLD_ABSOLUTE_PRESSURE = new ObdPid("01", "0B", "Intake manifold absolute pressure");
	public static final ObdPid ENGINE_RPM = new ObdPid("01", "0C", "Engine RPM");
	public static final ObdPid VEHICLE_SPEED = new ObdPid("01", "0D", "Vehicle speed");
	public static final ObdPid TIMING_ADVANCE = new ObdPid("01", "0E", "Timing advance");
	public static final ObdPid INTAKE_AIR_TEMPERATURE = new ObdPid("01", "0F", "Intake air temperature");
	
	public static final ObdPid MAF_AIR_FLOW_RATE = new ObdPid("01", "10", "MAF air flow rate");
	public static final ObdPid THROTTLE_POSITION = new ObdPid("01", "11", "Throttle position");
	public static final ObdPid RUN_TIME_SINCE_ENGINE_START = new ObdPid("01", "1F", "Run time since engine start");
	
	public static final ObdPid DISTANCE_TRAVELED_WITH_MIL_ON = new ObdPid("01", "21", "Distance traveled with MIL on");
	public static final ObdPid COMMANDED_EGR = new ObdPid("01", "2C", "Commanded EGR");
	public static final ObdPid EGR_ERROR = new ObdPid("01", "2D", "EGR Error");
	
	public static final ObdPid BAROMETRIC_PRESSURE = new ObdPid("01", "33", "Barometric pressure");
	public static final ObdPid CATALYST_TEMPERATURE_BANK_1_SENSOR_1 = new ObdPid("01", "3C", "Catalyst Temperature Bank 1, Sensor 1");
		
	public static final ObdPid CONTROL_MODULE_VOLTAGE = new ObdPid("01", "42", "Control module voltage");
	public static final ObdPid ABSOLUTE_LOAD_VALUE = new ObdPid("01", "43", "Absolute Load Value");
	public static final ObdPid COMMAND_EQUIVALENCE_RATIO = new ObdPid("01", "44", "Command equivalence ratio");
	public static final ObdPid RELATIVE_THROTTLE_POSITION = new ObdPid("01", "45", "Relative throttle position");
	public static final ObdPid ABSOLUTE_THROTTLE_POSITION_B = new ObdPid("01", "47", "Absolute throttle position B");
	public static final ObdPid ACCELERATOR_PEDAL_POSITION_D = new ObdPid("01", "49", "Accelerator pedal position D");
	public static final ObdPid ACCELERATOR_PEDAL_POSITION_E = new ObdPid("01", "4A", "Accelerator pedal position E");
	public static final ObdPid COMMANDED_THROTTLE_ACTUATOR = new ObdPid("01", "4C", "Commanded throttle actuator");
	
	public static final ObdPid[] DEFINED_OBD_PIDS = new ObdPid[] {CALCULATED_ENGINE_LOAD_VALUE, 
		ENGINE_COOLANT_TEMPERATURE, SHORT_TERM_FUEL_TRIM, LONG_TERM_FUEL_TRIM, INTAKE_MANIFOLD_ABSOLUTE_PRESSURE, 
		ENGINE_RPM, VEHICLE_SPEED, TIMING_ADVANCE, INTAKE_AIR_TEMPERATURE, MAF_AIR_FLOW_RATE, THROTTLE_POSITION, RUN_TIME_SINCE_ENGINE_START, 
		DISTANCE_TRAVELED_WITH_MIL_ON, COMMANDED_EGR, EGR_ERROR, BAROMETRIC_PRESSURE, CATALYST_TEMPERATURE_BANK_1_SENSOR_1, CONTROL_MODULE_VOLTAGE, 
		ABSOLUTE_LOAD_VALUE, COMMAND_EQUIVALENCE_RATIO, RELATIVE_THROTTLE_POSITION, ABSOLUTE_THROTTLE_POSITION_B, ACCELERATOR_PEDAL_POSITION_D, 
		ACCELERATOR_PEDAL_POSITION_E, COMMANDED_THROTTLE_ACTUATOR};
	
	
	
	public static ObdPid getObdPid(String mode, String pid) {
		// TODO - This is only for the presention. this is friekin awful...
		if (mode.equals("01")) {
			
			if (pid.equals("04")) {
			return CALCULATED_ENGINE_LOAD_VALUE;
			} else if (pid.equals( "05")) {
			return ENGINE_COOLANT_TEMPERATURE;
			} else if (pid.equals( "06")) {
			return SHORT_TERM_FUEL_TRIM;
			} else if (pid.equals( "07")) {
			return LONG_TERM_FUEL_TRIM;
			} else if (pid.equals( "0B")) {
			return INTAKE_MANIFOLD_ABSOLUTE_PRESSURE;
			} else if (pid.equals( "0C")) {
			return ENGINE_RPM;
			} else if (pid.equals( "0D")) {
			return VEHICLE_SPEED;
			} else if (pid.equals( "0E")) {
			return TIMING_ADVANCE;
			} else if (pid.equals( "0F")) {
			return INTAKE_AIR_TEMPERATURE;
			} else if (pid.equals( "10")) {
			return MAF_AIR_FLOW_RATE;
			} else if (pid.equals( "11")) {
			return THROTTLE_POSITION;
			} else if (pid.equals( "1F")) {
			return RUN_TIME_SINCE_ENGINE_START;
			} else if (pid.equals( "21")) {
			return DISTANCE_TRAVELED_WITH_MIL_ON;
			} else if (pid.equals( "2C")) {
			return COMMANDED_EGR;
			} else if (pid.equals("2D")) {
			return EGR_ERROR;
			} else if (pid.equals("33")) {
			return BAROMETRIC_PRESSURE;
			} else if (pid.equals("3C")) {
			return CATALYST_TEMPERATURE_BANK_1_SENSOR_1;
			} else if (pid.equals("42")) {
			return CONTROL_MODULE_VOLTAGE;
			} else if (pid.equals("43")) {
			return ABSOLUTE_LOAD_VALUE;
			} else if (pid.equals("44")) {
			return COMMAND_EQUIVALENCE_RATIO;
			} else if (pid.equals("45")) {
			return RELATIVE_THROTTLE_POSITION;
			} else if (pid.equals("47")) {
			return ABSOLUTE_THROTTLE_POSITION_B;
			} else if (pid.equals("49")) {
			return ACCELERATOR_PEDAL_POSITION_D;
			} else if (pid.equals("4A")) {
			return ACCELERATOR_PEDAL_POSITION_E;
			} else if (pid.equals("4C")) {
			return COMMANDED_THROTTLE_ACTUATOR;		
			
			}			
		}
		return null;
	}
	
}
