package models.obdme.statistics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name="location")
public class Location extends GenericModel {
	
	/* Persisted Fields */
	
	@Column(name="latitude", nullable=false)
	public double latitude;
	
	@Column(name="longitude", nullable=false)
	public double longitude;
	
	@Column(name="accuracy", nullable=true)
	public float accuracy;	
	
	@Column(name="bearing", nullable=true)
	public float bearing;
	
	/* End Persisted Fields */
	
	/* Default Constructor. */
	public Location(){}
} 