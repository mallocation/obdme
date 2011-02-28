package edu.unl.csce.obdme.settingsmenu;

import edu.unl.csce.obdme.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class VehicleInformation extends Activity {
	/** The vehicles list view. */
    private ListView vehiclesListView;
    
    /** The shared prefs. */
	SharedPreferences sharedPrefs;
	
	/** The title bar. */
	private TextView titleBar;
	
	/** The root ui. */
	private LinearLayout rootUI;
	
	/** The data list. */
	private ListView dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		this.rootUI = buildRootDataListView();
		setContentView(this.rootUI);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		titleBar = (TextView) findViewById(R.id.title_left_text);
		titleBar.setText(R.string.vehicle_info_title);
		titleBar = (TextView) findViewById(R.id.title_right_text);
		
		sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), MODE_PRIVATE);
		
		//Build the list view and set the content view for this activity
		this.rootUI = buildRootDataListView();
		setContentView(this.rootUI);
	}
	
	/**
	 * Builds the root data list view.
	 *
	 * @return the linear layout
	 */
	private LinearLayout buildRootDataListView() {

		//Construct the root linear layout
		LinearLayout rootLinearLayout = new LinearLayout(this);
		rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams rootParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rootLinearLayout.setLayoutParams(rootParams);

		//Build the data list object
		this.dataList = new ListView(this);
		this.dataList.setItemsCanFocus(false);
		
		//TODO: build list based on users vehicles

		//Add the list to the root linear layout
		rootLinearLayout.addView(dataList);

		//Return the root linear layout
		return rootLinearLayout;
	}

	
	
}
