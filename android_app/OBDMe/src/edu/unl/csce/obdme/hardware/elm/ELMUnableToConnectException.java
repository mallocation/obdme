package edu.unl.csce.obdme.hardware.elm;

/**
 * The Class ELMUnableToConnectException.
 */
public class ELMUnableToConnectException extends ELMConnectionException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2617573996586710645L;

	/**
	 * Instantiates a new eLM unable to connect exception.
	 *
	 * @param msg the msg
	 */
	public ELMUnableToConnectException(String msg){
		super(msg);
	}
	
}
