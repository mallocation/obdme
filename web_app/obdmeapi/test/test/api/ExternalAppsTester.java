//package test.api;
//
//import models.obdme.Applications.ExternalApp;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import play.mvc.results.RenderJson;
//import play.test.Fixtures;
//import play.test.FunctionalTest;
//
//public class ExternalAppsTester extends FunctionalTest {
//	
//	public static String TEST_API_KEY;
//	
//	@Test
//	public void testCreateExternalApp() {
//		ExternalApp app = ExternalApp.createExternalAppForApplication("TEST APPLICATION NAME");
//		TEST_API_KEY = app.apikey;
//		assertNotNull(app);
//	}	
//	
//	@Test
//	public void testNoAccess() {
//		assertTrue(!ExternalApp.hasAccess("somerandomjunkthatdoesn'thaveaccess"));
//	}
//	
//	@Test
//	public void testHasAccess() {
//		assertTrue(ExternalApp.hasAccess(TEST_API_KEY));
//	}
//	
//}