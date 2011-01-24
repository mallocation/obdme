package edu.unl.csce.obdme.factory;

import edu.unl.csce.obdme.applications.ExternalApp;
import edu.unl.csce.obdme.data.ObdMeData;
import edu.unl.csce.obdme.data.sql.dbcp.IConnectionPool;
import edu.unl.csce.obdme.users.User;

public class DAOFactory {
	
	private IConnectionPool dbConnectionPool;
	
	public DAOFactory(IConnectionPool dbConnectionPool) {
		this.dbConnectionPool = dbConnectionPool;
	}
	
	private ObdMeData _dbObdmeData = null;
	private ObdMeData getOBDMeDataClass() {
		if (_dbObdmeData == null) {
			_dbObdmeData = new ObdMeData(dbConnectionPool);
		}
		return _dbObdmeData;
	}
	
	private ExternalApp _externalApp;
	public ExternalApp ExternalAppDAO() {
		if (_externalApp == null) {
			_externalApp = new ExternalApp(getOBDMeDataClass());
		}
		return _externalApp;
	}
	
	private User _user;
	public User UsersDAO() {
		if (_user == null) {
			_user = new User(getOBDMeDataClass());
		}
		return _user;
	}
}