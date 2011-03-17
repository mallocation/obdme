package obdmedb.tests.applications;

import models.obdmedb.applications.ExternalApp;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ExternalAppTest extends UnitTest {
	
	private static final String TEST_APP_NAME = "obdmedb testerz";
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void testCreateExternalApp() {
		ExternalApp newApp = ExternalApp.createNewExternalApplication(TEST_APP_NAME);
		assertEquals(newApp.name, TEST_APP_NAME);
		assertTrue(newApp.apikey.length() == 64);
		assertTrue(newApp.getId() > 0L);
	}
	
	@Test
	public void testHasAccess() {
		ExternalApp newApp = ExternalApp.createNewExternalApplication(TEST_APP_NAME);
		assertEquals(newApp.name, TEST_APP_NAME);
		assertTrue(newApp.apikey.length() == 64);
		assertTrue(newApp.getId() > 0L);
		assertTrue(ExternalApp.hasAccessToObdme(newApp.apikey));		
	}
	
	@Test
	public void testNoAccess() {
		testCreateExternalApp();
		assertTrue(!ExternalApp.hasAccessToObdme("abc1234abc13435674890598"));
	}

}
