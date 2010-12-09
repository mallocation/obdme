package edu.unl.csce.obdme.terminal;

import edu.unl.csce.obdme.bluetooth.CommunicationInterface;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;

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


public class HardwareTerminal extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel commandPanel;
	private JTextField commandText;
	private JButton btnSendCommand;
	private JTextArea txtOut;
	private JButton btnClear;
	private JMenuBar menuBar;
	private JMenu devicesMenu;
	
	private CommunicationInterface commInterface;
	
	
	
	public HardwareTerminal() {
		this.setLayout(new GridBagLayout());
		
		this.setMinimumSize(new Dimension(800, 600));
		this.setResizable(false);
		
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
					CommPortIdentifier selectedPort = null;
					try {
						selectedPort = CommPortIdentifier.getPortIdentifier(src.getText());
					} catch (NoSuchPortException nspe) {
						selectedPort = null;
					}
					if (selectedPort != null) enableCommPort(selectedPort);				
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
			public void windowClosing(WindowEvent e) {
				closeCommInterface();				
			}			
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});		
	}
	
	private void enableCommPort(CommPortIdentifier cpiToEnable) {
		clearOutputWindow();
		
		CommunicationInterface newInterface = new CommunicationInterface(cpiToEnable.getName());
		
		try {
			newInterface.initializeConnection();			
			txtOut.append("Connection changed to " + cpiToEnable.getName() + "\n");
			this.setTitle("Hardware Terminal - " + cpiToEnable.getName());
			this.closeCommInterface();
			this.commInterface = newInterface;
		} catch (Exception e) {
			newInterface.closeConnection();
			txtOut.append("Unable to connect to " + cpiToEnable.getName() + "\n");			
		}		
	}
	
	private void closeCommInterface() {
		if (this.commInterface != null)
			this.commInterface.closeConnection();
	}
	
	
	
	private void sendCommand(String command) {		
		if (this.commInterface != null) {
			txtOut.append("> " + command + "\n");
			String responseString = this.commInterface.sendCommand(command);
			txtOut.append(responseString + "\n");
		} else {
			txtOut.append("Please select a serial interface.\n");
		}
	}
	
	
	
	private void clearOutputWindow(){
		this.txtOut.setText("");
	}
	
	
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HardwareTerminal ht = new HardwareTerminal();
		ht.setVisible(true);

	}

}
