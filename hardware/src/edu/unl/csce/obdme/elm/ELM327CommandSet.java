package edu.unl.csce.obdme.elm;

import org.apache.log4j.Logger;

import edu.unl.csce.obdme.bluetooth.CommunicationInterface;

/**
 * The Class ELM327CommandSet.
 */
public class ELM327CommandSet {
	
	/** The serial interface to the ELM327. */
	protected CommunicationInterface serialInterface;
	
	/** The Log4J logger object. */
	private Logger log;

	/**
	 * Instantiates a new ELM327 command set.
	 *
	 * @param serialInterface the serial interface
	 */
	public ELM327CommandSet(CommunicationInterface serialInterface) {
		
		//Save a reference to the serial interface object
		this.serialInterface = serialInterface;
		
		//Initialize the logger for this instance
		log = Logger.getLogger(ELM327CommandSet.class);
	}
	
	/**
	 * Repeat last command.
	 *
	 * @return true, if successful
	 */
	public boolean repeatLastCommand() {
		return false;
	}
	
	/**
	 * This command is used to change the RS232 baud rate divisor to the hex value provided by 
	 * hh, while under computer control. It is not intended for casual experimenting - if 
	 * you wish to change the baud rate from a terminal program, you should use PP 0C.
	 * 
	 * Since some interface circuits are not able to operate at high data rates, the BRD 
	 * command uses a sequence of sends and receives to test the interface, with any failure 
	 * resulting in a fallback to the previous baud rate. This allows several baud rates to 
	 * be tested and a reliable one chosen for the communications. The entire process is 
	 * described in detail in the ‘Using Higher RS232 Baud Rates’ section, on pages 59 and 60.
	 * 
	 * If successful, the actual baud rate (in kbps) will be 4000 divided by the divisor (hh).
	 *
	 * @return true, if successful
	 */
	public boolean tryBaudRateDivisor() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * This command allows the timeout used for the Baud Rate handshake (ie. AT BRD) to be 
	 * varied. The time delay is given by hh x 5.0 msec, where hh is a hexadecimal value. 
	 * The default value for this setting is 0F, providing 75 msec. Note that a value of 00 
	 * does not result in 0 msec - it provides the maximum time of 256 x 5.0 msec, or 1.28 seconds.
	 *
	 * @return true, if successful
	 */
	public boolean setBaudRateTimeout() {
		//TODO This needs to be implemented
		return false;
	}
	
	/**
	 * This command is used to set the options to their default (or factory) settings, 
	 * as when power is first applied. The last stored protocol will be retrieved from memory, 
	 * and will become the current setting (possibly closing other protocols that are active). 
	 * Any settings that the user had made for custom headers, filters, or masks will be restored 
	 * to their default values, and all timer settings will also be restored to their defaults.
	 *
	 * @return true, if successful
	 */
	public boolean allToDefaults() {
		if(this.serialInterface.sendATCommand("D").contains("OK")) {
			log.info("The device was sucessfully set to defaults.");
			return true;
		}
		else {
			log.error("The device never responded after the defaults request.");
			return false;
		}
	}
	
	/**
	 * These commands control whether or not the characters received on the RS232 port are echoed 
	 * (retransmitted) back to the host computer. Character echo can be used to confirm that the 
	 * characters sent to the ELM327 were received correctly. The default is E1 (or echo on).
	 *
	 * @return true, if successful
	 */
	public boolean echoOff() {
		if(this.serialInterface.sendATCommand("E0").contains("OK")) {
			serialInterface.setEchoCommand(false);
			log.info("The command echo option was sucessfully disabled.");
			return true;
		}
		else {
			log.error("The device never responded after the echo off request.");
			return false;
		}
	}
	
	/**
	 * These commands control whether or not the characters received on the RS232 port are echoed 
	 * (retransmitted) back to the host computer. Character echo can be used to confirm that the 
	 * characters sent to the ELM327 were received correctly. The default is E1 (or echo on).
	 *
	 * @return true, if successful
	 */
	public boolean echoOn() {
		if(this.serialInterface.sendATCommand("E1").contains("OK")) {
			serialInterface.setEchoCommand(true);
			log.info("The command echo option was sucessfully enabled.");
			return true;
		}
		else {
			log.error("The device never responded after the echo on request.");
			return false;
		}
	}
	
