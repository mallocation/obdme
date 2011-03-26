package models.obdmedb.statistics;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import models.obdmedb.User;
import models.obdmedb.vehicles.Vehicle;

import java.util.*;

@Entity
@Table(name="vehicledataset")
public class VehicleDataset extends Model {
	
	@ManyToOne(optional=false)
	@JoinColumn(name="vehicleid", referencedColumnName="id")
	public Vehicle vehicle;
	
	@ManyToOne(optional=true)
	@JoinColumn(name="userid", referencedColumnName="id")
	public User user;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="timestamp")
	public Date timestamp;
	
	@OneToMany
	@JoinColumn(name="datasetid", referencedColumnName="id")
	public List<VehicleDataPoint> datapoints;
	
	public VehicleDataset(Vehicle vehicle, User user, Date timestamp) {
		this.vehicle = vehicle;
		this.user = user;
		this.timestamp = timestamp;
		this.datapoints = new ArrayList<VehicleDataPoint>();
	}
	
}