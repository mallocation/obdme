package edu.unl.csce.obdme.terminal;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;


public class HardwareTerminal extends JFrame implements SerialPortEventListener {
	
	private static final long serialVersionUID = 1L;
	private static final String WINDOW_TITLE = "Hardware Terminal";
	
	private JPanel commandPanel;
	private JTextField commandText;
	private JButton btnSendCommand;
	private JTextArea txtOut;
	private JButton btnClear;
	private JMenuBar menuBar;
	private JMenu devicesMenu;
		
	private Logger log;
	
	@SuppressWarnings("unchecked")
	public HardwareTerminal() {
		this.log = Logger.getLogger(this.getClass());
		
		this.setLayout(new GridBagLayout());
		
		this.setMinimumSize(new Dimension(800, 600));
		this.setResizable(false);
		this.setTitle(WINDOW_TITLE);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		commandPanel = new JPanel();		
		commandPanel.setLayout(new BorderLayout());
		
		
		commandText = new JTextField();		
		commandText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendCommand(commandText.getText());
					commandText.setText("");
				}
			}
		
		});
		
		btnSendCommand = new JButton("Send");
		btnSendCommand.addActionListener(new ActionListener() {
			@Override
			
			public void actionPerformed(ActionEvent arg0) {
				sendCommand(commandText.getText());
				commandText.setText("");
			}
		});
		
		commandPanel.add(commandText, BorderLayout.CENTER);
		commandPanel.add(btnSendCommand, BorderLayout.LINE_END);
		
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
		this.add(commandPanel, gbc);
		
		
		txtOut = new JTextArea();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(2, 2, 2, 2);
		this.add(new JScrollPane(txtOut), gbc);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearOutputWindow();				
			}
		});		
		
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		gbc.weightx = 0.3;
		gbc.weighty = 0;
		gbc.insets = new Insets(0,0,0,0);		
		this.add(btnClear, gbc);
		
		/* End of building the main layout t*/
		
		/* Build the devices menu */
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		devicesMenu = new JMenu("Devices");
		menuBar.add(devicesMenu);
		
		Enumeration e = CommPortIdentifier.getPortIdentifiers();
		while (e.hasMoreElements()) {
			CommPortIdentifier cpi = (CommPortIdentifier)e.nextElement();
			JMenuItem cpiMenuItem = new JMenuItem(cpi.getName());

			cpiMenuItem.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					JMenuItem src = (JMenuItem)e.getSource();
					CommPortIdentifier portID = null;
					
					try {
						portID = CommPortIdentifier.getPortIdentifier(src.getText());
						
						if (portID.isCurrentlyOwned()) {
							log.error("Serial port " + portID.getName() + " is in use.");
						} else {
							enableCommPort(portID);
							
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						System.exit(-1);
					}			
				}
			});
			devicesMenu.add(cpiMenuItem);
		}
		/* End of building the devices menu */
		
		this.addWindowListener(new WindowListener() {			
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {}			
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});		
	}
	
	private void enableCommPort(CommPortIdentifier cpiToEnable) {
		clearOutputWindow();
		
		try {
			CommPort commPort = cpiToEnable.open(cpiToEnable.getName(), 2000);
			
			if (commPort instanceof SerialPort) {
				SerialPort serPort = (SerialPort)commPort;
				serPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				
				this.in = serPort.getInputStream();
				this.out = serPort.getOutputStream();
				
				serPort.addEventListener(this);
				serPort.notifyOnDataAvailable(true);
				
				this.setTitle(WINDOW_TITLE + " - " + serPort.getName());
				this.txtOut.setText("");
				this.txtOut.append("Connection changed to " + serPort.getName() + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}	
	}
	
	private void sendCommand(String command) {		
		if (this.in != null) {
			String cmd = command + "\r";
			txtOut.append("> " + command + "\n");			
			byte[] bytes = null;
			try {
				bytes = cmd.getBytes("ASCII");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
			if (bytes != null) {
				try {
					this.out.write(bytes);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		} else {
			txtOut.append("Please select a serial interface.\n");
		}
	}
	
	
	
	private void clearOutputWindow(){
		this.txtOut.setText("");
	}
	
	private InputStream in;
	private OutputStream out;
	
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		int data;
		byte[] buffer = new byte[1024];
		try {
			int len = 0;
			while ( (data = in.read()) > -1) {
				if (data == '\r') {
					break;
				}
				buffer[len++] = (byte)data;
			}
			String sData = new String(buffer,0,len);
			this.txtOut.append(sData + "\n");
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HardwareTerminal ht = new HardwareTerminal();
		ht.setVisible(true);

	}

}

