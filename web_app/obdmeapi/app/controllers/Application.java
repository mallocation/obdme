package controllers;

import play.db.DB;
import play.mvc.Controller;

public class Application extends Controller {
	
	public static void login() {
		Login.index();
		
	}

}