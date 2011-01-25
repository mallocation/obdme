package edu.unl.csce.obdme.hardware.obd;

import edu.unl.csce.obdme.hardware.elm.ELMException;

/**
 * The Class OBDException.
 */
public class OBDException extends ELMException {

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

