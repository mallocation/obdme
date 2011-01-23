package edu.unl.csce.obdme.data.sql;

public class SqlProcedureParameter {
	
	private Object paramValue;
	private int sqlDataType;
	
	public SqlProcedureParameter(Object paramValue, int sqlDataType) {
		this.paramValue = paramValue;
		this.sqlDataType = sqlDataType;
	}
	
	public Object getParameterValue() {
		return this.paramValue;
	}
	
	public int getSqlDataType() {
		return this.sqlDataType;
	}

}
