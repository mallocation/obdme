package edu.unl.csce.obdme.hardware.elm;

/**
 * The Class ELMDeviceBusErrorException.
 */
public class ELMDeviceBusErrorException extends ELMBusException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7485555452247470590L;

	/**
	 * Instantiates a new eLM device bus error exception.
	 *
	 * @param msg the msg
	 */
	public ELMDeviceBusErrorException(String msg){
		super(msg);
	}
	
}