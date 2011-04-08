package edu.unl.csce.obdme.api.entities.graph.statistics;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import edu.unl.csce.obdme.api.entities.graph.inertial.VehicleAcceleration;
import edu.unl.csce.obdme.api.entities.graph.spatial.VehicleLocation;

public class StatDataset {
	
	@JsonProperty("datapoints")
	public List<StatDataPoint> datapoints;
	
	@JsonProperty("email")
	public String email;
	
	@JsonProperty("timestamp")
	@JsonSerialize(using=CustomDateSerializer.class)
	public Date timestamp;
	
	@JsonProperty("location")
	public VehicleLocation location;
	
	@JsonProperty("acceleration")
	public VehicleAcceleration acceleration;
	
	@JsonProperty("tripid")
	public Long tripId;
	
	public StatDataset() {
		this.datapoints = new ArrayList<StatDataPoint>();
		this.tripId = -1L;
	}
	
	public StatDataset(Long tripId) {
		this.datapoints = new ArrayList<StatDataPoint>();
		this.tripId = tripId;
	}
	
	private static class CustomDateSerializer extends JsonSerializer<Date> {
		@Override
		public void serialize(Date arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException, JsonProcessingException {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			String formattedDate = sdf.format(arg0);
			arg1.writeString(formattedDate);			
		}		
	}	
}