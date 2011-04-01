package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.obdmedb.obd.ObdPid;
import models.obdmedb.statistics.VehicleDataPoint;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Charts extends Controller {

    public static void index() {    	
		List<Double> values = VehicleDataPoint.getLatestValuesForPid(ObdPid.getObdPid("01", "3C"), "1HGFA16537L077259", 500);
    	StringBuffer sb = new StringBuffer();
    	sb.append("[");
    	for (Double val : values) {
    		sb.append(val.doubleValue() + ",");
    	}
    	sb.deleteCharAt(sb.length() - 1);
    	sb.append("]");
    	String data = sb.toString();
    	int numPoints = values.size();
    	List<ObdPid> pidList = new ArrayList<ObdPid>();
    	Collections.addAll(pidList, ObdPid.DEFINED_OBD_PIDS);
		render(data, numPoints, pidList);
	}

}