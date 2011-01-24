package edu.unl.csce.obdme.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import edu.unl.csce.obdme.data.interfaces.IExternalApp;
import edu.unl.csce.obdme.data.interfaces.IUserBase;
import edu.unl.csce.obdme.data.sql.DataAccessBase;
import edu.unl.csce.obdme.data.sql.SqlProcedureParameter;
import edu.unl.csce.obdme.data.sql.dbcp.IConnectionPool;

public class ObdMeData extends DataAccessBase implements IExternalApp, IUserBase {

	public ObdMeData(IConnectionPool cp) {
		super(cp);
	}

//	public ResultSet getExternalApp(String apiKey) {
//		String procedure = "Pr_GetExternalApp";
//		ArrayList<SqlProcedureParameter> params = new ArrayList<SqlProcedureParameter>();		
//		params.add(new SqlProcedureParameter(apiKey, Types.VARCHAR));		
//		return this.executeStoredProcedure(procedure, params);
//	}

	public void addApiUsage(String apiKey, String requestPath, String httpMethod, int responseCode, String ipAddress) {
		String procedure = "Pr_AddApiUsageLog";		
		ArrayList<SqlProcedureParameter> params = new ArrayList<SqlProcedureParameter>();		
		params.add(new SqlProcedureParameter(apiKey, Types.VARCHAR));
		params.add(new SqlProcedureParameter(requestPath, Types.VARCHAR));
		params.add(new SqlProcedureParameter(httpMethod, Types.VARCHAR));
		params.add(new SqlProcedureParameter(responseCode, Types.INTEGER));
		params.add(new SqlProcedureParameter(ipAddress, Types.VARCHAR));		
		this.executeNonQueryStoredProcedure(procedure, params);			
	}

	public boolean isValidApiKey(String apiKey) {
		String procedure = "Pr_GetIsValidApiKey";
		ArrayList<SqlProcedureParameter> params = new ArrayList<SqlProcedureParameter>();
		params.add(new SqlProcedureParameter(apiKey, Types.VARCHAR));
		Connection c = this.getConnectionPool().getConnection();
		ResultSet rs = null;
		try {
			rs = this.executeStoredProcedure(c, procedure, params);
			rs.next();
			return rs.getBoolean(1);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			this.closeResultSet(rs);
			this.getConnectionPool().freeConnection(c);
		}
	}

	@Override
	public boolean validateUserPassword(String email, String password) {
		String procedure = "Pr_ValidateUserPassword";
		ArrayList<SqlProcedureParameter> params = new ArrayList<SqlProcedureParameter>();
		params.add(new SqlProcedureParameter(email, Types.VARCHAR));
		params.add(new SqlProcedureParameter(password, Types.VARCHAR));
		Connection c = this.getConnectionPool().getConnection();
		ResultSet rs = null;
		try {
			rs = this.executeStoredProcedure(c, procedure, params);
			rs.next();
			return rs.getBoolean(1);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			this.closeResultSet(rs);
			this.getConnectionPool().freeConnection(c);
		}
	}

	@Override
	public boolean isRegisteredUser(String email) {
		String procedure = "Pr_GetIsEmailRegistered";
		ArrayList<SqlProcedureParameter> params = new ArrayList<SqlProcedureParameter>();
		params.add(new SqlProcedureParameter(email, Types.VARCHAR));
		Connection c = this.getConnectionPool().getConnection();
		ResultSet rs = null;
		try {
			rs = this.executeStoredProcedure(c, procedure, params);
			rs.next();
			return rs.getBoolean(1);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			this.closeResultSet(rs);
			this.getConnectionPool();
		}
	}

}
