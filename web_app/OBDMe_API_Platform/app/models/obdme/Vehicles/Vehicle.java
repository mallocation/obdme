package models.obdme.Vehicles;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.obdme.User;
import models.obdme.statistics.VehicleDataset;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="vehicles")
public class Vehicle extends Model {
	
	/* Persisted Fields */
	
	@Required
	@Column(name="vin", unique=true, nullable=false)
	public String VIN;
	
	/* End Persisted Fields */
	
	/* Persisted Relations */
	
	@ManyToMany(mappedBy="vehicles")
	Set<User> owners;
	
	@OneToMany
	@JoinColumn(name="vehicle_id", nullable=false)
	Set<VehicleDataset> datasets;
	
	/* End Persisted Relations */
	
	/* Default Constructor */
	public Vehicle(){}
	
	private Vehicle(String VIN) {
		this.VIN = VIN;
	}	
	
	public static Vehicle addVehicle(String vin) {
		Vehicle vehicle = findByVIN(vin);
		if (vehicle == null) {
			vehicle = new Vehicle(vin);
			vehicle.validateAndSave();
		}
		return vehicle;
	}
	
	
	/**
	 * Find a vehicle by VIN number.
	 *
	 * @param VIN the VIN for the vehicle
	 * @return the vehicle if it exists, or null otherwise
	 */
	public static Vehicle findByVIN(String VIN) {
		return find("VIN", VIN).first();
	}
}