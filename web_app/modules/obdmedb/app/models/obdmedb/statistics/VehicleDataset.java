package models.obdmedb.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.obdmedb.User;
import models.obdmedb.spatial.VehicleLocation;
import models.obdmedb.vehicles.Vehicle;
import play.db.jpa.Model;


@Entity
@Table(name="vehicledataset")
@SuppressWarnings("serial")
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
	
	@OneToOne(optional=true)
	@JoinColumn(name="locationid", referencedColumnName="id")
	public VehicleLocation location;
	
	@OneToMany
	@JoinColumn(name="datasetid", referencedColumnName="id")
	public List<VehicleDataPoint> datapoints;
	
	public VehicleDataset(Vehicle vehicle, User user, Date timestamp, VehicleLocation location) {
		this.vehicle = vehicle;
		this.user = user;
		this.timestamp = timestamp;
		this.location = location;
		this.datapoints = new ArrayList<VehicleDataPoint>();
	}
	
	public static List<VehicleDataset> getLatestDatasetsForVehicle(Vehicle vehicle, int count) {
		List<VehicleDataset> datasets = VehicleDataset.find("select vds from VehicleDataset vds " +
										"where vehicleid=? " + 
										"order by timestamp", vehicle.getId()).fetch(count);
		return datasets;
	}
	
	
}