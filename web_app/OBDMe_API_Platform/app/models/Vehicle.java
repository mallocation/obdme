package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="Vehicle")
public class Vehicle extends Model {
	
	@Required
	@Column(unique=true)
	public String VIN;
	
	public Vehicle(String VIN) {
		this.VIN = VIN;
		// TODO Auto-generated constructor stub
	}    
}
