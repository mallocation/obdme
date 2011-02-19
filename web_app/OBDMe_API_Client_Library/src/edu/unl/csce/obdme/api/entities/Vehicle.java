package edu.unl.csce.obdme.api.entities;

import java.io.Serializable;

public class Vehicle implements Serializable {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = -8327945489817252252L;
	
	private long id;
	private String VIN;
	
	public long getId() {
		return this.id;
	}
	
	public String getVIN() {
		return this.VIN;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setVIN(String VIN) {
		this.VIN = VIN;
	}
}
