package controllers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.NumberFormatter;

import models.obdmedb.User;
import models.obdmedb.statistics.VehicleDataPoint;
import models.obdmedb.trips.Trip;
import models.obdmedb.trips.Trip.LatestTripForUser;
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
		public String dataPoints;
		public String vehicleName;
		public IndexTripBinding(Long id, String tripName, String vehicleName, Long dataPoints) {
			this.tripId = id;
			this.tripName = tripName;
			this.vehicleName = vehicleName;
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setGroupingUsed(true);
			this.dataPoints = nf.format(dataPoints);		
		}
	}
	
    public static void index() {
    	User user = User.findByEmail(Security.connected());
    	
    	//Get the user's trips
    	//List<Trip> userTrips = Trip.getLatestTripsForUser(user, 5);
    	List<LatestTripForUser> userTrips = Trip.getLatestTripsListForUser(user, 5);
    	List<IndexTripBinding> trips = new ArrayList<IndexTripBinding>();
    	for (LatestTripForUser userTrip : userTrips) {
    		trips.add(new IndexTripBinding(userTrip.tripId, userTrip.tripAlias, userTrip.vehicleAlias, VehicleDataPoint.selectDataPointCountForTrip(userTrip.tripId)));
    	} 	
    	render(trips);
	}
}