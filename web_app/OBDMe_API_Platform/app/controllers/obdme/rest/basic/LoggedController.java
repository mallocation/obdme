package controllers.obdme.rest.basic;

import play.mvc.Finally;

/**
 * The Class LoggedController.
 */
public class LoggedController extends SecureController {
	
	/**
	 * Log api usage.
	 * Will be called automatically by any rest api extending 
	 * this controller class.
	 * 
	 * @param apikey the apikey of the calling application.
	 */
	@Finally
	@SuppressWarnings("unused")
    private static void logApiUsage(String apikey) {
		validation.clear();
		validation.required(apikey);
		if (!validation.hasErrors()) {
			/* Log the api usage */
//			new ApiUsageLog(apikey, request.path, request.method, response.status, request.remoteAddress).validateAndSave();
		}		
    }
}
