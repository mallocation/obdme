package controllers.ajax;

import play.data.validation.Required;
import play.mvc.Controller;

public class Kml extends Controller {
	
	public static void getKmlForTrip() {
		response.current().contentType = "application/vnd.google-earth.kml+xml";
		renderTemplate("Ajax/Kml/basictrip.kml");
		
	}

}
