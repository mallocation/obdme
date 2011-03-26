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
	
//	public User (String email, String clearTextPassword) {
//		this.email = email;
//		this.passwordhash = EncryptionUtils.encryptPassword(clearTextPassword);
//		this.regdate = new Date();
//	}
	
	public static User createUserFromClearTextCredentials(String email, String clearTextPassword) {
		String encryptedPassword = EncryptionUtils.encryptPassword(clearTextPassword);
		return createUserFromEncryptedCredentials(email, encryptedPassword);
	}
	
	public static User createUserFromEncryptedCredentials(String email, String encryptedPassword) {
		User user = new User();
		user.email = email;
		user.passwordhash = encryptedPassword;
		user.regdate = new Date();
		return user.validateAndSave() ? user : null;		
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
	
	public static boolean isValidCredentialsClearText(String email, String clearTextPassword) {
		return validateUserCredentialsClearText(email, clearTextPassword) != null;
	}
	
	public static boolean isValidCredentialsEncrypted(String email, String encryptedPassword) {
		return validateUserCredentialsEncrypted(email, encryptedPassword) != null;
	}
	
	public static User validateUserCredentialsClearText(String email, String clearTextPassword) {
		String encryptedPassword = EncryptionUtils.encryptPassword(clearTextPassword);
		return validateUserCredentialsEncrypted(email, encryptedPassword);
	}
	
	public static User validateUserCredentialsEncrypted(String email, String encryptedPassword) {
		return find("email like ? and passwordhash like ?", email, encryptedPassword).first();
	}
	
	public String getEmail() {
		return email.toLowerCase();
	}

	public void setEmail(String email) {
		this.email = email.toLowerCase().trim();
	}
}