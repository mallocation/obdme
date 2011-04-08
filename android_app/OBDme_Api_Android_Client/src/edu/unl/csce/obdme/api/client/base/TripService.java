package edu.unl.csce.obdme.api.client.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.unl.csce.obdme.api.entities.graph.trips.Trip;
import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;
import edu.unl.csce.obdme.client.http.handler.BasicObjectHandler;


/**
 * The Class TripService.
 * This class is used to interact with trips in the obdme system.
 */
public class TripService extends ServiceWrapper {
	
	/** The Constant TRIPS_BASE_URL. */
	private static final String TRIPS_BASE_URL = "/graph/trips";
	
	
	/**
	 * Creates the trip.
	 *
	 * @param userEmail the user's email to tie the trip to.
	 * @param alias the alias of the trip
	 * @param handler the handler
	 */
	public void createTripAsync(String userEmail, String alias, BasicObjectHandler<Trip> handler) {
		String request = String.format("%s/email/%s", TRIPS_BASE_URL, userEmail);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("alias", alias));
		super.performPostAsync(request, parameters, handler);
	}
	
	public Trip createTrip(String userEmail, String alias) throws ObdmeException, CommException {
		String request = String.format("%s/email/%s", TRIPS_BASE_URL, userEmail);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("alias", alias));
		
		
		String response = super.performPost(request, parameters);
		try {
			Trip trip = new ObjectMapper().readValue(response, Trip.class);
			return trip;
		} catch (JsonParseException e) {
			throw new ObdmeException("Could not parse trip response.");
		} catch (JsonMappingException e) {
			throw new ObdmeException("Could not parse trip response.");
		} catch (IOException e) {
			throw new ObdmeException("Could not parse trip response.");
		}		
	}
	
	/**
	 * Update trip.
	 *
	 * @param email the user's email
	 * @param tripId the trip id
	 * @param alias the alias of the trip
	 * @param handler the handler
	 */
	public void updateTripAsync(String email, Long tripId, String alias, BasicObjectHandler<Trip> handler) {
		String request = String.format("%s/email/%s/id/%s", TRIPS_BASE_URL, email, tripId.toString());
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("alias", alias));
		super.performPutAsync(request, parameters, handler);
	}	

}