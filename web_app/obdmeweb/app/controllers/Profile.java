package controllers;

import models.StatusMessage;
import models.obdmedb.User;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Profile extends Controller {
	
	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("userEmail", user.getEmail());
            renderArgs.put("userFirstName", user.getFirstname());
            renderArgs.put("userLastName", user.getLastname());
            
            //Convert send email boolean into something the jQuery switch understands
            if(user.isSendemail()) {
            	renderArgs.put("sendEmail", "'on'");
            }
            else{
            	renderArgs.put("sendEmail", "'off'");
            }
            
          //Convert send sms boolean into something the jQuery switch understands
            if(user.isSendsms()) {
            	renderArgs.put("sendSMS", "'on'");
            }
            else{
            	renderArgs.put("sendSMS", "'off'");
            }
        }
    }

    public static void index() { 
		render();
	}
    
    public static void sendEmailAJAX(String value) {
    	
    	//Get the user
    	User user = User.find("byEmail", Security.connected()).first();
    	
    	//Set the new switch value
    	if(value.equals("on")) {
    		user.setSendemail(true);
    	}
    	else {
    		user.setSendemail(false);
    	}
    	
    	user._save();
    	
    	Logger.info("Send email switch changed to " + value);
    }
    
    public static void sendSMSAJAX(String value) {
    	
    	//Get the user
    	User user = User.find("byEmail", Security.connected()).first();
    	
    	//Set the new switch value
    	if(value.equals("on")) {
    		user.setSendsms(true);
    	}
    	else {
    		user.setSendsms(false);
    	}
    	
    	user._save();
    	
    	Logger.info("Send SMS switch changed to " + value);
    }
    
    public static void firstNameAJAX(String value) {
    	
    	//Save the users information
    	User user = User.find("byEmail", Security.connected()).first();
    	String previousFirstName = user.getFirstname();
       	user.setFirstname(value);
    	user._save();
    	
    	Logger.info("Lastname changed from " + previousFirstName + " to " + value);
    }
    
    public static void lastNameAJAX(String value) {
    	
    	//Save the users information
    	User user = User.find("byEmail", Security.connected()).first();
    	String previousLastName = user.getLastname();
    	user.setLastname(value);
    	user._save();
    	
    	Logger.info("Lastname changed from " + previousLastName + " to " + value);
    }
    
    public static void passwordChangeAJAX(String currentPassword, String newPassword) {
    	
    	Logger.info(currentPassword + newPassword);
    	//Save the users information
    	User user = User.find("byEmail", Security.connected()).first();
    	if (User.isValidCredentialsClearText(user.getEmail(), currentPassword)) {
    		User.changeUserPassword(user.getEmail(), currentPassword, newPassword);
    		renderJSON(new StatusMessage("ok", "Password Updated", ""));
    	}
    	else {
    		renderJSON(new StatusMessage("fail", "Password Not Updated", ""));
    	}
    }
}