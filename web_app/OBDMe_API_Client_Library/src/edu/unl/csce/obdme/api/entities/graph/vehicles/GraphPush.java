package edu.unl.csce.obdme.api.entities.graph.vehicles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class GraphPush implements Serializable {
	
	private static final long serialVersionUID = -3981394150612806357L;
	private List<GraphEntry> entries;
	
	public GraphPush() {
		this.entries = new ArrayList<GraphEntry>();
	}

	public List<GraphEntry> getEntries() {
		return this.entries;
	}

	public void setEntries(List<GraphEntry> entries) {
		this.entries = entries;
	}
	
	public String toJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}