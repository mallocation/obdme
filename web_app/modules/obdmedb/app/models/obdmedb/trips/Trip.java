package models.obdmedb.trips;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import models.obdmedb.User;
import models.obdmedb.spatial.VehicleLocation;
import models.obdmedb.statistics.VehicleDataset;
import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
@Table(name="trip")
public class Trip extends Model {
	
	@ManyToOne
	@JoinColumn(name="userid")
	public User operator;
	
	@Column(name="alias")
	public String alias;
	
	public Trip(User operator, String alias) {
		this.operator = operator;
		this.alias = alias;
	}
	
	public User getOperator() {
		return this.operator;
	}
	
	public String getAlias() {
		return this.alias;
	}	
	
	public static List<Trip> getTripsForUser(User operator) {
		return Trip.find("userid=?", operator.id).fetch();
	}
	
	public static List<Trip> getLatestTripsForUser(User operator, int count) {
		return Trip.find("operator=? order by id desc", operator).fetch(count);
	}
	
	public static class LatestTripForUser {
		public long tripId;
		public String tripAlias;
		public String vehicleAlias;
	}
	public static List<LatestTripForUser> getLatestTripsListForUser(User operator, int count) {
		String SQL="select tp.id, tp.alias, if(uv.alias is null, veh.vin, uv.alias) as vehiclealias from trip tp, vehicledataset vd, vehicle veh, uservehicle uv "+
			"where tp.userid=? "+
			"and vd.tripid = tp.id "+
			"and vd.vehicleid = veh.id "+
			"and uv.userid=tp.userid and uv.vehicleid=veh.id "+
			"group by tp.id "+
			"order by tp.id desc "+
			"limit ?";
		Query query = JPA.em().createNativeQuery(SQL);
		query.setParameter(1, operator.getId());
		query.setParameter(2, count);
		List<Object[]> queryResults = query.getResultList();
		List<LatestTripForUser> results = new ArrayList<LatestTripForUser>();
		for (Object[] row : queryResults) {
			LatestTripForUser trip = new LatestTripForUser();
			trip.tripId = Long.parseLong(row[0].toString());
			trip.tripAlias = row[1].toString();
			trip.vehicleAlias = row[2].toString();
			results.add(trip);
		}
		return results;
	}
	
	public static Trip getTripForUser(Long tripId, User operator) {
		Trip trip = findById(tripId);
		if (trip == null) {
			return null;
		}
		
		if (trip.getOperator().getEmail().equals(operator.getEmail())) {
			return trip;
		} else {
			return null;
		}
	}
	
	
	
	public static List<VehicleLocation> getCoordinatesForTrip(Long tripId) {
		Trip trip = Trip.findById(tripId);
		
		if (trip == null) {
			return new ArrayList<VehicleLocation>();
		}
				
		String hqlQuery = "select vl " +
							"from " + VehicleDataset.class.getName() + " vd JOIN vd.location vl " + 
							"where vd.trip=? " +
							"order by vd.timestamp asc";
		return VehicleLocation.find(hqlQuery, trip).fetch();
	}	
}
	