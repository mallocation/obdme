package edu.unl.csce.obdme.api.entities.graph.vehicles;

import java.io.Serializable;

/**
 * The Class GraphPoint.
 */
public class GraphPoint implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1683133256179599140L;
	
	/** The mode. */
	private String mode;
	
	/** The pid. */
	private String pid;
	
	/** The value. */
	private String value;
	
	/**
	 * Instantiates a new graph point.
	 */
	public GraphPoint() {}
	
	/**
	 * Instantiates a new graph point.
	 *
	 * @param mode the mode
	 * @param pid the pid
	 * @param value the value
	 */
	public GraphPoint(String mode, String pid, String value) {
		this.mode = mode;
		this.pid = pid;
		this.value = value;
	}

	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode the new mode
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Gets the pid.
	 *
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * Sets the pid.
	 *
	 * @param pid the new pid
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}