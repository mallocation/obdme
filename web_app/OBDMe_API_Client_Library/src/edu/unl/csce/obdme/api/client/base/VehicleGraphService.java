package edu.unl.csce.obdme.api.client.base;


public class VehicleGraphService extends ProtectedServiceWrapper {
	
//	private static final String VEHICLE_GRAPH_BASE = "/graph/vehicles";

	public VehicleGraphService(String apiKey) {
		super(apiKey);
	}
	
//	// TODO finish this implementation
//	public void pushVehicleGraphData(GraphPush graphPush, Handler handler) {
//		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//		parameters.add(new BasicNameValuePair("graphpush", graphPush.toJSONString()));
//		//this.performPost(VEHICLE_GRAPH_BASE, parameters, new PushGraphListener(handler));
//	}
//	
//	private static final class PushGraphListener implements RequestListener {
//
//		private Handler handler;
//		
//		public PushGraphListener(Handler handler) {
//			this.handler = handler;			
//		}
//
//		@Override
//		public void onComplete(String response) {
//			boolean result = Boolean.valueOf(response).booleanValue();
//			handler.sendMessage(handler.obtainMessage(0, result));			
//		}
//		
//	}
}