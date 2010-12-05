package edu.unl.csce.obdme.terminal;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.ScrollPaneConstants;

//import edu.unl.csce.obdme.elm.CommunicationInterface;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;



public class HardwareTerminal {

	private static final int ENTER_KEY_CODE = 10;
	
	//private CommunicationInterface commInterface;
	private JFrame mainWindow;

	private JPanel commandPanel;
	private JTextField txtCommand;
	private JButton bSendCommand;

	private JPanel terminalPanel;
	private JTextArea txtTerminal;
	private JScrollPane spTerminal;
	private JMenuBar frameMenuBar;
	private JMenu frameDevicesMenu;	
	
	HardwareTerminal() {
		mainWindow = new JFrame("Hardware Terminal");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(700, 500);

		commandPanel = new JPanel();
		commandPanel.setLayout(new BorderLayout());

		txtCommand = new JTextField();
		bSendCommand = new JButton("Send");
		
		txtCommand.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == ENTER_KEY_CODE) {
					JTextField textField = (JTextField) e.getSource();
					sendCommand(textField.getText());
					textField.setText("");				
				}
			}
		});		

		
		bSendCommand.addActionListener(new ActionListener() {
			@Override
			
			public void actionPerformed(ActionEvent arg0) {
				sendCommand(txtCommand.getText());		
				txtCommand.setText("");
			}
		});
		

		commandPanel.add(txtCommand, BorderLayout.CENTER);
		commandPanel.add(bSendCommand, BorderLayout.LINE_END);

		terminalPanel = new JPanel();
		terminalPanel.setLayout(new GridLayout());
		txtTerminal= new JTextArea();
		txtTerminal.setEditable(false);
		//txtTerminal.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		spTerminal = new JScrollPane(txtTerminal);
		spTerminal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		terminalPanel.add(spTerminal);
		//terminalPanel.add(txtTerminal);

		mainWindow.setLayout(new BorderLayout());


		mainWindow.add(commandPanel, BorderLayout.PAGE_START);
		mainWindow.add(terminalPanel, BorderLayout.CENTER);

		
		frameMenuBar = new JMenuBar();
		frameDevicesMenu = new JMenu("Devices");
		
		
		
		Enumeration e = CommPortIdentifier.getPortIdentifiers();		
		while (e.hasMoreElements()) {
			JMenuItem device = new JMenuItem(((CommPortIdentifier)e.nextElement()).getName());
			device.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					JMenuItem dev = (JMenuItem) e.getSource();
					CommPortIdentifier selectedPort = null;
					try {
						selectedPort = CommPortIdentifier.getPortIdentifier(dev.getText());
					} catch (NoSuchPortException e1) {
						selectedPort = null;
					}
					if (selectedPort != null) enableCommPort(selectedPort);
				}
			});
			frameDevicesMenu.add(device);
		}
		
		frameMenuBar.add(frameDevicesMenu);
		mainWindow.setJMenuBar(frameMenuBar);
	}
	
	private void enableCommPort(CommPortIdentifier commPort) {
		txtTerminal.setText("");
		txtTerminal.append("Connection changed to " + commPort.getName() + "\n");
		mainWindow.setTitle("Hardware Terminal - " + commPort.getName());
	}
	
	private void sendCommand(String command) {
		txtTerminal.append("> " + command + "\n");
	}

	private void show() {
		mainWindow.setVisible(true);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//CommunicationInterface commInterface = new CommunicationInterface("/dev/tty.OBDMe-DevB");

		HardwareTerminal o = new HardwareTerminal();
		o.show();
	}




}

