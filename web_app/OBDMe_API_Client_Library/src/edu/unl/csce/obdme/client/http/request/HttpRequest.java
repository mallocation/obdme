package edu.unl.csce.obdme.client.http.request;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.protocol.HTTP;

import android.util.Log;
import edu.unl.csce.obdme.client.http.HttpConnectionFactory;
import edu.unl.csce.obdme.client.http.HttpConnectionManager;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpRequest.
 */
public class HttpRequest implements Runnable {
	
	/** The Constant GET. */
	public static final int GET = 0;
	
	/** The Constant POST. */
	public static final int POST = 1;
	
	/** The Constant PUT. */
	public static final int PUT = 2;
	
	/** The Constant DELETE. */
	public static final int DELETE = 3;	
	
	/** The http method for this request. */
	private int httpMethod;
	
	/** The url of the request. */
	private String url;
	
	/** The parameters. */
	private List<NameValuePair> parameters;
	
	/** The listener. */
	private RequestListener listener;
	
	/**
	 * Instantiates a new http request.
	 *
	 * @param httpMethod the http method
	 * @param url the url
	 * @param parameters the parameters
	 * @param listener the listener
	 */
	public HttpRequest(int httpMethod, String url, List<NameValuePair> parameters, RequestListener listener) {
		this.httpMethod = httpMethod;
		this.url = url;
		this.parameters = parameters;
		this.listener = listener;
	}
	
	/**
	 * Sets the parameters.
	 *
	 * @param parameters the new parameters
	 */
	protected void setParameters(List<NameValuePair> parameters) {
		this.parameters = parameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		HttpRequestBase request = null;
		HttpResponse response = null;
		BasicResponseHandler brh = null;
		String responseString = null;
		try {			
			switch (this.httpMethod) {
			case HttpRequest.GET:
				request = new HttpGet(formatGetDeleteRequestUrl(this.url, this.parameters));
				break;
			case HttpRequest.DELETE:
				request = new HttpDelete(formatGetDeleteRequestUrl(this.url, this.parameters));
				break;
			case HttpRequest.POST:
				request = new HttpPost(this.url);
				((HttpPost)request).setEntity(getEntityForPutPostRequests(this.parameters));
				break;
			case HttpRequest.PUT:
				request = new HttpPut(this.url);
				((HttpPut)request).setEntity(getEntityForPutPostRequests(this.parameters));
				break;
			default:
				break;
			}
			
			/* Get the response */
			response = HttpConnectionFactory.getInstance().getHttpClient().execute(request);
			
			/* Try to read the response into a string, will complete for only 2XX status codes */
			brh = new BasicResponseHandler();
			responseString = brh.handleResponse(response);
			
			/* Response was successful, so notify the request listener */
			if (this.listener != null) {
				this.listener.onComplete(responseString);
			}
			Log.i("HTTPRequest", "Completed with code of " + response.getStatusLine().getStatusCode());				
		} catch (Exception e) {
			Log.e("HTTPRequest", "Exception", e);
		} finally {
			HttpConnectionManager.getInstance().requestCompleted(this);			
		}
	}
	
	/**
	 * Format a GET or DELETE url.
	 *
	 * @param url the url
	 * @param parameters the parameters
	 * @return the string
	 */
	private static String formatGetDeleteRequestUrl(String url, List<NameValuePair> parameters) {
		return url + "?" + paramsNVPToUrlEncodedString(parameters);		
	}
	
	/**
	 * Gets the entity for PUT and POST requests.
	 *
	 * @param parameters the parameters
	 * @return the entity for put post requests
	 */
	private static HttpEntity getEntityForPutPostRequests(List<NameValuePair> parameters) {
		try {
			return new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * List of NameValuePair to url encoded string.
	 *
	 * @param parameters the parameters
	 * @return the string
	 */
	private static String paramsNVPToUrlEncodedString(List<NameValuePair> parameters) {
		return URLEncodedUtils.format(parameters, HTTP.UTF_8);
	}

}
