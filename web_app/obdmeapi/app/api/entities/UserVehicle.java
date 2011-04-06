package api.entities;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class UserVehicle implements Serializable {
	
	@SerializedName("vehicleid")
	private Long vehicleId;
	@SerializedName("vin")
	private String VIN;
	@SerializedName("alias")
	private String alias;	
	
	public UserVehicle(long vehicleId, String VIN, String alias) {
		this.vehicleId = vehicleId;
		this.VIN = VIN;
		this.alias = alias;		
	}	
	public Long getVehicleId() {
		return this.vehicleId;
	}
	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getVIN() {
		return this.VIN;
	}
	public void setVIN(String VIN) {
		this.VIN = VIN;
	}
	public String getAlias() {
		return this.alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}	

}