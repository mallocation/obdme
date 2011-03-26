//package models.obdme;
//
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Table;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//
//import org.hibernate.annotations.AccessType;
//
//import models.obdme.Vehicles.UserVehicle;
//import models.obdme.Vehicles.Vehicle;
//import play.data.validation.Email;
//import play.data.validation.Required;
//import play.db.jpa.Model;
//
////@Entity
////@Table(name="userbase")
//public class User {//extends Model {
//	
////	/* Persisted Fields */	
////	@Email
////	@Required
////	@Column(name="email", unique=true)
////	public String email;
////	
////	@Required
////	@Column(name="passwordhash", nullable=false, length=64)
////	public String passwordhash;
////	
////	@Required
////	@Column(name="regdate")
////	@Temporal(TemporalType.TIMESTAMP)
////	public Date regdate;	
////	/* End Persisted Fields */
////	
////	/* Default Constructor */
////	public User(){}
////	
////	private User(String email, String passwordhash) {
////		this.email = email.toLowerCase();
////		this.passwordhash = passwordhash;
////		this.regdate = new Date();
////	}
////
////	/**
////	 * Creates a user in the UserBase table. 
////	 * @param email E-mail address of the user.
////	 * @param password Unhashed password of user.
////	 * @return User if the user was created successfully, or null otherwise.
////	 */
////	public static User createUser(String email, String passwordhash) {
////		email = email.trim();
////		User newUser = new User(email, passwordhash);
////		return newUser.validateAndSave() ? newUser : null;
////	}
////	
////	public static User findByUserId(long userid) {
////		return find("id", userid).first();
////	}
////	
////	/*
////	 * Find a user by e-mail address.
////	 * Returns null if a user is not found.
////	 * @param email E-mail address of the user.
////	 */
////	public static User findByEmail(String email) {
////		return find("email", email.toLowerCase()).first();
////	}
////	
////	/*
////	 * Try to login a user.  This method will check for 
////	 * e-mail and password in the database.
////	 * Returns null if the login is invalid.
////	 * @param email E-mail address of the user.
////	 * @param password Hashed password of the user (SHA).
////	 */
////	public static User validateUserLogin(String email, String pw) {
////		return find("email like ? and passwordhash like ?", email.toLowerCase(), pw).first();
////	}
////	
////	public UserVehicle addVehicleToUser(Vehicle v, String userVehicleAlias) {	
////		UserVehicle uv = new UserVehicle();
////		uv.setUser(this);
////		uv.setVehicle(v);
////		uv.setAlias(userVehicleAlias);
////		this.validateAndSave();
////		uv.validateAndSave();		
////		return uv;	
////	}
//}
