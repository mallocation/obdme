package models.obdme.Vehicles;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import models.obdme.User;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="Vehicle")
public class Vehicle extends Model {
	
	@Required
	@Column(unique=true, nullable=false)
	public String VIN;
	
	public Vehicle(String VIN) {
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