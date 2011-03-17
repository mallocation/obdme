package models.obdmedb.vehicles;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.obdmedb.User;
import play.db.jpa.Model;

@Entity
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
}
