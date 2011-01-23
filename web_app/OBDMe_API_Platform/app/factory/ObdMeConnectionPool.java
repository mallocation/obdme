package factory;

import java.sql.Connection;
import java.sql.SQLException;

import play.db.DB;

import edu.unl.csce.obdme.data.sql.dbcp.IConnectionPool;

public class ObdMeConnectionPool implements IConnectionPool {
	
	private ObdMeConnectionPool(){}
	
	private static ObdMeConnectionPool pool = null;
	public static ObdMeConnectionPool getInstance() {
		if (pool == null) {
			pool = new ObdMeConnectionPool();
		}
		return pool;
	}

	@Override
	public void freeConnection(Connection c) {		
		//DB.close();		
	}

	@Override
	public Connection getConnection() {
		return DB.getConnection();
	}
}
