package models.obdme.statistics;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.obdme.Vehicles.Vehicle;

import play.db.jpa.Model;

@Entity
@Table(name="vehicledataset")
public class VehicleDataset extends Model {
	
	@Column(name="date", nullable=false)
	public Date date;
	
	/* Persisted Relations */
	
	@OneToMany
	@JoinColumn(name="dataset_id", nullable=false)
	Set<Datapoint> datapoints;
	
	@OneToMany
	@JoinColumn(name="dataset_id")
	Location location;
	
	/* End Persisted Relations */
	
}