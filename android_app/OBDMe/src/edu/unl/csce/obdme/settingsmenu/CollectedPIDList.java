package edu.unl.csce.obdme.settingsmenu;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.obd.configuration.OBDConfigurationManager;

/**
 * The Class CollectedPIDList.
 */
public class CollectedPIDList extends Activity {

	/** The elm framework. */
	private ELMFramework elmFramework;
	
	/** The root ui. */
	private LinearLayout rootUI;
	
	/** The supported pollable pid list. */
	private HashMap<String, List<String>> supportedPollablePIDList;
	
	/** The data list. */
	private ListView dataList;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Set the window title
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		//Get a reference to the ELMFramework singleton
		elmFramework = ((OBDMeApplication)getApplication()).getELMFramework();

		//Get the collected PID List
		this.supportedPollablePIDList = elmFramework.getObdFramework().getSupportedPollablePIDList();
		
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
		this.dataList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		//Build the list of displayed PIDs
		this.dataList.setAdapter(buildListAdapter());

		//Init the checkbox status based on the current configurations
		initCheckedStatus();

		//Add the list to the root linear layout
		rootLinearLayout.addView(dataList);

		//Return the root linear layout
		return rootLinearLayout;
	}

	/**
	 * Builds the list adapter.
	 *
	 * @return the array adapter
	 */
	private ArrayAdapter<String> buildListAdapter() {
		
		//Build the data adapter that will build the content displayed in the ListView
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.settings_data_list_item);

		//For all the collected PIDs
		for (String currentMode : this.supportedPollablePIDList.keySet()) {
			for ( String str : this.supportedPollablePIDList.get(currentMode)) {
				String currentPID = str;

				//Add the PID to the array adapter
				dataAdapter.add(elmFramework.getConfiguredPID(currentMode, currentPID).getPidName());
			}
		}

		//Return the array adapter
		return dataAdapter;

	}

	/**
	 * Inits the checked status.
	 */
	private void initCheckedStatus( ) {

		int index = 0;

		//For all the collected PIDs
		for (String currentMode : this.supportedPollablePIDList.keySet()) {
			for ( String str : this.supportedPollablePIDList.get(currentMode)) {
				String currentPID = str;

				//If the PID is displayed 
				if (elmFramework.getConfiguredPID(currentMode, currentPID).isCollected()) {
					
					//Set the PID checked in the list
					this.dataList.setItemChecked(index, true);
				}
				
				//Otherwise
				else {
					
					//Set the PID not checked in the list
					this.dataList.setItemChecked(index, false);
				}

				index += 1;
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	public void onDestroy(){
		super.onDestroy();
		
		//Get the Sparse boolean array that indicates the checked status of each item in the list
		SparseBooleanArray checked = this.dataList.getCheckedItemPositions();
		int index = 0;

		//For all the collected PIDs
		for (String currentMode : this.supportedPollablePIDList.keySet()) {
			for ( String str : this.supportedPollablePIDList.get(currentMode)) {
				String currentPID = str;

				//If the current PID is selected
				if (checked.get(index)) {
					
					//Set the PID displayed in the application
					elmFramework.getConfiguredPID(currentMode, currentPID).setCollected(true);
				}
				
				//Otherwise
				else {
					
					//Set the PID not displayed in the application
					elmFramework.getConfiguredPID(currentMode, currentPID).setCollected(false);
				}

				index += 1;
			}
		}

		OBDConfigurationManager.writeOBDConfiguration(getApplicationContext(),
				elmFramework.getObdFramework().getConfiguredProtocol());
		
	}

	/**
	 * On terminate.
	 */
	public void onTerminate() {

		//Finish this activity
		this.finish();

	}

}