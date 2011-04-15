package controllers.ajax;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.obdmedb.obd.ObdPid;
import models.obdmedb.statistics.VehicleDataPoint;
import models.obdmedb.statistics.VehicleDataPoint.LatestDataPoint;
import models.obdmedb.vehicles.Vehicle;
import play.Logger;
import play.data.validation.Required;
import play.mvc.Controller;
import utilities.format.DateFormat;
import utilities.obd.ObdPidUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

public class Charts extends Controller {

    public static void index() {
        render();
    }
    
    
    /**
     * Renders the latest N points for a vehicle, in JSON format.
     * 
     * {
     * 		"unit": "V",
     * 		"points": [
     * 			{"x":"2011-04-09 20:04:19.0","y":1071.0},
     * 			{"x":"2011-04-09 20:04:16.0","y":1048.0}
     * 		]
     * }
     *
     * @param VIN the vIN
     * @param mode the mode
     * @param pid the pid
     */
    public static void latestPointsForVehicle(@Required String VIN, @Required String mode, @Required String pid) {
    	Vehicle veh = Vehicle.findByVIN(VIN);
    	ObdPid obdPid = ObdPid.getObdPid(mode, pid);
    	ChartCollection result = new ChartCollection();
    	result.unit = ObdPidUtils.getPidUnit(obdPid);
    	result.points = VehicleDataPoint.selectLatestDataPoints(obdPid, veh, 200);
    	
    	JsonSerializer<LatestDataPoint> jsonSer = new JsonSerializer<LatestDataPoint>() {
    		@Override
    		public JsonElement serialize(LatestDataPoint arg0, Type arg1, JsonSerializationContext arg2) {
    			JsonObject obj = new JsonObject();
    			obj.addProperty("x", arg0.timestamp.toString());
    			obj.addProperty("y", arg0.value);
    			return obj;
    		}
    	};
    	
    	renderJSON(result, jsonSer);
    }
    
    public static void pointsForVehicleInDate(@Required String VIN, @Required String mode, @Required String pid, String date) {
    	Vehicle veh = Vehicle.findByVIN(VIN);
    	Date forDate = DateFormat.parseDateFromObdmeStandard(date);
    	
    	if (forDate == null) {
    		Logger.error("Date " + date.toString() + " not in yyyy-MM-dd form. Using today's date.");
    		forDate = new Date();
    	} else {
    		Logger.info("Date received: " + date);
    	}    	
    	
    	ObdPid obdPid = ObdPid.getObdPid(mode, pid);
    	ChartCollection result = new ChartCollection();
    	
    	result.unit = ObdPidUtils.getPidUnit(obdPid);
    	result.points = VehicleDataPoint.selectDataPointsForDate(obdPid, veh, forDate);
    	
    	JsonSerializer<LatestDataPoint> jsonSer = new JsonSerializer<LatestDataPoint>() {
    		@Override
    		public JsonElement serialize(LatestDataPoint arg0, Type arg1, JsonSerializationContext arg2) {
    			JsonObject obj = new JsonObject();
    			obj.addProperty("x", arg0.timestamp.getTime());// sdf.format(arg0.timestamp));
    			obj.addProperty("y", arg0.value);
    			return obj;
    		}
    	};
    	
    	
    	//Mon, 25 Dec 1995 13:30:00 GMT
    	
    	
    	renderJSON(result, jsonSer);    	
    }
    
    public static class ChartCollection {
    	@SerializedName("unit")
    	public String unit;
    	
    	@SerializedName("points")
    	public List<LatestDataPoint> points;    	
    }
    
    
    

}
