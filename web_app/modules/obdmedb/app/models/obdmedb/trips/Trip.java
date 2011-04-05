package models.obdmedb.trips;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import models.obdmedb.User;

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
}
