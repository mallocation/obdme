package edu.unl.csce.obdme.api.entities.graph.vehicles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GraphPush.
 */
public class GraphPush implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3981394150612806357L;
	
	/** The entries. */
	private List<GraphEntry> entries;
	
	/**
	 * Instantiates a new graph push.
	 */
	public GraphPush() {
		this.entries = new ArrayList<GraphEntry>();
	}

	/**
	 * Gets the entries.
	 *
	 * @return the entries
	 */
	public List<GraphEntry> getEntries() {
		return this.entries;
	}

	/**
	 * Sets the entries.
	 *
	 * @param entries the new entries
	 */
	public void setEntries(List<GraphEntry> entries) {
		this.entries = entries;
	}
	
//	/**
//	 * To json string.
//	 *
//	 * @return the string
//	 */
//	public String toJSONString() {
//		Gson gson = new Gson();
//		return gson.toJson(this);
//	}
}