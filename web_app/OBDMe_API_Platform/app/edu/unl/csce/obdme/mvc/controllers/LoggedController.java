package edu.unl.csce.obdme.mvc.controllers;

import play.mvc.*;

public class LoggedController extends BasicController {

	@Finally
	@SuppressWarnings("unused")
    private static void logApiUsage() {
		if (request.params._contains("apikey")) {
			String sApiKey = request.params.get("apikey");
			if (getAppFactory().ExternalApp().isValidApiKey(sApiKey)) {				
				String requestPath, httpMethod, ipAddress;
				int responseCode;
				
				requestPath = request.path;
				httpMethod = request.method;
				ipAddress = request.remoteAddress;
				responseCode = response.status;		
				getAppFactory().ExternalApp().logApiUsage(sApiKey, requestPath, httpMethod, responseCode, ipAddress);				
			}
		}	
    }
}
