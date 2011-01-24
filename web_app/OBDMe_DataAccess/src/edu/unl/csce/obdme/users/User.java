package edu.unl.csce.obdme.users;

import edu.unl.csce.obdme.data.interfaces.IUserBase;

public class User {
	
	private IUserBase data;	
	
	public User(IUserBase data) {
		this.data = data;
	}
	
	public boolean isRegisteredUser(String email) {
		return this.data.isRegisteredUser(email);
	}	
	
	public boolean validateUserEmailAndPassword(String email, String password) {
		//TODO place some validation in there to check for email address pattern		
		return this.data.validateUserPassword(email, password);		
	}
}