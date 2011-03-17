package controllers.api;

import models.obdme.User;
import models.obdme.Vehicles.Vehicle;
import models.obdme.graph.Trip;
import play.data.validation.Required;
import play.mvc.Controller;

// TODO: Auto-generated Javadoc
/**
 * The Class Trips.
 */
public class Trips extends Controller {
	
	/**
	 * Creates a trip for a user and a vehicle
	 *
	 * @param email the email of the registered user.
	 * @param VIN the VIN of the vehicle.
	 * @param alias the alias of the trip.
	 */
	public static void createTripForUserByVIN(@Required String email, @Required String VIN, String alias) {
		User user = User.findByEmail(email);
		Vehicle vehicle = Vehicle.findByVIN(VIN);
		
		validation.required(user);
		validation.required(vehicle);
		
		//Create the trip, and save to db
		Trip trip = new Trip(user, vehicle, alias);
		trip.validateAndSave();
				
		renderJSON(trip);		
	}

}