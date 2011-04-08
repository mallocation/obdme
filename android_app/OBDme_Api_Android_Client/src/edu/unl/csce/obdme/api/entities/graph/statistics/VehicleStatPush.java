package edu.unl.csce.obdme.api.entities.graph.statistics;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.annotate.JsonProperty;

import android.util.Log;

/**
 * The Class VehicleStatPush.
 */
public class VehicleStatPush implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7328584761603203312L;

	/**
	 * Instantiates a new vehicle stat push.
	 *
	 * @param VIN the vIN
	 */
	public VehicleStatPush(String VIN) {
		this.VIN = VIN;
		this.statSets = new ArrayList<StatDataset>();
	}

	/** The stat sets. */
	@JsonProperty("datasets")
	public List<StatDataset> statSets;

	/** The VIN. */
	@JsonProperty("vin")
	public String VIN;

	/**
	 * To json string.
	 *
	 * @return the string
	 */
	public String toJSONString() {
		StringWriter json = new StringWriter();
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator = null;

		try {
			jsonGenerator = jsonFactory.createJsonGenerator(json);
		} catch (IOException e) {
			Log.e("VehicleStatPush", e.getMessage());
			return null;
		}

		try {

			//Write the StatPush object
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("vin", this.VIN);

			//Write the DataSet objects
			jsonGenerator.writeArrayFieldStart("datasets");
			for (StatDataset ds: this.statSets) {
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField("email", ds.email);
				jsonGenerator.writeStringField("timestamp", DateFormat.getDateTimeInstance().format(ds.timestamp));
				
				if (ds.tripId == null || ds.tripId < 1L) {
					jsonGenerator.writeNullField("tripid");
				} else {
					jsonGenerator.writeNumberField("tripid", ds.tripId);
				}


				//VehicleLocation Object
				if (ds.location == null) {
					jsonGenerator.writeNullField("location");
				}
				else {
					jsonGenerator.writeObjectFieldStart("location");
					jsonGenerator.writeStringField("accuracy", ds.location.accuracy);
					jsonGenerator.writeStringField("bearing", ds.location.bearing);
					jsonGenerator.writeStringField("altitude", ds.location.altitude);
					jsonGenerator.writeStringField("latitude", ds.location.latitude);
					jsonGenerator.writeStringField("longitude", ds.location.longitude);
					jsonGenerator.writeEndObject();
				}

				//VehicleAcceleration Object
				if (ds.acceleration == null) {
					jsonGenerator.writeNullField("acceleration");
				}
				else {
					jsonGenerator.writeObjectFieldStart("acceleration");
					jsonGenerator.writeStringField("accel_x", ds.acceleration.accel_x);
					jsonGenerator.writeStringField("accel_y", ds.acceleration.accel_y);
					jsonGenerator.writeStringField("accel_z", ds.acceleration.accel_z);
					jsonGenerator.writeStringField("linear_accel_x", ds.acceleration.linear_accel_x);
					jsonGenerator.writeStringField("linear_accel_y", ds.acceleration.linear_accel_y);
					jsonGenerator.writeStringField("linear_accel_z", ds.acceleration.linear_accel_z);
					jsonGenerator.writeEndObject();
				}

				//Write the DataPoint objects
				jsonGenerator.writeArrayFieldStart("datapoints");
				for (StatDataPoint dp: ds.datapoints) {
					jsonGenerator.writeStartObject();
					jsonGenerator.writeStringField("mode", dp.mode);
					jsonGenerator.writeStringField("pid", dp.pid);
					jsonGenerator.writeStringField("value", dp.value);
					jsonGenerator.writeEndObject();
				}
				jsonGenerator.writeEndArray();
				jsonGenerator.writeEndObject();
			}
			jsonGenerator.writeEndArray();
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			return json.toString();

		} catch (Exception e) {
			Log.e("VehicleStatPush", e.getMessage());
			return null;
		}

		//		GraphPush gp = createGraphPush();
		//		StringWriter json = new StringWriter();
		//		JsonFactory f = new JsonFactory();
		//		JsonGenerator g = null;
		//		try {
		//			g = f.createJsonGenerator(json);
		//		} catch (IOException e) {
		//			fail(e.getMessage());
		//		}		
		//		// start it out
		//		try {
		//			g.writeStartObject();			
		//			g.writeArrayFieldStart("entries");			
		//			for (GraphEntry entry : gp.getEntries()) {
		//				g.writeStartObject();				
		//				g.writeArrayFieldStart("points");				
		//				for (GraphPoint p : entry.getPoints()) {
		//					g.writeStartObject();
		//					g.writeStringField("mode", p.getMode());
		//					g.writeStringField("pid", p.getPid());
		//					g.writeStringField("value", p.getValue());
		//					g.writeEndObject();
		//				}				
		//				g.writeEndArray();
		//				g.writeEndObject();				
		//			}			
		//			g.writeEndArray();			
		//			g.writeEndObject();
		//			g.close();
		//		} catch (Exception e) {
		//			fail(e.getMessage());
		//		}
		//		Log.i("", json.toString());
		//		assertTrue(json.toString().length() > 0);
	}

}