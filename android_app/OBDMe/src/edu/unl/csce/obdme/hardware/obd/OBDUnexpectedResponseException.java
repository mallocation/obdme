package edu.unl.csce.obdme.hardware.obd;

// TODO: Auto-generated Javadoc
/**
 * The Class OBDUnexpectedResponseException.
 */
public class OBDUnexpectedResponseException extends OBDParserException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6409758170056180050L;

	/**
	 * Instantiates a new oBD unexpected response exception.
	 *
	 * @param msg the msg
	 */
	public OBDUnexpectedResponseException(String msg){
		super(msg);
	}
}
