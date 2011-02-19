package models.obdme.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.obdme.Vehicles.Vehicle;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="vehicledataset")
public class VehicleDataset extends Model {
	
	/* Persisted Fields */
	
	@Column(name="date", nullable=false)
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