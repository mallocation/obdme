package models.obdme.Applications;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

//@Entity
//@Table(name="apiusagelog")
public class ApiUsageLog {// extends Model {
	
//	/* Persisted Fields */
//	
//	@Required
//	@Column(name="requestpath")
//	public String requestpath;
//	
//	@Required
//	@Column(name="httpmethod")
//	public String httpmethod;
//	
//	@Required
//	@Column(name="responsestatus")
//	public int responsestatus;
//	
//	@Required
//	@Column(name="ipaddress")
//	public String ipaddress;
//	
//	@Required
//	@Column(name="time")
//	public Date time;
//	
//	/* End Persisted Fields */
//	
//	/* Persisted Relations */
//	
//	@ManyToOne(optional=false)
//	@JoinColumn(name="externalapp_id", nullable=false, updatable=false)
//	public ExternalApp externalApp;	
//	
//	/* End Persisted Relations */
//	
//	public ApiUsageLog(ExternalApp externalApp, String requestPath, String httpMethod, int responseStatus, String ipAddress) {
//		this.externalApp = externalApp;
//		this.requestpath = requestPath;
//		this.httpmethod = httpMethod;
//		this.responsestatus = responseStatus;
//		this.ipaddress = ipAddress;
//		this.time = new Date();
//	}
	
}