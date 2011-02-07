package edu.unl.csce.obdme.hardware.elm;

import edu.unl.csce.obdme.R;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * The Class ELMIgnitionPoller.
 */
public class ELMIgnitionPoller {

	/** The app handler. */
	private final Handler appHandler;

	/** The ign poller thread. */
	private ELMIgnitionPollerThread ignPollerThread;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The message state. */
	private int messageState;

	/** The context. */
	private Context context;
	
	/** The polling interval. */
	private int pollingInterval;

	/** The Constant MESSAGE_STATE_CHANGE. */
	public static final int MESSAGE_STATE_CHANGE = 0;

	/** The Constant IGNITION_NONE. */
	public static final int IGNITION_NONE = 0;

	/** The Constant IGNITION_POLLING. */
	public static final int IGNITION_POLLING = 1;

	/** The Constant IGNITION_ON. */
	public static final int IGNITION_ON = 2;

	/** The Constant IGNITION_OFF. */
	public static final int IGNITION_OFF = 3;

	/**
	 * Instantiates a new eLM ignition poller.
	 *
	 * @param context the context
	 * @param handler the handler
	 * @param elmFramework the elm framework
	 */
	public ELMIgnitionPoller(Context context, Handler handler, ELMFramework elmFramework) {
		messageState = IGNITION_NONE;
		appHandler = handler;
		this.elmFramework = elmFramework;
		this.context = context;
		this.pollingInterval = 1000;
	}
	
	/**
	 * Instantiates a new eLM ignition poller.
	 *
	 * @param context the context
	 * @param handler the handler
	 * @param elmFramework the elm framework
	 * @param pollingInterval the polling interval
	 */
	public ELMIgnitionPoller(Context context, Handler handler, ELMFramework elmFramework, int pollingInterval) {
		messageState = IGNITION_NONE;
		appHandler = handler;
		this.elmFramework = elmFramework;
		this.context = context;
		this.pollingInterval = pollingInterval;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	private synchronized void setState(int state) {
		if(messageState != state) {
			messageState = state;
			appHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
		}
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public synchronized int getPollerState() {
		return messageState;
	}

	/**
	 * Start polling.
	 */
	public synchronized void startPolling() {
		if (ignPollerThread != null) {
			ignPollerThread.cancel();
			ignPollerThread = null;
		}

		ignPollerThread = new ELMIgnitionPollerThread(elmFramework);
		ignPollerThread.start();
		setState(IGNITION_POLLING);
	}

	/**
	 * Stop.
	 */
	public synchronized void stop() {
		if (ignPollerThread != null) {
			ignPollerThread.cancel();
			while(ignPollerThread.isAlive()){
				//Wait
			}
			ignPollerThread = null;
		}
		setState(IGNITION_NONE);
	}

	/**
	 * Sets the polling interval.
	 *
	 * @param pollingInterval the pollingInterval to set
	 */
	public synchronized void setPollingInterval(int pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	/**
	 * Gets the polling interval.
	 *
	 * @return the pollingInterval
	 */
	public synchronized int getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * The Class ELMIgnitionPollerThread.
	 */
	private class ELMIgnitionPollerThread extends Thread {

		/** The elm framework. */
		private ELMFramework elmFramework;
		
		/** The continue polling. */
		private boolean continuePolling;

		/**
		 * Instantiates a new eLM ignition poller thread.
		 *
		 * @param elmFramework the elm framework
		 */
		public ELMIgnitionPollerThread(ELMFramework elmFramework) {
			
			//Set the thread name
			setName("Ignition Poller");
			
			this.elmFramework = elmFramework;
			continuePolling = true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while(continuePolling) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_elmframework_ignpoller),
					"Started polling ignition status");
				}
				
				//If the ignition indicates on
				if(this.elmFramework.checkVehicleIgnition()) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_elmframework_ignpoller),
						"Vehicle Ignition Indicated On");
					}
					
					//Set the ignition state to on
					setState(IGNITION_ON);
				}
				
				//Otherwise
				else {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_elmframework_ignpoller),
						"Vehicle Ignition Indicated Off");
					}
					
					//Set the ignition state to off
					setState(IGNITION_OFF);
				}

				//Sleep for two seconds
				try {
					Thread.sleep(pollingInterval);
				} catch (InterruptedException e) {
					//We don't really care...
				}
			}
		}
		
		/**
		 * Cancel.
		 */
		public void cancel(){
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_ignpoller),
				"Canceling ignition poller");
			}
			continuePolling = false;
		}

	};
}
