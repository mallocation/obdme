package models.obdmedb.statistics;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import play.db.jpa.GenericModel;

@Entity
@Embeddable
@Table(name="vehicledatapoint")
public class VehicleDataPoint extends GenericModel {
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	public String id;
	
	@ManyToOne
	@JoinColumn(name="datasetid", referencedColumnName="id")
	public VehicleDataset dataset;
	
	@Column(name="mode")
	public String mode;
	
	@Column(name="pid")
	public String pid;
	
	@Column(name="value")
	public double value;
	
	public VehicleDataPoint(VehicleDataset dataset, String mode, String pid, double value) {
		this.dataset = dataset;
		this.mode = mode;
		this.pid = pid;
		this.value = value;
	}
	
}