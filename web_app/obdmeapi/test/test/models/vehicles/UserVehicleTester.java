package test.models.vehicles;

import java.util.List;

import models.obdme.User;
import models.obdme.Vehicles.UserVehicle;
import models.obdme.Vehicles.Vehicle;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserVehicleTester extends UnitTest {
	
	private static boolean isSetup = false;
	
	@Before
	public void setup() {
		if (!isSetup) {
			Fixtures.deleteAll();
			isSetup = true;
		}
	}
	
	@Test
	public void testCreateUserCreateVehicleAssociate() {
		User user = User.createUser("farmboy30@gmail.com", "qwerty");
		Vehicle vehicle = Vehicle.addVehicle("1234AQERR");
		UserVehicle uv = user.addVehicleToUser(vehicle, "Juggernaut");
		assertNotNull(uv);
	}
	
	@Test
	public void testGetVehiclesForUser() {
		List<UserVehicle> uv = UserVehicle.getVehiclesForUser(User.findByEmail("farmboy30@gmail.com"));
		assertTrue(uv.size() > 0);
	}

}
