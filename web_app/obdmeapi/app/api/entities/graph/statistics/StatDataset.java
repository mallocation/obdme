package api.entities.graph.statistics;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.google.gson.annotations.SerializedName;

public class StatDataset implements Serializable {
	
	@SerializedName("timestamp")
	private Date timestamp;
	
	@SerializedName("datapoints")
	private List<StatDataPoint> datapoints;
	
	@SerializedName("email")
	private String email;
	
	@SerializedName("accuracy")
	public Float accuracy;
	
	@SerializedName("bearing")
	public Float bearing;
	
	@SerializedName("altitude")
	public Double altitude;
	
	@SerializedName("latitude")
	public Double latitude;
	
	@SerializedName("longitude")
	public Double longitude;
	

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

	public Float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Float accuracy) {
		this.accuracy = accuracy;
	}

	public Float getBearing() {
		return bearing;
	}

	public void setBearing(Float bearing) {
		this.bearing = bearing;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}	

}