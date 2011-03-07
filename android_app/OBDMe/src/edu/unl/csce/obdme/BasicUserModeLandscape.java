package edu.unl.csce.obdme;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;
import edu.unl.csce.obdme.collector.DataCollector;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.obd.OBDPID;
import edu.unl.csce.obdme.utilities.AppSettings;
import edu.unl.csce.obdme.utilities.UnitConversion;

/**
 * The Class BasicUserModeLandscape.
 */
public class BasicUserModeLandscape {

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The flipper. */
	private ViewFlipper flipper;

	/** The flip interval. */
	private int flipInterval;

	/** The values maps. */
	private HashMap<Integer, HashMap<String,TextView>> valuesMaps;

	/** The data update thread. */
	private BasicUIDataUpdater dataUpdateThread;

	/** The data collector thread. */
	private DataCollector dataCollectorThread;

	/** The custom font. */
	private Typeface customFont;

	/** The app settings. */
	private AppSettings appSettings;

	private Context context;

	/**
	 * Instantiates a new basic user mode landscape.
	 *
	 * @param context the context
	 */
	public BasicUserModeLandscape(Context context) {
		this.context = context;
		this.appSettings = ((OBDMeApplication)context.getApplicationContext()).getApplicationSettings();
		this.dataCollectorThread = ((OBDMeApplication)context.getApplicationContext()).getDataCollector();
		this.customFont = Typeface.createFromAsset(context.getAssets(), "LCD.ttf");  

		//Build the view flipper
		flipper = buildViewFlipper();

		//Set the default animations (timer controlled)
		flipper.setInAnimation(inFromRightAnimation(false));
		flipper.setOutAnimation(outToLeftAnimation(false));

		//Set the default flip interval
		this.flipInterval = 5000;
		flipper.setFlipInterval(flipInterval);

	}

	/**
	 * Instantiates a new basic user mode landscape.
	 *
	 * @param context the context
	 * @param flipInterval the flip interval
	 */
	public BasicUserModeLandscape(Context context, int flipInterval) {
		this.appSettings = ((OBDMeApplication)context.getApplicationContext()).getApplicationSettings();
		this.dataCollectorThread = ((OBDMeApplication)context.getApplicationContext()).getDataCollector();
		this.customFont = Typeface.createFromAsset(context.getAssets(), "LCD.ttf");  

		//Build the view flipper
		flipper = buildViewFlipper();

		//Set the default animations (timer controlled)
		flipper.setInAnimation(inFromRightAnimation(false));
		flipper.setOutAnimation(outToLeftAnimation(false));

		//Set the default flip interval
		this.flipInterval = flipInterval;
		flipper.setFlipInterval(flipInterval);

	}

