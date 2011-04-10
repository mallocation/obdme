package models.obdmedb.trips;

import play.*;
import play.db.DB;
import play.db.jpa.*;

import javax.persistence.*;

import models.obdmedb.User;
import models.obdmedb.spatial.VehicleLocation;
import models.obdmedb.statistics.VehicleDataset;

import java.util.*;

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
