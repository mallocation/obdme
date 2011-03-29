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

public class VehicleStatPush implements Serializable {
	
	private static final long serialVersionUID = -7328584761603203312L;
	
	public VehicleStatPush(String VIN) {
		this.VIN = VIN;
		this.statSets = new ArrayList<StatDataset>();
	}

	@JsonProperty("datasets")
	public List<StatDataset> statSets;
	
	@JsonProperty("vin")
	public String VIN;
	
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
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("vin", this.VIN);
			jsonGenerator.writeArrayFieldStart("datasets");
			for (StatDataset ds: this.statSets) {
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField("email", ds.email);
				jsonGenerator.writeStringField("timestamp", DateFormat.getDateTimeInstance().format(ds.timestamp));
				jsonGenerator.writeArrayFieldStart("datapoints");
				for (StatDataPoint dp: ds.datapoints) {
					jsonGenerator.writeStartObject();
					jsonGenerator.writeStringField("mode", dp.mode);
					jsonGenerator.writeStringField("pid", dp.pid);
					jsonGenerator.writeNumberField("value", dp.value);
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