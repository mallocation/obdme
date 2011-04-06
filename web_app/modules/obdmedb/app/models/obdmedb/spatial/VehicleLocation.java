package models.obdmedb.spatial;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Embeddable
@Table(name="vehiclelocation")
@SuppressWarnings("serial")
public class VehicleLocation extends Model {
	
	@Column(name="accuracy")
	public float accuracy;
	
	@Column(name="bearing")
	public float bearing;
	
	@Column(name="altitude")
	public double altitude;
	
	@Column(name="latitude")
	public double latitude;
	
	@Column(name="longitude")
	public double longitude;
	
	public VehicleLocation(float accuracy, float bearing, 
			double altitude, double latitude, double longitude) {
		this.accuracy = accuracy;
		this.bearing = bearing;
		this.altitude = altitude;
		this.latitude = latitude;
		this.longitude = longitude;
	}

}