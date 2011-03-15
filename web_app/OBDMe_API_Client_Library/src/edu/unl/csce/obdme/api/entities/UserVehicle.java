package edu.unl.csce.obdme.api.entities;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The Class UserVehicle.
 * This class represents a relationship between a user and
 * a vehicle.
 */

public class UserVehicle implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8668797973007384931L;
	
	@JsonProperty("vehicleid")
	private Long vehicleId;
	@JsonProperty("vin")
	private String VIN;
	@JsonProperty("alias")
	private String alias;

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