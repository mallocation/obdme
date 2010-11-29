package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

/**
 * The Class CANCommands.
 */
public class CANCommands extends OBDCommands {
	
	/** The Log4J logger object. */
	private Logger log;

	/**
	 * Instantiates a new CAN commands object.
	 *
	 * @param serialInterface the serial interface
	 */
	public CANCommands(CommunicationInterface serialInterface) {
		super(serialInterface);
		
		//Initialize the logger for this instance
		log = Logger.getLogger(CANCommands.class);
		
		// TODO Auto-generated constructor stub
	}

}
