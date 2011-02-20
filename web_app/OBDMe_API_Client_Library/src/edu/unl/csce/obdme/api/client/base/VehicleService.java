package edu.unl.csce.obdme.api.client.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import edu.unl.csce.obdme.api.entities.Vehicle;
import edu.unl.csce.obdme.client.http.request.RequestListener;

/**
 * The Vehicle Service class.
 * Use this class to communicate with vehicle-oriented data
 * in the OBDMe system.
 */
public class VehicleService extends ProtectedServiceWrapper {
	
	/** The base path for the vehicles service. */
	private static final String VEHICLE_SERVICE_BASE_PATH = "/vehicles";

	/**
	 * Instantiates a new vehicle service.
	 *
	 * @param apiKey the api key of the calling application.
	 */
	public VehicleService(String apiKey) {
		super(apiKey);
	}
	
	/**
	 * Adds a vehicle to the OBDMe system if it does not already exist.
	 * The result will be a Vehicle identity.
	 *
	 * @param VIN The vehicle identification number of the vehicle.
	 * @param handler The result handler.
	 */
	public void addVehicle(String VIN, Handler handler) {
		this.addVehicle(VIN, 0, handler);
	}
	
	/**
	 * Adds a vehicle to the OBDMe system if it does not already
	 * exist, and ties a user to the vehicle.
	 * The result will be a Vehicle entity.
	 *
	 * @param VIN The vehicle identification number of the vehicle.
	 * @param userId The user id to tie the vehicle to (Pass 0 for null)
	 * @param handler The result handler.
	 */
	public void addVehicle(String VIN, long userId, Handler handler) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("VIN", VIN));
		if (userId > 0) { 
			parameters.add(new BasicNameValuePair("userid", Long.toString(userId))); 
		}
		this.performPut(VEHICLE_SERVICE_BASE_PATH, parameters, new BasicVehicleRequestListener(handler));		
	}
	
	/**
	 * Gets a vehicle from the OBDMe system.
	 * The result will be a vehicle identity.
	 *
	 * @param VIN The vehicle identification number.
	 * @param handler The result handler.
	 */
	public void getVehicle(String VIN, Handler handler) {
		String requestPath = String.format("%s/%s", VEHICLE_SERVICE_BASE_PATH, VIN);
		this.performGet(requestPath, null, new BasicVehicleRequestListener(handler));
	}
	
	/**
	 *	Request listener class for basic vehicle queries.
	 *	Response is parsed into a vehicle entity.
	 */
	private static final class BasicVehicleRequestListener implements RequestListener {
		
		/** The handler. */
		private Handler handler;
		
		/**
		 * Instantiates a new basic vehicle request listener.
		 *
		 * @param handler the handler
		 */
		public BasicVehicleRequestListener(Handler handler) {
			this.handler = handler;
		}
		
		/* (non-Javadoc)
		 * @see edu.unl.csce.obdme.client.http.request.RequestListener#onComplete(java.lang.String)
		 */
		@Override
		public void onComplete(String response) {
			Gson gson = new Gson();
			Vehicle vehicle;
			try {
				vehicle = gson.fromJson(response, Vehicle.class);
			} catch (JsonSyntaxException e) {
				vehicle = null;
			}
			handler.sendMessage(handler.obtainMessage(0, vehicle));
		}		
	}
	
	

}
