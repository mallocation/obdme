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

public class UserService extends ProtectedServiceWrapper {
	private static final String USERS_SERVICE_BASE_PATH = "/users";

	public UserService(String apiKey) {
		super(apiKey);
	}
	
	public void isUserRegistered(String email, Handler handler) {
		this.getUserByEmailMethod(email, new IsUserRegisteredListener(handler));
	}	
	
	public void getUserByEmail(String email, Handler handler) {
		this.getUserByEmailMethod(email, new GetUserByEmailListener(handler));
	}
	
	private void getUserByEmailMethod(String email, RequestListener listener) {
		super.performGet(USERS_SERVICE_BASE_PATH + "/" + email, null, listener);
	}
	
	private class IsUserRegisteredListener implements RequestListener {
		private final Handler handler;
		public IsUserRegisteredListener(Handler handler) {
			this.handler = handler;
		}
		@Override
		public void onComplete(String response) {
			User user = userFromJsonString(response);
			this.handler.sendMessage(handler.obtainMessage(0, user != null));			
		}
	}
	
	private class GetUserByEmailListener implements RequestListener {
		private final Handler handler;		
		public GetUserByEmailListener(Handler handler) {
			this.handler = handler;
		}
		@Override
		public void onComplete(String response) {
			User user = userFromJsonString(response);			
			handler.sendMessage(handler.obtainMessage(0,user));			
		}		
	}
	
	public void createUser(String email, String password, Handler handler) {		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("email", email));
		parameters.add(new BasicNameValuePair("pw", EncryptionUtils.encryptPassword(password)));		
		this.performPost(USERS_SERVICE_BASE_PATH, parameters, new CreateUserListener(handler));
	}
	
	private class CreateUserListener implements RequestListener {
		private final Handler handler;
		public CreateUserListener(Handler handler) {
			this.handler = handler;
		}
		@Override
		public void onComplete(String response) {
			User newUser = userFromJsonString(response);
			this.handler.sendMessage(handler.obtainMessage(0, newUser));
		}
		
	}
	
	private User userFromJsonString(String jsonUser) {
		Gson gson = new Gson();
		User user = gson.fromJson(jsonUser, User.class);
		return user;
	}		
}