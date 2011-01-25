package edu.unl.csce.obdme.hardware.elm;

/**
 * The Class ELMDeviceDataErrorException.
 */
class ELMDeviceDataErrorException extends ELMDataException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3437585125647748761L;

	/**
	 * Instantiates a new eLM device data error exception.
	 *
	 * @param msg the msg
	 */
	public ELMDeviceDataErrorException(String msg){
		super(msg);
	}
	
}