package controllers.api;

import models.obdme.User;
import models.obdme.Vehicles.Vehicle;
import play.data.validation.Required;
import play.mvc.Controller;

public class Vehicles extends Controller {

    public static void getVehicleByVIN(String VIN) { 	
    	renderJSON(Vehicle.findByVIN(VIN));
    }
    
    public static void addVehicle(@Required String VIN, long userid) {
    	Vehicle vehicle = Vehicle.addVehicle(VIN);
    	if (userid > 0) {
    		User user = User.findByUserId(userid);
    		if (user != null) {
    			user.vehicles.add(vehicle);
    			user.save();
    		}
    	}
    	renderJSON(vehicle);
    }
    
    public static void getVehiclesForUser(String email) {
    	User user = User.findByEmail(email);
    	if (user != null) {
    		renderJSON(user.vehicles);
    	}
    }

}