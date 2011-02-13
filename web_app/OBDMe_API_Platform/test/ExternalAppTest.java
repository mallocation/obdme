import models.obdme.Applications.ExternalApp;

import org.junit.Test;

import play.Logger;
import play.test.UnitTest;

public class ExternalAppTest extends UnitTest {
	
	@Test
	public void testCreateApplicationApiKey() {
		String name = "OBDMe API Android Library";
		String apikey = ExternalApp.createApiKeyForApplication(name);
		Logger.info(String.format("APIKey = %s", apikey));
		assertNotNull(apikey);
	}

}
