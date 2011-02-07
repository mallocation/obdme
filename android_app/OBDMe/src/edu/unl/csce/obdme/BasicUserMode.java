package edu.unl.csce.obdme;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
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

/**
 * The Class BasicUserMode.
 */
public class BasicUserMode {

	/** The elm framework. */
	private ELMFramework elmFramework;
	
	/** The flipper. */
	private ViewFlipper flipper;
	
	/** The flip interval. */
	private int flipInterval;
	
	/** The values map. */
	private ConcurrentHashMap<String,TextView> valuesMap;

	private BasicUIDataUpdater dataUpdateThread;

	/**
	 * Instantiates a new basic user mode.
	 *
	 * @param context the context
	 */
	public BasicUserMode(Context context) {

		//Build the view flipper
		flipper = buildViewFlipper(context);
		
		//Set the default animations (timer controlled)
		flipper.setInAnimation(inFromRightAnimation(false));
		flipper.setOutAnimation(outToLeftAnimation(false));
		
		//Set the default flip interval
		this.flipInterval = 5000;
		flipper.setFlipInterval(flipInterval);

	}

	/**
	 * Instantiates a new basic user mode.
	 *
	 * @param context the context
	 * @param flipInterval the flip interval
	 */
	public BasicUserMode(Context context, int flipInterval) {

		//Build the view flipper
		flipper = buildViewFlipper(context);
		
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
	private ViewFlipper buildViewFlipper(Context context) {
		
		//Get the enabled pollable PIDS
		elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		HashMap<String, List<String>> pollablePIDList = elmFramework.getObdFramework().getEnabledPollablePIDList();

		//Start a new View Flipper object
		ViewFlipper rootFlipper = new ViewFlipper(context);
		
		//Initialize the values map (this contains references to ALL the stats displayed in the list view
		valuesMap = new ConcurrentHashMap<String, TextView>();

		//For all the pollable and enabled PIDS,
		for (String currentMode : pollablePIDList.keySet()) {
			for (Iterator<String> pidIrt = pollablePIDList.get(currentMode).iterator(); pidIrt.hasNext(); ) {

				OBDPID upperPID = null;
				OBDPID lowerPID = null;

				//If there is a PID left in the Iterator
				if (pidIrt.hasNext()) {
					
					//Save the reference
					upperPID = elmFramework.getConfiguredPID(currentMode, pidIrt.next());
					
					//If there is another PID to be read (second)
					if (pidIrt.hasNext()) {
						
						//Save the reference
						lowerPID = elmFramework.getConfiguredPID(currentMode, pidIrt.next());
						
						//Create a 2 PID view and add it to the flipper
						rootFlipper.addView(buildEnabledView(context, upperPID, lowerPID));
					}
					
					//Otherwise there is only one PID left in the iterator
					else {
						
						//Add a view with only one PID shown
						rootFlipper.addView(buildEnabledView(context, upperPID));
					}
				}
			}
		}		

		//Return the flipper view
		return rootFlipper;
	}

	/**
	 * Builds the enabled view.
	 *
	 * @param context the context
	 * @param upperPID the upper pid
	 * @param lowerPID the lower pid
	 * @return the view
	 */
	private View buildEnabledView(Context context, OBDPID upperPID, OBDPID lowerPID) {

		//Initialize the Linear layout for this view and set the parameters
		LinearLayout rootLinearLayout = new LinearLayout(context);
		rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams rootParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rootLinearLayout.setLayoutParams(rootParams);
		rootLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
		rootLinearLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.obdme_background));

		//Add the upper title and value TextView objects to this linear layout
		rootLinearLayout.addView(buildTitleView(context, upperPID));
		rootLinearLayout.addView(buildValueView(context, upperPID));

		//Add the lower title and value TextView objects to this linear layout
		rootLinearLayout.addView(buildTitleView(context, lowerPID));
		rootLinearLayout.addView(buildValueView(context, lowerPID));

		//Return this single view object
		return rootLinearLayout;

	}

	/**
	 * Builds the enabled view.
	 *
	 * @param context the context
	 * @param singlePID the single pid
	 * @return the view
	 */
	private View buildEnabledView(Context context, OBDPID singlePID) {
		
		//Initialize the Linear layout for this view and set the parameters
		LinearLayout rootLinearLayout = new LinearLayout(context);
		rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams rootParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rootLinearLayout.setLayoutParams(rootParams);
		rootLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

		//Add the upper title and value TextView objects to this linear layout
		rootLinearLayout.addView(buildTitleView(context, singlePID));
		rootLinearLayout.addView(buildValueView(context, singlePID));

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
	private TextView buildTitleView(Context context, OBDPID pid) {

		//Initialize the title TextView and set the parameters
		TextView titleTextView = new TextView(context);
		LayoutParams titleParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		titleTextView.setLayoutParams(titleParams);
		
		//Set the TextView text size based on the amount of text to be displayed
		if (pid.getPidName().length() <= 10) {
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
	 * @return the text view
	 */
	private TextView buildValueView(Context context, OBDPID pid) {

		//Initialize the value TextView and set the parameters
		TextView valueTextView = new TextView(context);
		LayoutParams valueParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		valueTextView.setLayoutParams(valueParams);
		
		//Set the text size (in DIP)
		valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 95);
		
		//The the padding
		valueTextView.setPadding(5, 5, 5, 5);
		
		//Set the gravity
		valueTextView.setGravity(Gravity.CENTER);
		
		//Set the default values text
		valueTextView.setText("--");
		
		//Set the color
		valueTextView.setTextColor(context.getResources().getColor(android.R.color.white));
		
		//Add a reference to this view to the values hashmap
		valuesMap.put(pid.getParentMode()+pid.getPidHex(), valueTextView);

		//Return the values text view
		return valueTextView;
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
	 * @param flipper the flipper to set
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
	 * @param flipInterval the flipInterval to set
	 */
	public void setFlipInterval(int flipInterval) {
		this.flipInterval = flipInterval;
		flipper.setFlipInterval(flipInterval);
	}

	/**
	 * Gets the flip interval.
	 *
	 * @return the flipInterval
	 */
	public int getFlipInterval() {
		return flipInterval;
	}

	private class BasicUIDataUpdater extends Thread {


		/**
		 * Instantiates a new database writer thread.
		 */
		public BasicUIDataUpdater() {
			
			//Set the thread name
			setName("Basic UI Data Updater");

		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(DataCollector collectorThread) {
			for(String entry : valuesMap.keySet()) {
				valuesMap.get(entry).setText(collectorThread.getCurrentData(entry));
			}
		}

	};


}
