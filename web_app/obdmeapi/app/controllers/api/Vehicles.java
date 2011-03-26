package controllers.api;

import java.util.ArrayList;
import java.util.List;

import models.obdmedb.User;
import models.obdmedb.vehicles.UserVehicle;
import models.obdmedb.vehicles.Vehicle;
import play.i18n.Messages;
import play.mvc.Controller;
import api.entities.ApiError;

/**
 * This controller handles all things Vehicle related to the obdme api.
 */
public class Vehicles extends Controller {
	
	/**
	 * Adds/updates a vehicle in the obdme system.
	 * At this time there are no properties to update with a vehicle, so this method
	 * simply adds a vehicle to the obdme system if it does not already exist.
	 * 
	 * This method renders the resulting vehicle in json.
	 *
	 * @param VIN the VIN of the vehicle.
	 */
	public static void addUpdateVehicle(String VIN) {
		Vehicle v = Vehicle.addVehicleIfNotExist(VIN);
		renderJSON(new api.entities.Vehicle(v));
	}

    /**
     * Adds/updates a vehicle owner and the owner's alias.
     * 
     * @param email the email of the user
     * @param VIN the VIN of the vehicle
     * @param alias the alias the user has used for the vehicle.
     */
    public static void addUpdateVehicleOwner(String email, String VIN, String alias) {
    	Vehicle vehicle;
    	User user;
    	
    	/* first, make sure we've got a user */
    	user = User.findByEmail(email);
    	if (user == null) {
    		renderJSON(new ApiError(Messages.get("api.error.user.notexist", email)));
    	}
    	
    	/* next, take care of the vehicle */
    	vehicle = Vehicle.findByVIN(VIN);
    	
    	/* create the vehicle if it does not exist */
    	if (vehicle == null) {
    		vehicle = Vehicle.addVehicleIfNotExist(VIN);
    	}    	
    	
    	/* get the user/vehicle relationship */
    	UserVehicle uv = UserVehicle.getVehicleForUser(user, vehicle);
    	if (uv == null) {
    		uv = new UserVehicle();
    		uv.user = user;
    		uv.vehicle = vehicle;
    	}
    	uv.alias = alias;
    	uv.validateAndSave();
    	
    	api.entities.UserVehicle result = new api.entities.UserVehicle(vehicle.getId(), vehicle.VIN, uv.alias);
    	renderJSON(result);
    }
    
    /**
     * Gets a vehicle by VIN.
     * If the VIN does not exist, an api error is thrown.
     *
     * @param VIN the VIN of the vehicle to retrieve.
     */
    public static void getVehicleByVIN(String VIN) {
    	Vehicle v = Vehicle.findByVIN(VIN);
    	if (v == null) {
    		renderJSON(new ApiError(Messages.get("api.vehicles.error.vehicle.notexist", VIN)));
    	}    	
    	renderJSON(new api.entities.Vehicle(v));
    }
    
    /**
     * Gets the vehicles that are registered for a user.
     *
     * @param email the email of the user
     */
    public static void getVehiclesForUser(String email) {
    	List<UserVehicle> userVehicles = UserVehicle.getVehiclesForUser(User.findByEmail(email));
    	List<api.entities.UserVehicle> result = new ArrayList<api.entities.UserVehicle>();
    	for (UserVehicle uv : userVehicles) {
    		result.add(new api.entities.UserVehicle(uv.id, uv.vehicle.VIN, uv.alias));    		
    	}
    	renderJSON(result);
    }

}