package models.obdme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import models.obdme.Vehicles.Vehicle;

import org.eclipse.jdt.core.dom.ThisExpression;

import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="UserBase")
public class User extends Model {
	
	@Email
	@Required
	@Column(unique=true)
	public String email;
	
	@Password
	@Required
	@Column(length=64)
	public String passwordhash;
	
	public String firstname;
	
	public String lastname;
	
	@Required
	public Date regdate;
	
	@ManyToMany(fetch=FetchType.LAZY)
	public List<Vehicle> vehicles;
	
	public User(String email, String passwordhash) {
		this.email = email;
		this.passwordhash = passwordhash;
		this.regdate = new Date();
		this.vehicles = new ArrayList<Vehicle>();
	}
	

	/**
	 * Attach a vehicle to a user.
	 * This will ensure that a specific range of statistics
	 * are tied to a user.
	 *
	 * @param vehicle the vehicle to attach
	 */
	public void attachVehicle(Vehicle vehicle) {
		this.vehicles.add(vehicle);
		this.save();
	}
	
	
	/**
	 * Creates a user in the UserBase table. 
	 * @param email E-mail address of the user.
	 * @param password Unhashed password of user.
	 * @return User if the user was created successfully, or null otherwise.
	 */
	public static User createUser(String email, String passwordhash) {
		User newUser = new User(email, passwordhash);
		return newUser.validateAndSave() ? newUser : null;
	}
	
	public static User findByUserId(long userid) {
		return find("id", userid).first();
	}
	
	/*
	 * Find a user by e-mail address.
	 * Returns null if a user is not found.
	 * @param email E-mail address of the user.
	 */
	public static User findByEmail(String email) {
		return find("email", email).first();
	}
	
	/*
	 * Try to login a user.  This method will check for 
	 * e-mail and password in the database.
	 * Returns null if the login is invalid.
	 * @param email E-mail address of the user.
	 * @param password Hashed password of the user (SHA).
	 */
	public static User validateUserLogin(String email, String pw) {
		return find("email like ? and passwordhash like ?", email, pw).first();
	}
	
}
