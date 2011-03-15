package api.entities;

import java.io.Serializable;

public class ApiError implements Serializable {
	
	private Error error;
	
	public ApiError(String message) {		
		this.error = new Error(message);		
	}
	
	public void setError(Error error) {
		this.error = error;
	}
	
	public Error getError() {
		return this.error;
	}	
		
	private class Error implements Serializable {
		
		private String message;
		
		public Error(String message) {
			this.message = message;
		}
		
		@SuppressWarnings("unused")
		public void setMessage(String message) {
			this.message = message;
		}
		
		@SuppressWarnings("unused")
		public String getMessage() {
			return this.message;
		}
		
	}

}