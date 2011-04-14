package controllers;

import play.mvc.Controller;
import play.mvc.With;

import play.mvc.Before;
import models.obdmedb.User;
import models.obdmedb.vehicles.UserVehicle;
import models.obdmedb.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;
import models.obdmedb.trips.Trip;

@With(Secure.class)
public class Home extends Controller {

	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("vehicles", UserVehicle.getVehiclesForUser(user));

			List<Trip> userTrips = Trip.getLatestTripsForUser(user, 4);
	    	List<IndexTripBinding> trips = new ArrayList<IndexTripBinding>();
	    	for (Trip userTrip : userTrips) {
	    		trips.add(new IndexTripBinding(userTrip.getId(), userTrip.getAlias()));
	    	}
			render(trips);
        }
    }

	public static class IndexTripBinding {
		public Long tripId;
		public String tripName;
		public IndexTripBinding(Long id, String name) {
			this.tripId = id;
			this.tripName = name;
		}
	}

    public static void index() {    	
		render();
	}

}