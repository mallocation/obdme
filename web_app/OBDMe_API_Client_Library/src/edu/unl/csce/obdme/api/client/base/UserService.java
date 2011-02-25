package edu.unl.csce.obdme.api.client.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;

import com.google.gson.Gson;

import edu.unl.csce.obdme.api.entities.User;
import edu.unl.csce.obdme.client.http.request.RequestListener;
import edu.unl.csce.obdme.encryption.EncryptionUtils;

/**
 * The Class UserService.
 */
public class UserService extends ProtectedServiceWrapper {
	
	/** The Constant USERS_SERVICE_BASE_PATH. */
	private static final String USERS_SERVICE_BASE_PATH = "/users";

	/**
	 * Instantiates a new user service.
	 *
	 * @param apiKey the api key
	 */
	public UserService(String apiKey) {
		super(apiKey);
	}
	
	/**
	 * Creates a user within the OBDMe system.
	 *
	 * @param email the email for the new user
	 * @param password the user's password (unencrypted)
	 * @param handler the handler for the completion of the call.
	 */
	public void createUser(String email, String password, Handler handler) {		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("email", email));
		parameters.add(new BasicNameValuePair("pw", EncryptionUtils.encryptPassword(password)));		
		this.performPost(USERS_SERVICE_BASE_PATH, parameters, new CreateUserListener(handler));
	}
	
	/**
	 * Returns a boolean representing if the email is already registered
	 * in the OBDMe system.
	 *
	 * @param email the email
	 * @param handler the handler
	 */
	public void isUserRegistered(String email, Handler handler) {
		this.getUserByEmailMethod(email, new IsUserRegisteredListener(handler));
	}	
	
	/**
	 * Returns a user from the OBDMe system based on the email.
	 * If a user is not found, a null value is returned.
	 *
	 * @param email the email
	 * @param handler the handler
	 * @return the user by email
	 */
	public void getUserByEmail(String email, Handler handler) {
		this.getUserByEmailMethod(email, new GetUserByEmailListener(handler));
	}
	
	/**
	 * Validates a user's credentials with the OBDMe System (Single sign on).
	 * Returns a user object if the credentials are correct, otherwise a
	 * null value is returned.
	 *
	 * @param email the email of the user
	 * @param password the user's password (unencrypted)	
	 * @param handler the handler
	 */
	public void validateUserCredentials(String email, String password, Handler handler) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("pw", EncryptionUtils.encryptPassword(password)));
		this.performGet(USERS_SERVICE_BASE_PATH + "/" + email + "/login", parameters, new GetUserByEmailListener(handler)); 
	}
	
	/**
	 * Gets the user by email method.
	 *
	 * @param email the email
	 * @param listener the listener
	 * @return the user by email method
	 */
	private void getUserByEmailMethod(String email, RequestListener listener) {
		super.performGet(USERS_SERVICE_BASE_PATH + "/" + email, null, listener);
	}
	
	/**
	 * The listener interface for receiving isUserRegistered events.
	 * The class that is interested in processing a isUserRegistered
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addIsUserRegisteredListener<code> method. When
	 * the isUserRegistered event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see IsUserRegisteredEvent
	 */
	private class IsUserRegisteredListener implements RequestListener {
		
		/** The handler. */
		private final Handler handler;
		
		/**
		 * Instantiates a new checks if is user registered listener.
		 *
		 * @param handler the handler
		 */
		public IsUserRegisteredListener(Handler handler) {
			this.handler = handler;
		}
		
		/* (non-Javadoc)
		 * @see edu.unl.csce.obdme.client.http.request.RequestListener#onComplete(java.lang.String)
		 */
		@Override
		public void onComplete(String response) {
			User user = userFromJsonString(response);
			this.handler.sendMessage(handler.obtainMessage(0, user != null));			
		}
	}
	
	/**
	 * The listener interface for receiving getUserByEmail events.
	 * The class that is interested in processing a getUserByEmail
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addGetUserByEmailListener<code> method. When
	 * the getUserByEmail event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see GetUserByEmailEvent
	 */
	private class GetUserByEmailListener implements RequestListener {
		
		/** The handler. */
		private final Handler handler;		
		
		/**
		 * Instantiates a new gets the user by email listener.
		 *
		 * @param handler the handler
		 */
		public GetUserByEmailListener(Handler handler) {
			this.handler = handler;
		}
		
		/* (non-Javadoc)
		 * @see edu.unl.csce.obdme.client.http.request.RequestListener#onComplete(java.lang.String)
		 */
		@Override
		public void onComplete(String response) {
			User user = userFromJsonString(response);			
			handler.sendMessage(handler.obtainMessage(0,user));			
		}		
	}
	
	
	
	/**
	 * The listener interface for receiving createUser events.
	 * The class that is interested in processing a createUser
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addCreateUserListener<code> method. When
	 * the createUser event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see CreateUserEvent
	 */
	private class CreateUserListener implements RequestListener {
		
		/** The handler. */
		private final Handler handler;
		
		/**
		 * Instantiates a new creates the user listener.
		 *
		 * @param handler the handler
		 */
		public CreateUserListener(Handler handler) {
			this.handler = handler;
		}
		
		/* (non-Javadoc)
		 * @see edu.unl.csce.obdme.client.http.request.RequestListener#onComplete(java.lang.String)
		 */
		@Override
		public void onComplete(String response) {
			User newUser = userFromJsonString(response);
			this.handler.sendMessage(handler.obtainMessage(0, newUser));
		}
		
	}
	
	/**
	 * User from json string.
	 *
	 * @param jsonUser the json user
	 * @return the user
	 */
	private User userFromJsonString(String jsonUser) {
		Gson gson = new Gson();
		User user = gson.fromJson(jsonUser, User.class);
		return user;
	}		
}