package edu.unl.csce.obdme.mvc.controllers;

import java.util.HashMap;
import java.util.Map;

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
	private static void validateAccessAndHttpRequest() {
		Map<String,String> kv = new HashMap<String, String>();
		kv.put("apikey", "34567898765456789fasfmlaksasdfasdfasdf");
		System.out.println(edu.unl.csce.obdme.http.request.RequestBuilder.computeRequestSignature(kv));
		
		
		
		Request request = Controller.request;
		if (!RequestValidation.isValidHttpRequest(request.params.allSimple())) {
			BadRequest br = new BadRequest();
			Logger.error(br, "Request was made in an invalid format.", br);
		}		
	}
}
