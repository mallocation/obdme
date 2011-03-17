package api.entities.graph.vehicle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ThisExpression;

import com.google.gson.Gson;


public class GraphPush implements Serializable {
	
	private List<GraphEntry> entries;
	
	public GraphPush() {
		this.entries = new ArrayList<GraphEntry>();
	}

	public List<GraphEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<GraphEntry> entries) {
		this.entries = entries;
	}
	
	public static GraphPush fromJSONString(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, GraphPush.class);
	}
	
	public String toJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
