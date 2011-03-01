package models.obdme.statistics;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.obdme.Vehicles.Vehicle;
import play.db.jpa.Model;

@Entity
@Table(name="vehicledataset")
public class VehicleDataset extends Model {
	
	/* Persisted Fields */
	
	@Column(name="timestamp", nullable=false)
	public Date timestamp;
	
	/* End Persisted Fields */	
	
	/* Persisted Relations */
	
	@ManyToOne
	@JoinColumn(name="vehicle_id")
	Vehicle vehicle;
	
	public VehicleDataset(Vehicle vehicle, Date timestamp) {
		this.vehicle = vehicle;
		this.timestamp = timestamp;
	}
		
	/* End Persisted Relations */
	
}