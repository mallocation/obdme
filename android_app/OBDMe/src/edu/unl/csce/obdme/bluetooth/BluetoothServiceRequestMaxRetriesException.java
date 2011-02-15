package edu.unl.csce.obdme.bluetooth;

/**
 * The Class BluetoothServiceRequestTimeoutException.
 */
public class BluetoothServiceRequestMaxRetriesException extends BluetoothServiceException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1466341856923194145L;

	/**
	 * Instantiates a new bluetooth service request timeout exception.
	 *
	 * @param msg the msg
	 */
	public BluetoothServiceRequestMaxRetriesException(String msg){
		super(msg);
	}
}