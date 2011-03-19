package edu.unl.csce.obdme.api.client.base;

import java.util.List;

import org.apache.http.NameValuePair;

import edu.unl.csce.obdme.client.http.HttpConnectionManager;
import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;
import edu.unl.csce.obdme.client.http.handler.IAsyncHttpHandler;
import edu.unl.csce.obdme.client.http.request.AsyncHttpRequest;
import edu.unl.csce.obdme.client.http.request.HttpRequest;

public class ServiceWrapper {
	private static final String OBDME_API_BASE_PATH = "http://obdme.com/api";
	//private static final String OBDME_API_BASE_PATH = "http://api.obdme.com/api";
	
	/* Synchronous requests */
	
	protected String performGet(String requestPath, List<NameValuePair> parameters) throws ObdmeException, CommException {
		return this.performRequest(HttpRequest.GET, requestPath, parameters);
	}
	
	protected String performDelete(String requestPath, List<NameValuePair> parameters) throws ObdmeException, CommException {
		return this.performRequest(HttpRequest.DELETE, requestPath, parameters);
	}
	
	protected String performPost(String requestPath, List<NameValuePair> parameters) throws ObdmeException, CommException {
		return this.performRequest(HttpRequest.POST, requestPath, parameters);	
	}	

	protected String performPut(String requestPath, List<NameValuePair> parameters) throws ObdmeException, CommException {
		return this.performRequest(HttpRequest.PUT, requestPath, parameters);
	}
	
	protected String performRequest(int httpMethod, String requestPath, List<NameValuePair> parameters) throws ObdmeException, CommException {
		String url = joinBaseWithRequestPath(requestPath);
		HttpRequest req = new HttpRequest(httpMethod, url, parameters);
		return req.performRequestForResponse();
	}	
	
	/* Asynchronous requests */
	
	protected void performGetAsync(String requestPath, List<NameValuePair> parameters, IAsyncHttpHandler handler) {
		this.performRequestAsync(HttpRequest.GET, requestPath, parameters, handler);
	}
	
	protected void performDeleteAsync(String requestPath, List<NameValuePair> parameters, IAsyncHttpHandler handler) {
		this.performRequestAsync(HttpRequest.DELETE, requestPath, parameters, handler);
	}
	
	protected void performPostAsync(String requestPath, List<NameValuePair> parameters, IAsyncHttpHandler handler) {
		this.performRequestAsync(HttpRequest.POST, requestPath, parameters, handler);
	}	

	protected void performPutAsync(String requestPath, List<NameValuePair> parameters, IAsyncHttpHandler handler) {
		this.performRequestAsync(HttpRequest.PUT, requestPath, parameters, handler);
	}
	
	protected void performRequestAsync(int httpMethod, String requestPath, List<NameValuePair> parameters, IAsyncHttpHandler handler) {
		String url = joinBaseWithRequestPath(requestPath);
		AsyncHttpRequest request = new AsyncHttpRequest(httpMethod, url, parameters, handler);
		HttpConnectionManager.getInstance().push(request);
	}	
	
	private String joinBaseWithRequestPath(String requestPath) {
		return String.format("%s%s", OBDME_API_BASE_PATH, requestPath);
	}
	
}
