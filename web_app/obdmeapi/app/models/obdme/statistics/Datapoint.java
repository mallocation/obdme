package models.obdme.statistics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenericGenerator;

import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.Model;

//@Entity
//@Table(name="datapoint")
public class Datapoint extends GenericModel {
	    
//	/* Persisted Fields */
//	@Id
//	@GeneratedValue(generator="system-uuid")
//	@GenericGenerator(name="system-uuid",strategy="uuid")
//	@Column(name="id", nullable=false)
//	public String id;
//	
//	@Column(name="value", nullable=false)
//	public double value;
//	
//	@Column(name="obd_mode", nullable=true)
//	public String obdmode;
//	
//	@Column(name="obd_pid", nullable=true)
//	public String obdpid;
//	
//	/* End Persisted Fields */
//	
//	/* Persisted Relations */
//	
//	@ManyToOne
//	@JoinColumn(name="dataset_id")
//	VehicleDataset dataset;
//	
//	/* End Persisted Relations */
//	
//	public Datapoint(VehicleDataset dataset, String obdmode, String obdpid, double value) {
//		this.dataset = dataset;
//		this.value = value;
//		this.obdmode = obdmode;
//		this.obdpid = obdpid;
//	}
	
	
}
