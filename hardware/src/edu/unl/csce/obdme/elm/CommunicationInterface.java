package edu.unl.csce.obdme.elm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	private byte ASCII_NEW_LINE = 0x0a;
	
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
	
	public void establishConnection() throws IOException {
		
		if (inputStream.available() > 0) {

			log.info("Data recieved on input stream.");

			//Initialize the string buffer and received char variable
			StringBuffer recievedData = new StringBuffer();
			byte recievedByte = 0;

			//While there are bytes available in the buffer, read them until
			//a return character in encountered (end of command)
			do {
				recievedByte = (byte) inputStream.read();
				recievedData.append((char)recievedByte);
			} while (inputStream.available() > 0);

			//Trim the input data (remove the carriage return and the new line return)
			String receivedString = recievedData.substring(0, recievedData.length()-2);
			log.info("Data recieved: " + receivedString);

			//Check if the received data was an ACK command from the device
			
		}

	}
	
	public void sendString(String command) throws Exception{

		byte[] byteArray = new String(command + "\r").getBytes("ASCII");
		
		outputStream.write(byteArray);
		
		StringBuffer recievedData = new StringBuffer();
		byte recievedByte = 0;
		
		while (inputStream.available() <= 0) {
			
		}
				
		do {
			recievedByte = (byte) inputStream.read();
			recievedData.append((char)recievedByte);
		} while (inputStream.available() > 0);

		//Trim the input data (remove the carriage return and the new line return)
		String receivedString = recievedData.substring(0, recievedData.length()-2);
		log.info("Data recieved: " + receivedString);

		//Send the byte command
		log.info("Sending byte array: " + byteArrayToHexString(byteArray));
		outputStream.write(byteArray);

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
