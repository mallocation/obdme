package controllers.ajax;

import play.data.validation.Required;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

import models.obdmedb.spatial.VehicleLocation;
import models.obdmedb.trips.Trip;

public class Kml extends Controller {
	
	public static class KmlTripBinding {
		public KmlCoordinate startPoint;
		public KmlCoordinate endPoint;
		public KmlCoordinate[] coordinates;		
	}
	
	public static class KmlCoordinate {
		public double lat;
		public double lon;
		public double alt;
		
		public KmlCoordinate() {}
		
		public KmlCoordinate(double lat, double lon, double alt) {
			this.lat = lat;
			this.lon = lon;
			this.alt = alt;
		}
	}
	
	
	public static void getKmlForTrip(@Required Long tripId) {
		
		
		List<VehicleLocation> coordinates = Trip.getCoordinatesForTrip(tripId);
		List<KmlCoordinate> kmlCoordnates = new ArrayList<KmlCoordinate>();
		
		for (VehicleLocation coord : coordinates) {
			kmlCoordnates.add(new KmlCoordinate(coord.latitude, coord.longitude, coord.altitude));
		}
		
		KmlTripBinding renderKml = new KmlTripBinding();
		renderKml.startPoint = kmlCoordnates.get(0);
		renderKml.endPoint = kmlCoordnates.get(kmlCoordnates.size() - 1);
		renderKml.coordinates = kmlCoordnates.toArray(new KmlCoordinate[0]);
		
		response.current().contentType = "application/vnd.google-earth.kml+xml";
		renderTemplate("Ajax/Kml/basictrip.kml", renderKml);
		
	}

}
