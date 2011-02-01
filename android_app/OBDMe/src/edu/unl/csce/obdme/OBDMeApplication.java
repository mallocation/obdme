package edu.unl.csce.obdme;

import android.app.Application;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.bluetooth.BluetoothService;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;

/**
 * The Class OBDMeApplication.
 */
public class OBDMeApplication extends Application {
	
	/** The instance. */
	private static OBDMeApplication instance = null;
	
	/** The bluetooth service. */
	private BluetoothService bluetoothService = null;
	
	/** The elm framework. */
	private ELMFramework elmFramework = null;
	
	/** The web framework. */
	private ObdMeService webFramework = null;
	
	/**
	 * Gets the single instance of OBDMeApplication.
	 *
	 * @return single instance of OBDMeApplication
	 */
	public static OBDMeApplication getInstance() {
        checkInstance();
        return instance;
    }
	
	/**
	 * Gets the bluetooth service.
	 *
	 * @return the bluetooth service
	 */
	public BluetoothService getBluetoothService() {
        if (bluetoothService == null) {
            checkInstance();
            bluetoothService = new BluetoothService(getApplicationContext());
        }
        return bluetoothService;
    }
	
	/**
	 * Gets the eLM framework.
	 *
	 * @return the eLM framework
	 */
	public ELMFramework getELMFramework() {
        if (elmFramework == null) {
            checkInstance();
            elmFramework = new ELMFramework(getApplicationContext(), getBluetoothService());
        }
        return elmFramework;
    }
	
	/**
	 * Gets the framework.
	 *
	 * @return the framework
	 */
	public ObdMeService getWebFramework() {
        if (webFramework == null) {
            checkInstance();
            webFramework = new ObdMeService(getString(R.string.webservice_apikey));
        }
        return webFramework;
    }
	
	/**
	 * Check instance.
	 */
	private static void checkInstance() {
        if (instance == null)
            throw new IllegalStateException("Application not created yet!");
    }
	
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	public void onCreate() {
        super.onCreate();
        //provide an instance for our static accessors
        instance = this;
        getELMFramework();
    }

}
