package edu.unl.csce.obdme.mvc.controllers;

import factory.AppFactory;
import play.mvc.*;

public class BasicController extends Controller {
	
	private static AppFactory _factory = new AppFactory();
	protected static AppFactory getAppFactory() {
		return _factory;
	}

    public static void index() {
        render();
    }
}
