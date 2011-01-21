package edu.unl.csce.obdme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OBDMeSettings extends Activity {
	
	/** The title bar. */
	private TextView titleBar;
	
	/** Toast */
	int toastDuration;
	Toast toast;
	CharSequence toastText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.settings);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		titleBar = (TextView) findViewById(R.id.title_left_text);
		titleBar.setText(R.string.settings_title);
		
		// Set up list view
		ListView menuList = (ListView) findViewById(R.id.SettingsListView);
		String[] items = { getResources().getString(R.string.settings_modeselect),
			getResources().getString(R.string.settings_accountinformation),
			getResources().getString(R.string.settings_vehicleinformation),
			getResources().getString(R.string.settings_bluetoothsettings),
			getResources().getString(R.string.settings_dataupload),
			getResources().getString(R.string.settings_datacollection),
			getResources().getString(R.string.settings_displaydata)};
		
		ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		
		menuList.setAdapter(adapt);
		
		menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Context context = getApplicationContext();
            	TextView textView = (TextView) itemClicked;
                String strText = textView.getText().toString();
                if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_modeselect))) {
                    // Launch the Game Activity
                    //startActivity(new Intent(QuizMenuActivity.this, QuizGameActivity.class));
                	toastText = "mode select";
                	toast = Toast.makeText(context, toastText, toastDuration);
                	toast.show();
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_accountinformation))) {
                    // Launch the Help Activity
                    //startActivity(new Intent(QuizMenuActivity.this, QuizHelpActivity.class));
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_vehicleinformation))) {
                    // Launch the Settings Activity
                    //startActivity(new Intent(QuizMenuActivity.this, QuizSettingsActivity.class));
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_bluetoothsettings))) {
                    // Launch the Scores Activity
                    //startActivity(new Intent(QuizMenuActivity.this, QuizScoresActivity.class));
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_dataupload))) {
                    // Launch the Scores Activity
                    //startActivity(new Intent(QuizMenuActivity.this, QuizScoresActivity.class));
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_datacollection))) {
                    // Launch the Scores Activity
                    //startActivity(new Intent(QuizMenuActivity.this, QuizScoresActivity.class));
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_displaydata))) {
                    // Launch the Scores Activity
                    //startActivity(new Intent(QuizMenuActivity.this, QuizScoresActivity.class));
                }
            }
        });
	}

}