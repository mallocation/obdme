package edu.unl.csce.obdme.client.http.exception;

/**
 * ObdmeException
 * This class represents an error raised by the OBDme API.
 * The error message returned from the API will be the 
 * message of this exception.
 */
public class ObdmeException extends Exception {
	
	private static final long serialVersionUID = -3686112741654944569L;

	/**
	 * Instantiates a new OBDme exception.
	 *
	 * @param message the message returned from the API
	 */
	public ObdmeException(String message) {
		super(message);
	}

}
