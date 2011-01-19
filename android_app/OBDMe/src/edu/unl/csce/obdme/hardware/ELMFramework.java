package edu.unl.csce.obdme.hardware;

import edu.unl.csce.obdme.bluetooth.BluetoothService;

public class ELMFramework extends Thread{
	
	private static final String ELM_DEVICE_IDENTIFIER = "ELM327 v1.4";
	
	private BluetoothService bluetoothService;

	public ELMFramework(BluetoothService bluetoothService) {
		this.bluetoothService = bluetoothService;
	}

	public boolean initConnection() {
		bluetoothService.write("ATD");
		if(!bluetoothService.getResponseFromQueue(true).contains("OK")) {
			return false;
		}
		bluetoothService.write("ATE0");
		if(!bluetoothService.getResponseFromQueue(true).contains("OK")) {
			return false;
		}
		
		return true;
	}
	
	public boolean verifyHardwareVersion() {
		bluetoothService.write("ATI");
		if(!bluetoothService.getResponseFromQueue(true).contains(ELM_DEVICE_IDENTIFIER)) {
			return false;
		}
		else {
			return true;
		}
	}
	
}
