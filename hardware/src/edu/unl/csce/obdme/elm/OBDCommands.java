package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

/**
 * The Class OBDCommands.
 */
public class OBDCommands extends ELM327CommandSet {
	
	/** The Log4J logger object. */
	private Logger log;

	/**
	 * Instantiates a new OBD commands object.
	 *
	 * @param serialInterface the serial interface
	 */
	public OBDCommands(CommunicationInterface serialInterface) {
		
		super(serialInterface);
		
		//Initialize the logger for this instance
		log = Logger.getLogger(OBDCommands.class);
		
		// TODO Auto-generated constructor stub
	}

}
