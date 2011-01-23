package edu.unl.csce.obdme.api.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import factory.AppFactory;

import play.test.FunctionalTest;
import play.test.UnitTest;

public class RequestValidation extends UnitTest {
	
	private AppFactory factory = new AppFactory();
	
	@Test
	public void testApiKeyValidation() {
		String sApiKey = "34567898765456789fasfmlaksasdfasdfasdf";
		String sComputedSig = "3E3BF2F0A86B5943BAC12FD6361BCD824C656C00";
		Map<String,String> params = new HashMap<String, String>();
		params.put("apikey", sApiKey);
		params.put("sig", sComputedSig);
		
		assertTrue(edu.unl.csce.obdme.http.validation.RequestValidation.isValidHttpRequest(params));		
	}
	
	@Test
	public void testNotValidApiKey() {
		String sNotValidApiKey = "34567898765456789fasfmlaksasdfasdfasdf";
		assertTrue(!factory.ExternalApp().isValidApiKey(sNotValidApiKey));
	}
	
	@Test
	public void testValidApiKey() {
		String sValidApiKey = "5a830756c4c7821c21a90f38bf6b8849006b7b61fbfefb9dd02e2979e13140d9bf831b26a54e068f4b31ba80c71bb3275728ba81c8bc02821949c1701d0fc6e8";
		assertTrue(factory.ExternalApp().isValidApiKey(sValidApiKey));
	}

}