	/**
	 * Builds the view flipper.
	 *
	 * @param context the context
	 * @return the view flipper
	 */
	private ViewFlipper buildViewFlipper() {

		//Get the enabled pollable PIDS
		elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		HashMap<String, List<String>> pollablePIDList = elmFramework.getObdFramework().getDisplayedPIDList();

		//Start a new View Flipper object
		ViewFlipper rootFlipper = new ViewFlipper(context);

		rootFlipper.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_landscape));

		this.valuesMaps = new HashMap<Integer, HashMap<String, TextView>>();

		Integer currentView = new Integer(0);

		//For all the pollable and enabled PIDS,
		for (String currentMode : pollablePIDList.keySet()) {
			for (Iterator<String> pidIrt = pollablePIDList.get(currentMode).iterator(); pidIrt.hasNext(); ) {

				this.valuesMaps.put(currentView, new HashMap<String, TextView>());

				OBDPID middlePID = null;

				//Save the reference
				middlePID = elmFramework.getConfiguredPID(currentMode, pidIrt.next());
				rootFlipper.addView(buildEnabledView(middlePID, currentView));

				currentView = new Integer(currentView+1);

			}
		}	

		//Return the flipper view
		return rootFlipper;
	}

	/**
	 * Builds the enabled view.
	 *
	 * @param context the context
	 * @param middlePID the middle pid
	 * @param currentView the current view
	 * @return the view
	 */
	private View buildEnabledView(OBDPID middlePID, Integer currentView) {

		//Initialize the Linear layout for this view and set the parameters
		LinearLayout rootLinearLayout = new LinearLayout(context);
		rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams rootParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rootLinearLayout.setLayoutParams(rootParams);
		rootLinearLayout.setGravity(Gravity.CENTER);

		//Add the upper title and value TextView objects to this linear layout
		rootLinearLayout.addView(buildTitleView(middlePID));
		rootLinearLayout.addView(buildValueView(middlePID, currentView));

		//Return this single view object
		return rootLinearLayout;

	}

	/**
	 * Builds the title view.
	 *
	 * @param context the context
	 * @param pid the pid
	 * @return the text view
	 */
	private TextView buildTitleView(OBDPID pid) {

		//Initialize the title TextView and set the parameters
		TextView titleTextView = new TextView(context);
		LayoutParams titleParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		titleTextView.setLayoutParams(titleParams);

		//Set the TextView text size based on the amount of text to be displayed
		if (pid.getPidName().length() <= 10) {
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
		}
		else if (pid.getPidName().length() > 10 && pid.getPidName().length() <= 20) {
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 45);
		}
		else if (pid.getPidName().length() > 20 && pid.getPidName().length() <= 30){
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
		}
		else if (pid.getPidName().length() > 30 && pid.getPidName().length() <= 40){
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
		}
		else {
			titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
		}

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
	private LinearLayout buildValueView(OBDPID pid, Integer currentView) {

		//Initialize the value LinearLayout and set the parameters
		LinearLayout valueLinearLayout = new LinearLayout(context);
		valueLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams rootParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		valueLinearLayout.setLayoutParams(rootParams);
		valueLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		//Initialize the value TextView and set the parameters
		TextView valueTextView = new TextView(context);
		valueTextView.setTypeface(customFont);
		LayoutParams valueParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		valueTextView.setLayoutParams(valueParams);

		//Set the text size (in DIP)
		valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 130);

		//The the padding
		valueTextView.setPadding(5, 5, 5, 5);

		//If the constructor was passed a data collector thread, set the values from that
		if (dataCollectorThread.getPollerState() == DataCollector.COLLECTOR_POLLING) {
			valueTextView.setText(this.dataCollectorThread.getCurrentData(pid.getParentMode(),pid.getPidHex()));
		}
		else {
			valueTextView.setText("8888");
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
		unitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);

		//The the padding
		unitTextView.setPadding(5, 5, 5, 5);

		//Set the unit text
		if(appSettings.isEnglishUnits()) {
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
	 * In from right animation.
	 *
	 * @param fling the fling
	 * @return the animation
	 */
	private Animation inFromRightAnimation(boolean fling) {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		//If this is a fling, speed up the animation
		if (fling) {
			inFromRight.setDuration(500);
		}
		else {
			inFromRight.setDuration(1000);
		}
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	/**
	 * Out to left animation.
	 *
	 * @param fling the fling
	 * @return the animation
	 */
	private Animation outToLeftAnimation(boolean fling) {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		//If this is a fling, speed up the animation
		if (fling) {
			outtoLeft.setDuration(500);
		}
		else {
			outtoLeft.setDuration(1000);
		}
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	/**
	 * In from left animation.
	 *
	 * @param fling the fling
	 * @return the animation
	 */
	private Animation inFromLeftAnimation(boolean fling) {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		//If this is a fling, speed up the animation
		if (fling) {
			inFromLeft.setDuration(500);
		}
		else {
			inFromLeft.setDuration(1000);
		}
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	/**
	 * Out to right animation.
	 *
	 * @param fling the fling
	 * @return the animation
	 */
	private Animation outToRightAnimation(boolean fling) {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		//If this is a fling, speed up the animation
		if (fling) {
			outtoRight.setDuration(500);
		}
		else {
			outtoRight.setDuration(1000);  
		}
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	/**
	 * Fling left.
	 */
	public void flingLeft() {

		//Stop timed flipping
		flipper.stopFlipping();

		//Set the fling animation based on the direction of the fling
		flipper.setInAnimation(inFromLeftAnimation(true));
		flipper.setOutAnimation(outToRightAnimation(true));

		//Show the new view
		flipper.showPrevious();

	}

	/**
	 * Fling right.
	 */
	public void flingRight() {

		//Stop timed flipping
		flipper.stopFlipping();

		//Set the fling animation based on the direction of the fling
		flipper.setInAnimation(inFromRightAnimation(true));
		flipper.setOutAnimation(outToLeftAnimation(true));

		//Show the new view
		flipper.showNext();

	}

	/**
	 * Update values.
	 *
	 * @param collectorThread the collector thread
	 */
	public void updateValues(DataCollector collectorThread) {
		if (dataUpdateThread == null) {
			dataUpdateThread = new BasicUIDataUpdater(); 
		}
		else {
			if (!dataUpdateThread.isAlive()) {
				dataUpdateThread.run(collectorThread);
			}
		}
	}

	/**
	 * Start flipping.
	 */
	public void startFlipping() {
		flipper.startFlipping();
	}

	/**
	 * Stop flipping.
	 */
	public void stopFlipping() {
		flipper.stopFlipping();
	}


	/**
	 * Sets the flipper.
	 *
	 * @param flipper the new flipper
	 */
	public void setFlipper(ViewFlipper flipper) {
		this.flipper = flipper;
	}

	/**
	 * Gets the flipper.
	 *
	 * @return the flipper
	 */
	public ViewFlipper getFlipper() {
		return flipper;
	}

	/**
	 * Sets the flip interval.
	 *
	 * @param flipInterval the new flip interval
	 */
	public void setFlipInterval(int flipInterval) {
		this.flipInterval = flipInterval;
		flipper.setFlipInterval(flipInterval);
	}

	/**
	 * Gets the flip interval.
	 *
	 * @return the flip interval
	 */
	public int getFlipInterval() {
		return flipInterval;
	}

	/**
	 * The Class BasicUIDataUpdater.
	 */
	private class BasicUIDataUpdater extends Thread {


		/**
		 * Instantiates a new basic ui data updater.
		 */
		public BasicUIDataUpdater() {

			//Set the thread name
			setName("Basic UI Data Updater");

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
			int currentView = flipper.indexOfChild(flipper.getCurrentView());
			for(String entry : valuesMaps.get(currentView).keySet()) {
				valuesMaps.get(currentView).get(entry).setText(collectorThread.getCurrentData(entry));
			}
		}


	};


}
