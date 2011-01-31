package edu.unl.csce.obdme.http.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.unl.csce.obdme.http.request.ParamConstants;
import edu.unl.csce.obdme.http.request.RequestBuilder;

public class RequestValidation {	
	
	public static boolean isValidHttpRequest(String domain, String path, Map<String, String> requestParams, List<String> excludeParams) {
		/* Build an exclude list, always want to exclude the signature parameter */
		if (excludeParams == null) {
			excludeParams = new ArrayList<String>();
		}
		excludeParams.add(ParamConstants.OBDME_REQUEST_SIGNATURE_PARAM);
		
		/* Compute the signature */
		String computedSignature = RequestBuilder.computeRequestSignature(domain, path, requestParams, excludeParams);
		
		/* Get the sent signature */
		String sentSignature = requestParams.get(ParamConstants.OBDME_REQUEST_SIGNATURE_PARAM);
		
		/* Return the two signatures equal */
		return computedSignature.equals(sentSignature);
	}
}
