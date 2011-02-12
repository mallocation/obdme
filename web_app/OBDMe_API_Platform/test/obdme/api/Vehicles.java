package obdme.api;

import java.net.URLEncoder;

import org.junit.Test;

import play.Logger;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class Vehicles extends FunctionalTest {
	
	private static final String TEST_VIN = "1M8GDM9AXKP042788";
	
	@Test
	public void testAddVehicle() {
		/* build up the body for adding a vehicle */		
		String body = String.format("VIN=%s", TEST_VIN);
		Response response = PUT("/api/vehicles", "text/html", body);		
		assertIsOk(response);
		Logger.info("Test Add Vehicle Completed");
	}
	
	@Test
	public void testGetVehicle() {
		Response response = GET(String.format("/api/vehicles/%s", TEST_VIN));		
		assertNotNull(response);
		Logger.info("Test Get Vehicle Completed.");
	}
	
	
	
	

}
