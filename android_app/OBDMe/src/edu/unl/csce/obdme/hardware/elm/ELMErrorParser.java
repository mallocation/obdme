package edu.unl.csce.obdme.hardware.elm;

/**
 * The Class ELMErrors.
 */
public abstract class ELMErrorParser {
	
	/** The Constant ELM_DEVICE_BUFFER_FULL. */
	private static final String ELM_DEVICE_BUFFER_FULL = "BUFFER FULL";
	
	/** The Constant ELM_DEVICE_BUS_BUSY. */
	private static final String ELM_DEVICE_BUS_BUSY = "BUS BUSY";
	
	/** The Constant ELM_DEVICE_BUS_ERROR. */
	private static final String ELM_DEVICE_BUS_ERROR = "BUS ERROR";
	
	/** The Constant ELM_DEVICE_CAN_ERROR. */
	private static final String ELM_DEVICE_CAN_ERROR = "CAN ERROR";
	
	/** The Constant ELM_DEVICE_DATA_ERROR. */
	private static final String ELM_DEVICE_DATA_ERROR = "DATA ERROR";
	
	/** The Constant ELM_DEVICE_NO_DATA. */
	private static final String ELM_DEVICE_NO_DATA = "NO DATA";
	
	/** The Constant ELM_DEVICE_STOPPED. */
	private static final String ELM_DEVICE_STOPPED = "STOPPED";
	
	/** The Constant ELM_DEVICE_UNABLE_TO_CONNECT. */
	private static final String ELM_DEVICE_UNABLE_TO_CONNECT = "UNABLE TO CONNECT";
	
	/**
	 * The Enum ELM_ERROR.
	 */
	public enum ELM_ERROR{
		
		/** Buffer full. */
		BUFFER_FULL, 
		
		/** Bus busy. */
		BUS_BUSY, 
		
		/** Bus busy error. */
		BUS_ERROR, 
		
		/** CAN error. */
		CAN_ERROR, 
		
		/** Data error. */
		DATA_ERROR,
		
		/** No data. */
		NO_DATA,
		
		/** Stopped. */
		STOPPED,
		
		/** Unable to connect. */
		UNABLE_TO_CONNECT};

	/**
	 * Parses the string for error.
	 *
	 * @param stringToParse the string to parse
	 * @return the ELM error
	 * @throws ELMException 
	 */
	public static void parseStringForError(String stringToParse) throws ELMException {
		
		//If the buffer is full
		if(stringToParse.contains(ELM_DEVICE_BUFFER_FULL)) {
			throw new ELMDeviceBufferFullException("Buffer full exception received from device. " +
					" Response from device: " + stringToParse);
		}
		
		//If the bus is busy
		else if(stringToParse.contains(ELM_DEVICE_BUS_BUSY)) {
			throw new ELMDeviceBusBusyException("Device bus is busy and cannot process the request.  " +
					"Response from device: " + stringToParse);
		}
		
		//If there is a bus error
		else if(stringToParse.contains(ELM_DEVICE_BUS_ERROR)) {
			throw new ELMDeviceBusErrorException("There was a device bus error in the device response." +
					"  Response from device: " + stringToParse);
		}
		
		//If there is a CAN error
		else if(stringToParse.contains(ELM_DEVICE_CAN_ERROR)) {
			throw new ELMDeviceCANErrorException("The device responded with a CAN error.  " +
					"Response from device: " + stringToParse);
		}
		
		//If there is a data error
		else if(stringToParse.contains(ELM_DEVICE_DATA_ERROR)) {
			throw new ELMDeviceDataErrorException("The device responded with a data error.  " +
					"Response from device: " + stringToParse);
		}
		
		//If there is no data
		else if(stringToParse.contains(ELM_DEVICE_NO_DATA)) {
			throw new ELMDeviceNoDataException("There was no data returned from the OBD request." +
					"  Response from device: " + stringToParse);
		}
		
		//If the device is stopped
		else if(stringToParse.contains(ELM_DEVICE_STOPPED)) {
			throw new ELMDeviceStoppedException("The device is currently stopped and can not respond to the request." +
					"  Response from device: " + stringToParse);
		}
		
		//If unable to connect
		else if(stringToParse.contains(ELM_DEVICE_UNABLE_TO_CONNECT)) {
			throw new ELMUnableToConnectException("The device was unable to connect to the onbard ECU." +
					"  Response from device: " + stringToParse);
		}
		
	}
	
	/**
	 * Gets the string for ELM error.
	 *
	 * @param elmError the ELM error
	 * @return the string for ELM error
	 */
	public static String getStringForELMError(ELM_ERROR elmError) {
		
		//Switch on the error enumerator
		switch(elmError) {
		
		case BUFFER_FULL:
			return ELM_DEVICE_BUFFER_FULL;
			
		case BUS_BUSY:
			return ELM_DEVICE_BUS_BUSY;
			
		case BUS_ERROR:
			return ELM_DEVICE_BUS_ERROR;
			
		case CAN_ERROR:
			return ELM_DEVICE_CAN_ERROR;
			
		case DATA_ERROR:
			return ELM_DEVICE_DATA_ERROR;
			
		case NO_DATA:
			return ELM_DEVICE_NO_DATA;
			
		case STOPPED:
			return ELM_DEVICE_STOPPED;
			
		case UNABLE_TO_CONNECT:
			return ELM_DEVICE_UNABLE_TO_CONNECT;
			
		default:
			return null;
		}	
	}
	
}
