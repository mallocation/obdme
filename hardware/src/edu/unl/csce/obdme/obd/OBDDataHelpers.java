package edu.unl.csce.obdme.obd;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class OBDDataHelpers {
	
	public OBDDataHelpers() {
		
	}

	public List<Integer> stringToIntArray(String byteString) {
		
		StringTokenizer tokenizedString = new StringTokenizer(byteString);
		List<Integer> intList = new ArrayList<Integer>(tokenizedString.countTokens());
		
	    while(tokenizedString.hasMoreTokens()){
	    	 intList.add(Integer.parseInt(tokenizedString.nextToken(), 16));
	    }
	    
	    return intList;
		
	}
	
	public List<Integer> processHeader(List<Integer> header, int modeValue, int pidValue) throws Exception {
		
		if (header.get(0) == Integer.parseInt(Integer.toString(modeValue + 40),16)) {
			header.remove(0);
		}
		else {
			throw new Exception("The response did not have the expected mode.");
		}
		
		if (header.get(0) == Integer.parseInt(Integer.toString(pidValue),16)) {
			header.remove(0);
		}
		else {
			throw new Exception("The response did not have the expected PID.");
		}
		
		return header;
		
	}
	
	
	
}
