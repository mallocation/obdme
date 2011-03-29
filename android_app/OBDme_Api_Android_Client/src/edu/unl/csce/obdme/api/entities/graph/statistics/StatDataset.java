package edu.unl.csce.obdme.api.entities.graph.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class StatDataset {
	
	@JsonProperty("datapoints")
	public List<StatDataPoint> datapoints;
	
	@JsonProperty("email")
	public String email;
	
	@JsonProperty("timestamp")
	public Date timestamp;
	
	public StatDataset(String email, Date timestamp) {
		this.email = email;
		this.timestamp = timestamp;
		this.datapoints = new ArrayList<StatDataPoint>();
	}
	
}