package edu.unl.csce.obdme.api.entities.graph.vehicles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphEntry implements Serializable {
	
	private static final long serialVersionUID = -8575830165765977418L;
	private String VIN;
	private List<GraphPoint> points;
	
	public GraphEntry() {
		this.points = new ArrayList<GraphPoint>();
	}

	public String getVIN() {
		return VIN;
	}

	public void setVIN(String vIN) {
		VIN = vIN;
	}

	public List<GraphPoint> getPoints() {
		return points;
	}

	public void setPoints(List<GraphPoint> points) {
		this.points = points;
	}
}