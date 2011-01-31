package edu.unl.csce.obdme.api.client.base;

import java.util.List;

import org.apache.http.NameValuePair;

import edu.unl.csce.obdme.client.http.HttpConnectionManager;
import edu.unl.csce.obdme.client.http.request.HttpRequest;
import edu.unl.csce.obdme.client.http.request.RequestListener;

public class ServiceWrapper {
	private static final String OBDME_API_BASE_PATH = "http://obdme.com/api";	
	
	protected void performGet(String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		this.performRequest(HttpRequest.GET, requestPath, parameters, listener);
	}
	
	protected void performDelete(String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		this.performRequest(HttpRequest.DELETE, requestPath, parameters, listener);
	}
	
	protected void performPost(String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		this.performRequest(HttpRequest.POST, requestPath, parameters, listener);	
	}
	

	protected void performPut(String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		this.performRequest(HttpRequest.PUT, requestPath, parameters, listener);
	}
	
	protected void performRequest(int httpMethod, String requestPath, List<NameValuePair> parameters, RequestListener listener) {		
		HttpRequest req = new HttpRequest(httpMethod, joinBaseWithRequestPath(requestPath), parameters, listener);
		HttpConnectionManager.getInstance().push(req);
	}	
	
	private String joinBaseWithRequestPath(String requestPath) {
		return OBDME_API_BASE_PATH + requestPath;
	}
	
}
