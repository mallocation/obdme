package obdmedb.tests.trips;

import java.util.List;

import models.obdmedb.User;
import models.obdmedb.trips.Trip;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class TripTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void testCreateTripForUser() {
		//first create the user.
		String userEmail = "a@a.com";
		User operator = User.createUserFromClearTextCredentials(userEmail, "qwerty");
		assertNotNull(operator);
		
		String tripAlias = "bcat's trip around teh world.";
		
		//Next create the trip.
		Trip trip = new Trip(operator, tripAlias);
		
		assertTrue(trip.validateAndSave());		
		assertNotNull(trip);
		assertTrue(trip.getId() > 0L);
		assertEquals(operator.getEmail(), trip.getOperator().getEmail());	
	}
	
	@Test
	public void testCreateMultipleTripsForUser() {
		String userEmail = "a@a.com";
		User operator = User.createUserFromClearTextCredentials(userEmail, "qwerty");
		
		String tripAlias1 = "shaker's trip";
		String tripAlias2 = "church trip";
		
		Trip trip1 = new Trip(operator, tripAlias1);
		Trip trip2 = new Trip(operator, tripAlias2);
		assertTrue(trip1.validateAndSave());
		assertTrue(trip2.validateAndSave());
		
		assertEquals(trip1.getOperator().getEmail(), userEmail);
		assertEquals(trip2.getOperator().getEmail(), userEmail);
		
		List<Trip> userTrips = Trip.getTripsForUser(operator);
		
		assertEquals(userTrips.size(), 2);		
	}
	

}
