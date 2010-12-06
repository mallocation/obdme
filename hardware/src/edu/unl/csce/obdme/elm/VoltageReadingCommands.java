package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

import edu.unl.csce.obdme.bluetooth.CommunicationInterface;

/**
 * The Class VoltageReadingCommands.
 */
public class VoltageReadingCommands extends ELM327CommandSet {

	/** The Log4J logger object. */
	private Logger log;

	/**
	 * Instantiates a new voltage reading command object.
	 *
	 * @param serialInterface the serial interface
	 */
	public VoltageReadingCommands(CommunicationInterface serialInterface) {
		
		super(serialInterface);
		
		//Initialize the logger for this instance
		log = Logger.getLogger(VoltageReadingCommands.class);
		
		// TODO Auto-generated constructor stub
	}

}
