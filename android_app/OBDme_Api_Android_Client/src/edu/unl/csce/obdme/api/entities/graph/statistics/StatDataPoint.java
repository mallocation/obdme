package edu.unl.csce.obdme.api.entities.graph.statistics;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class StatDataPoint implements Serializable {
	
	private static final long serialVersionUID = -7551017654799099864L;
	
	public StatDataPoint(String mode, String pid, double value) {
		this.mode = mode;
		this.pid = pid;
		this.value = value;
	}

	@JsonProperty("mode")
	public String mode;
	
	@JsonProperty("pid")
	public String pid;
	
	@JsonProperty("value")
	public double value;

}
