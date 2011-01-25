package edu.unl.csce.obdme.hardware.elm;

/**
 * The Class ELMDeviceStoppedException.
 */
public class ELMDeviceStoppedException extends ELMException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7098124426198162189L;

	/**
	 * Instantiates a new eLM device stopped exception.
	 *
	 * @param msg the msg
	 */
	public ELMDeviceStoppedException(String msg){
		super(msg);
	}
	
}