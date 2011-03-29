package edu.unl.csce.obdme.client.http.request;

import java.util.List;

import org.apache.http.NameValuePair;

import edu.unl.csce.obdme.client.http.HttpConnectionManager;
import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;
import edu.unl.csce.obdme.client.http.handler.IAsyncHttpHandler;

/**
 * The Class HttpRequest.
 */
public class AsyncHttpRequest extends HttpRequest implements Runnable {
	
	private IAsyncHttpHandler handler;

	public AsyncHttpRequest(int httpMethod, String url, List<NameValuePair> parameters, IAsyncHttpHandler handler) {
		super(httpMethod, url, parameters);		
		this.handler = handler;
	}
	
	public AsyncHttpRequest(int httpMethod, String url, String requestBody, IAsyncHttpHandler handler) {
		super(httpMethod, url, requestBody);
		this.handler = handler;
	}

	@Override
	public void run() {
		String response = null;		
		try {
			response = super.performRequestForResponse();
			handler.onCompleted(response);
		} catch (ObdmeException e) {
			handler.onObdmeException(e.getMessage());
		} catch (CommException e) {
			handler.onCommException(e.getMessage());
		} finally {
			HttpConnectionManager.getInstance().requestCompleted(this);
		}
	}
}
