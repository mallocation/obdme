package models.obdmedb.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.obdmedb.User;
import models.obdmedb.vehicles.Vehicle;

import org.hibernate.annotations.GenericGenerator;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

@Entity
@Embeddable
@Table(name="vehiclelocation")
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
