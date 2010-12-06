package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

import edu.unl.csce.obdme.bluetooth.CommunicationInterface;

// TODO: Auto-generated Javadoc
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
	
	/**
	 * The CEA command is used to turn off the special features that are set with 
	 * the CEA hh command.
	 *
	 * @return true, if successful
	 */
	public boolean turnOffCANExtendedAddressing() {
		
		return false;
	}
	
	/**
	 * Some CAN protocols extend the addressing fields by using the first of the eight 
	 * data bytes as a target or receiver’s address. This type of formatting does not 
	 * comply with any OBD standard, but by adding it, we allow for some experimentation.
	 * 
	 * Sending the CEA hh command causes the ELM327 to insert the hh value as the first 
	 * data byte of all CAN messages that you send. It also adds one more filtering step 
	 * to received messages, only passing ones that have the Tester Address in the first 
	 * byte position (in addition to requiring that ID bits match the patterns set by AT 
	 * CF and CM, or CRA). The AT CEA hh command can be sent at any time, and changes are 
	 * effective immediately, allowing for changes of the address ‘on-the-fly’. There is 
	 * a more lengthy discussion of extended addressing in the ‘Using CAN Extended 
	 * Addresses’ section on page 47.
	 * 
	 * The CEA mode of operation is off by default, and once on, can be turned off at any 
	 * time by sending AT CEA, with no address. Note that the CEA setting has no effect 
	 * when J1939 formatting is on.
	 *
	 * @param hh the hh
	 * @return true, if successful
	 */
	public boolean useCANExtendedAddress(String hh) {
		
		return false;
	}
	
	/**
	 * Automatic formatting off.
	 *
	 * @return true, if successful
	 */
	public boolean automaticFormattingOff() {
		
		return false;
	}
	
	/**
	 * Automatic formatting on.
	 *
	 * @return true, if successful
	 */
	public boolean automaticFormattingOn() {
		
		return false;
	}
	
	/**
	 * The CAN Filter works in conjunction with the CAN Mask to determine what information 
	 * is to be accepted by the receiver. As each message is received, the incoming CAN 
	 * ID bits are compared to the CAN Filter bits (when the mask bit is a ‘1’). If all 
	 * of the relevant bits match, the message will be accepted, and processed by the 
	 * ELM327, otherwise it will be discarded. This three nibble version of the CAN Filter 
	 * command makes it a little easier to set filters with 11 bit ID CAN systems. Only 
	 * the rightmost 11 bits of the provided nibbles are used, and the most significant bit 
	 * is ignored. The data is actually stored as four bytes internally however, with this 
	 * command adding leading zeros for the other bytes. See the CM command(s) for more details.
	 *
	 * @return true, if successful
	 */
	public boolean setIDFilter() {
		
		return false;
	}
	
	/**
	 * The ISO 15765-4 CAN protocol expects a ‘Flow Control’ message to always be sent in response 
	 * to a ‘First Frame’ message, and the ELM327 automatically sends these without any intervention 
	 * by the user. If experimenting with a non-OBD system, it may be desirable to turn this automatic 
	 * response off, and the AT CFC0 command has been provided for that purpose. The default setting is 
	 * CFC1 - Flow Controls on.
	 * 
	 * Note that during monitoring (AT MA, MR, or MT), there are never any Flow Controls sent no matter 
	 * what the CFC option is set to.
	 *
	 * @return true, if successful
	 */
	public boolean flowControlsOff() {
		
		return false;
	}
	
	/**
	 * The ISO 15765-4 CAN protocol expects a ‘Flow Control’ message to always be sent in response 
	 * to a ‘First Frame’ message, and the ELM327 automatically sends these without any intervention 
	 * by the user. If experimenting with a non-OBD system, it may be desirable to turn this automatic 
	 * response off, and the AT CFC0 command has been provided for that purpose. The default setting is 
	 * CFC1 - Flow Controls on.
	 * 
	 * Note that during monitoring (AT MA, MR, or MT), there are never any Flow Controls sent no matter 
	 * what the CFC option is set to.
	 *
	 * @return true, if successful
	 */
	public boolean flowControlsOn() {
		
		return false;
	}
	
	/**
	 * There can be a great many messages being transmitted in a CAN system at any one time. In order to 
	 * limit what the ELM327 views, there needs to be a system of filtering out the relevant ones from all 
	 * the others. This is accomplished by the filter, which works in conjunction with the mask. A mask is 
	 * a group of bits that show the ELM327 which bits in the filter are relevant, and which ones can be 
	 * ignored. A ‘must match’ condition is signalled by setting a mask bit to '1', while a 'don't care' is 
	 * signalled by setting a bit to '0'. This three digit variation of the CM command is used to provide 
	 * mask values for 11 bit ID systems (the most significant bit is always ignored).
	 * 
	 * Note that a common storage location is used internally for the 29 bit and 11 bit masks, so an 11 bit 
	 * mask could conceivably be assigned with the next command (CM hh hh hh hh), should you wish to do the 
	 * extra typing. The values are right justified, so you would need to provide five leading zeros followed 
	 * by the three mask bytes.
	 *
	 * @return true, if successful
	 */
	public boolean setIDMask() {
		
		return false;
	}
	
	/**
	 * This command is used to assign the five most significant bits of the 29 bit CAN ID that is used for 
	 * sending messages (the other 24 bits are set with the AT SH command). Many systems use these bits to 
	 * assign a priority value to messages, and to determine the protocol. Any bits provided in excess of 
	 * the five required are ignored, and not stored by the ELM327 (it only uses the five least significant 
	 * bits of this byte). The default value for these priority bits is hex 18, which can be restored at any 
	 * time with the AT D command.
	 *
	 * @return true, if successful
	 */
	public boolean setCANPriority() {
		
		return false;
	}
	
	/**
	 * The AT CRA command is used to restore the CAN receive filters to their default values. Note that it 
	 * does not have any arguments (ie no data).
	 *
	 * @return true, if successful
	 */
	public boolean resetReceiveAddressFilters() {
		
		return false;
	}
	
	/**
	 * Sets the can receive address.
	 *
	 * @return true, if successful
	 */
	public boolean setCANReceiveAddress() {
		
		return false;
	}
	
	/**
	 * The CAN protocol requires that statistics be kept regarding the number of transmit and receive errors 
	 * detected. If there should be a significant number of errors (due to a hardware or software problem), 
	 * the device will go off-line in order to not affect other data on the bus. The AT CS command lets you 
	 * see both the transmitter (Tx) and the receiver (Rx) error counts, in hexadecimal. If the transmitter 
	 * should be off (count >FF), you will see ‘OFF’ rather than a specific count.
	 *
	 * @return true, if successful
	 */
	public boolean showCANStatusCounts() {
		
		return false;
	}
	
	/**
	 * The ELM327 was designed to be completely silent while monitoring a CAN bus. Because of this, it is 
	 * able to report exactly what it sees, without colouring the information in any way. Occasionally 
	 * (when bench testing, or when connecting to a dedicated CAN port), it may be preferred that the 
	 * ELM327 does not operate silently (ie generates ACK bits, etc.), and this is what the CSM command 
	 * is for. CSM1 turns it on, CSM0 turns it off, and the default value is determined by PP 21. Be 
	 * careful when experimenting with this. If you should choose the wrong baud rate then monitor the 
	 * CAN bus with the silent monitoring off, you will disturb the flow of data. Always keep the silent 
	 * monitoring on until you are certain that you have chosen the correct baud rate.
	 *
	 * @return true, if successful
	 */
	public boolean SilentMonitoringOn() {
		
		return false;
	}
	
	/**
	 * The ELM327 was designed to be completely silent while monitoring a CAN bus. Because of this, it is 
	 * able to report exactly what it sees, without colouring the information in any way. Occasionally 
	 * (when bench testing, or when connecting to a dedicated CAN port), it may be preferred that the 
	 * ELM327 does not operate silently (ie generates ACK bits, etc.), and this is what the CSM command 
	 * is for. CSM1 turns it on, CSM0 turns it off, and the default value is determined by PP 21. Be 
	 * careful when experimenting with this. If you should choose the wrong baud rate then monitor the 
	 * CAN bus with the silent monitoring off, you will disturb the flow of data. Always keep the silent 
	 * monitoring on until you are certain that you have chosen the correct baud rate.
	 *
	 * @return true, if successful
	 */
	public boolean SilentMonitoringOff() {
		
		return false;
	}
	
	/**
	 * Display of the dlc on.
	 *
	 * @return true, if successful
	 */
	public boolean displayOfTheDLCOn() {
		
		return false;
	}
	
	/**
	 * Display of the dlc off.
	 *
	 * @return true, if successful
	 */
	public boolean displayOfTheDLCOff() {
		
		return false;
	}
	
	/**
	 * This command sets how the ELM327 responds to First Frame messages when automatic Flow Control 
	 * responses are enabled. The single digit provided can either be ‘0’ (the default) for fully 
	 * automatic responses, ‘1’ for completely user defined responses, or ‘2’ for user defined data 
	 * bytes in the response. Note that FC modes 1 and 2 can only be enabled if you have defined the 
	 * needed data and possibly ID bytes. If you have not, you will get an error. More complete details 
	 * and examples can be found in the Altering Flow Control Messages section (page 46).
	 *
	 * @return true, if successful
	 */
	public boolean flowControlSetMode() {
		
		return false;
	}
	
	/**
	 * The header (or more properly ‘CAN ID’) bytes used for CAN Flow Control messages can be set 
	 * using this command. Only the right-most 11 bits of those provided will be used - the most 
	 * significant bit is always removed. This command only affects Flow Control mode 1.
	 *
	 * @return true, if successful
	 */
	public boolean flowControlSetHeader() {
		
		return false;
	}
	
	/**
	 * The data bytes that are sent in a CAN Flow Control message may be defined with this command. 
	 * One to five data bytes may be specified, with the remainder of the data bytes in the message 
	 * being automatically set to the default CAN filler byte, if required by the protocol. Data 
	 * provided with this command is only used when Flow Control modes 1 or 2 have been enabled.
	 *
	 * @return true, if successful
	 */
	public boolean flowControlSetData() {
		
		return false;
	}
	
	/**
	 * Protocol b options baud rate.
	 *
	 * @return true, if successful
	 */
	public boolean protocolBOptionsBaudRate() {
		
		return false;
	}
	
	/**
	 * This command causes a special ‘Remote Frame’ CAN message to be sent. This type of message 
	 * has no data bytes, and has its Remote Transmission Request (RTR) bit set. The headers and 
	 * filters will remain as previously set (ie the ELM327 does not make any assumptions as to 
	 * what format a response may have), so adjustments may need to be made to the mask and 
	 * filter. This command must be used with an active CAN protocol (one that has been sending 
	 * and receiving messages), as it can not initiate a protocol search. Note that the CAF1 setting 
	 * normally eliminates the display of all RTRs, so if you are monitoring messages and want to see 
	 * the RTRs, you will have to turn off formatting, or else turn the headers on.
	 * 
	 * The ELM327 treats an RTR just like any other message sent, and will wait for a response from 
	 * the vehicle (unless AT R0 has been chosen).
	 *
	 * @return true, if successful
	 */
	public boolean sendRTRMessage() {
		
		return false;
	}
	
	/**
	 * These commands modify the current CAN protocol settings to allow the sending of variable data 
	 * length messages, just as bit 6 of PP 2C and PP 2E do for protocols B and C. This allows 
	 * experimenting with variable data length messages for any of the CAN protocols (not just B 
	 * and C). The V1 command will always override any protocol setting, and force a variable data 
	 * length message. The default setting is V0, providing data lengths as determined by the protocol.
	 *
	 * @return true, if successful
	 */
	public boolean useVariableDLCOn() {
		
		return false;
	}
	
	/**
	 * These commands modify the current CAN protocol settings to allow the sending of variable data 
	 * length messages, just as bit 6 of PP 2C and PP 2E do for protocols B and C. This allows 
	 * experimenting with variable data length messages for any of the CAN protocols (not just B 
	 * and C). The V1 command will always override any protocol setting, and force a variable data 
	 * length message. The default setting is V0, providing data lengths as determined by the protocol.
	 *
	 * @return true, if successful
	 */
	public boolean useVariableDLCOff() {
		
		return false;
	}

}
