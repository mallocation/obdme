package obdmedb.tests.applications;

import models.obdmedb.applications.ApiUsageLog;
import models.obdmedb.applications.ExternalApp;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ApiUsageLogTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void testLogExternalApp() {
		ExternalApp app = ExternalApp.createNewExternalApplication("obdme testerz");
		assertNotNull(app);		
		String apiKey = app.apikey;		
		assertTrue(ApiUsageLog.addApiUsageLog(apiKey, "/testthis", "POST", 200, "127.0.0.1"));		
	}

}
