package edu.unl.csce.obdme.client.http;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * A factory for creating HttpConnection objects.
 */
public class HttpConnectionFactory {
	
	/** The Constant OBDME_LIBRARY_USER_AGENT. */
	private static final String OBDME_LIBRARY_USER_AGENT = "OBDMe API Android Library";	
	
	/** The http client. */
	private HttpClient httpClient;	
	
	/**
	 * Gets the single instance of HttpConnectionFactory.
	 *
	 * @return single instance of HttpConnectionFactory
	 */
	public static HttpConnectionFactory getInstance() {
		return HttpConnectionFactoryHolder.INSTANCE;
	}	
	
	/**
	 * The Class HttpConnectionFactoryHolder.
	 */
	private static class HttpConnectionFactoryHolder {
		
		/** The Constant INSTANCE. */
		public static final HttpConnectionFactory INSTANCE = new HttpConnectionFactory(); 
	}	
	
	/**
	 * Instantiates a new http connection factory.
	 */
	private HttpConnectionFactory() {
		SchemeRegistry sr = new SchemeRegistry();
		HttpParams params = new BasicHttpParams();
		sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams.setUserAgent(params, OBDME_LIBRARY_USER_AGENT);
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, sr);
		this.httpClient = new DefaultHttpClient(ccm, params);
	}	
	
	/**
	 * Gets the http client.
	 *
	 * @return the http client
	 */
	public synchronized HttpClient getHttpClient() {
		return this.httpClient;		
	}
}