package api.entities.graph.inertial;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class StatAcceleration implements Serializable {
	
	@SerializedName("accel_x")
	public String accel_x;
	
	@SerializedName("accel_y")
	public String accel_y;
	
	@SerializedName("accel_z")
	public String accel_z;
	
	@SerializedName("linear_accel_x")
	public String linear_accel_x;
	
	@SerializedName("linear_accel_y")
	public String linear_accel_y;
	
	@SerializedName("linear_accel_z")
	public String linear_accel_z;

	public float getAccel_x() {
		return Float.parseFloat(accel_x);
	}

	public void setAccel_x(String accel_x) {
		this.accel_x = accel_x;
	}

	public float getAccel_y() {
		return Float.parseFloat(accel_y);
	}

	public void setAccel_y(String accel_y) {
		this.accel_y = accel_y;
	}

	public float getAccel_z() {
		return Float.parseFloat(accel_z);
	}

	public void setAccel_z(String accel_z) {
		this.accel_z = accel_z;
	}

	public float getLinear_accel_x() {
		return Float.parseFloat(linear_accel_x);
	}

	public void setLinear_accel_x(String linear_accel_x) {
		this.linear_accel_x = linear_accel_x;
	}

	public float getLinear_accel_y() {
		return Float.parseFloat(linear_accel_y);
	}

	public void setLinear_accel_y(String linear_accel_y) {
		this.linear_accel_y = linear_accel_y;
	}

	public float getLinear_accel_z() {
		return Float.parseFloat(linear_accel_z);
	}

	public void setLinear_accel_z(String linear_accel_z) {
		this.linear_accel_z = linear_accel_z;
	}
	

}
