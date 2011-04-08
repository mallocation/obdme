package edu.unl.csce.obdme.api.test;

import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.api.entities.graph.trips.Trip;
import edu.unl.csce.obdme.client.http.handler.BasicObjectHandler;
import android.test.AndroidTestCase;

public class TripsServiceTest extends AndroidTestCase {
	
	private static final String email = "farmboy30@gmail.com";
	private static final String tripAlias = "TRIP OH YEAH!";
	
	private ObdMeService service;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		service = new ObdMeService("abcd");
	}
	
	public void testCreateTripForUser() {
		final Object lock = new Object();
		BasicObjectHandler<Trip> handler = new BasicObjectHandler<Trip>(Trip.class) {

			@Override
			public void onCommException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}				
			}

			@Override
			public void onObdmeException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}				
			}

			@Override
			public void onOperationCompleted(Trip result) {
				assertEquals(tripAlias, result.getAlias());
				assertTrue(result.getId() > 0L);
				
			}
		};
		service.getTripService().createTrip(email, tripAlias, handler);
	}
	
}
