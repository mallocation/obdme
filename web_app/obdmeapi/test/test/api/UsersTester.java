package test.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import models.obdme.User;
import models.obdme.Applications.ExternalApp;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;

public class UsersTester extends FunctionalTest {
	
	private static final String APP_NAME = "Tester";
	private static String TEST_API_KEY;
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
		TEST_API_KEY = ExternalApp.createExternalAppForApplication(APP_NAME).apikey;
	}
	
	@Test
	public void createNewUser() {
		String email = "farmboy30@gmail.com";
		String pw = "qwerty";
		String path = "/api/users";		
		Map<String, String> parameters = new HashMap<String, String>();		
		parameters.put("email", email);
		parameters.put("pw", pw);
		parameters.put("apikey", TEST_API_KEY);
		Response resp = POST(path, parameters, new HashMap<String, File>());
		assertIsOk(resp);
		String userjson = new String(resp.out.toByteArray());
		Gson gson = new Gson();		
		assertNotNull(gson.fromJson(userjson, User.class));
	}
	
	
	@Test
	public void testGetExistingUser() {
		createNewUser();
		
		String email = "farmboy30@gmail.com";
		String path = String.format("/api/users/%s?apikey=%s", email, TEST_API_KEY);
		Response response = GET(path);
		String userjson = new String(response.out.toByteArray());
		Gson gson = new Gson();
		User user = gson.fromJson(userjson,User.class);
		assertNotNull(user);
	}

}
