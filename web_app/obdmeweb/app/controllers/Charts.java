package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.obdmedb.User;
import models.obdmedb.obd.ObdPid;
import models.obdmedb.statistics.VehicleDataPoint;
import models.obdmedb.statistics.VehicleDataPoint.LatestDataPoint;
import models.obdmedb.vehicles.Vehicle;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Charts extends Controller {


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

	public static void index() {
		render();
	}

}