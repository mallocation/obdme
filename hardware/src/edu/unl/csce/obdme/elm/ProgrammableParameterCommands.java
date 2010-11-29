package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

/**
 * The Class ProgrammableParameterCommands.
 */
public class ProgrammableParameterCommands extends ELM327CommandSet {

	/** The Log4J logger object. */
	private Logger log;

	/**
	 * Instantiates a new programmable parameter commands object.
	 *
	 * @param serialInterface the serial interface
	 */
	public ProgrammableParameterCommands(CommunicationInterface serialInterface) {
		
		super(serialInterface);
		
		//Initialize the logger for this instance
		log = Logger.getLogger(ProgrammableParameterCommands.class);
		
		// TODO Auto-generated constructor stub
	}

}
