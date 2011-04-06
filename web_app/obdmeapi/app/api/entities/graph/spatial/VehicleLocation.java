package api.entities.graph.spatial;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class VehicleLocation implements Serializable {
	
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
