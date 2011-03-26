//package test.api;
//
//import java.io.File;
//import java.lang.reflect.Type;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import models.obdme.User;
//import models.obdme.Applications.ExternalApp;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import api.entities.UserVehicle;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import play.libs.WS;
//import play.mvc.Finally;
//import play.mvc.Http;
//import play.mvc.Http.Response;
//import play.test.Fixtures;
//import play.test.FunctionalTest;
//import edu.unl.csce.obdme.encryption.EncryptionUtils;
//
//public class AdvancedTest extends FunctionalTest {
//	
//	private ExternalApp externalApp;
//	
//	@Before
//	public void setup() {
//		Fixtures.deleteAll();		
//		// make sure we've got access.		
//		externalApp = ExternalApp.createExternalAppForApplication("Testing Application");
//	}
//	
//	@Test
//	public void createUserVehicleAndAssociation() {
//		String email = "farmboy30@gmail.com";
//		String VIN = "1234ABCDEFG567";
//		String alias = "Curtis' Test Vehicle!";
//		String requestPath = null;
//		User regUser = null;
//		UserVehicle uv = null;
//		Gson gson = new Gson();
//		
//		//First Create a User
//		Map<String, String> parameters = new HashMap<String, String>();
//		parameters.put("email", email);
//		parameters.put("pw", EncryptionUtils.encryptPassword("qwerty"));
//		parameters.put("apikey", externalApp.apikey);
//		Response response = POST("/api/users", parameters, new HashMap<String, File>());
//		
//		regUser = gson.fromJson(new String(response.out.toByteArray()), User.class);
//		
//		/* Tests */
//		assertNotNull(regUser);
//		assertTrue(email.equals(regUser.email));
//		
//		// Next Create a Vehicle and associate the user with that vehicle
//		parameters.clear();
//		requestPath = String.format("/api/vehicles/vin/%s/user/%s", VIN, email); 
//		response = PUT(requestPath, APPLICATION_X_WWW_FORM_URLENCODED, "alias=" + WS.encode(alias));
//		uv = gson.fromJson(new String(response.out.toByteArray()), UserVehicle.class);
//		
//		/* Tests */
//		assertNotNull(uv);
//		assertTrue(uv.getVIN().equals(VIN));
//		assertTrue(uv.getAlias().equals(alias));
//		assertTrue(uv.getVehicleId() > 0L);		
//	}
//	
//	@Test
//	public void addAnotherVehicleToUser() {
//		createUserVehicleAndAssociation();
//		String email="farmboy30@gmail.com";
//		String VIN = "1234999999ABBCC";
//		Gson gson = new Gson();
//		String requestPath = String.format("/api/vehicles/vin/%s/user/%s", VIN, email);
//		Response response = PUT(requestPath, APPLICATION_X_WWW_FORM_URLENCODED, "alias=");
//		UserVehicle uv = gson.fromJson(new String(response.out.toByteArray()), UserVehicle.class);
//		assertNotNull(uv);
//		assertTrue(uv.getVIN().equals(VIN));
//		assertTrue(uv.getVehicleId() > 0L);
//	}
//	
//	@Test
//	public void getVehiclesForUser() {
//		addAnotherVehicleToUser();
//		String email = "farmboy30@gmail.com";
//		String requestPath = String.format("/api/vehicles/user/%s", email);
//		Response response = GET(requestPath);
//		Gson gson = new Gson();
//		Type type = new TypeToken<List<UserVehicle>>(){}.getType();
//		List<UserVehicle> uv = gson.fromJson(new String(response.out.toByteArray()), type);
//		assertNotNull(uv);
//		assertTrue(uv.size() > 0);
//	}
//	
//}
