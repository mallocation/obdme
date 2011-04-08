package edu.unl.csce.obdme.api.entities.graph.trips;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class Trip implements Serializable {
	
	private static final long serialVersionUID = -1952537954933976561L;

	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("alias")
	private String alias;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}