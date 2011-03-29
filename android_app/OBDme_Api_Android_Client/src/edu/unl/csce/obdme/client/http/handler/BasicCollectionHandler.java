package edu.unl.csce.obdme.client.http.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

public abstract class BasicCollectionHandler<T> implements IAsyncHttpHandler {
	
	private Class<T> clazz;
	
	public BasicCollectionHandler(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	
	@Override
	public void onCompleted(String response) {
		try {
			List<T> result = new ObjectMapper().readValue(response, TypeFactory.collectionType(ArrayList.class, this.clazz));
			this.onOperationCompleted(result);
		} catch (JsonParseException e) {
			this.onOperationCompleted(null);
		} catch (JsonMappingException e) {
			this.onOperationCompleted(null);
		} catch (IOException e) {
			this.onOperationCompleted(null);
		}
	}
	
	public abstract void onOperationCompleted(Collection<T> result);
}
