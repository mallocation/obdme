package models.obdme.Applications;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.DateTime;

import edu.unl.csce.obdme.encryption.EncryptionUtils;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="ExternalApp")
public class ExternalApp extends Model {
	
	@Required
	@Column(unique=true)
	public String apikey;

	@Required
	public String name;
	
	public ExternalApp(String name, String apikey) {
		this.name = name;
		this.apikey = apikey != null ? apikey : createApiKeyForApplication(name);
	}
	
	public static boolean hasAccess(String apikey) {
		return findByApiKey(apikey) != null;
	}
	
	public static ExternalApp findByApiKey(String apikey) {
		return find("apikey", apikey).first();
	}
	
	/*
	 * Creates an API key for an application.
	 * @param applicationName the name of the application.
	 */
	public static String createApiKeyForApplication(String applicationName) {
		String uniqueAppIdentifer = applicationName + UUID.randomUUID().toString();
		return EncryptionUtils.encryptString(uniqueAppIdentifer, EncryptionUtils.SHA_256);		
	}
    
}
