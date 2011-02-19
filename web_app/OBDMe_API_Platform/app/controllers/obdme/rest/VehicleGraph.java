package controllers.obdme.rest;

import com.mysql.jdbc.PingTarget;

import models.obdme.Vehicles.Vehicle;
import models.obdme.statistics.Datapoint;
import models.obdme.statistics.VehicleDataset;
import play.Logger;
import play.mvc.Controller;
import api.entities.graph.GraphEntry;
import api.entities.graph.GraphPoint;

public class VehicleGraph extends Controller {

    public static void index() {
        render();
    }
    
    
    public static void addGraphEntry() {
    	GraphEntry graphentry = GraphEntry.fromJSON(request.params.get("graphentry"));   	
    	Logger.info(graphentry.toJSONString());
    	Vehicle vehicle = Vehicle.findByVIN(graphentry.getVIN());
    	VehicleDataset ds = new VehicleDataset(vehicle, graphentry.getTimestamp());
    	ds.validateAndSave();
    	
    	for(GraphPoint point : graphentry.getPoints()) {
    		Datapoint dp = new Datapoint(ds, point.getMode(), point.getPid(), Double.parseDouble(point.getValue()));
    		dp.save();
    	}
    }
}
