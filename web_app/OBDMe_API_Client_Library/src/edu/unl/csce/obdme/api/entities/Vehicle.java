package edu.unl.csce.obdme.api.entities;

import java.io.Serializable;

/**
 * The Class Vehicle.
 */
public class Vehicle implements Serializable {

	/** Default serial version id. */
	private static final long serialVersionUID = -8327945489817252252L;
	
	/** The id. */
	private long id;
	
	/** The VIN. */
	private String VIN;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * Gets the vIN.
	 *
	 * @return the vIN
	 */
	public String getVIN() {
		return this.VIN;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Sets the vIN.
	 *
	 * @param VIN the new vIN
	 */
	public void setVIN(String VIN) {
		this.VIN = VIN;
	}
}
