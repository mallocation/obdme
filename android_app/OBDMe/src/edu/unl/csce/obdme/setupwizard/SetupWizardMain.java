package edu.unl.csce.obdme.setupwizard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import edu.unl.csce.obdme.R;

/**
 * The Class SetupWizardMain.
 */
public class SetupWizardMain extends Activity {
	
	/** The Constant SETUP_MAIN_RESULT_OK. */
	public static final int SETUP_MAIN_RESULT_OK = 10;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_launcher),
				"Starting the OBDMe Setup Wizard Activity.");

		setContentView(R.layout.setupwizard_main);

        
        Button next = (Button) findViewById(R.id.setupwizard_main_button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view) {
            	Intent intent = new Intent(view.getContext(), SetupWizardAccount.class);
				startActivityForResult(intent, SetupWizardAccount.SETUP_ACCOUNT_RESULT_OK);
            }

        });

	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SetupWizardAccount.SETUP_ACCOUNT_RESULT_OK:
			if (resultCode == Activity.RESULT_OK) {
				
				//Backprop
				setResult(Activity.RESULT_OK);
				
				//Finish the activity
				finish();
			}
			break;
		}
    }

}
