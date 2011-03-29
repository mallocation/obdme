package edu.unl.csce.obdme.api.client.base;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.unl.csce.obdme.api.entities.graph.statistics.VehicleStatPush;
import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;

public class StatisticsService extends ServiceWrapper {
	
	private static final String STATISTICS_BASE_URL = "/graph/statistics";
	
	public boolean logVehicleStatistics(String VIN, VehicleStatPush dataPush) throws ObdmeException, CommException {
		String requestPath = String.format("%s/vehicle/%s", STATISTICS_BASE_URL, VIN);
		String requestBody = dataPush.toJSONString();
		String response = super.performPost(requestPath, requestBody);
		try {
			return new ObjectMapper().readValue(response, Boolean.class).booleanValue();
		} catch (JsonParseException e) {
			return false;
		} catch (JsonMappingException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
	}
	

}
