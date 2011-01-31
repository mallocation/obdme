package edu.unl.csce.obdme.api.client.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.unl.csce.obdme.client.http.request.RequestListener;
import edu.unl.csce.obdme.http.request.ParamConstants;

public class ProtectedServiceWrapper extends ServiceWrapper {
	private String apiKey;
	
	public ProtectedServiceWrapper(String apiKey) {
		this.apiKey = apiKey;		
	}
	
	@Override
	protected void performRequest(int httpMethod, String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		if (parameters == null) {
			parameters = new ArrayList<NameValuePair>();
		}
		parameters.add(new BasicNameValuePair(ParamConstants.OBDME_REQUEST_APIKEY_PARAM, this.apiKey));
		super.performRequest(httpMethod, requestPath, parameters, listener);
	}
}