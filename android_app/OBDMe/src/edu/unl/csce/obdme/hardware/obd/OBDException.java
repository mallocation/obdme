package edu.unl.csce.obdme.hardware.obd;

/**
 * The Class OBDException.
 */
public class OBDException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7653284572371418132L;

	/**
	 * Instantiates a new oBD exception.
	 *
	 * @param msg the msg
	 */
	public OBDException(String msg){
		super(msg);
	}
}

class OBDParserException extends OBDException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -618104209319738839L;

	public OBDParserException(String msg){
		super(msg);
	}
}

class OBDResponseLengthException extends OBDParserException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1493123340001843414L;

	public OBDResponseLengthException(String msg){
		super(msg);
	}
}

class OBDUnexpectedResponseException extends OBDParserException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6409758170056180050L;

	public OBDUnexpectedResponseException(String msg){
		super(msg);
	}
}