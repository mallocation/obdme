package edu.unl.csce.obdme.client.http.request;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving request events.
 * The class that is interested in processing a request
 * event implements this interface.
 *
 * @see RequestEvent
 */
public interface RequestListener {
	
	/**
	 * On Http request complete
	 *
	 * @param response the response
	 */
	void onComplete(final String response);
	
}
