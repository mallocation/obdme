package edu.unl.csce.obdme.obd;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * The Class OBDDataHelpers.
 */
public class OBDDataHelpers {
	
	/** The log. */
	private Logger log;
	
	/**
	 * Instantiates a new OBD data helper.
	 */
	public OBDDataHelpers() {
		log = Logger.getLogger(OBDDataHelpers.class);
	}

	/**
	 * Converts a hexidecimal string to integer array.
	 *
	 * @param byteString the byte string
	 * @return the list
	 */
	public List<Integer> stringToIntArray(String byteString) {
		
		StringTokenizer tokenizedString = new StringTokenizer(byteString);
		List<Integer> intList = new ArrayList<Integer>(tokenizedString.countTokens());
		
	    while(tokenizedString.hasMoreTokens()){
	    	 intList.add(Integer.parseInt(tokenizedString.nextToken(), 16));
	    }
	    
	    return intList;
		
	}
	
	/**
	 * Process the OBD header information.  This method checks to make sure that this
	 * response correlates to the one that we requested.
	 *
	 * @param response the response passed in for processing
	 * @param mode the mode enumeration
	 * @param pid the PID enumeration
	 * @return the returned response with the verified header removed
	 * @throws Exception the exception thrown when the header is not what was expected
	 */
	public List<Integer> processHeader(List<Integer> response, MODES mode, PIDS pid) throws Exception {
		
		if (response.get(0) == Integer.parseInt(Integer.toString(mode.getValue() + 40),16)) {
			response.remove(0);
		}
		else {
			throw new Exception("The response did not have the expected mode.");
		}
		
		if (response.get(0).equals(pid.getValue())) {
			response.remove(0);
		}
		else {
			throw new Exception("The response did not have the expected PID.");
		}
		
		return response;
		
	}
	
	/**
	 * Make bit string from a list of integers.  This method returns a string of
	 * bits (each integer is represented by exactly 8-bits.
	 *
	 * @param responseValues the list of integers from byte values in the response
	 * @return the bit string
	 */
	public String makeBitString(List<Integer> response) {
		
		Iterator<Integer> responseValueItr = response.iterator();
		StringBuffer encodedBits = new StringBuffer();
		
		
		while (responseValueItr.hasNext()) {
			String bitString = Integer.toBinaryString(responseValueItr.next());
			if (bitString.length() < 8) {
				String precedingZeros = "";
				for (int i = 0; i < 8 - bitString.length(); i++) {
					precedingZeros += "0";
				}
				bitString = precedingZeros + bitString;
			}
			encodedBits.append(bitString);
		}
		
		return encodedBits.toString();
	}
	
	/**
	 * Prints the supported PIDs.
	 *
	 * @param validPIDS the list of valid PID's
	 */
	public void printSupportedPIDS(EnumMap<PIDS, Boolean> validPIDS) {
		
		Iterator<Entry<PIDS, Boolean>> supportedPIDS = validPIDS.entrySet().iterator();
		
		while(supportedPIDS.hasNext()) {
			
			Entry<PIDS, Boolean> currentEntry = supportedPIDS.next();
			
			if(currentEntry.getValue()) {
				log.info(currentEntry.getKey().getPid() + " " + currentEntry.getKey().toString() + 
						" (" + currentEntry.getKey().getDescription() + ")");
			}
		}
	}
	
	
	
}
