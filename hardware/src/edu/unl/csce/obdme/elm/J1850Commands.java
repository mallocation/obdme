package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

import edu.unl.csce.obdme.bluetooth.CommunicationInterface;

/**
 * The Class J1850Commands.
 */
public class J1850Commands extends OBDCommands {
	
	/** The Log4J logger object. */
	private Logger log;

	/**
	 * Instantiates a new J1850 commands object.
	 *
	 * @param serialInterface the serial interface
	 */
	public J1850Commands(CommunicationInterface serialInterface) {
		super(serialInterface);
		
		//Initialize the logger for this instance
		log = Logger.getLogger(J1850Commands.class);
		
		// TODO Auto-generated constructor stub
	}

}
