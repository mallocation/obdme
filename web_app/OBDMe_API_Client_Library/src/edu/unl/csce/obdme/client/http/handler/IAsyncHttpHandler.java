package edu.unl.csce.obdme.client.http.handler;

public interface IAsyncHttpHandler {
	
	void onObdmeException(String message);
	
	void onCommException(String message);
	
	void onCompleted(String response);

}