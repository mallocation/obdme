package edu.unl.csce.obdme.hardware.obd;

/**
 * The Class OBDResponseLengthException.
 */
public class OBDResponseLengthException extends OBDParserException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1493123340001843414L;

	/**
	 * Instantiates a new oBD response length exception.
	 *
	 * @param msg the msg
	 */
	public OBDResponseLengthException(String msg){
		super(msg);
	}
}

