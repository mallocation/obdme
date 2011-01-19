package edu.unl.csce.obdme.setupwizard;

import edu.unl.csce.obdme.OBDMe;
import edu.unl.csce.obdme.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * The Class OBDMeSetupWizardMain.
 */
public class SetupWizardComplete extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizard_complete),
				"Starting the OBDMe Setup Wizard Complete Activity.");

		setContentView(R.layout.setupwizard_complete);

        
        Button next = (Button) findViewById(R.id.setupwizard_complete_button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view) {
            	Intent intent = new Intent(view.getContext(), OBDMe.class);
            	
            	
        		finish();
        		startActivity(intent);
            }

        });

	}

}
