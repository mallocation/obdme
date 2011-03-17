package controllers.api;

import java.util.ArrayList;
import java.util.List;

import api.entities.ApiError;

import models.obdme.User;
import models.obdme.Vehicles.UserVehicle;
import models.obdme.Vehicles.Vehicle;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Controller;

public class Vehicles extends Controller {

    public static void getVehicleByVIN(String VIN) {
    	Vehicle v = Vehicle.findByVIN(VIN);
    	if (v == null) {
    		renderJSON(new ApiError(Messages.get("api.vehicles.error.vehicle.notexist", VIN)));
    	}    	
    	renderJSON(new api.entities.Vehicle(v.getId(), v.VIN));
    }
     
    public static void addUpdateVehicleOwner(String email, String VIN, String alias) {
    	Vehicle vehicle;
    	User user;
    	
    	/* first take care of the vehicle */
    	vehicle = Vehicle.findByVIN(VIN);
    	
    	/* create the vehicle if it does not exist */
    	if (vehicle == null) {
    		vehicle = new Vehicle();
    		vehicle.VIN = VIN;
    		vehicle.validateAndSave();
    	}
    	
    	/* retrieve the user */
    	user = User.findByEmail(email);
    	
    	/* get the user/vehicle relationship */
    	UserVehicle uv = UserVehicle.getVehicleForUser(user, vehicle);
    	if (uv == null) {
    		uv = new UserVehicle();
    		uv.setUser(user);
    		uv.setVehicle(vehicle);
    	}
    	uv.setAlias(alias);
    	uv.validateAndSave();
    	
    	api.entities.UserVehicle result = new api.entities.UserVehicle(vehicle.getId(), vehicle.VIN, uv.alias);
    	renderJSON(result);
    }
    
    public static void getVehiclesForUser(String email) {
    	List<UserVehicle> userVehicles = UserVehicle.getVehiclesForUser(User.findByEmail(email));
    	List<api.entities.UserVehicle> result = new ArrayList<api.entities.UserVehicle>();
    	for (UserVehicle uv : userVehicles) {
    		result.add(new api.entities.UserVehicle(uv.id, uv.vehicle.VIN, uv.alias));    		
    	}
    	renderJSON(result);
    }

}