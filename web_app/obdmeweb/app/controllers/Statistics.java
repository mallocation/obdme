package controllers;

import play.mvc.Controller;
import play.mvc.With;

import models.obdmedb.obd.ObdPid;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@With(Secure.class)
public class Statistics extends Controller {

    public static void index() {    	
		List<ObdPid> pidList = new ArrayList<ObdPid>();
    	Collections.addAll(pidList, ObdPid.DEFINED_OBD_PIDS);
    	render(pidList);
	}

}