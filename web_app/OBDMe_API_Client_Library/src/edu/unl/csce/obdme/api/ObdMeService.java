package edu.unl.csce.obdme.api;

import edu.unl.csce.obdme.api.client.base.UserService;

public class ObdMeService {

	private final UserService usersService;
	
	/*
	 * Construct a new OBDMe Service reference
	 * 
	 * @param apiKey The API key given by the OBDMe system.  Necessary to make requests.
	 */
	public ObdMeService(String apiKey) {	
		usersService = new UserService(apiKey);
	}
	
	public UserService getUsersService() {
		return this.usersService;
	}	
}