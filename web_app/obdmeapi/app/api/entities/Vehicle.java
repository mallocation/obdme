package api.entities;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Vehicle implements Serializable {
	
	@SerializedName("vehicleid")
	private Long vehicleId;
	@SerializedName("vin")
	private String VIN;
	
	public Vehicle(Long vehicleId, String VIN) {
		this.vehicleId = vehicleId;
		this.VIN = VIN;
	}
	
	public Vehicle(models.obdmedb.vehicles.Vehicle modelVehicle) {
		this.vehicleId = modelVehicle.getId();
		this.VIN = modelVehicle.getVIN();
	}
	
	public Long getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getVIN() {
		return VIN;
	}
	public void setVIN(String vIN) {
		VIN = vIN;
	}

}
