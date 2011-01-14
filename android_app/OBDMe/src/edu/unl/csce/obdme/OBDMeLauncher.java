package edu.unl.csce.obdme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;


public class OBDMeLauncher extends Activity {

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
					
					Class intentTarget = null;
					
					if (sharedPrefs.contains(getResources().getString(R.string.prefs_firstrun))) {						
						//The first run preference exists.  First time setup already completed.
						intentTarget = edu.unl.csce.obdme.OBDMe.class;
					} else {
						//This is the first time the user is running the app, start the setup process.
						intentTarget = edu.unl.csce.obdme.setupwizard.OBDMeSetupWizardMain.class;
					}
					
					//Start the desired activity
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), intentTarget);
										
					//Finish the splash screen
					finish();
					
					//Start the intended activity
					startActivity(intent);
				}
			}
		};
		
		splashThread.start();

	}
}
