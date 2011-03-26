package models.obdmedb.applications;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="apiusagelog")
public class ApiUsageLog extends Model {
	
	@ManyToOne(optional=false)
	@JoinColumn(name="externalappid", referencedColumnName="id")
	public ExternalApp externalApp;
	
	@Required
	@Column(name="requestpath")
	public String requestPath;
	
	@Required
	@Column(name="httpmethod")
	public String httpMethod;
	
	@Required
	@Column(name="responsestatus")
	public int responseStatus;
	
	@Required
	@Column(name="ipaddress")
	public String ipAddress;
	
	@Required
	@Column(name="time")
	public Date time;
	
	private ApiUsageLog(ExternalApp app, String requestPath, String httpMethod, int responseStatus, String ipAddress){
		this.externalApp = app;
		this.requestPath = requestPath;
		this.httpMethod = httpMethod;
		this.responseStatus = responseStatus;
		this.ipAddress = ipAddress;
		this.time = new Date();
	}
	
	public static boolean addApiUsageLog(String apiKey, String requestPath, String httpMethod, int responseStatus, String ipAddress) {
		ExternalApp app = ExternalApp.findByApiKey(apiKey);
		if (app == null) {
			return false;
		}
		ApiUsageLog log = new ApiUsageLog(app, requestPath, httpMethod, responseStatus, ipAddress);
		return log.validateAndSave();
	}
    
}