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

public class StatDataset {
	
	@JsonProperty("datapoints")
	public List<StatDataPoint> datapoints;
	
	@JsonProperty("email")
	public String email;
	
	@JsonProperty("timestamp")
	@JsonSerialize(using=CustomDateSerializer.class)
	public Date timestamp;
	
	public StatDataset() {
		this.datapoints = new ArrayList<StatDataPoint>();
	}
	
//	public StatDataset(String email, Date timestamp) {
//		this.email = email;
//		this.timestamp = timestamp;
//		this.datapoints = new ArrayList<StatDataPoint>();
//	}
	
	private static class CustomDateSerializer extends JsonSerializer<Date> {
		@Override
		public void serialize(Date arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException, JsonProcessingException {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			String formattedDate = sdf.format(arg0);
			arg1.writeString(formattedDate);			
		}		
	}	
}