package controllers;

import java.util.ArrayList;
import java.util.List;

import models.obdmedb.User;
import models.obdmedb.trips.Trip;
import play.libs.XML;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.results.RenderXml;
import play.templates.Template;

@With(Secure.class)
public class Trips extends Controller {
	
	public static class IndexTripBinding {
		public Long tripId;
		public String tripName;
		public IndexTripBinding(Long id, String name) {
			this.tripId = id;
			this.tripName = name;
		}
	}
	
    public static void index() {
    	User user = User.findByEmail(Security.connected());
    	
    	//Get the user's trips
    	List<Trip> userTrips = Trip.getLatestTripsForUser(user, 5);
    	List<IndexTripBinding> trips = new ArrayList<IndexTripBinding>();
    	for (Trip userTrip : userTrips) {
    		trips.add(new IndexTripBinding(userTrip.getId(), userTrip.getAlias()));
    	} 	
    	render(trips);
	}
}