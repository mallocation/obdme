package api.entities.graph.vehicle;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class GraphPoint implements Serializable {
	private String mode;
	private String pid;
	private String value;
	
	public GraphPoint(){}
	
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
