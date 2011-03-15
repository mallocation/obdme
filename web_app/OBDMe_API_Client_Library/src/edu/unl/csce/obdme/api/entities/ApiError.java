package edu.unl.csce.obdme.api.entities;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class ApiError implements Serializable {
	
	private static final long serialVersionUID = 8798436619674806539L;
	
	@JsonProperty("error")
	private Error error;
	
	public void setError(Error error) {
		this.error = error;
	}
	
	public Error getError() {
		return this.error;
	}	
		
	public static final class Error implements Serializable {
		
		private static final long serialVersionUID = 5252524704353735601L;
		
		@JsonProperty("message")
		private String message;
		
		public void setMessage(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
		
	}

}
