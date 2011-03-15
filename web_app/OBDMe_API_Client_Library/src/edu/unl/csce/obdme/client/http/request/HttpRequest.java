package edu.unl.csce.obdme.client.http.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.util.Log;
import edu.unl.csce.obdme.api.entities.ApiError;
import edu.unl.csce.obdme.client.http.HttpConnectionFactory;
import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;

/**
 * The Class HttpRequest.
 */
public class HttpRequest {
	
	private static final String LOG_TAG = "HttpRequest";
	
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
	
	/**
	 * Instantiates a new http request.
	 *
	 * @param httpMethod the http method
	 * @param url the url
	 * @param parameters the parameters
	 */
	public HttpRequest(int httpMethod, String url, List<NameValuePair> parameters) {
		this.httpMethod = httpMethod;
		this.url = url;
		this.parameters = parameters;
	}
	
	/**
	 * Sets the parameters.
	 *
	 * @param parameters the new parameters
	 */
	protected void setParameters(List<NameValuePair> parameters) {
		this.parameters = parameters;
	}
	
	public String performRequestForResponse() throws ObdmeException, CommException {
		String responseString = null;		
		HttpRequestBase request = null;
		HttpResponse response = null;
		
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
					
		try {
			response = HttpConnectionFactory.getInstance().getHttpClient().execute(request);			
		} catch (ClientProtocolException e) {
			Log.e(LOG_TAG, e.getMessage());
			throw new CommException(e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
			throw new CommException(e.getMessage());
		}
		
		/* Response made it back to phone, let's try to decipher it */
		try {
			responseString = new BasicResponseHandler().handleResponse(response);
		} catch (HttpResponseException e) {
			//Error >= 300, Pass this back as an obdme exception
			Log.e(LOG_TAG, e.getMessage());
			throw new ObdmeException(e.getMessage());
		} catch (IOException e) {
			// Pass this back as a comm exception
			Log.e(LOG_TAG, e.getMessage());
			throw new CommException(e.getMessage());
		}
		
		/* We've got a response, check for an error */
		try {
			ApiError apiError = new ObjectMapper().readValue(responseString, ApiError.class);
			if (apiError != null) {
				throw new ObdmeException(apiError.getError().getMessage());
			}
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		
		/* made it this far, should be a successful object returned */
		return responseString;		
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
