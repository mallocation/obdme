package models.obdme.Vehicles;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="user_vehicle")
public class UserVehicle extends GenericModel {
	
	@Id
	@Column(name="userid")
	public long userId;
	
	@Id
	@Column(name="vehicleid")
	public long vehicleId;
	   
}
