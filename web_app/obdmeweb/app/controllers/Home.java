package controllers;

import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Home extends Controller {

    public static void index() {    	
		render();
	}

}