package edu.unl.csce.obdme.data.sql;

import edu.unl.csce.obdme.data.sql.dbcp.IConnectionPool;

public class DataAccessBase {
	
	private IConnectionPool connectionPool;
	
	public DataAccessBase(IConnectionPool cp) {
		this.connectionPool = cp;
	}
	
	
	

}
