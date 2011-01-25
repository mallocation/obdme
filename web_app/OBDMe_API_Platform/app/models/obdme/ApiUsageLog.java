package models.obdme;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="ApiUsageLog")
public class ApiUsageLog extends Model {
	
	@Required
	@ManyToOne
	public ExternalApp externalapp;
	
	@Required
	public String requestpath;
	
	@Required
	public String httpmethod;
	
	@Required
	public int responsestatus;
	
	@Required
	public String ipaddress;
	
	@Required
	public Date time;
	
	public ApiUsageLog(ExternalApp externalApp, String requestPath, String httpMethod, int responseStatus, String ipAddress) {
		this.externalapp = externalApp;
		this.requestpath = requestPath;
		this.httpmethod = httpMethod;
		this.responsestatus = responseStatus;
		this.ipaddress = ipAddress;
		this.time = new Date();
	}
	
	public ApiUsageLog(String apiKey, String requestPath, String httpMethod, int responseStatus, String ipAddress) {
		this.externalapp = ExternalApp.findByApiKey(apiKey);
		this.requestpath = requestPath;
		this.httpmethod = httpMethod;
		this.responsestatus = responseStatus;
		this.ipaddress = ipAddress;
		this.time = new Date();
	}
}