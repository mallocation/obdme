package factory;

import edu.unl.csce.obdme.applications.ExternalApp;
import edu.unl.csce.obdme.data.ObdMeData;

public class AppFactory {
	
	private ObdMeData _obdmeDataInstance = new ObdMeData(ObdMeConnectionPool.getInstance());
	private ObdMeData getObdMeDataClass() {
		return _obdmeDataInstance;
	}
	
	private ExternalApp externalApp = new ExternalApp(getObdMeDataClass());
	public ExternalApp ExternalApp() {		
		return externalApp;
	}
}
