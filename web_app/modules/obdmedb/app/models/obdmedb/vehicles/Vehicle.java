package models.obdmedb.vehicles;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="vehicle")
public class Vehicle extends Model {

	@Required
	@Column(name="vin", unique=true)
	public String VIN;
	
	public String getVIN() {
		return VIN;
	}

	public void setVIN(String VIN) {
		this.VIN = VIN;
	}

	private Vehicle(String VIN) {
		this.VIN = VIN;
	}
	
	public static Vehicle addVehicleIfNotExist(String VIN) {
		Vehicle veh = findByVIN(VIN);
		if (veh == null) {
			veh = new Vehicle(VIN.toUpperCase());
			veh.validateAndSave();
		}
		return veh;
	}
	
	/**
	 * Find a vehicle by VIN.
	 *
	 * @param VIN the VIN to search for
	 * @return the vehicle if exists otherwise null
	 */
	public static Vehicle findByVIN(String VIN) {
		return find("vin", VIN).first();
	}	
    
}