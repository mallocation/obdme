package edu.unl.csce.obdme.api.entities.graph.vehicles;

import java.io.Serializable;

public class GraphPoint implements Serializable {
	
	private static final long serialVersionUID = -1683133256179599140L;
	private String mode;
	private String pid;
	private String value;
	
	public GraphPoint(String mode, String pid, String value) {
		this.mode = mode;
		this.pid = pid;
		this.value = value;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}