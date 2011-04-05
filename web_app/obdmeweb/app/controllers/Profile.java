package controllers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import models.StatusMessage;
import models.obdmedb.User;
import play.Logger;
import play.db.jpa.Blob;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import play.vfs.VirtualFile;

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
    }
    
    public static void firstNameAJAX(String value) {
    	
    	//Save the users information
    	User user = User.find("byEmail", Security.connected()).first();
       	user.setFirstname(value);
    	user._save();
    }
    
    public static void lastNameAJAX(String value) {
    	
    	//Save the users information
    	User user = User.find("byEmail", Security.connected()).first();
    	user.setLastname(value);
    	user._save();
    }
    
    public static void passwordChangeAJAX(String currentPassword, String newPassword) {
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

	public static void newProfileAJAX() {
    
    	//Save the users information
    	User user = User.find("byEmail", Security.connected()).first();
    	if (user.getFirstname() == null && user.getLastname() == null) {
    		renderJSON(new StatusMessage("new", "Connected User is New", ""));
    	}
    	else {
    		renderJSON(new StatusMessage("existing", "Connected User is existing", ""));
    	}
    }
	
	public static void uploadAvatar(File photo) {	
		
			int IMAGE_WIDTH = 100;
			int IMAGE_HEIGHT = 100;
		
			Logger.info("upload photo");
			try {
				//Read in the uploaded image
				BufferedImage bufferedImage = ImageIO.read(photo);
				
				//Make a new image to store the final image in
				BufferedImage resizedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				
				//Create a manipulatable graphics object
				Graphics2D imageGraphic = resizedImage.createGraphics();
				
				//Draw the input image
				imageGraphic.drawImage(bufferedImage, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
				
				//Load and draw the icon overall
				BufferedImage bufferedOverlayImage = ImageIO.read(VirtualFile.fromRelativePath("/app/files/profile/avatar_overlay.png").getRealFile());
				imageGraphic.drawImage(bufferedOverlayImage, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
				
				//Dispose
				imageGraphic.dispose();
				
				//Load the icon mask
				BufferedImage bufferedOverlayMask = ImageIO.read(VirtualFile.fromRelativePath("/app/files/profile/avatar_mask.png").getRealFile());
				Color transparentColor = new Color(0, 0, 0, 0);
				Color maskColor = new Color(255,0,255);
				
				//for each mask pixel 
				for (int i = 0; i < IMAGE_WIDTH; i++) {
					for (int j = 0; j < IMAGE_HEIGHT; j++) {
						if (bufferedOverlayMask.getRGB(i, j) == maskColor.getRGB()) {
							resizedImage.setRGB(i, j, transparentColor.getRGB());
						}
					}
				}
				
				//Output the image as a Byte Array Output Stream
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(resizedImage, "png", baos);
				
				//Save the Byte Array Output Stream into the blob
				InputStream imageIS = new ByteArrayInputStream(baos.toByteArray());
				Blob imageBlob = new Blob();
				imageBlob.set(imageIS, "image");
				
				//Commit the image to the database
				User user = User.find("byEmail", Security.connected()).first();
				user.setAvatar(imageBlob);
				user.save();
				
			} catch (IOException e) {
				e.printStackTrace();
				Logger.error("There was an IO exception", e);
			}
			
			//Redirect back to the profile page
			Profile.index();
	}
	
	public static void getAvatar() {
		User user = User.find("byEmail", Security.connected()).first();
		Blob avatar = user.getAvatar();
		if (avatar.exists()) {
			renderBinary(avatar.get());
		}
		else {
			renderBinary(VirtualFile.fromRelativePath("/app/files/profile/avatar_default.png").getRealFile());
		}
	}
}