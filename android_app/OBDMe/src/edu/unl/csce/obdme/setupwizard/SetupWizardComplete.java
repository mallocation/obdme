package edu.unl.csce.obdme.setupwizard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import edu.unl.csce.obdme.R;

/**
 * The Class SetupWizardComplete.
 */
public class SetupWizardComplete extends Activity {
	
	/** The Constant SETUP_COMPLETE_RESULT_OK. */
	public static final int SETUP_COMPLETE_RESULT_OK = 10;
	
	/** The prefs. */
	private SharedPreferences prefs;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizard_complete),
				"Starting the OBDMe Setup Wizard Complete Activity.");

		//Set the content view
		setContentView(R.layout.setupwizard_complete);
		
		prefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), 0);
        
        Button next = (Button) findViewById(R.id.setupwizard_complete_button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view) {
            	
            	SharedPreferences.Editor editor = prefs.edit();
				editor.putString(getResources().getString(R.string.prefs_firstrun), "done");
				editor.commit();
            	
            	//On the button press, backprop to the last activity
            	setResult(Activity.RESULT_OK);
        		finish();
            }

        });

	}

}
