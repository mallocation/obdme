package controllers;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import models.obdme.User;
import controllers.obdme.rest.LoggedController;
import edu.unl.csce.obdme.http.encryption.EncryptionUtils;

public class Users extends LoggedController {

    public static void index() {
    	List<User> users = models.obdme.User.all().fetch();
    	renderJSON(users);
    }
    
    public static void getUser(String email) {
    	models.obdme.User user = models.obdme.User.findByEmail(email);
    	renderJSON(user);
    }
    
    public static void createUser(String email, String password) {
    	models.obdme.User user = new models.obdme.User(email, password);
    	user.validateAndSave();
    }
    
    public static void encryptPassword() {
    	String pw = request.params.get("pw");
    	try {
    		renderText(EncryptionUtils.encryptPassword(pw));	
    	} catch (NoSuchAlgorithmException nsae) {
    		renderText("error");
    	}
    	
    }    
    public static void isEmailRegistered(String email) {
    	
    	
//    	//Check if a user exists for this email
//    	renderJSON(oFactory.UsersDAO().isRegisteredUser(email));    	
    }
    
    public static void isPasswordCorrect(String email) {
    	if (!request.params._contains("pw")) {
    		badRequest();
    	}
    	String password = request.params.get("pw");    	
    	//run pr_validateuserpassword
    	//renderJSON(oFactory.UsersDAO().validateUserEmailAndPassword(email, password));
    }

}
