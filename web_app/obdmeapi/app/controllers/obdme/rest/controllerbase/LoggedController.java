package controllers.obdme.rest.controllerbase;

import models.obdmedb.applications.ApiUsageLog;
import models.obdmedb.applications.ExternalApp;
import play.mvc.Finally;

/**
 * The Class LoggedController.
 */
public class LoggedController extends SecureController {
	
//	/**
//	 * Log api usage.
//	 * Will be called automatically by any rest api extending 
//	 * this controller class.
//	 * 
//	 * @param apikey the apikey of the calling application.
//	 */
//	@Finally
//	@SuppressWarnings("unused")
//    private static void logApiUsage(String apikey) {
//		validation.clear();
//		validation.required(apikey);
//		ExternalApp externalApp = ExternalApp.findByApiKey(apikey);
//		validation.required(externalApp);
//		if (!validation.hasErrors()) {
//			/* Log the api usage */
//			ApiUsageLog.addApiUsageLog(apikey, request.path, request.method, response.status, request.remoteAddress);			
//		}		
//    }
}
