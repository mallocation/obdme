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
	
	@Column(name="maker")
	public String maker;
	
	@Column(name="model")
	public String model;
	
	@Column(name="year")
	public String year;
	
	@Column(name="avatar")//, columnDefinition="BLOB")
	public Blob avatar;
	
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
	
	public static Vehicle findByVIN(String VIN) {
		return find("vin", VIN).first();
	}	
	
	public static Vehicle findById(long id) {
		return find("id", id).first();
	}
	
	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Blob getAvatar() {
		return avatar;
	}

	public void setAvatar(Blob avatar) {
		this.avatar = avatar;
	}
    
}