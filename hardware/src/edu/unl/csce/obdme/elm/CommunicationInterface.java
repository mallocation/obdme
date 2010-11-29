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
	
	/** The ASCI i_ ne w_ line. */
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
	
	public void establishConnection() {

	}

}
