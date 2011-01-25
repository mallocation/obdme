package edu.unl.csce.obdme.hardware.elm;

/**
 * The Class ELMDeviceBufferFullException.
 */
class ELMDeviceBufferFullException extends ELMException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4812310042103893126L;

	/**
	 * Instantiates a new eLM device buffer full exception.
	 *
	 * @param msg the msg
	 */
	public ELMDeviceBufferFullException(String msg){
		super(msg);
	}
	
}