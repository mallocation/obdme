package edu.unl.csce.obdme.client.http.handler;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.util.Log;


public abstract class BasicTypeHandler<T> implements IAsyncHttpHandler {
	
	private static final String LOG_TAG = "BasicTypeHandler";
	
	private TypeReference<T> typeRef;
	
	public BasicTypeHandler(TypeReference<T> typeRef) {
		this.typeRef = typeRef;
	}
	
	@Override
	public abstract void onCommException(String message);

	@Override
	public void onCompleted(String response) {
		try {
			T result = new ObjectMapper().reader(this.typeRef).readValue(response);
			this.onOperationCompleted(result);
		} catch (JsonParseException e) {
			Log.e(LOG_TAG, e.getMessage());
			this.onOperationCompleted(null);
		} catch (JsonMappingException e) {
			Log.e(LOG_TAG, e.getMessage());
			this.onOperationCompleted(null);
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
			this.onOperationCompleted(null);
		}
	}

	@Override
	public abstract void onObdmeException(String message);
	
	public abstract void onOperationCompleted(T result);
}
