package api.entities.graph.spatial;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class StatLocation implements Serializable {
	
	@SerializedName("accuracy")
	public String accuracy;
	
	@SerializedName("bearing")
	public String bearing;
	
	@SerializedName("altitude")
	public String altitude;
	
	@SerializedName("latitude")
	public String latitude;
	
	@SerializedName("longitude")
	public String longitude;
	
	public float getAccuracy() {
		return Float.parseFloat(accuracy);
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	public float getBearing() {
		return Float.parseFloat(bearing);
	}

	public void setBearing(String bearing) {
		this.bearing = bearing;
	}

	public double getAltitude() {
		return Double.parseDouble(altitude);
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	public double getLatitude() {
		return Double.parseDouble(latitude);
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return Double.parseDouble(longitude);
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}	

}
