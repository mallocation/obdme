package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.obdmedb.User;
import models.obdmedb.obd.ObdPid;
import models.obdmedb.vehicles.UserVehicle;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import utilities.format.DateFormat;

@With(Secure.class)
public class Charts extends Controller {


    public static void index() {    	
    	User user = User.findByEmail(Security.connected());
    	UserVehicle uv = UserVehicle.getVehiclesForUser(user).get(0);
    	chartVin(uv.getVehicle().getId());
	}
    
    public static void chartVin(Long vehicleid) {
    	List<ObdPid> pidList = new ArrayList<ObdPid>();
    	Collections.addAll(pidList, ObdPid.DEFINED_OBD_PIDS);
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");
    	renderArgs.put("currentDate", DateFormat.formatDateToObdmeStandard(new Date()));
    	renderArgs.put("currentDateAlt", sdf.format(new Date()));
    	
    	renderTemplate("Charts/index.html", pidList);
    }

	@Before
	static void setPIDLists() {
		List<ObdPid> pidListUnsorted = new ArrayList<ObdPid>();
		Collections.addAll(pidListUnsorted, ObdPid.DEFINED_OBD_PIDS);
		renderArgs.put("pidListUnsorted", pidListUnsorted);
		
		List<ObdPid> pidListSortedName = new ArrayList<ObdPid>();
		Collections.addAll(pidListSortedName, ObdPid.DEFINED_OBD_PIDS);
		Collections.sort(pidListSortedName);
		renderArgs.put("pidListSortedName", pidListSortedName);
	}



}