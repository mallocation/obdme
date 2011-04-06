package api.entities.graph.spatial;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class VehicleAcceleration implements Serializable {
	
	@SerializedName("linearxaccel")
	public Double linearxaccel;
	
	@SerializedName("linearyaccel")
	public Double linearyaccel;
	
	@SerializedName("linearzaccel")
	public Double linearzaccel;
	

}
