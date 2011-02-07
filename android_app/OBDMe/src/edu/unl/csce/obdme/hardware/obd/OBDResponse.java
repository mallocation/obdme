package edu.unl.csce.obdme.hardware.obd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;

import edu.unl.csce.obdme.hardware.elm.ELMErrorParser;
import edu.unl.csce.obdme.hardware.elm.ELMException;

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

	private Object processedResponse;

	/** The Constant ELM_DEVICE_SEARCHING. */
	private static final String ELM_DEVICE_SEARCHING = "SEARCHING...";

	private Context context;

	/**
	 * Instantiates a new oBD response.
	 *
	 * @param originalRequest the original request
	 * @param response the response
	 * @throws OBDParserException 
	 */
	public OBDResponse(Context context, OBDRequest originalRequest, String response) throws ELMException {
		
		//Initialize the variables
		this.context = context;
		this.originalRequest = originalRequest;
		this.rawResponse = response;
		this.responseBytes = new ArrayList<String>();
		
		//Perform the response
		parseResponse(tokenizeResponse());
		processResponse(originalRequest);
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
	private void parseResponse(LinkedList<String> responseArrayList) throws ELMException {

		try {
			//Check to see if the response contains searching
			//This occurs right after a protocol autosearch
			ELMErrorParser.parseStringForError(responseArrayList.peek());

			//Check if the first list is indicating a protocol search
			if(responseArrayList.get(0).equals(ELM_DEVICE_SEARCHING)) {

				//Throw it away
				responseArrayList.remove();
			}

			//Check if the first line is indicating a long response (greater than 8 bytes)
			if(responseArrayList.peek().length() <= 4) {

				//Save it so that we can verify our decoded data later
				int responseLength = Integer.parseInt(responseArrayList.poll(), 16);  

				//If it is a response line 
				//EX: "0: 49 02 01 31 48 47"
				while(!responseArrayList.peek().equals(">")) {
					parseLongResponseLine(responseArrayList.poll());
				}

				//If the size is not correct, throw an error
				if(responseLength == responseBytes.size()) {
					verifyResponse();
				}
				else {
					//We cant recover from a parser exception... Scrap the response.
					throw new OBDResponseLengthException("The long response did not contain the expected number of bytes.");
				}

				//Remove the last long response indicator
				responseBytes.remove(0);

			}
			else {

				//Parse the normal response
				parseNormalResponseLine(responseArrayList.poll());

				//If the size is not correct, throw an error
				if(originalRequest.getReturnLength() == responseBytes.size() - 2) {
					verifyResponse();
				}
				else {
					//We cant recover from a parser exception... Scrap the response.
					throw new OBDResponseLengthException("The normal response did not contain the expected number of bytes.");
				}
			}
		} catch (NumberFormatException nfe) {
			// TODO Auto-generated catch block
			nfe.printStackTrace();	
		}

	}

	/**
	 * Parses the normal response line.
	 *
	 * @param responseLine the response line
	 */
	private void parseNormalResponseLine(String responseLine) {
		
		int index = 0;

		//While there is still data left in the response line
		while(index+2 <= responseLine.length()){
			
			//Take the first two characters
			responseBytes.add(responseLine.substring(index, index+2));
			
			//Increment by two characters
			index += 2;
		}

	}

	/**
	 * Parses the long response line.
	 *
	 * @param responseLine the response line
	 */
	private void parseLongResponseLine(String responseLine) {

		int index = 0;
		
		//While there is still data left in the response line
		while(index+2 <= responseLine.length()){
			//If it is the line header
			//EX: "0:"
			if(responseLine.substring(index, index+2).contains(":")) {
				//throw it away
				index += 2;
			}
			//Else if it's length is 2, its a byte
			//EX: "49"
			else {
				//Take the first two characters
				responseBytes.add(responseLine.substring(index, index+2));
				index += 2;
			}
		}
	}

	/**
	 * Verify response.
	 *
	 * @throws OBDUnexpectedResponseException the oBD unexpected response exception
	 */
	private void verifyResponse() throws OBDUnexpectedResponseException, NumberFormatException {

		try {
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
			int expectedPIDValue = originalRequest.getRequestPID().getPidValue();

			//If it's what we expected, remove it from the list
			if(expectedPIDValue == receivedPIDValue) {
				responseBytes.remove(0);
			}

			//Otherwise throw an exception
			else {
				throw new OBDUnexpectedResponseException("The response pid " + Integer.toString(receivedPIDValue) + 
						" did not match the expected response pid " + Integer.toString(expectedPIDValue));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void processResponse(OBDRequest originalRequest) {
		this.processedResponse=originalRequest.getRequestPID().evaluateResponse(this.responseBytes);
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

	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @return the processedResponse
	 */
	public Object getProcessedResponse() {
		return processedResponse;
	}

}
