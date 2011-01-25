package controllers.obdme.rest;

import java.util.HashMap;
import java.util.Map;

import models.obdme.ExternalApp;

import com.ning.http.client.RequestBuilder;

import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.results.BadRequest;
import edu.unl.csce.obdme.http.validation.RequestValidation;

public class SecureController extends Controller {
	
	@SuppressWarnings("unused")
	@Before
	private static void validateAccessAndHttpRequest(String apikey, String sig) {
		/* validate that the api and signature key exist */
		validation.required(apikey);
		validation.required(sig);
		
		if(validation.hasErrors()) {
			/* api key or signature were not present; access forbidden! */
			Logger.info("API Key and/or Signature are not present.");
			forbidden();
		}
			
		/* validate that the api key has access to the obdme system */
		validation.isTrue(ExternalApp.hasAccess(apikey));
		if (validation.hasErrors()) {
			/* api key does not have access to the system */
			Logger.info("API Key '" + apikey + "' does not have access to the OBDMe system.");
			forbidden();
		}
		
		/* validate the signature of the request */
		String computedSignature = edu.unl.csce.obdme.http.request.RequestBuilder.computeRequestSignature(request.params.allSimple());
		validation.isTrue(sig, computedSignature);
		if (validation.hasErrors()) {
			Logger.info("Computed signature '" + computedSignature + "' does not equal submitted signature '" + sig + "'.");
			forbidden();
		}
	}
}
