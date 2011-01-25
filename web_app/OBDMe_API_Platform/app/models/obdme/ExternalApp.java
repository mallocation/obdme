package models.obdme;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="ExternalApp")
public class ExternalApp extends Model {
	
	@Required
	public String apikey;

	@Required
	public String name;
	
	public static boolean hasAccess(String apikey) {
		return findByApiKey(apikey) != null;
	}
	
	public static ExternalApp findByApiKey(String apikey) {
		return find("apikey", apikey).first();
	}
    
}
