package models.obdmedb;

import play.*;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import edu.unl.csce.obdme.encryption.EncryptionUtils;

import java.util.*;

@Entity
@Table(name="userbase")
public class User extends Model {
	
	@Email
	@Required
	@Column(name="email", unique=true)
	public String email;
	
	@Column(name="passwordhash", length=64)
	@Required
	public String passwordhash;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="regdate")
	public Date regdate;
	
	public User (String email, String clearTextPassword) {
		this.email = email;
		this.passwordhash = EncryptionUtils.encryptPassword(clearTextPassword);
		this.regdate = new Date();
	}	
	
	public static User findByEmail(String email) {
		return find("email", email).first();
	}
	
	public static User findUserById(Long id) {
		return findById(id);
	}
	
	public static boolean isUserRegistered(String email) {
		return findByEmail(email) != null;
	}
	
	public static boolean isValidCredentials(String email, String password) {
		return validateUserCredentials(email, password) != null;
	}
	
	public static User validateUserCredentials(String email, String password) {
		String encryptedPassword = EncryptionUtils.encryptPassword(password);
		return find("email like ? and passwordhash like ?", email, encryptedPassword).first();
	}

	public String getEmail() {
		return email.toLowerCase();
	}

	public void setEmail(String email) {
		this.email = email.toLowerCase().trim();
	}
}