package models.obdmedb.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import models.obdmedb.obd.ObdPid;
import models.obdmedb.trips.Trip;
import models.obdmedb.vehicles.Vehicle;

import org.hibernate.annotations.GenericGenerator;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

@Entity
@Embeddable
@Table(name="vehicledatapoint")
@SuppressWarnings("serial")
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
		List<VehicleDataset> datasets = VehicleDataset.getLatestDatasetsForVehicle(vehicle, nLatestPoints);
		
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
	
	public static List<LatestDataPoint> selectLatestDataPoints(ObdPid obdPid, Vehicle vehicle, int nLatestPoints) {
		return selectLatestDataPointsSinceDataset(obdPid, vehicle, Long.MAX_VALUE, nLatestPoints);
	}

	

	public static List<LatestDataPoint> selectLatestDataPointsSinceDataset(ObdPid obdPid, Vehicle vehicle, long sinceDatasetId, int nLatestPoints) {
		String SQL = "select * from (select vd.timestamp, dp.value from vehicledataset vd " +
						"inner join vehicledatapoint dp on vd.id = dp.datasetid " +
						"where vd.id < ? and dp.mode like ? and dp.pid like ? and vd.vehicleid=? " +
						"order by vd.timestamp desc limit ?) as tempq order by tempq.timestamp asc";
		ArrayList<LatestDataPoint> parsedResults = new ArrayList<LatestDataPoint>();	
		
		Query query = JPA.em().createNativeQuery(SQL); 
		query.setParameter(1, sinceDatasetId);
		query.setParameter(2, obdPid.getMode());
		query.setParameter(3, obdPid.getPid());
		query.setParameter(4, vehicle.getId());
		query.setParameter(5, nLatestPoints);
		List<Object[]> results = query.getResultList();
		
		for (Object[] row : results) {
			LatestDataPoint result = new LatestDataPoint();
			result.timestamp = (Date) row[0];
			result.value = new Double(row[1].toString());
			parsedResults.add(result);
		}
		
		return parsedResults;
	}
	
	public static Long selectDataPointCountForTrip(Trip trip) {
		String SQL = "select count(*) from trip tr, vehicledataset vd, vehicledatapoint dp " + 
					"where tr.id = ? " + 
					"and vd.tripid = tr.id " + 
					"and dp.datasetid = vd.id";
		Query query = JPA.em().createNativeQuery(SQL);
		query.setParameter(1, trip.getId());
		return Long.parseLong(query.getSingleResult().toString());
	}
	
	public static class LatestDataPoint {
		public Date timestamp;
		public double value;
	}
	
}