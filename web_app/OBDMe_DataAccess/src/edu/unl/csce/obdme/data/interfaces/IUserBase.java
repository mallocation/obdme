package edu.unl.csce.obdme.data.interfaces;

public interface IUserBase {
	
	/*
	 * This method determines if an email represents a registered user.
	 */
	boolean isRegisteredUser(String email);
	
	/*
	 * This method will determine if the given email and password represent
	 * a valid user in the OBDMe system.
	 * @param email email of the user
	 * @param password password (encrypted) for the user
	 */
	boolean validateUserPassword(String email, String password);
}
