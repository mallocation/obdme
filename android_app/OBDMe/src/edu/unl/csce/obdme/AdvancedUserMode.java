package edu.unl.csce.obdme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import edu.unl.csce.obdme.collector.DataCollector;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.obd.OBDPID;
import edu.unl.csce.obdme.utils.UnitConversion;

public class AdvancedUserMode extends ListActivity{
	
	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The values maps. */
	private HashMap<Integer, HashMap<String,TextView>> valuesMaps;

	/** The data update thread. */
	private AdvancedUIDataUpdater dataUpdateThread;

	/** The shared prefs. */
	private SharedPreferences sharedPrefs;

	/** The context. */
	private Context context;

	/** The data collector thread. */
	private DataCollector dataCollectorThread;
	
	/** The listview */
	private ListView dataListView;
	
	private OBDPID listPID;
	private Integer currentView;
	
	
	/**
	 * Instantiates a new basic user mode portrait.
	 *
	 * @param context the context
	 */
	public AdvancedUserMode(Context context) {

		this.context = context;

		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());

		dataListView = buildListView(context);
		
	}

	/**
	 * Instantiates a new basic user mode portrait.
	 *
	 * @param context the context
	 * @param dataCollector the data collector
	 */
	public AdvancedUserMode(Context context, DataCollector dataCollector) {

		this.context = context;

		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());

		this.dataCollectorThread = dataCollector;
		
		dataListView = buildListView(context);

	}
	
	
	private ListView buildListView(Context context) {

		//Get the enabled pollable PIDS
		elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		HashMap<String, List<String>> pollablePIDList = elmFramework.getObdFramework().getDisplayedPIDList();

		//Start a new List View object
		ListView rootListView = new ListView(context);
		rootListView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_portrait));

		//Initialize the values map (this contains references to ALL the stats displayed in the list view
		this.valuesMaps = new HashMap<Integer, HashMap<String, TextView>>();

		currentView = new Integer(0);

		//For all the pollable and enabled PIDS,
		for (String currentMode : pollablePIDList.keySet()) {
			for (Iterator<String> pidIrt = pollablePIDList.get(currentMode).iterator(); pidIrt.hasNext(); ) {

				this.valuesMaps.put(currentView, new HashMap<String, TextView>());

				listPID = null;

				//If there is a PID left in the Iterator
				if (pidIrt.hasNext()) {

					//Save the reference
					listPID = elmFramework.getConfiguredPID(currentMode, pidIrt.next());
										
					rootListView.addView(buildEnabledView(context, listPID, currentView));
				}
				currentView = new Integer(currentView+1);
			}
		}		

		//Return the list view
		return rootListView;
	}
	
	
	/**
	 * Builds the enabled view.
	 *
	 * @param context the context
	 * @param listPID the list pid
	 * @param currentView the current view
	 * @return the view
	 */
	private View buildEnabledView(Context context, OBDPID listPID, Integer currentView) {

		//Initialize the Linear layout for this view and set the parameters
		LinearLayout dataLinearLayout = new LinearLayout(context);
		dataLinearLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams rootParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
		dataLinearLayout.setLayoutParams(rootParams);
		dataLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

		//Add the upper title and value TextView objects to this linear layout
		dataLinearLayout.addView(buildTitleView(context, listPID));
		dataLinearLayout.addView(buildValueView(context, listPID, currentView));

		//Return this single view object
		return dataLinearLayout;

	}
	
	/**
	 * Builds the title view.
	 *
	 * @param context the context
	 * @param pid the pid
	 * @return the text view
	 */
	private TextView buildTitleView(Context context, OBDPID pid) {

		//Initialize the title TextView and set the parameters
		TextView titleTextView = new TextView(context);
		LayoutParams titleParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		titleTextView.setLayoutParams(titleParams);

		//Set the TextView text size based on the amount of text to be displayed
/*		if (pid.getPidName().length() <= 10) {
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
		}
		else if (pid.getPidName().length() > 10 && pid.getPidName().length() <= 20) {
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
		}
		else if (pid.getPidName().length() > 20 && pid.getPidName().length() <= 30){
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
		}
		else if (pid.getPidName().length() > 30 && pid.getPidName().length() <= 40){
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
		}
		else {
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		}
*/
		titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
		
		//The the padding
		titleTextView.setPadding(5, 5, 5, 5);

		//Set the gravity
		titleTextView.setGravity(Gravity.CENTER);

		//Set the title text
		titleTextView.setText(pid.getPidName());

		//Set the color
		titleTextView.setTextColor(context.getResources().getColor(android.R.color.white));

		//Return the title text view
		return titleTextView;
	}

	/**
	 * Builds the value view.
	 *
	 * @param context the context
	 * @param pid the pid
	 * @param currentView the current view
	 * @return the linear layout
	 */
	private LinearLayout buildValueView(Context context, OBDPID pid, Integer currentView) {

		//Initialize the value LinearLayout and set the parameters
		LinearLayout valueLinearLayout = new LinearLayout(context);
		valueLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams rootParams = new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		valueLinearLayout.setLayoutParams(rootParams);
		valueLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		//Initialize the value TextView and set the parameters
		TextView valueTextView = new TextView(context);
		LayoutParams valueParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		valueTextView.setLayoutParams(valueParams);

		//Set the text size (in DIP)
		valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);

		//The the padding
		valueTextView.setPadding(5, 5, 5, 5);

		//If the constructor was passed a data collector thread, set the values from that
		if (dataCollectorThread != null) {
			valueTextView.setText(this.dataCollectorThread.getCurrentData(pid.getParentMode(),pid.getPidHex()));
		}

		//Otherwise init to "----"
		else {
			valueTextView.setText("----");
		}

		//Set the color
		valueTextView.setTextColor(context.getResources().getColor(android.R.color.white));

		valueLinearLayout.addView(valueTextView);

		//Add a reference to this view to the values hashmap
		valuesMaps.get(currentView).put(pid.getParentMode()+pid.getPidHex(), valueTextView);

		//Initialize the unit TextView and set the parameters
		TextView unitTextView = new TextView(context);
		LayoutParams unitParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		unitTextView.setLayoutParams(unitParams);

		//Set the text size (in DIP)
		unitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);

		//The the padding
		unitTextView.setPadding(5, 5, 5, 5);

		//Set the unit text
		if(this.sharedPrefs.getBoolean(this.context.getResources().getString(R.string.prefs_englishunits), false)) {
			unitTextView.setText(UnitConversion.getEnglishUnit(pid.getPidUnit()));
		}
		else {
			unitTextView.setText(pid.getPidUnit());
		}

		//Set the color
		unitTextView.setTextColor(context.getResources().getColor(android.R.color.white));

		valueLinearLayout.addView(unitTextView);



		//Return the values text view
		return valueLinearLayout;
	}
	
	/**
	 * Update values.
	 *
	 * @param collectorThread the collector thread
	 */
	public void updateValues(DataCollector collectorThread) {
		if (dataUpdateThread == null) {
			dataUpdateThread = new AdvancedUIDataUpdater(); 
		}
		else {
			if (!dataUpdateThread.isAlive()) {
				dataUpdateThread.run(collectorThread);
			}
		}
	}
	
	/**
	 * Gets the Data List View.
	 *
	 * @return the dataListView
	 */
	public ListView getDataListView() {
		return dataListView;
	}
	
	/**
	 * The Class BasicUIDataUpdater.
	 */
	private class AdvancedUIDataUpdater extends Thread {


		/**
		 * Instantiates a new basic ui data updater.
		 */
		public AdvancedUIDataUpdater() {

			//Set the thread name
			setName("Advanced UI Data Updater");

		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		/**
		 * Run.
		 *
		 * @param collectorThread the collector thread
		 */
		public void run(DataCollector collectorThread) {
			// TODO check to make sure this is correct
			int currentView = dataListView.indexOfChild(dataListView.getRootView());
			for(String entry : valuesMaps.get(currentView).keySet()) {
				valuesMaps.get(currentView).get(entry).setText(collectorThread.getCurrentData(entry));
			}
		}

	}

}
