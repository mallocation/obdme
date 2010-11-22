package obdme.hardware;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;



public class HardwareTerminal implements ActionListener {
	
	private JFrame mainWindow;
	
	private JPanel commandPanel;
	private JTextField txtCommand;
	private JButton bSendCommand;
	
	private JPanel terminalPanel;
	private JTextArea txtTerminal;
	private JScrollPane spTerminal;
	

	
	HardwareTerminal() {
		mainWindow = new JFrame("Hardware Terminal");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(700, 500);
		
		commandPanel = new JPanel();
		commandPanel.setLayout(new BorderLayout());
		
		txtCommand = new JTextField();
		bSendCommand = new JButton("Send");
		
		txtCommand.addActionListener(this);
		
		
		commandPanel.add(txtCommand, BorderLayout.CENTER);
		commandPanel.add(bSendCommand, BorderLayout.LINE_END);
		
		terminalPanel = new JPanel();
		terminalPanel.setLayout(new GridLayout());
		txtTerminal= new JTextArea();
		//txtTerminal.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		spTerminal = new JScrollPane(txtTerminal);
		spTerminal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		terminalPanel.add(spTerminal);
		//terminalPanel.add(txtTerminal);
		
		mainWindow.setLayout(new BorderLayout());
		
		
		mainWindow.add(commandPanel, BorderLayout.PAGE_START);
		mainWindow.add(terminalPanel, BorderLayout.CENTER);
		
		
		
		

	}
	
	private void show() {
		mainWindow.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HardwareTerminal o = new HardwareTerminal();
		o.show();
	}

	

}

