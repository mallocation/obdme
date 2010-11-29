package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class ISOCommands.
 */
public class ISOCommands extends OBDCommands {
	
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
	
	/**
	 * Perform a Fast Initiation
	 * 
	 * One version of the Keyword protocol (the ELM327 protocol 5) uses what is known as a 
	 * fast initiation sequence to begin communications. Usually, this sequence is performed 
	 * when the first message needs to be sent, and then the message is sent immediately after. 
	 * Some ECUs may need more time between the two however, and having a separate initiation 
	 * command allows you to control this time. Simply send AT FI, wait a little, then send the 
	 * message. You may need to experiment to get the right amount of delay. Protocol 5 must be 
	 * selected to use the AT FI command, or an error will result.
	 *
	 * @return the string
	 */
	public String fastInitiation() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Set the ISO Baud rate to 10400
	 * 
	 * This command restores the ISO 9141-2 and ISO 14230-4 baud rates to the default value of 10400.
	 *
	 * @return the string
	 */
	public String setISOBaudRate10() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Set the ISO Baud rate to 4800
	 * 
	 * This command is used to change the baud rate used for the ISO 9141-2 and ISO 14230-4 protocols 
	 * (numbers 3, 4, and 5) to 4800 baud, while relaxing some of the requirements for the initiation 
	 * byte transfers. It may be useful for experimenting with some vehicles. Normal (10,400 baud) 
	 * operation may be restored at any time with the IB 10 command.
	 *
	 * @return the string
	 */
	public String setISOBaudRate48() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Set the ISO Baud rate to 9600
	 * 
	 * This command is used to change the baud rate used for the ISO 9141-2 and ISO 14230-4 protocols 
	 * (numbers 3, 4, and 5) to 9600 baud, while relaxing some of the requirements for the initiation 
	 * byte transfers. It may be useful for experimenting with some vehicles. Normal (10,400 baud) operation 
	 * may be restored at any time with the IB 10 command.
	 *
	 * @return the string
	 */
	public String setISOBaudRate96() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Display the Key Words
	 * 
	 * When the ISO 9141-2 and ISO 14230-4 protocols are initialized, two special bytes (key words) are 
	 * passed to the ELM327 (the values are used internally to determine whether a particular protocol 
	 * variation can be supported by the ELM327). If you wish to see what the value of these bytes were, 
	 * simply send the AT KW command.
	 *
	 * @return the string
	 */
	public String displayKeyWords() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Key Word checks on
	 * 
	 * The ELM327 looks for specific bytes (called key words) to be sent to it during the ISO 9141-2 and 
	 * ISO14230-4 initiation sequences. If the bytes are not found, the initiation is said to have failed 
	 * (you might see ‘UNABLE TO CONNECT’ or perhaps ‘BUS INIT: ...ERROR’). This might occur if you are 
	 * trying to connect to a non-OBD compliant ECU, or perhaps to an older one.
	 * 
	 * If you wish to experiment with non-standard systems, you may have to tell the ELM327 to perform 
	 * the initiation sequence, but ignore the contents of the bytes that are sent and received. 
	 * To do this, send:
	 * >AT KW0
	 * 
	 * After turning keyword checking off, the ELM327 will still require the two key word bytes in the response, 
	 * but will not look at the actual values of the bytes. It will also send an acknowledgement to the 
	 * ECU, and will wait for the final response from it (but will not stop and report an error if none is 
	 * received). This may allow you to make a connection in an otherwise ‘impossible’ situation. Normal 
	 * behaviour can be returned with AT KW1, which is the default setting.
	 *
	 * @return the string
	 */
	public String keyWordCheckingOn() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Key Word checks off
	 * 
	 * The ELM327 looks for specific bytes (called key words) to be sent to it during the ISO 9141-2 and 
	 * ISO14230-4 initiation sequences. If the bytes are not found, the initiation is said to have failed 
	 * (you might see ‘UNABLE TO CONNECT’ or perhaps ‘BUS INIT: ...ERROR’). This might occur if you are 
	 * trying to connect to a non-OBD compliant ECU, or perhaps to an older one.
	 * 
	 * If you wish to experiment with non-standard systems, you may have to tell the ELM327 to perform 
	 * the initiation sequence, but ignore the contents of the bytes that are sent and received. 
	 * To do this, send:
	 * >AT KW0
	 * 
	 * After turning keyword checking off, the ELM327 will still require the two key word bytes in the response, 
	 * but will not look at the actual values of the bytes. It will also send an acknowledgement to the 
	 * ECU, and will wait for the final response from it (but will not stop and report an error if none is 
	 * received). This may allow you to make a connection in an otherwise ‘impossible’ situation. Normal 
	 * behaviour can be returned with AT KW1, which is the default setting.
	 *
	 * @return the string
	 */
	public String keyWordCheckingOff() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 *  Perform a Slow Initiation
	 *  
	 *  Protocols 3 and 4 use what is sometimes called a 5 baud, or slow initiation sequence in order to begin 
	 *  communications. Usually, the sequence is performed when the first message needs to be sent, and then 
	 *  the message is sent immediately after. Some ECUs may need more time between the two however, and having 
	 *  a separate initiation command allows you to control this time. Simply send AT SI, wait a little, then 
	 *  send the message. You may need to experiment a little to get the right amount of delay. Protocol 3 or 4 
	 *  must be selected to use the AT SI command, or an error will result.
	 *
	 * @return the string
	 */
	public String performSlowBaudInitiation() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Set Wakeup to hh
	 * 
	 * Once a data connection has been established, some protocols require that there be data flow every few seconds, 
	 * just so that the ECU knows to maintain the communications path open. If the messages do not appear, 
	 * the ECU will assume that you are finished, and will close the channel. The connection will need to 
	 * be initialized again to reestablish communications.
	 * 
	 * The ELM327 will automatically generate periodic messages, as required, in order to maintain a connection. 
	 * The replies to these messages are always ignored, and are not visible to the user. (Currently, only protocols 
	 * 3, 4, and 5 support these messages - nothing is available for CAN.)
	 * 
	 * The time interval between these periodic ‘wakeup’ messages can be adjusted in 20 msec increments using the 
	 * AT SW hh command, where hh is any hexadecimal value from 00 to FF. The maximum possible time delay of just 
	 * over 5 seconds results when a value of FF (decimal 255) is used. The default setting provides a nominal delay 
	 * of 3 seconds between messages.
	 * 
	 * Note that the value 00 (zero) is treated as a very special case, and must be used with caution, as it will 
	 * stop all periodic messages. This way of stopping the messages while keeping the rest of the protocol 
	 * functioning normally, is for experimenters, and is notintended to be used regularly. Issuing AT SW 00 will 
	 * not change a prior setting for the time between wakeup messages, if the protocol is re-initialized.
	 *
	 * @return the string
	 */
	public String setWakeUpInterval() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Set Wakeup Message to.
	 * 
	 * This command allows the user to override the default settings for the wakeup messages (sometimes known as the 
	 * ‘periodic idle’ messages). Simply provide the message that you wish to have sent (typically three header bytes 
	 * and one to three data bytes), and the ELM327 will then send them as required, at the rate determined by the AT 
	 * SW setting. Note that you do not have to add a checksum byte to the data – the ELM327 calculates the value and 
	 * adds it for you.
	 *
	 * @return the string
	 */
	public String setWakeUpMessage() {
		//TODO This needs to be implemented
		return null;
	}

}
