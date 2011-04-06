package api.entities.graph.statistics;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class StatDataPoint implements Serializable {
	
	@SerializedName("mode")
	private String mode;
	
	@SerializedName("pid")
	private String pid;
	
	@SerializedName("value")
	private String value;
	
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
		return Double.parseDouble(value);
	}

	public void setValue(String value) {
		this.value = value;
	}

}
