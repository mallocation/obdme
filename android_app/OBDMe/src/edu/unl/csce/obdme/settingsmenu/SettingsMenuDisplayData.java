package edu.unl.csce.obdme.settingsmenu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;

public class SettingsMenuDisplayData extends Activity {

	/** The title bar. */
	private TextView titleBar;

	/** The list list view */
	public ListView list;
	
	/** The array adapter */
	ArrayAdapter<String> adapter;
	
	/** The elm framework. */
	private ELMFramework elmFramework;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.settings_data_to_display);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		titleBar = (TextView) findViewById(R.id.title_left_text);
		titleBar.setText(R.string.display_data_title);
		
		elmFramework = ((OBDMeApplication)getApplication()).getELMFramework();
		
		ListView dataList = (ListView) findViewById(R.id.SettingsDataToDisplayListView);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.settings_data_list_item);
		HashMap<String, List<String>> pollablePIDList = elmFramework.getObdFramework().getPollablePIDList();
		
		for (String currentMode : pollablePIDList.keySet()) {
			for ( String str : pollablePIDList.get(currentMode)) {
			String currentPID = str;

			//Indicated if Enabled 
			elmFramework.getConfiguredPID(currentMode, currentPID).isEnabled();

			//Build the list to display using the pid name
			dataAdapter.add(elmFramework.getConfiguredPID(currentMode, currentPID).getPidName());
			}
		}
		dataList.setItemsCanFocus(false);
		dataList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		dataList.setAdapter(dataAdapter);
		
	}
}
