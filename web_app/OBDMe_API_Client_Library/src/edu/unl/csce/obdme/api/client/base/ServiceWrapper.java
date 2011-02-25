package edu.unl.csce.obdme.api.client.base;

import java.util.List;

import org.apache.http.NameValuePair;

import edu.unl.csce.obdme.client.http.HttpConnectionManager;
import edu.unl.csce.obdme.client.http.request.HttpRequest;
import edu.unl.csce.obdme.client.http.request.RequestListener;

/**
 * The Class ServiceWrapper.
 */
public class ServiceWrapper {
	
	/** The Constant OBDME_API_BASE_PATH. */
	private static final String OBDME_API_BASE_PATH = "http://obdme.com/api";	
	
	/**
	 * Perform get.
	 *
	 * @param requestPath the request path
	 * @param parameters the parameters
	 * @param listener the listener
	 */
	protected void performGet(String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		this.performRequest(HttpRequest.GET, requestPath, parameters, listener);
	}
	
	/**
	 * Perform delete.
	 *
	 * @param requestPath the request path
	 * @param parameters the parameters
	 * @param listener the listener
	 */
	protected void performDelete(String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		this.performRequest(HttpRequest.DELETE, requestPath, parameters, listener);
	}
	
	/**
	 * Perform post.
	 *
	 * @param requestPath the request path
	 * @param parameters the parameters
	 * @param listener the listener
	 */
	protected void performPost(String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		this.performRequest(HttpRequest.POST, requestPath, parameters, listener);	
	}	

	/**
	 * Perform put.
	 *
	 * @param requestPath the request path
	 * @param parameters the parameters
	 * @param listener the listener
	 */
	protected void performPut(String requestPath, List<NameValuePair> parameters, RequestListener listener) {
		this.performRequest(HttpRequest.PUT, requestPath, parameters, listener);
	}
	
	/**
	 * Perform request.
	 *
	 * @param httpMethod the http method
	 * @param requestPath the request path
	 * @param parameters the parameters
	 * @param listener the listener
	 */
	protected void performRequest(int httpMethod, String requestPath, List<NameValuePair> parameters, RequestListener listener) {		
		HttpRequest req = new HttpRequest(httpMethod, joinBaseWithRequestPath(requestPath), parameters, listener);
		HttpConnectionManager.getInstance().push(req);
	}	
	
	/**
	 * Join base with request path.
	 *
	 * @param requestPath the request path
	 * @return the string
	 */
	private String joinBaseWithRequestPath(String requestPath) {
		return String.format("%s%s", OBDME_API_BASE_PATH, requestPath);
	}
	
}
