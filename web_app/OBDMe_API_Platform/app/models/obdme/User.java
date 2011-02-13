package models.obdme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import models.obdme.Vehicles.Vehicle;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.eclipse.jdt.core.dom.ThisExpression;

import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="userbase")
public class User extends Model {
	
	/* Persisted Fields */
	
	@Email
	@Required
	@Column(name="email", unique=true, nullable=false)
	public String email;
	
	@Required
	@Column(name="passwordhash", nullable=false, length=64)
	public String passwordhash;
	
	@Required
	@Column(name="regdate", nullable=false)
	public Date regdate;
	
	/* End Persisted Fields */
	
	/* Persisted Relations */
	
	@ManyToMany
	@JoinTable(name="uservehicle")
	public Set<Vehicle> vehicles;
	
	/* End Persisted Relations */
	
	
	/* Default Constructor */
	public User(){}
	
	private User(String email, String passwordhash) {
		this.email = email;
		this.passwordhash = passwordhash;
		this.regdate = new Date();
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
