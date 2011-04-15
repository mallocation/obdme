package models.obdmedb.statistics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

	public static List<LatestDataPoint> selectDataPointsForDate(ObdPid obdPid, Vehicle vehicle, Date date) {
		Calendar cal = Calendar.getInstance();
		Date startDate, endDate;
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		startDate = cal.getTime();

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);


		endDate = cal.getTime();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String SQL = "select vd.timestamp, dp.value from vehicledataset vd " +
		"inner join vehicledatapoint dp on vd.id = dp.datasetid " +
		"where vd.vehicleid=? and " +
		"dp.mode like ? and dp.pid like ? and "+
		"vd.timestamp between ? and ?";
		Query query = JPA.em().createNativeQuery(SQL);
		query.setParameter(1, vehicle.getId());
		query.setParameter(2, obdPid.getMode());
		query.setParameter(3, obdPid.getPid());
		query.setParameter(4, dateFormat.format(startDate));
		query.setParameter(5, dateFormat.format(endDate));
		List<Object[]> queryResults = query.getResultList();
		List<LatestDataPoint> results = new ArrayList<LatestDataPoint>();
		for (Object[] row : queryResults) {
			LatestDataPoint dp = new LatestDataPoint();
			dp.timestamp = (Date) row[0];
			dp.value = Double.parseDouble(row[1].toString());
			results.add(dp);
		}
		return results;

	}

	public static Long selectDataPointCountForTrip(Long tripId) {
		String SQL = "select count(*) from trip tr, vehicledataset vd, vehicledatapoint dp " + 
		"where tr.id = ? " + 
		"and vd.tripid = tr.id " + 
		"and dp.datasetid = vd.id";
		Query query = JPA.em().createNativeQuery(SQL);
		query.setParameter(1, tripId);
		return Long.parseLong(query.getSingleResult().toString());
	}
	
	public static interface IObdPidHelper {
		String getObdPidUnit(ObdPid obdPid);
		String getObdPidDecimalFormat(ObdPid obdPid);		
	}

	public static List<TimeStatisticSet> selectMaxMinAvgAllPIDSForRange(Vehicle vehicle, Calendar startDate, Calendar endDate, IObdPidHelper pidHelper) {

		String SQL = "select dp.pid, Min(dp.value), Max(dp.value), Avg(dp.value) " +
		"from vehicledataset vd " +
		"inner join vehicledatapoint dp on vd.id = dp.datasetid " +
		"where vd.vehicleid=? and " +
		"vd.timestamp between ? and ? "+
		"group by dp.pid";


		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Query query = JPA.em().createNativeQuery(SQL); 
		query.setParameter(1, vehicle.getId());
		query.setParameter(2, dateFormat.format(startDate.getTime()));
		query.setParameter(3, dateFormat.format(endDate.getTime()));

		List<Object[]> queryResults = query.getResultList();

		List<TimeStatisticSet> statSet = new ArrayList<TimeStatisticSet>();

		for (Object[] row : queryResults) {
			TimeStatisticSet sp = new TimeStatisticSet();
			if (ObdPid.getObdPid("01", (String)row[0]) != null) {
				sp.pid = ObdPid.getObdPid("01", (String)row[0]) ;
				sp.unit = pidHelper.getObdPidUnit(sp.pid);
				//sp.unit = ObdPidUtils.getPidUnit(sp.pid);
				DecimalFormat df = new DecimalFormat();
				//df.applyPattern(ObdPidUtils.getPidDecimalFormat(sp.pid));
				df.applyPattern(pidHelper.getObdPidDecimalFormat(sp.pid));
				sp.minimum = df.format(Double.parseDouble(row[1].toString()));
				sp.maximum = df.format(Double.parseDouble(row[2].toString()));
				sp.average = df.format(Double.parseDouble(row[3].toString()));
				statSet.add(sp);
			}
		}

		return statSet;
	}

	public static PIDStatistic selectMaxMinAvgPIDSForRange(Vehicle vehicle, Calendar startDate, Calendar endDate, ObdPid obdpid) {

		String SQL = "select Min(dp.value), Max(dp.value), Avg(dp.value) " +
		"from vehicledataset vd " +
		"inner join vehicledatapoint dp on vd.id = dp.datasetid " +
		"where vd.vehicleid=? and " +
		"dp.pid like ? and dp.mode like ? and " +
		"vd.timestamp between ? and ? ";


		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Query query = JPA.em().createNativeQuery(SQL); 
		query.setParameter(1, vehicle.getId());
		query.setParameter(2, obdpid.getPid());
		query.setParameter(3, obdpid.getMode());
		query.setParameter(4, dateFormat.format(startDate.getTime()));
		query.setParameter(5, dateFormat.format(endDate.getTime()));

		List<Object[]> queryResults = query.getResultList();

		Object[] row = queryResults.get(0);
		Logger.info(queryResults.size()+"");

		if (row[0] != null) {
			PIDStatistic sp = new PIDStatistic();
			sp.pid = obdpid;
			sp.unit = ObdPidUtils.getPidUnit(sp.pid);
			DecimalFormat df = new DecimalFormat();
			df.applyPattern(ObdPidUtils.getPidDecimalFormat(sp.pid));
			sp.minimum = df.format(Double.parseDouble(row[0].toString()));
			sp.maximum = df.format(Double.parseDouble(row[1].toString()));
			sp.average = df.format(Double.parseDouble(row[2].toString()));
			return sp;
		}

		return null;
	}

	public static class LatestDataPoint {
		public Date timestamp;
		public double value;
	}

	public static class TimeStatisticSet {
		public ObdPid pid;
		public String unit;
		public String minimum;
		public String maximum;
		public String average;
	}

	public static class PIDStatistic {
		public ObdPid pid;
		public String unit;
		public String minimum;
		public String maximum;
		public String average;
	}

}