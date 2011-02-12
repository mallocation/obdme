package controllers.obdme.rest;

import models.obdme.User;
import models.obdme.Vehicles.Vehicle;
import play.data.validation.Required;
import play.mvc.Controller;

public class Vehicles extends Controller {

    public static void getVehicleByVIN(String VIN) {
    	Vehicle vehicle = Vehicle.findByVIN(VIN);    	
    	renderJSON(vehicle);
    }
    
    public static void addVehicle(@Required String VIN, long userid) {
    	Vehicle vehicle = Vehicle.addVehicle(VIN);
    	if (userid > 0) {
    		User.findByUserId(userid).attachVehicle(vehicle);    		
    	}   	
    	renderJSON(vehicle);
    }
    
    public static void attachVehicleToUser(String VIN, long userId) {
    	User user = User.findById(userId);
    	user.attachVehicle(Vehicle.addVehicle(VIN));
    }
    
}