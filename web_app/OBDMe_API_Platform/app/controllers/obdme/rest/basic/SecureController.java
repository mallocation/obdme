package controllers.obdme.rest.basic;

import org.eclipse.jdt.core.dom.ThisExpression;

import models.obdme.Applications.ExternalApp;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * Class SecureController.
 * This class is for rest api controllers that wrap secure data, such as
 * user information and vehicle information.
 * When extending this controller class, security will automatically be added
 * to your REST controller.
 */
public abstract class SecureController extends Controller {
	//private static final String[] EXCLUDE_PARAMS = {"body"};
	
	
	
	/**
	 * Validate access and http request.
	 *
	 * @param apikey the apikey of the calling applicaiton
	 * @param sig the signature of the request
	 */
	@SuppressWarnings("unused")
	@Before
	private static void validateAccessAndHttpRequest(String apikey, String sig) {
		/* validate that the api and signature key exist */
		validation.required(apikey);
		//validation.required(sig);

		if(validation.hasErrors()) {
			/* api key or signature were not present; access forbidden! */
			Logger.info("API Key is not present.");
			forbidden();
		}

		/* validate that the api key has access to the obdme system */
		validation.isTrue(ExternalApp.hasAccess(apikey));
		if (validation.hasErrors()) {
			/* api key does not have access to the system */
			Logger.info("API Key '" + apikey + "' does not have access to the OBDMe system.");
			forbidden();
		}
		
		/* Set up the params to exclude from encryption */
//		List<String> excludeParameters = new ArrayList<String>();
//		for(String s : EXCLUDE_PARAMS) {excludeParameters.add(s);}
//		excludeParameters.addAll(request.routeArgs.keySet());
//		
//		/* validate the signature of the request */
//		boolean bValidRequest = RequestValidation.isValidHttpRequest(request.domain, request.path, request.params.allSimple(), excludeParameters);
//		validation.isTrue(bValidRequest);
//		
//		if (validation.hasErrors()) {
//			Logger.error("Invalid request signature.");
//			forbidden();
//		}
		
		/* if we've gotten here, then it's a good request! have fun in the obdme system! */
	}
}
