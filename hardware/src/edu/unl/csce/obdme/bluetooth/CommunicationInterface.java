package edu.unl.csce.obdme.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class CommunicationInterface.
 */
public class CommunicationInterface {
	
	/** The Baud Rate. */
	private int BAUD_RATE = 38400;
	
	/** The echo command. */
	private boolean echoCommand = true;

	/** The Constant DEVICE_IDENTIFIER. */
	private static final String DEVICE_IDENTIFIER = "ELM327 v1.4";
	
	/** The Constant ASCII_COMMAND_PROMPT. */
	private static final char ASCII_COMMAND_PROMPT = '>';
	
	/** The last command. */
	private String lastCommand = "";
	
	/** The logger. */
	private Logger log;
	
	/** The serial port. */
	private SerialPort serialPort;
	
	/** The input stream. */
	private InputStream inputStream;

	/** The output stream. */
	private OutputStream outputStream;

	/**
	 * Instantiates a new communication interface.
	 *
	 * @param portName the port name
	 */
	public CommunicationInterface(String portName) {

		log = Logger.getLogger(CommunicationInterface.class);

		try {
			//Get the communication port identifier
			log.info("Getting port identifier for: " + portName);
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);

			//Set up the serial port
			log.info("Opening port");
			this.serialPort = (SerialPort)portId.open("serial talk", 4000);

			//Set up the input stream
			this.inputStream = this.serialPort.getInputStream();

			//Set up the output stream
			this.outputStream = this.serialPort.getOutputStream();

			//Set the serial port parameters (these are defaults)
			log.info("Setting port parameters");
			log.info("Baud Rate: " + BAUD_RATE);
			serialPort.setSerialPortParams(BAUD_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			
			while (inputStream.available() > 0) {
				inputStream.read();
			}
			outputStream.flush();
			
		} catch (NoSuchPortException nspe) {
			log.error("NoSuchPortException", nspe);
		} catch (PortInUseException piue) {
			log.error("PortInUseException", piue);
		} catch (IOException ioe) {
			log.error("IOException", ioe);
		} catch (UnsupportedCommOperationException ucoe) {
			log.error("UnsupportedCommOperationException", ucoe);
		}
		
		
	}
	
	/**
	 * Initialize connection to the ELM327 Device
	 *
	 * @throws Exception the exception
	 */
	public void initializeConnection() throws Exception {

		log.debug("Initializing Connection with the ELM327 Device.");
		
		//Flush the input stream
		log.info("Flushing the input stream of unread bytes.");
		while (this.inputStream.available() > 0) {
			this.inputStream.read();
		}
		
		//Restart the ELM327 Device
		log.debug("Restarting the ELM Device");
		try {
			byte[] commandByteArray = new String("ATZ\r").getBytes("ASCII");
			this.lastCommand = "ATZ\r";
			log.info("Sending Bytes: " + byteArrayToHexString(commandByteArray));
			this.outputStream.write(commandByteArray);
			this.outputStream.flush();
		} catch (UnsupportedEncodingException uee) {
			log.error("Error restarting the ELM327 Device", uee);
		} catch (IOException ioe) {
			log.error("Error restarting the ELM327 Device", ioe);
		}
		
		//Check that we got a response after the device restart
		if (!receiveResponse().contains(DEVICE_IDENTIFIER)) {
			log.error("The connectecd device is not an ELM327 v1.4 device.");
			throw new Exception("The connectecd device is not an ELM327 v1.4 device.");
		}
		else {
			log.debug("Connection to the ELM327 successfully established.");
		}
	
	}
	
	/**
	 * Send an AT command to the device.  This is done through the ELM command objects.
	 *
	 * @param command the command
	 * @return the string
	 */
	public String sendATCommand(String command) {
		
		log.info("Sending AT command AT" + command);
		String commandString = "AT" + command + "\r";

		try {
			
			//Form the byte array
			byte[] commandByteArray = new String(commandString).getBytes("ASCII");
			log.info("Sending bytes: " + byteArrayToHexString(commandByteArray));
			
			//Send the bytes and flush the output stream
			this.outputStream.write(commandByteArray);
			this.outputStream.flush();
			
		} catch (UnsupportedEncodingException uee) {
			log.error("Error sending AT" + command + "command.", uee);
		} catch (IOException ioe) {
			log.error("Error sending AT" + command + "command.", ioe);
		}
		
		//Save this as the last command executed
		this.lastCommand = commandString;
		
		//Return the response
		return receiveResponse();

	}
	
