package edu.unl.csce.obdme.client.http.exception;

// TODO: Auto-generated Javadoc
/**
 * CommException represents any exception that occurs when contacting the OBDme api.
 * This could involve connection errors, timeout errors, etc.
 * The message property of this exception will contain the details of the exception.
 */
public class CommException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4120212312644201428L;

	/**
	 * Instantiates a new comm exception.
	 *
	 * @param message the message
	 */
	public CommException(String message) {
		super(message);
	}

}
