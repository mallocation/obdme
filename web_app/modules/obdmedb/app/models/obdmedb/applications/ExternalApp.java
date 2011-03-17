package models.obdmedb.applications;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import edu.unl.csce.obdme.encryption.EncryptionUtils;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="externalapp")
public class ExternalApp extends Model {
	
	@Column(name="name")
	public String name;
	
	@Column(name="apikey", unique=true)
	public String apikey;
	
	public static ExternalApp createNewExternalApplication(String applicationName) {
		ExternalApp newApp = new ExternalApp();
		newApp.name = applicationName;
		newApp.apikey = createApiKeyForApplication(applicationName);
		newApp.validateAndSave();
		return newApp;		
	}
	
	private static String createApiKeyForApplication(String applicationName) {
		String uniqueAppId = String.format("%s%s", applicationName, UUID.randomUUID().toString());
		return EncryptionUtils.encryptString(uniqueAppId, EncryptionUtils.SHA_256);		
	}
	
	public static ExternalApp findByApiKey(String apiKey) {
		return find("apikey", apiKey).first();
	}
	
	public static boolean hasAccessToObdme(String apiKey) {
		return findByApiKey(apiKey) != null;
	}
    
}
