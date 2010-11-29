package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

/**
 * The Class J1939CANCommands.
 */
public class J1939CANCommands extends ELM327CommandSet{
	
	/** The Log4J logger object. */
	private Logger log;

	/**
	 * Instantiates a new J1939 CAN commands object.
	 *
	 * @param serialInterface the serial interface
	 */
	public J1939CANCommands(CommunicationInterface serialInterface) {
		
		super(serialInterface);
		
		//Initialize the logger for this instance
		log = Logger.getLogger(J1939CANCommands.class);
		
		// TODO Auto-generated constructor stub
	}

}
