package models.obdme.statistics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="datapoint")
public class Datapoint extends Model {
    
	/* Persisted Fields */
	
	@Column(name="value", nullable=false)
	public double value;
	
	/* End Persisted Fields */
	
	/* Persisted Relations */
	
	/* End Persisted Relations */
}
