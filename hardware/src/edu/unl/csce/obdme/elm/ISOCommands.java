package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

/**
 * The Class ISOCommands.
 */
public class ISOCommands extends ELM327CommandSet {
	
	/** The Log4J logger object. */
	private Logger log;
	
	/**
	 * Instantiates a new ISO commands object.
	 *
	 * @param serialInterface the serial interface
	 */
	public ISOCommands(CommunicationInterface serialInterface) {
		super(serialInterface);
		
		//Initialize the logger for this instance
		log = Logger.getLogger(ISOCommands.class);
		
		// TODO Auto-generated constructor stub
	}

}
