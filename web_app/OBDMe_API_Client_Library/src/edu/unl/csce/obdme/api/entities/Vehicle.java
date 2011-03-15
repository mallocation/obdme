package edu.unl.csce.obdme.api.entities;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The Class Vehicle.
 * This represents a vehicle associated with the obdme system.
 */
public class Vehicle implements Serializable {

	/** Default serial version id. */
	private static final long serialVersionUID = -8327945489817252252L;
	
	/** The vehicle id. */
	@JsonProperty("vehicleid")
	private long vehicleId;
	
	/** The VIN. */
	@JsonProperty("vin")
	private String VIN;

	/**
	 * Gets the VIN of the vehicle.
	 *
	 * @return the VIN
	 */
	public String getVIN() {
		return this.VIN;
	}
	
	/**
	 * Sets the VIN of the vehicle.
	 *
	 * @param VIN the new VIN
	 */
	public void setVIN(String VIN) {
		this.VIN = VIN;
	}
	
	/**
	 * Gets the vehicle id.
	 *
	 * @return the vehicle id
	 */
	public long getVehicleId() {
		return vehicleId;
	}


	/**
	 * Sets the vehicle id.
	 *
	 * @param vehicleId the new vehicle id
	 */
	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}
	
}