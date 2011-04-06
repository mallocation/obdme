package api.entities.graph.statistics;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import api.entities.graph.spatial.VehicleLocation;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class StatDataset implements Serializable {
	
	@SerializedName("timestamp")
	private Date timestamp;
	
	@SerializedName("datapoints")
	private List<StatDataPoint> datapoints;
	
	@SerializedName("email")
	private String email;
	
	@SerializedName("vehiclelocation")
	private VehicleLocation location;
	
	
	

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

	public void setLocation(VehicleLocation location) {
		this.location = location;
	}

	public VehicleLocation getLocation() {
		return location;
	}

}