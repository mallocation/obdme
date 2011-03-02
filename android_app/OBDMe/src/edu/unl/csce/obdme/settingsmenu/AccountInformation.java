package edu.unl.csce.obdme.settingsmenu;

import edu.unl.csce.obdme.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AccountInformation extends Activity {
	
	/** The title bar. */
	private TextView titleBar;
	
	/** The shared prefs. */
	SharedPreferences sharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		setContentView(R.layout.accountinformation);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		titleBar = (TextView) findViewById(R.id.title_left_text);
		titleBar.setText(R.string.account_info_title);
		
		sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), MODE_PRIVATE);
		
		setUIValues();
		
	}
	
	/**
	 * Function setUIValues to setup the UI
	 */
	private void setUIValues() {
		
		// Email title textview
		TextView emailTitle = (TextView) findViewById(R.id.useremail_titletext);
		emailTitle.setText(R.string.useremail_titletext);
		
		// Email value textview
		TextView emailValue = (TextView) findViewById(R.id.useremail_value);
		emailValue.setText(sharedPrefs.getString(getResources().getString(R.string.prefs_account_username), "---"));
		
	}

}
