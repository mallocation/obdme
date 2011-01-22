package edu.unl.csce.obdme.data.sql.dbcp;

import java.sql.Connection;

public interface IConnectionPool {	
	
	/*
	 * Returns a database connection from the pool.
	 */
	Connection getConnection();
	
	/*
	 * Free up the resources for a connection.
	 */
	void freeConnection(Connection c);
}
