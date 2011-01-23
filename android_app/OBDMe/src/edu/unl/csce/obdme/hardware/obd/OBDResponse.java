package edu.unl.csce.obdme.hardware.obd;

import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.unl.csce.obdme.hardware.elm.ELMErrors;

public class OBDResponse {

	private String responseString;
	private static final String ELM_DEVICE_SEARCHING = "SEARCHING...";

	public OBDResponse(String response) {
		this.setResponse(response);
		this.tokenizeResponse();

	}
	
	private LinkedList<String> tokenizeResponse() {
		LinkedList<String> responseArrayList = new LinkedList<String>();
		StringTokenizer tokenizer = new StringTokenizer(this.getResponse(), "\n", false);
		while (tokenizer.hasMoreTokens()) {
			responseArrayList.add(tokenizer.nextToken());
		}
		return responseArrayList;
	}

	private void parseResponse(LinkedList<String> responseArrayList) {
		
		//Make sure that we have a response to parse
		if(responseArrayList.size() <= 0) {
			//TODO Empty Response Exception
		}
		
		//Check to see if the response contains searching
		//This occurs right after a protocol autosearch
		if(ELMErrors.parseStringForError(responseArrayList.peek()) != null){
			//TODO ELMError Exception
		}
		
		
		
	}

	/**
	 * @param response the response to set
	 */
	private void setResponse(String response) {
		this.responseString = response;
	}

	/**
	 * @return the response
	 */
	public String getResponse() {
		return responseString;
	}

}
