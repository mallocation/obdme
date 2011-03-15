package edu.unl.csce.obdme.api.client.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.unl.csce.obdme.api.entities.UserVehicle;
import edu.unl.csce.obdme.api.entities.Vehicle;
import edu.unl.csce.obdme.client.http.handler.BasicObjectHandler;
import edu.unl.csce.obdme.client.http.handler.BasicTypeHandler;

/**
 * The Vehicle Service class.
 * Use this class to communicate with vehicle-oriented data
 * in the OBDMe system.
 */
public class VehicleService extends ProtectedServiceWrapper {
	
	/** The base path for the vehicles service. */
	private static final String VEHICLE_SERVICE_BASE_PATH = "/vehicles";

	/**
	 * Instantiates a new vehicle service.
	 *
	 * @param apiKey the api key of the calling application.
	 */
	public VehicleService(String apiKey) {
		super(apiKey);
	}
	
	/**
	 * Adds or updates the properties of a user owning a vehicle.
	 *
	 * @param VIN the VIN of the vehicle
	 * @param email the email of the user
	 * @param alias the alias of the vehicle
	 * @param handler the handler
	 */
	public void addUpdateVehicleToUserAsync(String VIN, String email, String alias, BasicObjectHandler<UserVehicle> handler) {
		String requestPath = String.format("%s/vin/%s/user/%s", VEHICLE_SERVICE_BASE_PATH, VIN, email);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("alias", alias));
		super.performPutAsync(requestPath, parameters, handler);
	}
	
	/**
	 * Gets a vehicle from the obdme system, asynchronously.
	 * 
	 * If the vehicle does not exist, and ObdmeException will be thrown.
	 * 
	 * @param VIN the VIN of the vehicle
	 * @param handler the handler
	 */
	public void getVehicleAsync(String VIN, BasicObjectHandler<Vehicle> handler) {
		String requestPath = String.format("%s/%s", VEHICLE_SERVICE_BASE_PATH, VIN);
		super.performGetAsync(requestPath, null, handler);
	}
	
	/**
	 * Gets a list of vehicles that the user has registered.
	 *
	 * @param userEmail the user's email
	 * @param handler the handler
	 */
	public void getVehiclesForUserAsync(String userEmail, BasicTypeHandler<List<UserVehicle>> handler) {
		String requestPath = String.format("%s/user/%s", VEHICLE_SERVICE_BASE_PATH, userEmail);
		super.performGetAsync(requestPath, null, handler);
	}
	
}