	/**
	 * Send OBD command to the device.  This is done through the OBD Communications objects.
	 *
	 * @param command the command
	 * @return the string
	 */
	public String sendOBDCommand(String command) {

		log.debug("Sending OBD command " + command);
		
		try {
			
			//Send the bytes and flush the output stream
			byte[] commandByteArray = new String(command + "\r").getBytes("ASCII");
			log.info("Sending bytes: " + byteArrayToHexString(commandByteArray));
			
			//Flush the output stream
			this.outputStream.write(commandByteArray);
			this.outputStream.flush();
			
		} catch (UnsupportedEncodingException uee) {
			log.error("Error sending " + command + " OBD command.", uee);
		} catch (IOException ioe) {
			log.error("Error sending " + command + " OBD command.", ioe);
		}
		
		//Set the last command and return the response
		this.lastCommand = command + "\r";
		return receiveResponse();

	}
	
	/**
	 * Receive response from the device.  This should only be executed when were are expected a response from the device.
	 * This thread blocks until a command prompt character is received.  The command prompt character indicates the end of the 
	 * transmission.
	 *
	 * @return the string
	 */
	public String receiveResponse() {
		
		log.debug("Waiting for response from the device.");
		
		//Initialize the buffers
		StringBuffer recievedData = new StringBuffer();
		char currentChar = ' ';

		try {
			do {
				
				//If there are bytes available, read them
				if (this.inputStream.available() > 0) {
					
					//Append the read byte to the buffer
					currentChar = (char)this.inputStream.read();
					recievedData.append(currentChar);
				}
			} while (currentChar != ASCII_COMMAND_PROMPT); //Continue until we reach a prompt character

		} catch (IOException ioe) {
			log.error("Exception when receiving data from the device:", ioe);
		}

		String recievedString = recievedData.toString();
		
		//Remove the echoed command if ECHO_COMMAND is true
		if (echoCommand) {
			recievedString = recievedString.replace(this.lastCommand, "");
		}
		
		//Remove extra characters and trim the string
		recievedString = recievedString.replace("\r", "");
		recievedString = recievedString.replace(">", "");
		recievedString = recievedString.trim();
	
		log.info("Received string from device: " + recievedString);
		
		//Return the received data
		return recievedString;
	
	}
	
	/**
	 * Converts a byte array to hex string.
	 *
	 * @param b the b
	 * @return the string
	 */
	public static String byteArrayToHexString(byte[] byteArray) {
		
		//Initialize the string buffer
		StringBuffer stringBuffer = new StringBuffer(byteArray.length * 2);
		
		//Start constructing the byte array
		stringBuffer.append("[ ");
		
		//For all the bytes in the array
		for (int i = 0; i < byteArray.length; i++) {
			
			//Convert the byte to an integer
			int v = byteArray[i] & 0xff;
			
			//Left shift
			if (v < 16) {
				stringBuffer.append('0');
			}
			
			//Add the hex string representation of the byte 
			stringBuffer.append("0x" + Integer.toHexString(v).toUpperCase() + " ");
		}
		
		//Close the byte array string
		stringBuffer.append("]");
		
		//Convert the string buffer to a string a return it
		return stringBuffer.toString();
	}

	/**
	 * Sets the echo command.
	 *
	 * @param echoCommand the echoCommand to set
	 */
	public void setEchoCommand(boolean echoCommand) {
		this.echoCommand = echoCommand;
	}

	/**
	 * Checks if is echo command is enabled in the communication protocol.
	 *
	 * @return the echoCommand
	 */
	public boolean isEchoCommand() {
		return echoCommand;
	}

}
