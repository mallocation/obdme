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
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.obdme.utils.IconFactory;

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

		User user = User.find("byEmail", Security.connected()).first();
		if (user != null) {
			HashMap<String, Blob> blobMap = IconFactory.makeIcons(photo);

			//Commit the image to the database

			user.setAvatar512(blobMap.get("512"));
			user.setAvatar256(blobMap.get("256"));
			user.setAvatar128(blobMap.get("128"));
			user.setAvatar64(blobMap.get("64"));
			user.setAvatar32(blobMap.get("32"));
			user.save();

		}

		//Redirect back to the profile page
		Profile.index();
	}

	public static void getAvatar(int size) {
		User user = User.find("byEmail", Security.connected()).first();
				
		if (user != null) {
			switch(size) {
			case 512:
				Blob avatar = user.getAvatar512();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(VirtualFile.fromRelativePath("/app/files/profile/avatar_default.png").getRealFile());
				}
				break;
			case 256:
				avatar = user.getAvatar256();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(VirtualFile.fromRelativePath("/app/files/profile/avatar_default.png").getRealFile());
				}
				break;
			case 128:
				avatar = user.getAvatar128();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(VirtualFile.fromRelativePath("/app/files/profile/avatar_default.png").getRealFile());
				}
				break;
			case 64:
				avatar = user.getAvatar64();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(VirtualFile.fromRelativePath("/app/files/profile/avatar_default.png").getRealFile());
				}
				break;
			case 32:
				avatar = user.getAvatar32();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(VirtualFile.fromRelativePath("/app/files/profile/avatar_default.png").getRealFile());
				}
				break;
			default:
				avatar = user.getAvatar128();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(VirtualFile.fromRelativePath("/app/files/profile/avatar_default.png").getRealFile());
				}
				break;	
			}
		}
		else {
			//TODO Log Error
		}
	}
}