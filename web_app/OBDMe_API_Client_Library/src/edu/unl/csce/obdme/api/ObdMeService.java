package edu.unl.csce.obdme.api;

import edu.unl.csce.obdme.api.client.base.UserService;
import edu.unl.csce.obdme.api.client.base.VehicleGraphService;
import edu.unl.csce.obdme.api.client.base.VehicleService;

// TODO: Auto-generated Javadoc
/**
 * The Class ObdMeService.
 */
public class ObdMeService {

	/** The users service. */
	private final UserService usersService;
	
	/** The vehicle service. */
	private final VehicleService vehicleService;
	
	/** The vehicle graph service. */
	private final VehicleGraphService vehicleGraphService;
	
	/**
	 * Construct a new OBDMe Service reference
	 * 
	 * @param apiKey The API key given by the OBDMe system.  Necessary to make requests.
	 */
	public ObdMeService(String apiKey) {	
		this.usersService = new UserService(apiKey);
		this.vehicleService = new VehicleService(apiKey);
		this.vehicleGraphService = new VehicleGraphService(apiKey);
	}
	
	/**
	 * Gets the users service.
	 *
	 * @return the users service
	 */
	public UserService getUsersService() {
		return this.usersService;
	}
	
	/**
	 * Gets the vehicle service.
	 *
	 * @return the vehicle service
	 */
	public VehicleService getVehicleService() {
		return this.vehicleService;
	}
	
	/**
	 * Gets the vehicle graph service.
	 *
	 * @return the vehicle graph service
	 */
	public VehicleGraphService getVehicleGraphService() {
		return this.vehicleGraphService;
	}
}