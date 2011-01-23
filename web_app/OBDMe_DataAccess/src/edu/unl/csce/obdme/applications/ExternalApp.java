package edu.unl.csce.obdme.applications;

import edu.unl.csce.obdme.data.interfaces.IExternalApp;

public class ExternalApp {
	
	private IExternalApp dataSource;
	
	public ExternalApp(IExternalApp data) {
		this.dataSource = data;
	}	
	
	public void logApiUsage(String apiKey, String requestPath, String httpMethod, int responseCode, String ipAddress) {		
		dataSource.addApiUsage(apiKey, requestPath, httpMethod, responseCode, ipAddress);
	}
	
	public boolean isValidApiKey(String apiKey) {
		return dataSource.isValidApiKey(apiKey);
	}
	
}