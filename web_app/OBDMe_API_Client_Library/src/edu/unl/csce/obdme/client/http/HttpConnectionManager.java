package edu.unl.csce.obdme.client.http;

import java.util.ArrayList;

import edu.unl.csce.obdme.client.http.request.HttpRequest;

/**
 * The Class HttpConnectionManager.
 * This class is used to handle http request execution and queuing.
 * 
 */
public class HttpConnectionManager {
	
	/** The Constant MAX_ACTIVE_REQUESTS. */
	private static final int MAX_ACTIVE_REQUESTS = 5;
	
	/** The request queue. */
	private ArrayList<HttpRequest> requestQueue;
	
	/** The current running requests. */
	private int currentRunningRequests = 0;
	
	/**
	 * Instantiates a new http connection manager.
	 */
	private HttpConnectionManager() {
		requestQueue = new ArrayList<HttpRequest>();
	}
	
	/**
	 * Gets the single instance of HttpConnectionManager.
	 *
	 * @return single instance of HttpConnectionManager
	 */
	public static HttpConnectionManager getInstance() {
		return HttpConnectionManagerBuilder.INSTANCE;
	}
	
	/**
	 * The Class HttpConnectionManagerBuilder.
	 */
	private static final class HttpConnectionManagerBuilder {
		
		/** The Constant INSTANCE. */
		public static final HttpConnectionManager INSTANCE = new HttpConnectionManager();
	}
	
	/**
	 * Start the next HttpRequest if one exists in the request queue and a 
	 * connection slot is open.
	 */
	private void startNext() {
		if (!requestQueue.isEmpty() && currentRunningRequests < MAX_ACTIVE_REQUESTS) {
			currentRunningRequests++;
			HttpRequest request = requestQueue.remove(0);
			new Thread(request).start();			
		}
	}
	
	/**
	 * Push a request into the queue.  If available, the request
	 * will be started.
	 *
	 * @param request the request to queue
	 */
	public void push(HttpRequest request) {
		this.requestQueue.add(request);
		startNext();
	}
	
	/**
	 * Call this method once a request has completed.
	 * This will free up an active connection for queued requests.
	 *
	 * @param request the request that has completed
	 */
	public void requestCompleted(HttpRequest request) {
		currentRunningRequests--;
		startNext();
	}
}