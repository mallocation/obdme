package models.obdmedb.vehicles;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.obdmedb.User;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
@Table(name="uservehicle")
public class UserVehicle extends Model {
	
	@ManyToOne(optional=false)
	@JoinColumn(name="userid", referencedColumnName="id")
	public User user;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="vehicleid", referencedColumnName="id")
	public Vehicle vehicle;
	
	@Column(name="alias")
	public String alias;

	public static List<UserVehicle> getVehiclesForUser(User user) {
		return UserVehicle.find("userid=?", user.getId()).fetch();
	}
	
	public static UserVehicle getVehicleForUser(User user, Vehicle vehicle) {
		return UserVehicle.find("userid=? and vehicleid=?", user.getId(), vehicle.getId()).first();
	}
	
	public static UserVehicle getVehicleForUser(User user, Long vehicleId) {
		return UserVehicle.find("userid=? and vehicleid=?", user.getId(), vehicleId).first();
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias.trim();
	}
}
