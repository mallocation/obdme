package controllers;

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
    
    public static void getAvatar(long id) {
		Vehicle vehicle = Vehicle.findById(id);
		
		Blob avatar = vehicle.getAvatar();
		if (avatar.exists()) {
			renderBinary(avatar.get());
		}
		else {
			renderBinary(VirtualFile.fromRelativePath("/app/files/vehicle/avatar_default.png").getRealFile());
		}
	}

}