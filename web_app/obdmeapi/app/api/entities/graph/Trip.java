package api.entities.graph;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Trip implements Serializable {
	
	@SerializedName("id")
	private Long id;
	
	@SerializedName("alias")
	private String alias;
	
	public Trip(models.obdmedb.trips.Trip modelTrip) {
		this.id = modelTrip.getId();
		this.alias = modelTrip.getAlias();
	}
	

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
