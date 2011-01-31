package edu.unl.csce.obdme;

import edu.unl.csce.obdme.setupwizard.SetupWizardAccount;
import edu.unl.csce.obdme.setupwizard.SetupWizardMain;
import edu.unl.csce.obdme.setupwizard.SetupWizardVehicle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


/**
 * The Class Splash.
 */
@SuppressWarnings("unused")
public class Splash extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_launcher),
				"Starting the OBDMe Launcher Activity.");

		setContentView(R.layout.splash);

		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (waited < getResources().getInteger(R.integer.splash_wait_milliseconds)) {
						sleep(500);
						waited += 500;
					}
				} catch (InterruptedException e) {
					//Do nothing here
				} finally {
					//Check which action we need to perform.
					//Is this a first start?  If so, run setup.
					
					SharedPreferences sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), 0);
					
					if (sharedPrefs.contains(getResources().getString(R.string.prefs_firstrun)) && 
							!getResources().getBoolean(R.bool.debug_setupwizard)) {						
						//The first run preference exists.  First time setup already completed.
						Intent intent = new Intent();
						intent.setClass(getBaseContext(), edu.unl.csce.obdme.OBDMe.class);
						startActivity(intent);
						finish();
					} else {
						//This is the first time the user is running the app, start the setup process.
						Intent intent = new Intent(getBaseContext(), edu.unl.csce.obdme.setupwizard.SetupWizardMain.class);
						startActivityForResult(intent, SetupWizardMain.SETUP_MAIN_RESULT_OK);
					}

					
				}
			}
		};
		
		splashThread.start();

	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SetupWizardMain.SETUP_MAIN_RESULT_OK:
			if (resultCode == Activity.RESULT_OK) {
				Thread splashThread = new Thread() {
					@Override
					public void run() {
						try {
							int waited = 0;
							while (waited < getResources().getInteger(R.integer.splash_wait_milliseconds)) {
								//((OBDMeApplication)getApplication()).getBluetoothService();
								sleep(500);
								//((OBDMeApplication)getApplication()).getELMFramework();
								waited += 500;
							}
						} catch (InterruptedException e) {
							//Do nothing here
						} finally {
							//Check which action we need to perform.
							//Is this a first start?  If so, run setup.
							
							SharedPreferences sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), 0);
							
							if (sharedPrefs.contains(getResources().getString(R.string.prefs_firstrun))) {						
								//The first run preference exists.  First time setup already completed.
								Intent intent = new Intent();
								intent.setClass(getBaseContext(), edu.unl.csce.obdme.OBDMe.class);
								startActivity(intent);
								finish();
							}

							
						}
					}
				};
				
				splashThread.start();
				
			}
			break;
		}
    }
}
