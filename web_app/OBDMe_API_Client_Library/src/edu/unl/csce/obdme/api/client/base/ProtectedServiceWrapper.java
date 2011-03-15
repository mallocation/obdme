package edu.unl.csce.obdme.api.client.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;
import edu.unl.csce.obdme.client.http.handler.IAsyncHttpHandler;
import edu.unl.csce.obdme.http.request.ParamConstants;

/**
 * The Class ProtectedServiceWrapper.
 */
public class ProtectedServiceWrapper extends ServiceWrapper {
	
	/** The api key. */
	private String apiKey;
	
	/**
	 * Instantiates a new protected service wrapper.
	 *
	 * @param apiKey the api key
	 */
	public ProtectedServiceWrapper(String apiKey) {
		this.apiKey = apiKey;		
	}
	
	/* (non-Javadoc)
	 * @see edu.unl.csce.obdme.api.client.base.ServiceWrapper#performRequest(int, java.lang.String, java.util.List, edu.unl.csce.obdme.client.http.request.RequestListener)
	 */
	@Override
	protected String performRequest(int httpMethod, String requestPath, java.util.List<NameValuePair> parameters) throws ObdmeException, CommException {
		if (parameters == null) {
			parameters = new ArrayList<NameValuePair>();
		}
		parameters.add(new BasicNameValuePair(ParamConstants.OBDME_REQUEST_APIKEY_PARAM, this.apiKey));
		return super.performRequest(httpMethod, requestPath, parameters);		
	}
	
	@Override
	protected void performRequestAsync(int httpMethod, String requestPath, List<NameValuePair> parameters, IAsyncHttpHandler handler) {
		if (parameters == null) {
			parameters = new ArrayList<NameValuePair>();
		}
		parameters.add(new BasicNameValuePair(ParamConstants.OBDME_REQUEST_APIKEY_PARAM, this.apiKey));
		super.performRequestAsync(httpMethod, requestPath, parameters, handler);
	}	
}