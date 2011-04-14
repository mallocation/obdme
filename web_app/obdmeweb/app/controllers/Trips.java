package controllers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.NumberFormatter;

import models.obdmedb.User;
import models.obdmedb.statistics.VehicleDataPoint;
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
		public String dataPoints;
		public IndexTripBinding(Long id, String name, Long dataPoints) {
			this.tripId = id;
			this.tripName = name;			
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setGroupingUsed(true);
			this.dataPoints = nf.format(dataPoints);		
		}
	}
	
    public static void index() {
    	User user = User.findByEmail(Security.connected());
    	
    	//Get the user's trips
    	List<Trip> userTrips = Trip.getLatestTripsForUser(user, 5);
    	List<IndexTripBinding> trips = new ArrayList<IndexTripBinding>();
    	for (Trip userTrip : userTrips) {
    		trips.add(new IndexTripBinding(userTrip.getId(), userTrip.getAlias(), VehicleDataPoint.selectDataPointCountForTrip(userTrip)));
    	} 	
    	render(trips);
	}
}