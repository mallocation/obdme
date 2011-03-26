package controllers.api;

import models.obdmedb.User;
import api.entities.ApiError;
import controllers.obdme.rest.controllerbase.LoggedController;
import controllers.obdme.rest.controllerbase.SecureController;
//import models.obdme.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.results.Forbidden;

/**
 * This class represents the controller used for all things pertaining to users within the obdme api.
 */
public class Users extends LoggedController {
    
    /**
     * Gets a user by email.
     * 
     * This method returns a user in json format.  If the user does not exist,
     * an api error will be thrown.
     *
     * @param email the email of the user to retrieve
     */
    public static void getUser(@Required String email) {
    	User user = User.findByEmail(email);
    	if (user != null) {
    		renderJSON(new api.entities.User(user));
    	} else {
    		renderJSON(new ApiError(Messages.get("api.users.error.user.notexist", email)));
    	}    	
    }
    
    /**
     * Creates a user within the obdme system.
     * 
     * This method will render the new user in json format if it does not exist.
     * If the email already exists, an api error will be thrown.
     *
     * @param email the email of the user
     * @param pw the pw (encrypted) of the user
     */
    public static void createUser(@Required @Email String email, @Required String pw) {
    	if (User.findByEmail(email) != null) {
    		renderJSON(new ApiError(Messages.get("api.users.error.adduser.errorexists", email)));
    	} else {
    		User newUser = User.createUserFromEncryptedCredentials(email, pw);
    		renderJSON(new api.entities.User(newUser));
    	}    	
    }
    
    
    /**
     * Validate user credentials within the obdme system.
     * 
     * This method will check the obdme system for a user with the corresponding
     * email and password (encrypted).  If the credentials are valid, the corresponding
     * user will be returned in json format.  Otherwise, an api error will be thrown,
     * saying the login is invalid.     * 
     *
     * @param email the email of the user to authenticate
     * @param pw the pw of the user to authenticate (encrypted)
     */
    public static void validateUserCredentials(@Required @Email String email, @Required String pw) {
    	User authUser = User.validateUserCredentialsEncrypted(email, pw);
    	
    	if (authUser == null) {
    		//invalid login
    		renderJSON(new ApiError(Messages.get("api.users.error.login.invalid")));  		
    	}
    	
    	renderJSON(new api.entities.User(authUser));
    }
}