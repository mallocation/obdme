package controllers.api.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

import com.google.gson.Gson;

import api.entities.ApiError;
import api.entities.graph.statistics.StatDataPoint;
import api.entities.graph.statistics.StatDataset;
import api.entities.graph.statistics.VehicleGraphPush;
import models.obdmedb.User;
import models.obdmedb.statistics.VehicleDataPoint;
import models.obdmedb.statistics.VehicleDataset;
import models.obdmedb.statistics.VehicleLocation;
import models.obdmedb.vehicles.Vehicle;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.*;
import play.mvc.results.BadRequest;

/**
 * This controller handles everything dealing with vehicle statistics in the obdme api.
 */
public class Statistics extends Controller {

	/**
	 * Post graph data for vehicle.
	 *
	 * @param VIN the VIN of the vehicle.
	 */
	public static void postGraphDataForVehicle(String VIN) {
		
		//ensure that the vehicle exists in obdme
		Vehicle vehicle = Vehicle.findByVIN(VIN);		
		if (vehicle == null) {
			// Vehicle doesn't exist, throw an error
			renderJSON(new ApiError(Messages.get("api.graph.statistics.error.vehiclenotexist", VIN)));
		}
		
		//Read the body of the request to obtain the vehicle data
		BufferedReader br = new BufferedReader(new InputStreamReader(request.body));
		StringBuffer sb = new StringBuffer();
		String currentLine = null;
		try {
			while ((currentLine = br.readLine()) != null) {
				sb.append(currentLine);
			}
		} catch (IOException e) {
			throw new BadRequest();
		}
		
		// Parse the vehicle stat data
		VehicleGraphPush graphPush = new Gson().fromJson(sb.toString(), VehicleGraphPush.class);
		
		//make sure the vin numbers correspond.
		if (!graphPush.getVIN().equals(VIN)) {
			renderJSON(new ApiError(Messages.get("api.graph.statistics.error.vinnotmatch")));
		}
		
		//create a stateless session - this will ensure a speedy batch insert.
		Session session = (Session)JPA.em().getDelegate();
		StatelessSession statelessSession = session.getSessionFactory().openStatelessSession();		
		statelessSession.beginTransaction();
		
		//insert the datasets and datapoints
		for (StatDataset dataset : graphPush.getDatasets()) {
			User loggedUser = User.findByEmail(dataset.getEmail());			
			
			//Make the location null initially
			VehicleLocation vl = null;
			
			//If there is location information define (at least the latitude and logitude)
			if (dataset.getLatitude() != null && dataset.getLongitude() != null) {
				
				//Make a new vehicle location
				vl = new VehicleLocation(dataset.getAccuracy(),
						dataset.getBearing(),
						dataset.getAltitude(), 
						dataset.getLatitude(), 
						dataset.getLongitude()); 
				
				//Insert the location
				statelessSession.insert(vl);
			}
			
			VehicleDataset ds = new VehicleDataset(vehicle, loggedUser, dataset.getTimestamp(), vl);
			statelessSession.insert(ds);
			for(StatDataPoint datapoint : dataset.getDatapoints()) {
				VehicleDataPoint dp = new VehicleDataPoint(ds, datapoint.getMode(), datapoint.getPid(), datapoint.getValue());
				statelessSession.insert(dp);
			}
		}
		
		//commit the transactions and close the stateless session
		statelessSession.getTransaction().commit();
		statelessSession.close();
		
		//if we got here, then it was a successful request.
		renderJSON(true);		
	}
}