package edu.unl.csce.obdme.hardware.elm;

/**
 * The Class ELMDeviceBusBusyException.
 */
public class ELMDeviceBusBusyException extends ELMBusException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2941621924016784499L;

	/**
	 * Instantiates a new eLM device bus busy exception.
	 *
	 * @param msg the msg
	 */
	public ELMDeviceBusBusyException(String msg){
		super(msg);
	}
	
}
