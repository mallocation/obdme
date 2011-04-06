package api.entities.graph.statistics;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import api.entities.graph.inertial.StatAcceleration;
import api.entities.graph.spatial.StatLocation;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class StatDataset implements Serializable {
	
	@SerializedName("timestamp")
	private Date timestamp;
	
	@SerializedName("datapoints")
	private List<StatDataPoint> datapoints;
	
	@SerializedName("email")
	private String email;
	
	@SerializedName("location")
	private StatLocation location;
	
	@SerializedName("acceleration")
	private StatAcceleration acceleration;
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<StatDataPoint> getDatapoints() {
		return datapoints;
	}

	public void setDatapoints(List<StatDataPoint> datapoints) {
		this.datapoints = datapoints;
	}

	public void setLocation(StatLocation location) {
		this.location = location;
	}

	public StatLocation getLocation() {
		return location;
	}

	public void setAcceleration(StatAcceleration acceleration) {
		this.acceleration = acceleration;
	}

	public StatAcceleration getAcceleration() {
		return acceleration;
	}

}