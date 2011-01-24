package edu.unl.csce.obdme.data.interfaces;

public interface IExternalApp {	
	
	/*
	 * Logs an API usage into the database.
	 * 
	 * @param apiKey API Key of the calling application.
	 * @param requestPath Requested path.
	 * @param httpMethod Method of the request (GET,POST,PUT)
	 * @param responseCode Code of the response (200,400,etc)
	 * @param ipAddress IP address of the calling application.
	 */
	void addApiUsage(String apiKey, String requestPath, String httpMethod, int responseCode, String ipAddress);
	
	/*
	 * Determines if an API key is regestered with the obdme system.
	 * 
	 * @apiKey API Key to check.
	 */
	boolean isValidApiKey(String apiKey);
}