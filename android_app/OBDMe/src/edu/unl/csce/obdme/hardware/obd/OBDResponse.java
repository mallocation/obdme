package edu.unl.csce.obdme.hardware.obd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import edu.unl.csce.obdme.hardware.elm.ELMErrors;

/**
 * The Class OBDResponse.
 */
public class OBDResponse {

	/** The raw response. */
	private String rawResponse;
	
	/** The response bytes. */
	private List<String> responseBytes;
	
	/** The original request. */
	private OBDRequest originalRequest;
	
	/** The Constant ELM_DEVICE_SEARCHING. */
	private static final String ELM_DEVICE_SEARCHING = "SEARCHING...";

	/**
	 * Instantiates a new oBD response.
	 *
	 * @param originalRequest the original request
	 * @param response the response
	 * @throws OBDParserException 
	 */
	public OBDResponse(OBDRequest originalRequest, String response) throws OBDParserException {
		setOriginalRequest(originalRequest);
		setRawResponse(response);
		responseBytes = new ArrayList<String>();
		parseResponse(tokenizeResponse());
	}

	/**
	 * Tokenize response.
	 *
	 * @return the linked list
	 */
	private LinkedList<String> tokenizeResponse() {
		LinkedList<String> responseArrayList = new LinkedList<String>();
		StringTokenizer tokenizer = new StringTokenizer(this.getRawResponse(), "\r", false);
		while (tokenizer.hasMoreTokens()) {
			responseArrayList.add(tokenizer.nextToken());
		}
		return responseArrayList;
	}

	/**
	 * Parses the response.
	 *
	 * @param responseArrayList the response array list
	 * @throws OBDParserException the oBD parser exception
	 */
	private void parseResponse(LinkedList<String> responseArrayList) throws OBDParserException {
				
		//Check to see if the response contains searching
		//This occurs right after a protocol autosearch
		if(ELMErrors.parseStringForError(responseArrayList.peek()) != null){
			//TODO ELMError Exception
		}

		//Check if the first list is indicating a protocol search
		if(responseArrayList.get(0).contains(ELM_DEVICE_SEARCHING)) {

			//Throw it away
			responseArrayList.remove();
		}

		//Check if the first line is indicating a long response (greater than 8 bytes)
		if(responseArrayList.peek().length() <= 4) {

			//Save it so that we can verify our decoded data later
			int responseLength = Integer.parseInt(responseArrayList.poll().trim(), 16);  

			//If it is a response line 
			//EX: "0: 49 02 01 31 48 47"
			while(!responseArrayList.peek().toString().equals(">")) {
				parseLongResponseLine(responseArrayList.poll());
			}
			
			//If the size is not correct, throw an error
			if(responseLength == responseBytes.size()) {
				verifyResponse();
			}
			else {
				throw new OBDResponseLengthException("The long response did not contain the expected number of bytes.");
			}
			
			//Remove the last long response indicator
			responseBytes.remove(0);
			
		}
		else {
			
			//Parse the normal response
			parseNormalResponseLine(responseArrayList.poll());
			
			//If the size is not correct, throw an error
			if(originalRequest.getReturnLength() == responseBytes.size()) {
				verifyResponse();
			}
			else {
				throw new OBDResponseLengthException("The normal response did not contain the expected number of bytes.");
			}
		}

	}

	/**
	 * Parses the normal response line.
	 *
	 * @param responseLine the response line
	 */
	private void parseNormalResponseLine(String responseLine) {

		StringTokenizer tokenizedString = new StringTokenizer(responseLine, " ", false);

		//While there are bytes to be parsed
		while(tokenizedString.hasMoreTokens()){

			String currentToken = tokenizedString.nextToken();

			//Else if it's length is 2, its a byte
			//EX: "49"
			if (currentToken.length() == 2){
				responseBytes.add(currentToken);
			}
		}

	}

	/**
	 * Parses the long response line.
	 *
	 * @param responseLine the response line
	 */
	private void parseLongResponseLine(String responseLine) {

		StringTokenizer tokenizedString = new StringTokenizer(responseLine, " ", false);

		//While there are bytes to be parsed
		while(tokenizedString.hasMoreTokens()){

			String currentToken = tokenizedString.nextToken();

			//If it is the line header
			//EX: "0:"
			if(currentToken.length() == 2 && currentToken.contains(":")) {
				//throw it away
			}
			//Else if it's length is 2, its a byte
			//EX: "49"
			else if (currentToken.length() == 2){
				responseBytes.add(currentToken);
			}
		}
	}
	
	/**
	 * Verify response.
	 *
	 * @throws OBDUnexpectedResponseException the oBD unexpected response exception
	 */
	private void verifyResponse() throws OBDUnexpectedResponseException {
		
		//Parse the integer values for the expected and received mode ID's
		int receivedModeValue = Integer.parseInt(responseBytes.get(0), 16);
		int expectedModeValue = Integer.parseInt(Integer.toString(
				Integer.parseInt(originalRequest.getMode()) + 40),16);
		
		//If it's what we expected, remove it from the list
		if(expectedModeValue == receivedModeValue) {
			responseBytes.remove(0);
		}
		
		//Otherwise throw an exception
		else {
			throw new OBDUnexpectedResponseException("The response mode " + Integer.toString(receivedModeValue) + 
					" did not match the expected response mode " + Integer.toString(expectedModeValue));
		}
		
		//Parse the integer values for the expected and received PID's
		int receivedPIDValue = Integer.parseInt(responseBytes.get(0), 16);
		int expectedPIDValue = Integer.parseInt(originalRequest.getPid(), 16);
		
		//If it's what we expected, remove it from the list
		if(expectedPIDValue == receivedPIDValue) {
			responseBytes.remove(0);
		}
		
		//Otherwise throw an exception
		else {
			throw new OBDUnexpectedResponseException("The response pid " + Integer.toString(receivedPIDValue) + 
					" did not match the expected response pid " + Integer.toString(expectedPIDValue));
		}
		
	}

	/**
	 * Sets the response bytes.
	 *
	 * @param responseBytes the responseBytes to set
	 */
	public void setResponseBytes(List<String> responseBytes) {
		this.responseBytes = responseBytes;
	}

	/**
	 * Gets the response bytes.
	 *
	 * @return the responseBytes
	 */
	public List<String> getResponseBytes() {
		return responseBytes;
	}

	/**
	 * Sets the raw response.
	 *
	 * @param rawResponse the rawResponse to set
	 */
	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}

	/**
	 * Gets the raw response.
	 *
	 * @return the rawResponse
	 */
	public String getRawResponse() {
		return rawResponse;
	}

	/**
	 * Sets the original request.
	 *
	 * @param originalRequest the originalRequest to set
	 */
	public void setOriginalRequest(OBDRequest originalRequest) {
		this.originalRequest = originalRequest;
	}

	/**
	 * Gets the original request.
	 *
	 * @return the originalRequest
	 */
	public OBDRequest getOriginalRequest() {
		return originalRequest;
	}

}
