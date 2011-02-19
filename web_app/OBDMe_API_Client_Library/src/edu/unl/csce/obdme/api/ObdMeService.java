package edu.unl.csce.obdme.api;

import edu.unl.csce.obdme.api.client.base.UserService;
import edu.unl.csce.obdme.api.client.base.VehicleService;

public class ObdMeService {

	private final UserService usersService;
	private final VehicleService vehicleService;
	
	/*
	 * Construct a new OBDMe Service reference
	 * 
	 * @param apiKey The API key given by the OBDMe system.  Necessary to make requests.
	 */
	public ObdMeService(String apiKey) {	
		this.usersService = new UserService(apiKey);
		this.vehicleService = new VehicleService(apiKey);
	}
	
	public UserService getUsersService() {
		return this.usersService;
	}
	
	public VehicleService getVehicleService() {
		return this.vehicleService;
	}
}