	/**
	 * There are certain events which may change how the ELM327 responds from that time onwards. 
	 * One of these is the occurrence of a fatal CAN error (ERR94), which blocks subsequent searching 
	 * through CAN protocols if PP 2A bit 5 is ‘1’. Normally, an event such as this will affect 
	 * all searches until the next power off and on, but it can be ‘forgotten’ using software, 
	 * with the AT FE command.
	 * 
	 * Another example is an ‘LV RESET’ event which will prevent searches through CAN protocols 
	 * if PP 2A bit 4 is ‘1’. It may also be forgotten with the AT FE command.
	 *
	 * @return true, if successful
	 */
	public boolean forgetEvents() {
		if(this.serialInterface.sendATCommand("FE").contains("OK")) {
			log.info("The forget events command option was sucessfully executed.");
			return true;
		}
		else {
			log.error("The device never responded after the forget events request.");
			return false;
		}
	}
	
	/**
	 * Issuing this command causes the chip to identify itself, by printing the startup product ID 
	 * string (currently ‘ELM327 v1.4b’). Software can use this to determine exactly which integrated 
	 * circuit it is talking to, without having to reset the IC.
	 *
	 * @return the string
	 */
	public String printVersionID() {
		
		String versionID = this.serialInterface.sendATCommand("I");
		
		if(!versionID.isEmpty()) {
			log.info("The version id request was sucessfully executed.");
			return versionID;
		}
		else {
			log.error("The device never responded after the version id request.");
			return null;
		}
	}
	
	/**
	 * This command causes the ELM327 to shut off all but ‘essential services’ in order to reduce
	 *  the power consumption to a minimum. The ELM327 will respond with an ‘OK’ (but no carriage 
	 *  return) and then, one second later, will change the state of the PwrCtrl output (pin 16) 
	 *  and will enter the low power (standby) mode. The IC can be brought back to normal operation 
	 *  through a character received at the RS232 input or a rising edge at the IgnMon (pin 15) 
	 *  input, in addition to the usual methods of resetting the IC (power off then on, a low on 
	 *  pin 1, or a brownout). See the Power Control section (page 62) for more information.
	 *
	 * @return true, if successful
	 */
	public boolean lowPowerMode() {
		if(this.serialInterface.sendATCommand("LP").contains("OK")) {
			log.info("The low power mode option was sucessfully enabled.");
			return true;
		}
		else {
			log.error("The device never responded after the low power mode request.");
			return false;
		}
	}
	
	/**
	 * The ELM327 has internal ‘non-volatile’ memory that is capable of remembering the last protocol 
	 * used, even after the power is turned off. This can be convenient if the IC is often used for 
	 * one particular protocol, as that will be the first one attempted when next powered on. To 
	 * enable this memory function, it is necessary to either use an AT command to select the M1 option, 
	 * or to have chosen ‘memory on’ as the default power on mode (by connecting pin 5 of the ELM327 
	 * to a high logic level).
	 * 
	 * When the memory function is enabled, each time that the ELM327 finds a valid OBD protocol, 
	 * that protocol will be memorized (stored) and will become the new default. If the memory 
	 * function is not enabled, protocols found during a session will not be memorized, and the 
	 * ELM327 will always start at power up using the same (last saved) protocol.
	 * 
	 * If the ELM327 is to be used in an environment where the protocol is constantly changing, 
	 * it would likely be best to turn the memory function off, and issue an AT SP 0 command once. 
	 * The SP 0 command tells the ELM327 to start in an 'Automatic' protocol search mode, which is 
	 * the most useful for an unknown environment. ICs come from the factory set to this mode. If, 
	 * however, you have only one vehicle that you regularly connect to, storing that vehicle’s 
	 * protocol as the default would make the most sense.
	 * 
	 * The default setting for the memory function is determined by the voltage level at pin 5 
	 * during power up (or system reset). If it is connected to a high level (VDD), then the 
	 * memory function will be on by default. If pin 5 is connected to a low level, the memory 
	 * saving will be off by default.
	 *
	 * @return true, if successful
	 */
	public boolean memoryOn() {
		return false;
	}
	
