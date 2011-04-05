package controllers.api.graph;

import models.obdmedb.User;
import models.obdmedb.trips.Trip;
import play.data.validation.Email;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Controller;
import api.entities.ApiError;

public class Trips extends Controller {

    public static void index() {
        render();
    }
    
    public static void createTripForUser(@Email String email, @Required String alias) {
    	User operator = User.findByEmail(email);
    	
    	if (operator == null) {
    		renderJSON(new ApiError(Messages.get("api.error.user.notexist", email)));
    	}
    	
    	Trip t = new Trip(operator, alias);
    	t.validateAndSave();
    	renderJSON(new api.entities.graph.Trip(t));    	
    }
    
    public static void updateTripForUser(@Email String email, @Required Long tripId, @Required String alias) {
    	User operator = User.findByEmail(email);
    	if (operator == null) {
    		renderJSON(new ApiError(Messages.get("api.error.user.notexist", email)));
    	}
    	
    	Trip trip = Trip.getTripForUser(tripId, operator);
    	if (trip == null) {
    		renderJSON(new ApiError(Messages.get("api.trips.error.tripnotexist")));
    	}
    	
    	trip.alias = alias;
    	trip.validateAndSave();
    	
    	renderJSON(new api.entities.graph.Trip(trip));    	
    }

}
