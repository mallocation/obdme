package edu.unl.csce.obdme.elm;

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

/**
 * The Class CommunicationInterface.
 */
public class CommunicationInterface {
	
	/** The Baud Rate. */
	private int BAUD_RATE = 38400;
	
	/** The ASCII new line. */
	private static final byte ASCII_NEW_LINE = 0x0a;
	
	/** The ASCII carriage return. */
	private static final byte ASCII_CARRIAGE_RETURN = 0x0d;
	
	/** The ASCII carriage return. */
	private boolean ECHO_COMMAND = true;
	
	/** The ASCII carriage return. */
	private static final char ASCII_COMMAND_PROMPT = '>';
	
	private String RESPONSE_OK = "OK\r";
	
	private String lastCommand = "";
	
	/** The log. */
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
	 * @throws Exception the exception
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
	
	public void initializeConnection() throws Exception {

		log.info("Initializing Connection with the ELM");
		
		log.info("Flushing the input stream of unread bytes.");
		while (this.inputStream.available() > 0) {
			this.inputStream.read();
		}
		
		log.info("Restarting the ELM Device");
		try {
			byte[] commandByteArray = new String("ATZ\r").getBytes("ASCII");
			this.lastCommand = "ATZ\r";
			log.info("Sending Bytes: " + byteArrayToHexString(commandByteArray));
			this.outputStream.write(commandByteArray);
			this.outputStream.flush();
		} catch (UnsupportedEncodingException uee) {
			log.error("UnsupportedEncodingException", uee);
		} catch (IOException ioe) {
			log.error("IOException", ioe);
		}
		
		//Check that we got a response after the device restart
		if (!recieveResponse().contains("ELM327 v1.4")) {
			log.error("The connectecd device is not an ELM device.");
			throw new Exception("The connectecd device is not an ELM device.");
		}
		else {
			log.info("Connection Successfully Established.");
		}
	
	}
	
	public String recieveResponse() {
		
		StringBuffer recievedData = new StringBuffer();

		log.info("Waiting for response from the device.");
		
		char currentChar = ' ';

		try {
			do {
				if (this.inputStream.available() > 0) {
					//Cast a byte from the buffer as a char and append it to the received string
					currentChar = (char)this.inputStream.read();
					recievedData.append(currentChar);
				}
			} while (currentChar != ASCII_COMMAND_PROMPT);

		} catch (IOException ioe) {
			log.error("IOException when receiving data from the device:", ioe);
		}

		String recievedString = recievedData.toString();
		
		if (ECHO_COMMAND) {
			recievedString = recievedString.replace(this.lastCommand, "");
		}
		
		recievedString = recievedString.replace("\r", "");
		recievedString = recievedString.replace(">", "");
		recievedString = recievedString.trim();
	
		log.info("Received string from device:" + recievedString);
		
		//Return the data without a carriage return
		return recievedString;
	
	}
	
	public void sendATCommand(String command) {

		log.info("Sending Config Command: " + command);
		
		try {
			byte[] commandByteArray = new String("AT" + command + "\r").getBytes("ASCII");
			this.lastCommand = "AT" + command + "\r";
			log.info("Sending Bytes: " + byteArrayToHexString(commandByteArray));
			this.outputStream.write(commandByteArray);
			this.outputStream.flush();
		} catch (UnsupportedEncodingException uee) {
			log.error("UnsupportedEncodingException", uee);
		} catch (IOException ioe) {
			log.error("IOException", ioe);
		}

	}
	
	public void sendOBDCommand(String command) {

		log.info("Sending Config Command: " + command);
		
		try {
			byte[] commandByteArray = new String(command + "\r").getBytes("ASCII");
			this.lastCommand = command + "\r";
			log.info("Sending Bytes: " + byteArrayToHexString(commandByteArray));
			this.outputStream.write(commandByteArray);
			//this.outputStream.flush();
		} catch (UnsupportedEncodingException uee) {
			log.error("UnsupportedEncodingException", uee);
		} catch (IOException ioe) {
			log.error("IOException", ioe);
		}

	}
	
	public static String byteArrayToHexString(byte[] b) {
		
		//Initialize the string buffer
		StringBuffer sb = new StringBuffer(b.length * 2);
		
		//Start constructing the byte array
		sb.append("[ ");
		
		//For all the bytes in the array
		for (int i = 0; i < b.length; i++) {
			
			//Convert the byte to an integer
			int v = b[i] & 0xff;
			
			//Left shift
			if (v < 16) {
				sb.append('0');
			}
			
			//Add the hex string representation of the byte 
			sb.append("0x" + Integer.toHexString(v).toUpperCase() + " ");
		}
		
		//Close the byte array string
		sb.append("]");
		
		//Convert the string buffer to a string a return it
		return sb.toString();
	}

}
