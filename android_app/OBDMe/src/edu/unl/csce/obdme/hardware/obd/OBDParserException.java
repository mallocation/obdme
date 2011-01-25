package edu.unl.csce.obdme.hardware.obd;

import edu.unl.csce.obdme.hardware.elm.ELMException;

/**
 * The Class OBDParserException.
 */
public class OBDParserException extends ELMException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -618104209319738839L;

	/**
	 * Instantiates a new oBD parser exception.
	 *
	 * @param msg the msg
	 */
	public OBDParserException(String msg){
		super(msg);
	}
}
