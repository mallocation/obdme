package edu.unl.csce.obdme.data.interfaces;

public interface IUserBase {	
	boolean isRegisteredUser(String email);
	boolean isPasswordCorrect(String email, String password);
}
