package edu.unl.csce.obdme.settingsmenu;

import edu.unl.csce.obdme.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class SettingsMenuVehicleInfo extends Activity {
	
	/** The title bar. */
	private TextView titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.vehicle_info);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		titleBar = (TextView) findViewById(R.id.title_left_text);
		titleBar.setText(R.string.vehicle_info_title);
	}

}
