package edu.unl.csce.obdme.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ElmSimulator {
	
	private static final String EMULATOR_PORT_NAME = "/dev/tty.OBDMe_EMU";
	
	private CommPortIdentifier portID;
	
	private Logger log;
	
	public ElmSimulator(String serialPortName) {
		log = Logger.getLogger(ElmSimulator.class);
				
		try {
			portID = CommPortIdentifier.getPortIdentifier(serialPortName);
			
			if (portID.isCurrentlyOwned()) {
				log.error("Serial port " + portID.getName() + " is in use.");
			} else {
				CommPort commPort = portID.open(ElmSimulator.EMULATOR_PORT_NAME, 2000);
				
				if (commPort instanceof SerialPort) {
					SerialPort serPort = (SerialPort)commPort;
					serPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					
					InputStream in = serPort.getInputStream();
					OutputStream out = serPort.getOutputStream();
					
					serPort.addEventListener(new SerialReader(in, out));
					serPort.notifyOnDataAvailable(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	private static class SerialReader implements SerialPortEventListener {
		private InputStream in;
		private OutputStream out;
		private byte[] buffer = new byte[1024];
		
		public SerialReader(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public void serialEvent(SerialPortEvent arg0) {
			int data;			
			try {
				int len = 0;
				while ( (data = in.read()) > -1 ) {
					if (data == '\n') {
						break;
					}
					buffer[len++] = (byte)data;					
				}
				this.respond(new String(buffer,0,len));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
		}
		
		private void respond(String request) {
			String response = "SHIT";
			try {
				byte[] bytes = response.getBytes("ASCII");
				this.out.write(bytes,0, bytes.length);
			} catch (Exception e) {
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		ElmSimulator sim = new ElmSimulator(EMULATOR_PORT_NAME);		
	}

}
