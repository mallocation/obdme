package edu.unl.csce.obdme.api.entities.graph.inertial;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The Class VehicleAcceleration.
 */
public class VehicleAcceleration {
	
	/** The accel_x. */
	@JsonProperty("accel_x")
	public String accel_x;
	
	/** The accel_y. */
	@JsonProperty("accel_y")
	public String accel_y;
	
	/** The accel_z. */
	@JsonProperty("accel_z")
	public String accel_z;
	
	/** The linear_accel_x. */
	@JsonProperty("linear_accel_x")
	public String linear_accel_x;
	
	/** The linear_accel_y. */
	@JsonProperty("linear_accel_y")
	public String linear_accel_y;
	
	/** The linear_accel_z. */
	@JsonProperty("linear_accel_z")
	public String linear_accel_z;

}
