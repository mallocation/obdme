package edu.unl.csce.obdme.data.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import edu.unl.csce.obdme.data.sql.dbcp.IConnectionPool;

public class DataAccessBase {
	
	private IConnectionPool connectionPool;
	
	protected DataAccessBase(IConnectionPool cp) {
		this.connectionPool = cp;
	}
	
	/*
	 * Retrieves the connection pool object for this
	 * data access class.
	 */
	protected IConnectionPool getConnectionPool() {
		return this.connectionPool;
	}
	
	/*
	 * Call this method if you wish to execute a stored procedure and disregard the results.
	 * JDBC resources will be freed automatically.
	 */
	protected void executeNonQueryStoredProcedure(String procedureName, ArrayList<SqlProcedureParameter> parameters) {
		Connection c = this.getConnectionPool().getConnection();
		try {
			this.executeStoredProcedure(c, procedureName, parameters);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			this.getConnectionPool().freeConnection(c);
		}
	}
	
	/* 
	 * Call this method if you wish to call a stored procedure and will use the results.
	 * JDBC resources will NOT be freed in this method.  Handle at your own convenience.
	 */
	protected ResultSet executeStoredProcedure(Connection c, String procedureName, ArrayList<SqlProcedureParameter> parameters) throws SQLException {
		StringBuffer sb = new StringBuffer("call " + procedureName + "(");
		CallableStatement cs = null;	
		if (parameters != null) {
			for (int i=0;i<parameters.size();i++){sb.append("?,");}
			sb.deleteCharAt(sb.length() - 1);
		}		
		sb.append(")");
		cs = c.prepareCall(sb.toString());
		if (parameters != null) {
			int i=1;
			for (SqlProcedureParameter param : parameters) {
				cs.setObject(i, param.getParameterValue(), param.getSqlDataType());
				i++;
			}
		}
		return cs.executeQuery();		 	
	}	
	
	protected void closeCallableStatement(CallableStatement cs) {
		try {
			cs.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	protected void closeResultSet(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}