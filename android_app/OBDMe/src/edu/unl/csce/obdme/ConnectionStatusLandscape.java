package edu.unl.csce.obdme;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * The Class ConnectionStatusLandscape.
 */
public class ConnectionStatusLandscape {

	/** The root. */
	private LinearLayout root;

	/** The context. */
	private Context context;

	/** The wireless bars. */
	private ArrayList<ImageView> wirelessBars;

	/** The continue wireless animation. */
	private boolean continueWirelessAnimation;

	/** The dongle image. */
	private ImageView dongleImage;

	/** The continue dongle animation. */
	private boolean continueDongleAnimation;

	/** The connection failed. */
	private boolean connectionFailed;

	/** The wireless fialed image. */
	private ImageView wirelessFialedImage;


	/**
	 * Instantiates a new connection status landscape.
	 *
	 * @param context the context
	 */
	public ConnectionStatusLandscape(Context context) {

		this.context = context;

		buildInitialView();

	}

	/**
	 * Builds the initial view.
	 */
	private void buildInitialView() {

		//Initalize the root linear layout
		LinearLayout rootLinearLayout = new LinearLayout(context);
		rootLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams rootParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rootLinearLayout.setLayoutParams(rootParams);
		rootLinearLayout.setGravity(Gravity.CENTER);
		rootLinearLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_landscape));

		//Add the object views to the linear layout
		rootLinearLayout.addView(buildPhoneIcon());
		rootLinearLayout.addView(buildConnectingIcon());
		rootLinearLayout.addView(buildDongleIcon());

		this.root = rootLinearLayout;

	}

	/**
	 * Builds the phone icon.
	 *
	 * @return the linear layout
	 */
	private LinearLayout buildPhoneIcon(){

		//Build the phone layout container
		LinearLayout phoneLinearLayout = new LinearLayout(context);
		phoneLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams rootParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		phoneLinearLayout.setLayoutParams(rootParams);
		phoneLinearLayout.setGravity(Gravity.CENTER);
		phoneLinearLayout.setPadding(10, 10, 10, 10);

		//Build the phone ImageView
		ImageView phoneImage = new ImageView(context);
		phoneImage.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.phone_vector));
		phoneImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
		phoneLinearLayout.addView(phoneImage);

		return phoneLinearLayout;

	}

	/**
	 * Builds the connecting icon.
	 *
	 * @return the frame layout
	 */
	private FrameLayout buildConnectingIcon(){

		//Initalize the array list that will hold references to the wireless bars
		this.wirelessBars = new ArrayList<ImageView>();

		//Construct a frame layout to hold the wireless signal bars
		FrameLayout connectingFrameLayout = new FrameLayout(context);
		LayoutParams rootParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		connectingFrameLayout.setLayoutParams(rootParams);
		connectingFrameLayout.setPadding(10, 10, 10, 10);

		//Build the large bar
		this.wirelessFialedImage = new ImageView(context);
		this.wirelessFialedImage.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connect_fail_landscape));
		this.wirelessFialedImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
		this.wirelessFialedImage.setVisibility(View.INVISIBLE);

		//Build the small bar
		ImageView wireless1Image = new ImageView(context);
		wireless1Image.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connecting_landscape_1));
		wireless1Image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		wireless1Image.setVisibility(View.INVISIBLE);
		wirelessBars.add(wireless1Image);

		//Build the medium bar
		ImageView wireless2Image = new ImageView(context);
		wireless2Image.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connecting_landscape_2));
		wireless2Image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		wireless2Image.setVisibility(View.INVISIBLE);
		wirelessBars.add(wireless2Image);

		//Build the large bar
		ImageView wireless3Image = new ImageView(context);
		wireless3Image.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connecting_landscape_3));
		wireless3Image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		wireless3Image.setVisibility(View.INVISIBLE);
		wirelessBars.add(wireless3Image);

		//Add all the wireless signal bars to the frame layout
		connectingFrameLayout.addView(wireless1Image);
		connectingFrameLayout.addView(wireless2Image);
		connectingFrameLayout.addView(wireless3Image);
		connectingFrameLayout.addView(this.wirelessFialedImage);

		return connectingFrameLayout;

	}

	/**
	 * Builds the dongle icon.
	 *
	 * @return the frame layout
	 */
	private FrameLayout buildDongleIcon(){

		FrameLayout dongleFrameLayout = new FrameLayout(context);
		LayoutParams rootParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		dongleFrameLayout.setLayoutParams(rootParams);
		dongleFrameLayout.setPadding(10, 10, 10, 10);

		dongleImage = new ImageView(context);
		dongleImage.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.obd_dongle)); //TODO
		dongleImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

		dongleFrameLayout.addView(dongleImage);

		return dongleFrameLayout;

	}

	/**
	 * Cycle wireless animation set.
	 *
	 * @param startOffset the start offset
	 * @return the animation set
	 */
	private AnimationSet cycleWirelessAnimationSet(int startOffset) {

		//Start the cyclic connecting animation set
		AnimationSet rootAnimationSet = new AnimationSet(true); 

		//Build the fade in action
		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(300);
		fadeIn.setStartOffset(0);
		fadeIn.setFillAfter(true);
		rootAnimationSet.addAnimation(fadeIn);

		//Build the fade out action
		AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(300);
		fadeOut.setStartOffset(300);
		fadeOut.setFillAfter(true);
		rootAnimationSet.addAnimation(fadeOut);

		//Set the sequence offset
		rootAnimationSet.setStartOffset(startOffset*200);

		//Apply permanence to the transformation
		rootAnimationSet.setFillAfter(true);

		return rootAnimationSet;

	}

	/**
	 * Cycle dongle animation set.
	 *
	 * @return the animation set
	 */
	private AnimationSet cycleDongleAnimationSet() {

		//Start the cyclic connecting animation set
		AnimationSet rootAnimationSet = new AnimationSet(true); 


		//Build the fade in action
		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(500);
		fadeIn.setStartOffset(0);
		rootAnimationSet.addAnimation(fadeIn);

		//Build the fade out action
		AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(500);
		fadeOut.setStartOffset(500);
		rootAnimationSet.addAnimation(fadeOut);

		rootAnimationSet.setStartOffset(0);

		//Apply permanence to the transformation
		rootAnimationSet.setFillAfter(true);

		return rootAnimationSet;

	}

	/**
	 * Fade in wireless animation set.
	 *
	 * @param startOffset the start offset
	 * @return the animation set
	 */
	private AnimationSet fadeInWirelessAnimationSet(int startOffset) {

		//Start the connected animation set
		AnimationSet rootAnimationSet = new AnimationSet(true); 

		//Build the fade in action
		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(300);
		fadeIn.setStartOffset(0);
		fadeIn.setFillAfter(true);
		rootAnimationSet.addAnimation(fadeIn);

		//Set the sequence offset
		rootAnimationSet.setStartOffset(startOffset*200);

		//Apply permanence to the transformation
		rootAnimationSet.setFillAfter(true);

		return rootAnimationSet;

	}

	/**
	 * Fade out wireless animation set.
	 *
	 * @return the animation set
	 */
	private AnimationSet fadeOutWirelessAnimationSet() {

		//Start the connected animation set
		AnimationSet rootAnimationSet = new AnimationSet(true); 

		//Build the fade in action
		AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(300);
		fadeOut.setStartOffset(0);
		fadeOut.setFillAfter(true);
		rootAnimationSet.addAnimation(fadeOut);

		//Set the sequence offset
		rootAnimationSet.setStartOffset(0);

		//Apply permanence to the transformation
		rootAnimationSet.setFillAfter(true);

		return rootAnimationSet;

	}

	/**
	 * Start wireless animation.
	 */
	public void startWirelessAnimation() {

		if(wirelessFialedImage.isShown()) {
			wirelessFialedImage.startAnimation(fadeOutWirelessAnimationSet());
		}

		if (!this.continueWirelessAnimation) {
			//Set the loop property of the custom animation looper
			this.continueWirelessAnimation = true;
			this.connectionFailed = false;

			//Reset the connection images to blue (connecting) in case they were set to connected (green) earlier
			wirelessBars.get(0).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connecting_landscape_1));
			wirelessBars.get(1).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connecting_landscape_2));
			wirelessBars.get(2).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connecting_landscape_3));

			//Start our custom animation handler
			wirelessAnimationHandler.postDelayed(wirelessAnimationRunnable, 0);
		}
	}

	/**
	 * Finish wireless animation.
	 */
	public void finishWirelessAnimation() {
		this.continueWirelessAnimation = false;
	}

	/**
	 * Start dongle animation.
	 */
	public void startDongleAnimation() {
		this.continueDongleAnimation = true;
		donlgeAnimationHandler.postDelayed(dongleAnimationRunnable, 1000);
	}

	/**
	 * Finish dongle animation.
	 */
	public void finishDongleAnimation() {
		this.continueDongleAnimation = false;
	}

	/**
	 * Sets the connection failed.
	 */
	public void setConnectionFailed() {
		this.connectionFailed = true;
	}
	
	/**
	 * Checks if is wireless animating.
	 *
	 * @return true, if is wireless animating
	 */
	public boolean isWirelessAnimating() {
		return this.continueWirelessAnimation;
	}
	
	/**
	 * Package state.
	 *
	 * @return the hash map
	 */
	public HashMap<String, Boolean> packageState() {
		
		HashMap<String, Boolean> stateMap = new HashMap<String, Boolean>();
		
		stateMap.put("cwa", this.continueWirelessAnimation);
		stateMap.put("cda" , this.continueDongleAnimation);
		stateMap.put("cf" , this.connectionFailed);
		
		return stateMap;
	}
	
	/**
	 * Sets the state.
	 *
	 * @param packagedState the packaged state
	 */
	public void setState(HashMap<String, Boolean> packagedState) {
		
		if (packagedState.get("cwa")) {
			startWirelessAnimation();
		}
		if(packagedState.get("cda")) {
			
			wirelessBars.get(0).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connected_landscape_1));
			wirelessBars.get(1).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connected_landscape_2));
			wirelessBars.get(2).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connected_landscape_3));
			
			for(int i = 0; i < wirelessBars.size(); i++) {
				
				//If the wireless bars are not visible yet, make them visible
				if(!wirelessBars.get(i).isShown()) {
					wirelessBars.get(i).setVisibility(View.VISIBLE);
				}
			}
			startDongleAnimation();
		}
		if(packagedState.get("cf")) {
			if(!wirelessFialedImage.isShown()) {
				wirelessFialedImage.setVisibility(View.VISIBLE);
			}
			wirelessFialedImage.startAnimation(fadeInWirelessAnimationSet(0));
		}
	}

	/**
	 * Sets the root.
	 *
	 * @param root the new root
	 */
	public void setRoot(LinearLayout root) {
		this.root = root;
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public LinearLayout getRoot() {
		return root;
	}

	/** The donlge animation handler. */
	private Handler donlgeAnimationHandler = new Handler();

	/** The dongle animation runnable. */
	private Runnable dongleAnimationRunnable = new Runnable() {

		public void run() {

			//If we want to continue the animation sequence
			if(continueDongleAnimation) {

				dongleImage.startAnimation(cycleDongleAnimationSet());

				//Re-run this runnable object in 1 second
				wirelessAnimationHandler.postDelayed(this, 1000);
			}

			//Otherwise we want to stop the sequence animation
			else {

			}
		}

	};

	/** The wireless animation handler. */
	private Handler wirelessAnimationHandler = new Handler();

	/** The wireless animation runnable. */
	private Runnable wirelessAnimationRunnable = new Runnable() {

		public void run() {

			//If we want to continue the animation sequence
			if(continueWirelessAnimation) {

				for(int i = 0; i < wirelessBars.size(); i++) {

					//If the wireless bars are not visible yet, make them visible
					if(!wirelessBars.get(i).isShown()) {
						wirelessBars.get(i).setVisibility(View.VISIBLE);
					}

					//Start the animation sequence for each wireless bar
					wirelessBars.get(i).startAnimation(cycleWirelessAnimationSet(i));
				}

				//Re-run this runnable object in 1 second
				wirelessAnimationHandler.postDelayed(this, 1000);
			}

			//Otherwise we want to stop the sequence animation
			else {
				if(connectionFailed) {

					if(!wirelessFialedImage.isShown()) {
						wirelessFialedImage.setVisibility(View.VISIBLE);
					}
					wirelessFialedImage.startAnimation(fadeInWirelessAnimationSet(0));

				}
				else {

					//Change the wireless bars to green
					wirelessBars.get(0).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connected_landscape_1));
					wirelessBars.get(1).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connected_landscape_2));
					wirelessBars.get(2).setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.wireless_connected_landscape_3));

					//Set the final fade in action to signify a connection
					for(int i = 0; i < wirelessBars.size(); i++) {
						wirelessBars.get(i).startAnimation(fadeInWirelessAnimationSet(i));
					}
				}
			}
		}

	};

}
