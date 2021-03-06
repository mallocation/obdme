package api.entities.graph.vehicle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.core.dom.ThisExpression;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class GraphEntry implements Serializable {

	private String VIN;	
	private List<GraphPoint> points;	
	private Date timestamp;
	
	public GraphEntry() {
		this.points = new ArrayList<GraphPoint>();
	}

	public String getVIN() {
		return VIN;
	}

	public void setVIN(String VIN) {
		this.VIN = VIN;
	}

	public List<GraphPoint> getPoints() {
		return points;
	}

	public void setPoints(List<GraphPoint> points) {
		this.points = points;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}