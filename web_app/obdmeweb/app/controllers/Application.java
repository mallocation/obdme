package controllers;

import play.*;
import play.mvc.*;
import models.*;

@With(Secure.class)
public class Application extends Controller {

    public static void index() {
			render();
	}

}