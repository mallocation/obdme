package edu.unl.csce.obdme.settingsmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.bluetooth.BluetoothDiscovery;

public class SettingsMenu extends Activity {
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
                	selectMode();
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_accountinformation))) {
                    // Launch the Account Info Activity
                    startActivity(new Intent(SettingsMenu.this, SettingsMenuAccountInfo.class));
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_vehicleinformation))) {
                    // Launch the Vehicle Info Activity
                    startActivity(new Intent(SettingsMenu.this, SettingsMenuVehicleInfo.class));
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_bluetoothsettings))) {
                    // Launch the Bluetooth Settings Activity
                    startActivity(new Intent(SettingsMenu.this, BluetoothDiscovery.class));
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_dataupload))) {
                    // Launch the Data Upload Activity
                    
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_datacollection))) {
                    // Launch the Data Collection Activity
                    
                } else if (strText.equalsIgnoreCase(getResources().getString(R.string.settings_displaydata))) {
                    // Launch the Display Data Activity
                    startActivity(new Intent(SettingsMenu.this, SettingsMenuDisplayData.class));
                }
            }
        });
	}
	
	private void selectMode() {
		final CharSequence[] items = {"User Mode", "Mechanic Mode"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Select a Mode");
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
    	    }
    	});
    	AlertDialog alert = builder.create();
    	
    	alert.show();
	}
}
