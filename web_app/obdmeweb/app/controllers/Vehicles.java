package controllers;

import java.io.File;
import java.util.HashMap;

import com.obdme.utils.IconFactory;

import models.StatusMessage;
import models.obdmedb.User;
import models.obdmedb.vehicles.UserVehicle;
import models.obdmedb.vehicles.Vehicle;
import play.Logger;
import play.db.jpa.Blob;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import play.vfs.VirtualFile;

@With(Secure.class)
public class Vehicles extends Controller {

	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("vehicles", UserVehicle.getVehiclesForUser(user));
        }
    }
	
    public static void index() {    	
		render();
	}
    
    public static void updateVehicleAJAX(Long vehicleId, String alias, String make, String model, String year) {
       	//Get the user thats logged in
    	User user = User.find("byEmail", Security.connected()).first();
    	
    	//Get the vehicle that the user is modifying
    	UserVehicle uservehicle = UserVehicle.getVehicleForUser(user, vehicleId);
    	
    	//If a vehicle was returned (the user owns the vehicle)
    	if(uservehicle != null) {
    		Vehicle vehicle = uservehicle.getVehicle();
    		uservehicle.setAlias(alias);
    		vehicle.setMaker(make);
    		vehicle.setModel(model);
    		vehicle.setYear(year);
    		vehicle.save();
    		uservehicle.save();
    		
    		renderJSON(new StatusMessage("ok", "Vehicle Updated", ""));
    	}
    	else {
    		Logger.warn("Security Exception: User " + user.email + " tried to modify a vehicle that does not belong to them.");
    		renderJSON(new StatusMessage("failure", "Vehicle Not Updated", ""));
    	}
    }
    
	public static void uploadAvatar(Long vehicleId, File photo) {	

		User user = User.find("byEmail", Security.connected()).first();
		UserVehicle uservehicle = UserVehicle.getVehicleForUser(user, vehicleId);
		
		if (uservehicle != null) {
			HashMap<String, Blob> blobMap = IconFactory.makeIcons(photo);
			Vehicle vehicle = uservehicle.getVehicle();
			
			//Commit the image to the database
			vehicle.setAvatar512(blobMap.get("512"));
			vehicle.setAvatar256(blobMap.get("256"));
			vehicle.setAvatar128(blobMap.get("128"));
			vehicle.setAvatar64(blobMap.get("64"));
			vehicle.setAvatar32(blobMap.get("32"));
			
			vehicle.save();
		}

		//Redirect back to the profile page
		Vehicles.index();
	}
    
    public static void getAvatar(long id, int size) {
		User user = User.find("byEmail", Security.connected()).first();
		UserVehicle uservehicle = UserVehicle.getVehicleForUser(user, id);
				
		if (uservehicle != null) {
			
			switch(size) {
			case 512:
				Blob avatar = uservehicle.getVehicle().getAvatar512();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(IconFactory.getVehicleDefaut(512));
				}
				break;
			case 256:
				avatar = uservehicle.getVehicle().getAvatar256();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(IconFactory.getVehicleDefaut(256));
				}
				break;
			case 128:
				avatar = uservehicle.getVehicle().getAvatar128();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(IconFactory.getVehicleDefaut(128));
				}
				break;
			case 64:
				avatar = uservehicle.getVehicle().getAvatar64();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(IconFactory.getVehicleDefaut(64));
				}
				break;
			case 32:
				avatar = uservehicle.getVehicle().getAvatar32();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(IconFactory.getVehicleDefaut(32));
				}
				break;
			default:
				avatar = uservehicle.getVehicle().getAvatar128();
				if (avatar.exists()) {
					renderBinary(avatar.get());
				}
				else {
					renderBinary(IconFactory.getVehicleDefaut(128));
				}
				break;	
			}
		}
		else {
			//TODO Log Error
		}
	}

}