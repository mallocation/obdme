package edu.unl.csce.obdme.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import edu.unl.csce.obdme.data.interfaces.IExternalApp;
import edu.unl.csce.obdme.data.sql.DataAccessBase;
import edu.unl.csce.obdme.data.sql.SqlProcedureParameter;
import edu.unl.csce.obdme.data.sql.dbcp.IConnectionPool;

public class ObdMeData extends DataAccessBase implements IExternalApp {

	public ObdMeData(IConnectionPool cp) {
		super(cp);
	}

	public ResultSet getExternalApp(String apiKey) {
		String procedure = "Pr_GetExternalApp";
		ArrayList<SqlProcedureParameter> params = new ArrayList<SqlProcedureParameter>();		
		params.add(new SqlProcedureParameter(apiKey, Types.VARCHAR));		
		return this.executeStoredProcedure(procedure, params);
	}

	public void addApiUsage(String apiKey, String requestPath, String httpMethod, int responseCode, String ipAddress) {
		String procedure = "Pr_AddApiUsageLog";		
		ArrayList<SqlProcedureParameter> params = new ArrayList<SqlProcedureParameter>();
		params.add(new SqlProcedureParameter(apiKey, Types.VARCHAR));
		params.add(new SqlProcedureParameter(requestPath, Types.VARCHAR));
		params.add(new SqlProcedureParameter(httpMethod, Types.VARCHAR));
		params.add(new SqlProcedureParameter(responseCode, Types.INTEGER));
		params.add(new SqlProcedureParameter(ipAddress, Types.VARCHAR));		
		this.executeStoredProcedure(procedure, params);		
	}

	public boolean isValidApiKey(String apiKey) {
		String procedure = "Pr_GetIsValidApiKey";
		ArrayList<SqlProcedureParameter> params = new ArrayList<SqlProcedureParameter>();
		params.add(new SqlProcedureParameter(apiKey, Types.VARCHAR));
		try {
			return this.executeStoredProcedureReturnBoolean(procedure, params);
		} catch (SQLException sqle) {
			return false;
		}
	}

}
