package controllers;

import edu.unl.csce.obdme.mvc.controllers.BasicController;
import edu.unl.csce.obdme.mvc.controllers.LoggedController;

public class Users extends LoggedController {

    public static void index() {
        renderText("hello world!");
    }

}
