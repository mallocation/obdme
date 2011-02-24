package controllers.api;

import controllers.obdme.rest.controllerbase.LoggedController;
import controllers.obdme.rest.controllerbase.SecureController;
import models.obdme.User;
import play.data.validation.Required;

public class Users extends LoggedController {
    
    public static void getUser(String email) {
    	renderJSON(User.findByEmail(email));
    }
    
    public static void createUser(@Required String email, @Required String pw) {
    	renderJSON(User.createUser(email, pw));
    }
    
    public static void validateUserCredentials(String email, String pw) {
    	renderJSON(User.validateUserLogin(email, pw));
    }
}