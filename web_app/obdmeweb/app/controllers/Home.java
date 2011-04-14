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
import models.obdmedb.trips.Trip.LatestTripForUser;

@With(Secure.class)
public class Home extends Controller {

	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("vehicles", UserVehicle.getVehiclesForUser(user));

			List<LatestTripForUser> userTrips = Trip.getLatestTripsListForUser(user, 3);
			render(userTrips);
        }
    }

    public static void index() {
		render();
	}

}