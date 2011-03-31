package models.obdmedb.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.obdmedb.obd.ObdPid;
import models.obdmedb.vehicles.Vehicle;

import org.hibernate.annotations.GenericGenerator;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

@Entity
@Embeddable
@Table(name="vehicledatapoint")
public class VehicleDataPoint extends GenericModel {
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	public String id;
	
	@ManyToOne
	@JoinColumn(name="datasetid", referencedColumnName="id")
	public VehicleDataset dataset;
	
	@Column(name="mode")
	public String mode;
	
	@Column(name="pid")
	public String pid;
	
	@Column(name="value")
	public double value;
	
	public VehicleDataPoint(VehicleDataset dataset, String mode, String pid, double value) {
		this.dataset = dataset;
		this.mode = mode;
		this.pid = pid;
		this.value = value;
	}
	
	public static List<Double> getLatestValuesForPid(ObdPid obdPid, String VIN, int nLatestPoints) {
		Vehicle vehicle = Vehicle.findByVIN(VIN);
		List<VehicleDataset> datasets = VehicleDataset.getLatestDatasetsForVehicle(Vehicle.findByVIN(VIN), nLatestPoints);
		
		if (datasets.size() < 1) {
			return new ArrayList<Double>();
		}
		
		long lSinceDataSetId = datasets.get(datasets.size() - 1).getId();
		
		
		List<Double> values = VehicleDataPoint.find(
				"select vdp.value from VehicleDataPoint vdp,  VehicleDataset vds " +
				//" inner join VehicleDataset vds" +
				//" on vds.id = vdp.datasetid and vds.vehicleid=?" +
				" where vdp.dataset = vds and vdp.mode like ? and vdp.pid like ?" +
				" and vds.id >= ?" + 
				" order by vds.timestamp asc", obdPid.getMode(), obdPid.getPid(), lSinceDataSetId).fetch(nLatestPoints);
		
		return values;		
	}
	
}