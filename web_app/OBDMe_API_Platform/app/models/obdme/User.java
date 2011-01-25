package models.obdme;

import play.*;
import play.data.binding.As;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.*;
import play.libs.Codec;

import javax.persistence.*;

import net.sf.oval.constraint.Email;

import java.util.*;

@Entity
@Table(name="UserBase")
public class User extends Model {
	
	@Email
	@Required	
	public String email;
	
	@Password
	@Required
	public String passwordhash;
	
	public User(String email, String passwordhash) {
		this.email = email;
		this.passwordhash = passwordhash;		
	}
	
	public static User findByEmail(String email) {
		return find("email", email).first();
	}
	
}
