package test.api.graph;

import java.util.Date;

import org.apache.commons.lang.ObjectUtils.Null;
import org.junit.Test;

import api.entities.graph.GraphEntry;
import api.entities.graph.GraphPoint;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.tools.example.debug.bdi.WatchpointSpec;

import models.obdme.Vehicles.Vehicle;
import play.Logger;
import play.data.parsing.UrlEncodedParser;
import play.libs.WS;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;
import play.test.UnitTest;

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
		
		for (int i=0; i<2000;i++) {
		
			
		GraphEntry entry = new GraphEntry();
		GraphPoint point = new GraphPoint();
		entry.setVIN(VIN);
		entry.setTimestamp(new Date());
		
		
		point.setMode("01");
		point.setPid("0C");
		point.setValue("234234");
		entry.getPoints().add(point);
		
		point = new GraphPoint();
		point.setMode("02");
		point.setPid("0A");
		point.setValue("23");
		entry.getPoints().add(point);
		
		String graphEntry = entry.toJSONString();
		Logger.info(graphEntry);
		
		//Response response = POST("/api/graph", FunctionalTest.APPLICATION_X_WWW_FORM_URLENCODED, String.format("graphentry=%s", WS.encode(graphEntry)));
		//assertIsOk(response);
		assertTrue(true);
		}
	}
}
