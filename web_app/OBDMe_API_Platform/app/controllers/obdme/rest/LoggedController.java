package controllers.obdme.rest;

import models.obdme.ApiUsageLog;
import play.mvc.*;

public class LoggedController extends SecureController {
	
	@Finally
	@SuppressWarnings("unused")
    private static void logApiUsage(String apikey) {
		validation.clear();
		validation.required(apikey);		
		if (!validation.hasErrors()) {
			new ApiUsageLog(apikey, request.path, request.method, response.status, request.remoteAddress).validateAndSave();
		}		
    }
}
