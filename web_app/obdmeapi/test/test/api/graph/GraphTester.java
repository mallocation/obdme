package test.api.graph;

import java.util.Date;

import models.obdme.Vehicles.Vehicle;

import org.junit.Test;

import play.Logger;
import play.libs.WS;
import play.mvc.Http.Response;
import play.test.FunctionalTest;
import api.entities.graph.vehicle.GraphEntry;
import api.entities.graph.vehicle.GraphPoint;
import api.entities.graph.vehicle.GraphPush;

public class GraphTester extends FunctionalTest {
	
	@Test
	public void testGraphEntry() {
//		Fixtures.deleteAll();
//		Fixtures.load("data.yml");
//		
		
		final String VIN = "ABCD123ABCD";
		Vehicle vehicle = Vehicle.findByVIN(VIN);
		if (vehicle == null)
			vehicle = Vehicle.addVehicle(VIN);
		
		GraphPush push = new GraphPush();
		
		GraphEntry entry = new GraphEntry();
		
		GraphPoint point = new GraphPoint();
		point.setMode("0C");
		point.setPid("0c");
		point.setValue("0");
		entry.getPoints().add(point);
		entry.setVIN(VIN);
		entry.setTimestamp(new Date());
		push.getEntries().add(entry);
		
		Logger.info(push.toJSONString());
		
		Response response = POST("/api/graph/vehicles", FunctionalTest.APPLICATION_X_WWW_FORM_URLENCODED, String.format("graphpush=%s", WS.encode(push.toJSONString())));
		assertIsOk(response);
		//assertTrue(true);
	}
}
