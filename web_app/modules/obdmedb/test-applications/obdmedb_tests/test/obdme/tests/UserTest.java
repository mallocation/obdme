package obdme.tests;

import models.obdmedb.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void testCreateUserClearTextCredentials() {
		String email = "farmboy30@gmail.com";
		String password = "qwerty";
		User user = User.createUserFromClearTextCredentials(email, password);		
		assertNotNull(user);
		assertTrue(user.getId() > 0L);
		assertTrue(user.getEmail().equals(email));
	}
	
	@Test
	public void testValidateUserCredentials() {
		String email = "farmboy30@gmail.com";
		String password = "qwerty";
		User user = User.createUserFromClearTextCredentials(email, password);
		assertNotNull(user);
		
		User authenticated = User.validateUserCredentialsClearText(email, password);
		assertNotNull(authenticated);
		assertTrue(authenticated.getEmail().equals(email));
		assertTrue(User.isValidCredentialsClearText(email, password));
	}
	
}