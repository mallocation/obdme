package edu.unl.csce.obdme.hardware.elm;

/**
 * The Class ELMDeviceNoDataException.
 */
public class ELMDeviceNoDataException extends ELMDataException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9164478893774069500L;

	/**
	 * Instantiates a new eLM device no data exception.
	 *
	 * @param msg the msg
	 */
	public ELMDeviceNoDataException(String msg){
		super(msg);
	}
	
}