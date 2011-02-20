package edu.unl.csce.obdme.api.client.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import edu.unl.csce.obdme.api.entities.graph.vehicles.GraphPush;
import edu.unl.csce.obdme.client.http.request.RequestListener;

public class VehicleGraphService extends ProtectedServiceWrapper {
	
	private static final String VEHICLE_GRAPH_BASE = "/graph/vehicles";

	public VehicleGraphService(String apiKey) {
		super(apiKey);
	}
	
	public void pushVehicleGraphData(GraphPush graphPush, Handler handler) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("graphentry", graphPush.toJSONString()));
		this.performPost(VEHICLE_GRAPH_BASE, parameters, new PushGraphListener(handler));
	}
	
	private static final class PushGraphListener implements RequestListener {

		private Handler handler;
		
		public PushGraphListener(Handler handler) {
			this.handler = handler;			
		}

		@Override
		public void onComplete(String response) {
			handler.sendMessage(handler.obtainMessage(0, null));			
		}
		
	}
}