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
	
	/**
	 * Allow Long messages
	 * 
	 * The standard OBDII protocols restrict the number of data bytes in a message to seven, 
	 * which the ELM327 normally does as well (for both send and receive). If AL is selected, 
	 * the ELM327 will allow long sends (eight data bytes) and long receives (unlimited in number). 
	 * The default is AL off (and NL selected).
	 *
	 * @return true, if successful
	 */
	public boolean allowLongMessages() {
		return false;
	}
	
	/**
	 * Automatically set the Receive address 
	 * 
	 * Responses from the vehicle will be acknowledged and displayed by the ELM327, if the internally 
	 * stored receive address matches the address that the message is being sent to. With the auto 
	 * receive mode in effect, the value used for the receive address will be chosen based on the 
	 * current header bytes, and will automatically be updated whenever the header bytes are changed.
	 * 
	 * The value that is used for the receive address is determined based on such things as the contents 
	 * of the first header byte, and whether the message uses physical addressing, functional addressing, 
	 * or if the user has set a value with the SR or RA commands.
	 * 
	 * Auto Receive is turned on by default, and is not used by the J1939 protocol.
	 *
	 * @return true, if successful
	 */
	public boolean automaticallyReceive() {
		return false;
	}
	
	/**
	 * Perform an OBD Buffer Dump
	 * 
	 * All messages sent and received by the ELM327 are stored temporarily in a set of twelve memory storage 
	 * locations called the OBD Buffer. Occasionally, it may be of use to view the contents of this buffer, 
	 * perhaps to see why an initiation failed, to see the header bytes in the last message, or just to learn 
	 * more of the structure of OBD messages. You can ask at any time for the contents of this buffer to be 
	 * ÔdumpedÕ (ie printed) Ð when you do, the ELM327 sends a length byte (representing the length of the 
	 * message in the buffer) followed by the contents of all twelve OBD buffer locations. For example, hereÕs 
	 * one ÔdumpÕ:
	 * 
	 * >AT BD 
	 * 05 C1 33 F1 3E 23 C4 00 00 10 F8 00 00
	 * 
	 * The 05 is the length byte - it tells us that only the following 5 bytes (C1 33 F1 3E and 23) are valid. 
	 * The remaining bytes are likely left over from a previous operation.
	 * 
	 * The length byte always represents the actual number of bytes received, whether they fit into the OBD 
	 * buffer or not. This may be useful when viewing long data streams (with AT AL), as it represents the actual 
	 * number of bytes received, mod 256. Note that only the first twelve bytes received are stored in the buffer.
	 *
	 * @return the string
	 */
	public String bufferDump() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Bypass the Initialization sequence
	 * 
	 * This command should be used with caution. It allows an OBD protocol to be made active without requiring 
	 * any sort of initiation or handshaking to occur. The initiation process is normally used to validate the 
	 * protocol, and without it, results may be difficult to predict. It should not be used for routine OBD use, 
	 * and has only been provided to allow the construction of ECU simulators and training demonstrators.
	 *
	 * @return true, if successful
	 */
	public boolean bypassInitialzationSequence() {
		return false;
	}
	
	/**
	 * Describe the current Protocol
	 * 
	 * The ELM327 automatically detects a vehicleÕs OBD protocol, but does not normally report what it is. 
	 * The DP command is a convenient means of asking what protocol the IC is currently set to (even if it has 
	 * not yet ÔconnectedÕ to the vehicle).
	 * 
	 * If a protocol is chosen and the automatic option is also selected, AT DP will show the word 'AUTO' before 
	 * the protocol description. Note that the description shows the actual protocol names, not the numbers used 
	 * by the protocol setting commands.
	 *
	 * @return the string
	 */
	
	public String describeCurrentProtocol() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Describe the Protocol by Number
	 * 
	 * This command is similar to the DP command, but it returns a number which represents the current protocol. 
	 * If the automatic search function is also enabled, the number will be preceded with the letter ÔAÕ. The number 
	 * is the same one that is used with the set protocol and test protocol commands.
	 *
	 * @return the string
	 */
	public String describeProtocolNumber() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * Headers on
	 * 
	 * These commands control whether or not the additional (header) bytes of information are shown in the 
	 * responses from the vehicle. These are not normally shown by the ELM327, but may be of interest (especially 
	 * if you receive multiple responses and wish to determine what modules they were from).
	 * 
	 * Turning the headers on (with AT H1) actually shows more than just the header bytes Ð you will see the 
	 * complete message as transmitted, including the check-digits and PCI bytes, and possibly the CAN data 
	 * length code (DLC) if it has been enabled with PP 29 or AT D1. The current version of this IC does not display 
	 * the CAN CRC code, nor the special J1850 IFR bytes (which some protocols use to acknowledge receipt of a message).
	 *
	 * @return true, if successful
	 */
	public boolean headersOn() {
		//Send the command
		this.serialInterface.sendATCommand("H1");
		
		//Check if we got what we expected in return
		if(this.serialInterface.recieveResponse().contains("OK")) {
			log.info("The device completed auto set protocol function");
			return true;
		}
		else {
			log.error("The device never responded after restart request.");
			return false;
		}
	}
	
	/**
	 * Headers off
	 * 
	 * These commands control whether or not the additional (header) bytes of information are shown in the 
	 * responses from the vehicle. These are not normally shown by the ELM327, but may be of interest (especially 
	 * if you receive multiple responses and wish to determine what modules they were from).
	 * 
	 * Turning the headers on (with AT H1) actually shows more than just the header bytes Ð you will see the 
	 * complete message as transmitted, including the check-digits and PCI bytes, and possibly the CAN data 
	 * length code (DLC) if it has been enabled with PP 29 or AT D1. The current version of this IC does not display 
	 * the CAN CRC code, nor the special J1850 IFR bytes (which some protocols use to acknowledge receipt of a message).
	 *
	 * @return true, if successful
	 */
	public boolean headersOff() {
		return false;
	}
	
	/**
	 * Monitor All messages
	 * 
	 * This command places the ELM327 into a bus monitoring mode, in which it continually monitors for (and displays) 
	 * all messages that it sees on the OBD bus. It is a quiet monitor, not sending In Frame Responses for J1850 
	 * systems, Acknowledges for CAN systems, or Wakeup (Ôkeep-aliveÕ) messages for the ISO 9141 and ISO 14230 
	 * protocols. Monitoring will continue until it is stopped by activity on the RS232 input, or the RTS pin.
	 * 
	 * To stop the monitoring, simply send any single character to the ELM327, then wait for it to respond with a 
	 * prompt character (Ô>Õ), or a low level output on the Busy pin. (Setting the RTS input to a low level will 
	 * interrupt the device as well.) Waiting for the prompt is necessary as the response time varies depending on 
	 * what the IC was doing when it was interrupted. If for instance it is in the middle of printing a line, it 
	 * will first complete that line then return to the command state, issuing the prompt character. If it were 
	 * simply waiting for input, it would return immediately. Note that the character which stops the monitoring 
	 * will always be discarded, and will not affect subsequent commands.
	 * 
	 * Beginning with v1.3 of this IC, all messages will be printed as found, even if the CAN auto formatting is on 
	 * (CAF1). The previous version of this IC (v1.2) did not display some illegal CAN messages if the automatic 
	 * formatting was on, but now all messages received are displayed, and if the data format does not appear to be 
	 * correct, then Ô<DATA ERRORÕ will be shown beside the data.
	 * 
	 * If this command is used with CAN protocols, and if the CAN filter and/or mask were previously set 
	 * (with CF, CM or CRA), then the MA command will be affected by the settings. For example, if the receive 
	 * address had been set previously with CRA 4B0, then the AT MA command would only be able to ÔseeÕ messages with 
	 * an ID of 4B0. This may not be what is desired - you may want to reset the masks and filters (with AT AR) first.
	 * 
	 * All of the monitoring commands (MA, MR and MT) operate by closing the current protocol (an AT PC is executed 
	 * internally), then configuring the IC for silent monitoring of the data (no wakeup messages, IFRs or CAN 
	 * acknowledges are sent by the ELM327). When the next OBD command is to be transmitted, the protocol will again 
	 * be initialized, and you may see messages stating this. ÔSEARCHING...Õ may also be seen, depending on what 
	 * changes were made while monitoring.
	 *
	 * @return true, if successful
	 */
	public boolean monitorAll() {
		return false;
	}
	
	/**
	 * Monitor for Receiver hh 
	 * 
	 * This command is very similar to the AT MA command except that it will only display messages that were sent 
	 * to the hex address given by hh. These are messages which are found to have the value hh in the second byte 
	 * of a traditional three byte OBD header, in bits 8 to 15 of a 29 bit CAN ID, or in bits 8 to 10 of an 11 bit 
	 * CAN ID. Any single RS232 character aborts the monitoring, as with the MA command.
	 * 
	 * Note that if this command is used with CAN protocols, and if the CAN filter and/or mask were previously set 
	 * (with CF, CM or CRA), then the MR command will over-write the previous values for these bits only - the others 
	 * will remain unchanged. As an example, if the receive address has been set with CRA 4B0, and then you send MR 02, 
	 * the 02 will replace the 4, and the CAN masks/filters will only allow IDs that are equal to 2B0. This is often 
	 * not what is desired - you may want to reset the masks and filters (with AT AR) first.
	 * 
	 * As with the AT MA command, this command begins by performing an internal Protocol Close. Subsequent OBD requests 
	 * may show ÔSEARCHINGÕ or ÔBUS INITÕ, etc. messages when the protocol is reactivated.
	 *
	 * @return true, if successful
	 */
	public boolean monitorForReceiver() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * Normal Length messages
	 * 
	 * This command is also very similar to the AT MA command, except that it will only display messages that were sent 
	 * by the transmitter with the hex address given by hh. These are messages which are found to have that value in the 
	 * third byte of a traditional three byte OBD header, or in bits 0 to 7 for CAN IDs. As with the MA and MR monitoring
	 * modes, any RS232 activity (single character) aborts the monitoring.
	 * 
	 * Note that if this command is used with CAN protocols, and if the CAN filter and/or mask were previously set 
	 * (with CF, CM or CRA), then the MT command will over-write the previous values for these bits only - the others will 
	 * remain unchanged. As an example, if the receive address has been set with CRA 4B0, and then you send MT 20, the 20 
	 * will replace the B0, and the CAN masks/filters will only allow IDs that are equal to 420. This is often not what is 
	 * desired - you may want to reset the masks and filters (with AT AR) first.
	 * 
	 * As with the AT MA command, this command begins by performing an internal Protocol Close. Subsequent OBD requests may 
	 * show ÔSEARCHINGÕ or ÔBUS INITÕ, etc. messages when the protocol is reactivated.
	 *
	 * @return true, if successful
	 */
	public boolean monitorForTransmitter() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * Setting the NL mode on forces all sends and receives to be limited to the standard seven data bytes in length, similar 
	 * to the other ELM32x OBD ICs. To allow longer messages, use the AL command.
	 * 
	 * Beginning with v1.2, the ELM327 does not require a change to AL to allow longer message lengths for the KWP protocols 
	 * to be received (as determined by the header length values). You can simply leave the IC set to the default setting of 
	 * NL, and all of the received bytes will be shown.
	 *
	 * @return true, if successful
	 */
	public boolean normalLengthMessages() {
		return false;
	}
	
	/**
	 * Protocol Close
	 * 
	 * There may be occasions where it is desirable to stop (deactivate) a protocol. Perhaps you are 
	 * not using the automatic protocol finding, and wish to manually activate and deactivate protocols. 
	 * Perhaps you wish to stop the sending of idle (wakeup) messages, or have another reason. The 
	 * PC command is used in these cases to force a protocol to close.
	 *
	 * @return true, if successful
	 */
	public boolean protocolClose() {
		return false;
	}
	
	/**
	 * Responses on
	 * 
	 * These commands control the ELM327Õs automatic receive (and display) of the messages returned 
	 * by the vehicle. If responses have been turned off, the IC will not wait for a reply from 
	 * the vehicle after sending a request, and will return immediately to wait for the next 
	 * RS232 command (the ELM327 does not print anything to say that the send was successful, 
	 * but you will see a message if it was not).
	 * 
	 * R0 may be useful to send commands blindly when using the IC for a non-OBD network 
	 * application, or when simulating an ECU in a learning environment. It is not recommended 
	 * that this option used for normal OBD communications, however, as the vehicle may have 
	 * difficulty if it is expecting an acknowledgment and never receives one.
	 * 
	 * An R0 setting will always override any Ônumber of responses digitÕ that is provided with 
	 * an OBD request. The default setting is R1, or responses on.
	 *
	 * @return true, if successful
	 */
	public boolean responsesOn() {
		return false;
	}
	
	/**
	 * Responses off
	 * 
	 * These commands control the ELM327Õs automatic receive (and display) of the messages returned 
	 * by the vehicle. If responses have been turned off, the IC will not wait for a reply from 
	 * the vehicle after sending a request, and will return immediately to wait for the next 
	 * RS232 command (the ELM327 does not print anything to say that the send was successful, 
	 * but you will see a message if it was not).
	 * 
	 * R0 may be useful to send commands blindly when using the IC for a non-OBD network 
	 * application, or when simulating an ECU in a learning environment. It is not recommended 
	 * that this option used for normal OBD communications, however, as the vehicle may have 
	 * difficulty if it is expecting an acknowledgment and never receives one.
	 * 
	 * An R0 setting will always override any Ônumber of responses digitÕ that is provided with 
	 * an OBD request. The default setting is R1, or responses on.
	 *
	 * @return true, if successful
	 */
	public boolean responsesOff() {
		return false;
	}
	
	/**
	 * Printing of Spaces on
	 * 
	 * These commands control whether or not space characters are inserted in the ECU response.
	 * 
	 * The ELM327 normally reports ECU responses as a series of hex characters that are separated 
	 * by space characters (to improve readability), but messages can be transferred much 
	 * more quickly if every third byte (the space) is removed. While this makes the message 
	 * less readable for humans, it can provide significant improvements for computer processing 
	 * of the data. By default, spaces are on (S1), and space characters are inserted in every 
	 * response.
	 *
	 * @return true, if successful
	 */
	public boolean printSpacesOn() {
		return false;
	}
	
	/**
	 * Printing of Spaces off
	 * 
	 * These commands control whether or not space characters are inserted in the ECU response.
	 * 
	 * The ELM327 normally reports ECU responses as a series of hex characters that are separated 
	 * by space characters (to improve readability), but messages can be transferred much 
	 * more quickly if every third byte (the space) is removed. While this makes the message 
	 * less readable for humans, it can provide significant improvements for computer processing 
	 * of the data. By default, spaces are on (S1), and space characters are inserted in every 
	 * response.
	 *
	 * @return true, if successful
	 */
	public boolean printSpacesOff() {
		return false;
	}
	
	/**
	 * Set the Header to 00 0x yz
	 * 
	 * Entering CAN 11 bit ID words (headers) normally requires that extra leading zeros be 
	 * added (eg. AT SH 00 07 DF), but this command simplifies doing so. The AT SH xyz command 
	 * accepts a three digit argument, takes only the right-most 11 bits from that, adds leading zeros, 
	 * and stores the result in the header storage locations for you. As an example, AT SH 7DF is 
	 * a valid command, and is quite useful for working with 11 bit CAN systems. It actually results 
	 * in the header bytes being stored internally as 00 07 DF.
	 *
	 * @return true, if successful
	 */
	public boolean setHeader() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * Set Protocol to h 
	 * 
	 * This command is used to set the ELM327 for operation using the protocol specified by 'h', and 
	 * to also save it as the new default. Note that the protocol will be saved no matter what the AT 
	 * M0/M1 setting is.
	 * 
	 * The ELM327 supports 12 different protocols (two can be user-defined). They are:
	 * 0 - Automatic 
	 * 1 - SAE J1850 PWM (41.6 kbaud) 
	 * 2 - SAE J1850 VPW (10.4 kbaud)
	 * 3 - ISO 9141-2 (5 baud init, 10.4 kbaud) 
	 * 4 - ISO 14230-4 KWP (5 baud init, 10.4 kbaud) 
	 * 5 - ISO 14230-4 KWP (fast init, 10.4 kbaud) 
	 * 6 - ISO 15765-4 CAN (11 bit ID, 500 kbaud) 
	 * 7 - ISO 15765-4 CAN (29 bit ID, 500 kbaud) 
	 * 8 - ISO 15765-4 CAN (11 bit ID, 250 kbaud) 
	 * 9 - ISO 15765-4 CAN (29 bit ID, 250 kbaud) 
	 * A - SAE J1939 CAN (29 bit ID, 250* kbaud) 
	 * B - USER1 CAN (11* bit ID, 125* kbaud) 
	 * C - USER2 CAN (11* bit ID, 50* kbaud)
	 * 
	 * The first protocol shown (0) is a convenient way of telling the ELM327 that the vehicleÕs protocol 
	 * is not known, and that it should perform a search. It causes the ELM327 to try all protocols 
	 * if necessary, looking for one that can be initiated correctly. When a valid protocol is found, and 
	 * the memory function is enabled, that protocol will then be remembered, and will become the new 
	 * default setting. When saved like this, the automatic mode searching will still be enabled, and 
	 * the next time the ELM327 fails to connect to the saved protocol, it will again search all protocols 
	 * for another valid one. Note that some vehicles respond to more than one protocol - during a search, 
	 * you may see more than one type of response.
	 * 
	 * ELM327 users often use the AT SP 0 command to reset the search protocol before starting (or restarting) 
	 * a connection. This works well, but as with any Set Protocol command, it involves a write to EEPROM, 
	 * and an unnecessary delay (of about 30 msec) while the write occurs. Beginning with v1.3 of the ELM327, 
	 * a write to EEPROM will no longer be performed for an SP 0 (or an SP A0, or SP 0A) command, but the 
	 * command will still reset the protocol to 0 for you. If you really want to change what is stored in 
	 * the internal EEPROM, you must now use the new AT SP 00 command.
	 * 
	 * If another protocol (other than 0) is selected with this command (eg. AT SP 3), that protocol will 
	 * become the default, and will be the only protocol used by the ELM327. Failure to initiate a connection 
	 * in this situation will result in a response such as ÔBUS INIT: ...ERRORÕ, and no other protocols will 
	 * be attempted. This is a useful setting if you know that your vehicle(s) only require the one protocol, 
	 * but also one that can cause a lot of problems if you do not understand it .
	 *
	 * @return true, if successful
	 */
	public boolean setProtocol() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * Set Protocol to Auto, h
	 * 
	 * This variation of the SP command allows you to choose a starting (default) protocol, while still 
	 * retaining the ability to automatically search for a valid protocol on a failure to connect. For 
	 * example, if your vehicle is ISO 9141-2, but you want to occasionally use the ELM327 circuit on other 
	 * vehicles, you might use the AT SP A3 command, so that the first protocol tried will then be yours 
	 * (3), but it will also automatically search for other protocols. Don't forget to disable the memory 
	 * function if doing this, or each new protocol detected will become your new default.
	 * 
	 * SP Ah will save the protocol information even if the memory option is off, except for SP A0 and 
	 * SP 0A which as of v1.3 no longer performs the write (if you need to change the EEPROM, use SP 00). 
	 * Note that the ÔAÕ can come before or after the h, so AT SP A3 can also be entered as AT SP 3A.
	 *
	 * @return true, if successful
	 */
	public boolean setProtocolAuto() {
		//Send the command
		this.serialInterface.sendATCommand("SP0");
		
		//Check if we got what we expected in return
		if(this.serialInterface.recieveResponse().contains("OK")) {
			log.info("The device completed auto set protocol function");
			return true;
		}
		else {
			log.error("The device never responded after restart request.");
			return false;
		}
	}
	
	/**
	 * Set the Receive address to hh
	 * 
	 * Depending on the application, users may wish to manually set the address to which the ELM327 will 
	 * respond. Issuing this command will turn off the AR mode, and force the IC to only accept responses 
	 * addressed to hh. Use caution with this setting, as depending on what you set it to, you may accept 
	 * a message that was actually meant for another module, possibly sending an IFR when you should not. 
	 * To turn off the SR filtering, simply send AT AR.
	 * 
	 * This command has limited use with CAN, as it only monitors one byte of the ID bits, and that is not 
	 * likely selective enough for most CAN applications (the CRA command may be a better choice). Also, the 
	 * command has no effect on the addresses used by the J1939 protocols, as the J1939 routines set their 
	 * own receive addresses based on the ID bit (header) values.
	 * 
	 * This SR command is exactly the same as the RA command, and can be used interchangeably with it. Note 
	 * that CAN Extended Addressing does not use this value - it uses the one set by the AT TA command.
	 *
	 * @return true, if successful
	 */
	public boolean setReceiveAddress() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * Set Timeout to hh
	 * 
	 * After sending a request, the ELM327 waits a preset time for a response before it can declare that 
	 * there was ÔNO DATAÕ received from the vehicle. The same timer setting is also used after a response 
	 * has been received, while waiting to see if more are coming. The AT ST command allows this timer to 
	 * be adjusted, in increments of 4 msec (or 20 msec if in the J1939 protocol, with JTM5 selected).
	 * 
	 * When Adaptive Timing is enabled, the AT ST time sets the maximum time that is to be allowed, even 
	 * if the adaptive algorithm determines that the setting should be longer. In most circumstances, it 
	 * is best to simply leave the AT ST time at the default setting, and let the adaptive timing algorithm 
	 * determine what to use for the timeout.
	 * 
	 * The ST timer is set to 32 by default (giving a time of approximately 200 msec), but this value can 
	 * be adjusted by changing PP 03. Note that a value of 00 does not result in a time of 0 msec Ð it will 
	 * restore the timer to the default value.
	 *
	 * @return true, if successful
	 */
	public boolean setTimeout() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * Set the Tester Address to hh 
	 * 
	 * This command is used to change the current tester (ie. scan tool) address that is used in the headers, 
	 * periodic messages, filters, etc. The ELM327 normally uses the value that is stored in PP 06 for this, 
	 * but the TA command allows you to temporarily override that value.
	 * 
	 * Sending AT TA will affect all protocols, including J1939. This provides a convenient means to change the 
	 * J1939 address from the default value of F9, without affecting other settings.
	 * 
	 * Although this command may appear to work Ôon the flyÕ, it is not recommended that you try to change this 
	 * address after a protocol is active, as the results may be unpredictable.
	 *
	 * @return true, if successful
	 */
	public boolean setTesterAddress() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * Try Protocol h
	 * 
	 * This command is identical to the SP command, except that the protocol that you select is not immediately 
	 * saved in internal memory, so does not change the default setting. Note that if the memory function is 
	 * enabled (AT M1), and this new protocol that you are trying is found to be valid, that protocol will then 
	 * be stored in memory as the new default.
	 *
	 * @return true, if successful
	 */
	public boolean tryProtocol() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * Try Protocol h with Auto
	 * 
	 * This command is very similar to the AT TP command above, except that if the protocol that is tried should 
	 * fail to initialize, the ELM327 will then automatically sequence through the other protocols, attempting to 
	 * connect to one of them.
	 *
	 * @return true, if successful
	 */
	public boolean tryProtocolAuto() {
		//TODO This needs to be implemented
		return false;
	}
	

}
