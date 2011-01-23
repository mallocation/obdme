package edu.unl.csce.obdme.data.interfaces;

public interface IExternalApp {	
	void addApiUsage(String apiKey, String requestPath, String httpMethod, int responseCode, String ipAddress);
	boolean isValidApiKey(String apiKey);
}