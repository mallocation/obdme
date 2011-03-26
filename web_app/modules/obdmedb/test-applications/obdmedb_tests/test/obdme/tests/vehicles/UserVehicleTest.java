package obdme.tests.vehicles;

import java.util.List;

import models.obdmedb.User;
import models.obdmedb.vehicles.UserVehicle;
import models.obdmedb.vehicles.Vehicle;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserVehicleTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void testCreateUserVehicleRelationship() {
		String email = "farmboy30@gmail.com";
		String password = "qwerty";
		String VIN = "1234ABC123";
		String alias = "Teh C4r";
		User user = User.createUserFromClearTextCredentials(email, password);
		assertNotNull(user);
		Vehicle veh = Vehicle.addVehicleIfNotExist(VIN);
		assertNotNull(veh);
		UserVehicle uv = UserVehicle.getVehicleForUser(user, veh);
		if (uv == null) {
			uv = new UserVehicle();
			uv.user = user;
			uv.vehicle = veh;
		}
		uv.alias = alias;
		assertTrue(uv.validateAndSave());
	}
	
	public void testGetUserVehicle() {
		String email = "farmboy30@gmail.com";
		String VIN = "1234ABC123";
		this.testCreateUserVehicleRelationship();		
		UserVehicle uv = UserVehicle.getVehicleForUser(User.findByEmail(email), Vehicle.findByVIN(VIN));
		assertNotNull(uv);		
		List<UserVehicle> luv = UserVehicle.getVehiclesForUser(User.findByEmail(email));
		assertTrue(luv.size() > 0);
	}

}
