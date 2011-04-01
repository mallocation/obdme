package controllers;

import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Notifications extends Controller {

    public static void index() {    	
		render();
	}

}