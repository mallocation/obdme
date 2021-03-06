package obdmedb.tests.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.obdmedb.statistics.VehicleDataPoint;
import models.obdmedb.statistics.VehicleDataset;
import models.obdmedb.vehicles.Vehicle;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.junit.Before;
import org.junit.Test;

import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

public class VehicleDatasetTest extends UnitTest {
	
	public static final String USER_EMAIL = "farmboy30@gmail.com"; 
	public static final String USER_PW = "qwerty";
	public static final String VIN = "1234ABC123";
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void testLogDataForVehicle() {
		//Create the vehicle
		Vehicle v = Vehicle.addVehicleIfNotExist(VIN);
		VehicleDataset ds = new VehicleDataset(v, null, new Date(), null, null);
		assertTrue(ds.validateAndSave());
		
		//create a stateless session - this will ensure a speedy batch insert.
		Session session = (Session)JPA.em().getDelegate();
		StatelessSession statelessSession = session.getSessionFactory().openStatelessSession();		
		statelessSession.beginTransaction();
		
		
		for (int i=0; i<500; i++) {
			VehicleDataPoint dp = new VehicleDataPoint(ds, "01", "0C", 14.4);
			statelessSession.insert(dp);
		}
		
		//commit the transactions and close the stateless session
		statelessSession.getTransaction().commit();
		statelessSession.close();
		
		assertTrue(ds.validateAndSave());
	}
	
	@Test
	public void testRetrieveDataForVehicle() {
		testLogDataForVehicle();
		Vehicle v = Vehicle.findByVIN(VIN);
		List<VehicleDataset> vehDatasets = VehicleDataset.find("vehicleid=?", v.getId()).fetch();
		assertNotNull(vehDatasets);
		assertTrue(vehDatasets.size() > 0);
		assertNotNull(vehDatasets.get(0));
		
	}
	
	

}
