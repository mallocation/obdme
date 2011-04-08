package test.api.graph;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import models.obdmedb.User;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.data.parsing.UrlEncodedParser;
import play.libs.WS;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;
import api.entities.graph.Trip;

import com.google.gson.Gson;

public class TripsTest extends FunctionalTest {
	
	private static final String TRIPS_URL = "/api/graph/trips";
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	
	@Test
	public void createTripForUser() {
		User user = User.createUserFromClearTextCredentials("a@a.com", "qwerty");
		Logger.info("Created user " + user.getEmail());
		
		Map<String, String> parameters = new HashMap<String, String>();
		String tripAlias = "My awesome trip";
		String requestUrl = String.format("%s/email/%s", TRIPS_URL, user.getEmail());
		parameters.put("alias", tripAlias);
		
		Logger.info("POST to: " + requestUrl);
		
		
		//Try to create a trip for the user.
		Response createTripResponse = POST(requestUrl, parameters, new HashMap<String, File>());
		assertIsOk(createTripResponse);
		
		String responseJson = new String(createTripResponse.out.toByteArray());
		
		Logger.info("Create trip response: " + responseJson);
		
		Trip result = new Gson().fromJson(responseJson, Trip.class);
		
		assertTrue(result.getId() > 0L);
		assertEquals(tripAlias, result.getAlias());		
	}
	
	@Test
	public void updateUserTrip() {
		//use the existing test to create a trip
		this.createTripForUser();
		
		Long tripId = models.obdmedb.trips.Trip.getTripsForUser(User.findByEmail("a@a.com")).get(0).getId();
		Logger.info("Testing update of trip " + tripId.toString());
		
		String newAlias = "Trip to shakers!!!!";	
		
		String putBody = "alias=" + WS.encode(newAlias);
		String requestUrl = String.format("%s/email/%s/id/%s", TRIPS_URL, "a@a.com", tripId);
		
		Logger.info("PUT to: " + requestUrl);
		Logger.info("body: " + putBody);
		Response updateTrip = PUT(requestUrl, APPLICATION_X_WWW_FORM_URLENCODED, putBody);
		
		assertIsOk(updateTrip);
		
		String responseJson = new String(updateTrip.out.toByteArray());
		
		Logger.info("Update trip response: " + responseJson);
		
		Trip result = new Gson().fromJson(responseJson, Trip.class);
		assertEquals(tripId, result.getId());
		assertEquals(newAlias, result.getAlias());		
	}
}
