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
	
	protected ResultSet executeStoredProcedure(String procedureName, ArrayList<SqlProcedureParameter> parameters) {
		Connection c = this.connectionPool.getConnection();
		StringBuffer sb = new StringBuffer("call " + procedureName + "(");
		CallableStatement cs = null;
		ResultSet rs = null;		
		if (parameters != null) {
			for (int i=0;i<parameters.size();i++){sb.append("?,");}
			sb.deleteCharAt(sb.length() - 1);
		}		
		sb.append(")");
		System.out.println("Executing : " + sb.toString());
		try {
			cs = c.prepareCall(sb.toString());
			if (parameters != null) {
				int i=1;
				for (SqlProcedureParameter param : parameters) {
					cs.setObject(i, param.getParameterValue(), param.getSqlDataType());
					i++;
				}
			}
			rs = cs.executeQuery();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}		
		this.connectionPool.freeConnection(c);		
		if (cs != null) {
			try {cs.close();}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}		
		if (rs != null) {
			try {rs.close();}
			catch (SQLException sqle){
				sqle.printStackTrace();
			}
		}
		return rs;		
	}
	
	protected boolean executeStoredProcedureReturnBoolean(String procedureName, ArrayList<SqlProcedureParameter> parameters) throws SQLException {
		return this.executeStoredProcedure(procedureName, parameters).getBoolean(1);
	}
	
}
