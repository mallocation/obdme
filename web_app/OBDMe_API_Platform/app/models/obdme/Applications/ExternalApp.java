package models.obdme.Applications;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.jdt.core.dom.ThisExpression;

import play.data.validation.Required;
import play.db.jpa.Model;
import edu.unl.csce.obdme.encryption.EncryptionUtils;

@Entity
@Table(name="externalapp")
public class ExternalApp extends Model {
	
	/* Persisted Fields */
	
	@Required
	@Column(name="apikey", unique=true)
	public String apikey;

	@Required
	@Column(name="name")
	public String name;
	
	/* End Persisted Fields */
	
	/* Default Constructor */
	public ExternalApp(){}
	
	private ExternalApp(String name, String apikey) {
		this.name = name;
		this.apikey = apikey == null ? createApiKeyForApplication(name) : apikey;
	}
	
	public static ExternalApp createExternalAppForApplication(String applicationName) {
		ExternalApp newApp = new ExternalApp(applicationName, null);
		newApp.validateAndSave();
		return newApp;
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
