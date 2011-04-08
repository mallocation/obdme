package edu.unl.csce.obdme.api.test;

import java.util.Date;

import android.test.AndroidTestCase;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.api.entities.graph.statistics.StatDataPoint;
import edu.unl.csce.obdme.api.entities.graph.statistics.StatDataset;
import edu.unl.csce.obdme.api.entities.graph.statistics.VehicleStatPush;
import edu.unl.csce.obdme.client.http.exception.CommException;
import edu.unl.csce.obdme.client.http.exception.ObdmeException;

public class StatisticsServiceTest extends AndroidTestCase {
	
	private ObdMeService service;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();		
		service = new ObdMeService("abcd");
	}
	
	public void testSerializeStatPush() {
		String VIN = "1234ABC123";
		String email = "farmboy30@gmail.com";
		VehicleStatPush statPush = new VehicleStatPush(VIN);
		for (int i=0; i<20; i++) {
			StatDataset ds = new StatDataset();
			ds.email = email;
			ds.timestamp = new Date();
			for (int j=0;j<20;j++) {
				StatDataPoint dp = new StatDataPoint("01", "0C", Double.toString(14.4));
				ds.datapoints.add(dp);
			}
			statPush.statSets.add(ds);
		}
		assertNotNull(statPush.toJSONString());	
	}
	
	public void testCreateVehicle() {
		//make sure the vehicle exists
		final String VIN = "1234ABC123";
		final String email = "farmboy30@gmail.com";
		try {
			service.getVehicleService().addUpdateVehicle(VIN);
		} catch (ObdmeException e) {
			fail(e.getMessage());
		} catch (CommException e) {
			fail(e.getMessage());
		}
		
		//create the data push
		VehicleStatPush statPush = new VehicleStatPush(VIN);
		for (int i=0; i<20; i++) {
			StatDataset ds = new StatDataset();
			ds.email = email;
			ds.timestamp = new Date();
			for (int j=0;j<20;j++) {
				StatDataPoint dp = new StatDataPoint("01", "0C", Double.toString(14.4));
				ds.datapoints.add(dp);
			}
			statPush.statSets.add(ds);
		}
		
		try {
			assertTrue(service.getStatisticsService().logVehicleStatistics(VIN, statPush));
		} catch (ObdmeException e) {
			fail(e.getMessage());
		} catch (CommException e) {
			fail(e.getMessage());
		}
		
	}

}
