package edu.unl.csce.obdme.api.entities.graph.spatial;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The Class VehicleLocation.
 */
public class VehicleLocation {
	
	/** The accuracy. */
	@JsonProperty("accuracy")
	public String accuracy;
	
	/** The bearing. */
	@JsonProperty("bearing")
	public String bearing;
	
	/** The altitude. */
	@JsonProperty("altitude")
	public String altitude;
	
	/** The latitude. */
	@JsonProperty("latitude")
	public String latitude;
	
	/** The longitude. */
	@JsonProperty("longitude")
	public String longitude;

}
