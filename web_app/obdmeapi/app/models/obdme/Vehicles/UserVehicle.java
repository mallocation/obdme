//package models.obdme.Vehicles;
//
//import java.util.List;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import models.obdme.User;
//import play.db.jpa.Model;
//
//@Entity
//@Table(name="user_vehicle")
//public class UserVehicle extends Model {
//	
//	@ManyToOne
//	@JoinColumn(name="user_id")
//	public User user;
//	
//	@ManyToOne
//	@JoinColumn(name="vehicle_id")
//	public Vehicle vehicle;
//	
//	@Column(name="alias")
//	public String alias;	
//
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//	public Vehicle getVehicle() {
//		return vehicle;
//	}
//
//	public void setVehicle(Vehicle vehicle) {
//		this.vehicle = vehicle;
//	}
//
//	public String getAlias() {
//		return alias;
//	}
//
//	public void setAlias(String alias) {
//		this.alias = alias;
//	}
//	
//	public static List<UserVehicle> getVehiclesForUser(User user) {
//		return UserVehicle.find("user_id=?", user.getId()).fetch();
//	}
//	
//	public static UserVehicle getVehicleForUser(User user, Vehicle vehicle) {
//		return UserVehicle.find("user_id=? and vehicle_id=?", user.getId(), vehicle.getId()).first();
//	}
//	
// 
//}
