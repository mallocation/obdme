package api.entities.graph.statistics;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class VehicleGraphPush implements Serializable {
	
	@SerializedName("vin")
	private String VIN;
	
	@SerializedName("datasets")
	private List<StatDataset> datasets;	

	public String getVIN() {
		return VIN;
	}

	public void setVIN(String VIN) {
		this.VIN = VIN;
	}

	public List<StatDataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<StatDataset> datasets) {
		this.datasets = datasets;
	}

}