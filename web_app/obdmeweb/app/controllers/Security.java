package controllers;

import play.Logger;
import models.obdmedb.User;
import models.*;
 
public class Security extends Secure.Security {
 
    static boolean authentify(String username, String password) {
		boolean validated = User.isValidCredentialsClearText(username, password);
		Logger.info("User Activity: " + username + " requesting authentication. User validated: " + validated + ".");
        return validated;
    }

	static void onAuthenticated() {
	    Application.index();
	}
	
	static void onDisconnected() {
	    Application.index();
	}
    
}