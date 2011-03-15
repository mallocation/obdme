package edu.unl.csce.obdme.api.client.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.unl.csce.obdme.api.entities.User;
import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;
import edu.unl.csce.obdme.client.http.handler.BasicObjectHandler;
import edu.unl.csce.obdme.encryption.EncryptionUtils;

public class UserService extends ProtectedServiceWrapper {
	private static final String USERS_SERVICE_BASE_PATH = "/users";

	public UserService(String apiKey) {
		super(apiKey);
	}
	
	private List<NameValuePair> getParametersForCreateUser(String email, String password) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("email", email));
		parameters.add(new BasicNameValuePair("pw", EncryptionUtils.encryptPassword(password)));
		return parameters;
	}	
	
	/**
	 * Creates a user in the obdme system.
	 *
	 * @param email the email of the user
	 * @param password the password of the user (clear text) - this will be encrypted and transmitted
	 * @return the user
	 * @throws ObdmeException Thrown if a user with the email already exists.
	 * @throws CommException the comm exception
	 */
	public User createUser(String email, String password) throws ObdmeException, CommException {
		List<NameValuePair> parameters = getParametersForCreateUser(email, password);
		String result = this.performPost(USERS_SERVICE_BASE_PATH, parameters);
		return userFromJsonString(result);
	}
	
	/**
	 * Creates a user in the obdme system, asynchronously.
	 * 
	 * Similar to the synchronous method, an obdme exception is thrown
	 * if a user already exists with the given email.
	 *
	 * @param email the email of the user
	 * @param password the password - clear text password
	 * @param handler the handler
	 */
	public void createUserAsync(String email, String password, BasicObjectHandler<User> handler) {		
		List<NameValuePair> parameters = getParametersForCreateUser(email, password);
		this.performPostAsync(USERS_SERVICE_BASE_PATH, parameters, handler);
	}
	
	/**
	 * Gets a user by email, asynchronously.
	 * 
	 * Similar to the synchronous method, an obdme exception is thrown
	 * if the user does not exist in the obdme system.
	 *
	 * @param email the email of the user
	 * @param handler the handler
	 * @return the user
	 */
	public void getUserByEmailAsync(String email, BasicObjectHandler<User> handler) {
		String requestPath = String.format("%s/%s", USERS_SERVICE_BASE_PATH, email);
		super.performGetAsync(requestPath, null, handler);
	}
	
	/**
	 * Gets a user by email.
	 *
	 * @param email the email of the user
	 * @return the user
	 * @throws ObdmeException Throws an exception if the user does not exist.
	 * @throws CommException the comm exception
	 */
	public User getUserByEmail(String email) throws ObdmeException, CommException {
		String requestPath = String.format("%s/%s", USERS_SERVICE_BASE_PATH, email);
		String response = super.performGet(requestPath, null);
		return userFromJsonString(response);
	}
	
	/**
	 * Validate user credentials.
	 * 
	 * If the login fails or is invalid, an obdme exception will be thrown.
	 *
	 * @param email the email of the user
	 * @param password the clear text password of the user
	 * @param handler the handler
	 */
	public void validateUserCredentials(String email, String password, BasicObjectHandler<User> handler) {
		String requestPath = String.format("%s/%s/login", USERS_SERVICE_BASE_PATH, email);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("pw", EncryptionUtils.encryptPassword(password)));
		super.performGetAsync(requestPath, parameters, handler); 
	}
	
	private User userFromJsonString(String jsonUser) {
		User user = null;
		try {
			user = new ObjectMapper().readValue(jsonUser, User.class);
		} catch (JsonParseException e) {
			user = null;
		} catch (JsonMappingException e) {
			user = null;
		} catch (IOException e) {
			user = null;
		}
		return user;
	}		
}