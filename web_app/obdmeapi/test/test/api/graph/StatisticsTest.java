package test.api.graph;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.obdmedb.vehicles.Vehicle;

import org.junit.Test;

import play.mvc.Before;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;
import api.entities.graph.statistics.StatDataPoint;
import api.entities.graph.statistics.StatDataset;
import api.entities.graph.statistics.VehicleGraphPush;

import com.google.gson.Gson;

public class StatisticsTest extends FunctionalTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void testPushDataForVehicle() {
		String VIN = "1234ABC123";
		String email = "farmboy30@gmail.com";
		Vehicle.addVehicleIfNotExist(VIN);
		
		VehicleGraphPush graphPush = new VehicleGraphPush();
		graphPush.setVIN(VIN);
		
		List<StatDataset> datasets = new ArrayList<StatDataset>();
		
		for (int i=0;i<20;i++) {
			StatDataset ds = new StatDataset();
			ds.setEmail(email);
			ds.setTimestamp(new Date());
			List<StatDataPoint> datapoints = new ArrayList<StatDataPoint>();
			for (int j=0;j<50;j++) {
				StatDataPoint dp = new StatDataPoint();
				dp.setMode("01");
				dp.setPid("01");
				dp.setValue("13.42");
				datapoints.add(dp);
			}
			ds.setDatapoints(datapoints);
			datasets.add(ds);
		}
		graphPush.setDatasets(datasets);
		
		String json = new Gson().toJson(graphPush);		
		Response response = POST(String.format("/api/graph/statistics/vehicle/%s", VIN), "text/html", json);
		assertIsOk(response);		
	}

}