	/**
	 * The ELM327 has internal ‘non-volatile’ memory that is capable of remembering the last protocol 
	 * used, even after the power is turned off. This can be convenient if the IC is often used for 
	 * one particular protocol, as that will be the first one attempted when next powered on. To 
	 * enable this memory function, it is necessary to either use an AT command to select the M1 option, 
	 * or to have chosen ‘memory on’ as the default power on mode (by connecting pin 5 of the ELM327 
	 * to a high logic level).
	 * 
	 * When the memory function is enabled, each time that the ELM327 finds a valid OBD protocol, 
	 * that protocol will be memorized (stored) and will become the new default. If the memory 
	 * function is not enabled, protocols found during a session will not be memorized, and the 
	 * ELM327 will always start at power up using the same (last saved) protocol.
	 * 
	 * If the ELM327 is to be used in an environment where the protocol is constantly changing, 
	 * it would likely be best to turn the memory function off, and issue an AT SP 0 command once. 
	 * The SP 0 command tells the ELM327 to start in an 'Automatic' protocol search mode, which is 
	 * the most useful for an unknown environment. ICs come from the factory set to this mode. If, 
	 * however, you have only one vehicle that you regularly connect to, storing that vehicle’s 
	 * protocol as the default would make the most sense.
	 * 
	 * The default setting for the memory function is determined by the voltage level at pin 5 
	 * during power up (or system reset). If it is connected to a high level (VDD), then the 
	 * memory function will be on by default. If pin 5 is connected to a low level, the memory 
	 * saving will be off by default.
	 *
	 * @return true, if successful
	 */
	public boolean memoryOff() {
		return false;
	}
	
	/**
	 * The byte value stored with the SD command is retrieved with this command. There is 
	 * only one memory location, so no address is required.
	 *
	 * @return the string
	 */
	public String readStoredData() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * The ELM327 is able to save one byte of information for you in a special nonvolatile 
	 * memory location, which is able to retain its contents even if the power is turned off. 
	 * Simply provide the byte to be stored, then retrieve it later with the read data (AT RD) 
	 * command. This location is ideal for storing user preferences, unit ids, occurrence counts, 
	 * or other information.
	 *
	 * @return true, if successful
	 */
	public boolean saveDataByte() {
		//TODO This needs to be implemented
		return true;
	}
	
	/**
	 * This command causes the ELM327 to perform a complete reset which is very similar to the AT Z command, 
	 * but does not include the power on LED test. Users may find this a convenient way to quickly 
	 * ‘start over’ without having the extra delay of the AT Z command.
	 * 
	 * If using variable RS232 baud rates (ie AT BRD commands), it is preferred that you reset the 
	 * IC using this command rather than AT Z, as AT WS will not affect the chosen RS232 baud rate, 
	 * and AT Z will.
	 *
	 * @return true, if successful
	 */
	public boolean warmStart() {
		return false;
	}
	
	/**
	 * This command causes the chip to perform a complete reset as if power were cycled off and then 
	 * on again. All settings are returned to their default values, and the chip will be put into the 
	 * idle state, waiting for characters on the RS232 bus. Any baud rate that was set with the AT BRD 
	 * command will be lost, and the ELM327 will return to the default baud rate setting.
	 *
	 * @return true, if successful
	 */
	public boolean restartAll() {
		
		if(this.serialInterface.sendATCommand("Z").contains("ELM v1.4")) {
			log.info("The device was successfully hard restarted.");
			return true;
		}
		else {
			log.error("The device never responded after hard restart request.");
			return false;
		}
		
	}
	
	/**
	 * This command displays the device description string. The default text is ‘OBDII to RS232 Interpreter’.
	 *
	 * @return the device description
	 */
	public String getDeviceDescription() {
		//TODO This needs to be implemented
		return null;
	}
	
	/**
	 * A device identifier string that was recorded with the @3 command is displayed with the @2 command. 
	 * All 12 characters and a terminating carriage return will be sent in response, if they have been 
	 * defined. If no identifier has been set, the @2 command returns an error response (‘?’). The identifier 
	 * may be useful for storing product codes, production dates, serial numbers, or other such codes.
	 *
	 * @return the device identifier
	 */
	public String getDeviceIdentifier() {
		//TODO This needs to be implemented
		return null;
	}

}
