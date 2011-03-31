package controllers.ajax;

import models.obdmedb.obd.ObdPid;
import models.obdmedb.statistics.VehicleDataPoint;
import play.mvc.Controller;

public class Graphing extends Controller {

    public static void index() {
        render();
    }
    
    public static void getLatestDataPointsForVehicle(String VIN, String mode, String pid, int count) {
    	renderJSON(VehicleDataPoint.getLatestValuesForPid(ObdPid.getObdPid(mode, pid), VIN, count));
    }

}
