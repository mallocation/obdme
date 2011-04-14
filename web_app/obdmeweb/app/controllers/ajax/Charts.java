package controllers.ajax;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import models.obdmedb.obd.ObdPid;
import models.obdmedb.statistics.VehicleDataPoint;
import models.obdmedb.statistics.VehicleDataPoint.LatestDataPoint;
import models.obdmedb.vehicles.Vehicle;
import play.data.validation.Required;
import play.mvc.*;
import utilities.obd.ObdPidUtils;

public class Charts extends Controller {

    public static void index() {
        render();
    }
    
    
    /**
     * Renders the latest N points for a vehicle, in JSON format.
     * 
     * [
     * 		{"x":"2011-04-09 20:04:19.0","y":1071.0},
     * 		{"x":"2011-04-09 20:04:16.0","y":1048.0}
     * ]
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
    
    public static class ChartCollection {
    	@SerializedName("unit")
    	public String unit;
    	
    	@SerializedName("points")
    	public List<LatestDataPoint> points;    	
    }
    
    
    

}
