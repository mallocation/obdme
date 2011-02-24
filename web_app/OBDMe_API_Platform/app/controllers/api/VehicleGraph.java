package controllers.api;

import models.obdme.Vehicles.Vehicle;
import models.obdme.statistics.Datapoint;
import models.obdme.statistics.VehicleDataset;
import play.Logger;
import play.mvc.Controller;
import api.entities.graph.vehicle.GraphEntry;
import api.entities.graph.vehicle.GraphPoint;
import api.entities.graph.vehicle.GraphPush;

public class VehicleGraph extends Controller {
    
    public static void pushVehicleGraphData() {
    	try {
    		GraphPush graphPush = GraphPush.fromJSONString(request.params.get("graphpush"));    
    		Logger.info("Retrieved graph push : " + request.params.get("graphpush"));
        	for(GraphEntry entry : graphPush.getEntries()) {
        		Vehicle vehicle = Vehicle.findByVIN(entry.getVIN());
        		VehicleDataset ds = new VehicleDataset(vehicle, entry.getTimestamp());
        		ds.save();
        		for (GraphPoint point : entry.getPoints()) {
        			Datapoint dp = new Datapoint(ds, point.getMode(), point.getPid(), Double.parseDouble(point.getValue()));
        			dp.save();
        		}
        	}
        	renderJSON(true);
    	} catch (Exception e) {
    		renderJSON(false);    		
    	}
    	
    	
    }
    
    
}
