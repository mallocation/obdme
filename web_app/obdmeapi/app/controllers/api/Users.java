package controllers.api;

import api.entities.ApiError;
import controllers.obdme.rest.controllerbase.LoggedController;
import controllers.obdme.rest.controllerbase.SecureController;
import models.obdme.User;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.results.Forbidden;

public class Users extends LoggedController {
    
    public static void getUser(String email) {
    	User user = User.findByEmail(email);
    	api.entities.User entity = null;
    	if (user != null) {
    		entity = api.entities.User.fromModelUser(user);
    		renderJSON(entity);
    	} else {
    		renderJSON(new ApiError(Messages.get("api.users.error.user.notexist", email)));
    	}    	
    }
    
    public static void createUser(@Required String email, @Required String pw) {
    	if (User.findByEmail(email) != null) {
    		renderJSON(new ApiError(Messages.get("api.users.error.adduser.errorexists", email)));
    	} else {
    		User newUser = User.createUser(email, pw);
    		renderJSON(api.entities.User.fromModelUser(newUser));
    	}    	
    }
    
    public static void validateUserCredentials(String email, String pw) {
    	User authorizedUser = User.validateUserLogin(email, pw);
    	
    	if (authorizedUser == null) {
    		// invalid login
    		renderJSON(new ApiError(Messages.get("api.users.error.login.invalid")));
    	}
    	
    	renderJSON(api.entities.User.fromModelUser(authorizedUser));
    }
}