package edu.unl.csce.obdme.api.client.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import edu.unl.csce.obdme.api.entities.graph.vehicles.GraphPush;
import edu.unl.csce.obdme.client.http.request.RequestListener;

/**
 * The Class VehicleGraphService.
 */
public class VehicleGraphService extends ProtectedServiceWrapper {
	
	/** The Constant VEHICLE_GRAPH_BASE. */
	private static final String VEHICLE_GRAPH_BASE = "/graph/vehicles";

	/**
	 * Instantiates a new vehicle graph service.
	 *
	 * @param apiKey the api key
	 */
	public VehicleGraphService(String apiKey) {
		super(apiKey);
	}
	
	/**
	 * Push vehicle graph data.
	 *
	 * @param graphPush the graph push
	 * @param handler the handler
	 */
	public void pushVehicleGraphData(GraphPush graphPush, Handler handler) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("graphpush", graphPush.toJSONString()));
		this.performPost(VEHICLE_GRAPH_BASE, parameters, new PushGraphListener(handler));
	}
	
	/**
	 * The listener interface for receiving pushGraph events.
	 * The class that is interested in processing a pushGraph
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addPushGraphListener<code> method. When
	 * the pushGraph event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see PushGraphEvent
	 */
	private static final class PushGraphListener implements RequestListener {

		/** The handler. */
		private Handler handler;
		
		/**
		 * Instantiates a new push graph listener.
		 *
		 * @param handler the handler
		 */
		public PushGraphListener(Handler handler) {
			this.handler = handler;			
		}

		/* (non-Javadoc)
		 * @see edu.unl.csce.obdme.client.http.request.RequestListener#onComplete(java.lang.String)
		 */
		@Override
		public void onComplete(String response) {
			boolean result = Boolean.valueOf(response).booleanValue();
			handler.sendMessage(handler.obtainMessage(0, result));			
		}
		
	}
}