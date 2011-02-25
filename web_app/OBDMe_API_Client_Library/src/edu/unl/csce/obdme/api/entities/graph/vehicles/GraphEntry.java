package edu.unl.csce.obdme.api.entities.graph.vehicles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Class GraphEntry.
 */
public class GraphEntry implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8575830165765977418L;
	
	/** The VIN. */
	private String VIN;
	
	/** The points. */
	private List<GraphPoint> points;
	
	/** The timestamp. */
	private Date timestamp;
	
	/**
	 * Instantiates a new graph entry.
	 */
	public GraphEntry() {
		this.points = new ArrayList<GraphPoint>();
	}

	/**
	 * Gets the vIN.
	 *
	 * @return the vIN
	 */
	public String getVIN() {
		return VIN;
	}

	/**
	 * Sets the vIN.
	 *
	 * @param vIN the new vIN
	 */
	public void setVIN(String vIN) {
		VIN = vIN;
	}

	/**
	 * Gets the points.
	 *
	 * @return the points
	 */
	public List<GraphPoint> getPoints() {
		return points;
	}

	/**
	 * Sets the points.
	 *
	 * @param points the new points
	 */
	public void setPoints(List<GraphPoint> points) {
		this.points = points;
	}
	
	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}