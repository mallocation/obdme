package edu.unl.csce.obdme.client.http.handler;

public interface IAsyncHttpHandler {
	
	void onObdmeException(final String message);
	
	void onCommException(final String message);
	
	void onCompleted(final String response);

}