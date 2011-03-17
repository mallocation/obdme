package obdme.tests.vehicles;

import models.obdmedb.User;
import models.obdmedb.vehicles.Vehicle;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class VehicleTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void testAddVehicle() {
		String VIN = "1234ABC1234";		
		Vehicle veh = Vehicle.addVehicleIfNotExist(VIN);
		assertNotNull(veh);
		assertEquals(VIN, veh.VIN);
		assertTrue(veh.getId() > 0L);
	}
	
	@Test
	public void testAddVehicleAndRetrieve() {
		String VIN = "ABCDEF1223333";
		Vehicle veh = Vehicle.addVehicleIfNotExist(VIN);
		
		assertNotNull(veh);
		
		Vehicle findVeh = Vehicle.findByVIN(VIN);
		
		assertNotNull(findVeh);
		assertTrue(veh.getId() > 0);
		assertTrue(veh.VIN.equals(VIN));
	}
}
