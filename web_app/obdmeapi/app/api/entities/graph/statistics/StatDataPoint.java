package api.entities.graph.statistics;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StatDataPoint implements Serializable {
	
	@SerializedName("mode")
	private String mode;
	
	@SerializedName("pid")
	private String pid;
	
	@SerializedName("value")
	private double value;
	
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

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
