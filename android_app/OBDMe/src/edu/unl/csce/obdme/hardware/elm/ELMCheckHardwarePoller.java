package edu.unl.csce.obdme.hardware.elm;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;

/**
 * The Class ELMCheckHardwarePoller.
 */
public class ELMCheckHardwarePoller {

	/** The app handler. */
	private final Handler appHandler;

	/** The ch poller thread. */
	private ELMCheckHardwarePollerThread chPollerThread;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The message state. */
	private int messageState;

	/** The context. */
	private Context context;
	
	/** The polling interval. */
	private int pollingInterval;

	/** The Constant STATE_CHANGE. */
	public static final int STATE_CHANGE = 13146141;

	/** The Constant CHECK_HARDWARE_NONE. */
	public static final int CHECK_HARDWARE_NONE = 0;

	/** The Constant CHECK_HARDWARE_POLLING. */
	public static final int CHECK_HARDWARE_POLLING = 1;

	/** The Constant CHECK_HARDWARE_VERIFIED. */
	public static final int CHECK_HARDWARE_VERIFIED = 2;
	
	/** The Constant CHECK_HARDWARE_FAILED. */
	public static final int CHECK_HARDWARE_FAILED = 3;

	/**
	 * Instantiates a new eLM check hardware poller.
	 *
	 * @param context the context
	 * @param handler the handler
	 */
	public ELMCheckHardwarePoller(Context context, Handler handler) {
		this.appHandler = handler;
		this.context = context;
		this.elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		this.pollingInterval = 1000;
		messageState = CHECK_HARDWARE_NONE;
	}
	
	/**
	 * Instantiates a new eLM check hardware poller.
	 *
	 * @param context the context
	 * @param handler the handler
	 * @param pollingInterval the polling interval
	 */
	public ELMCheckHardwarePoller(Context context, Handler handler, int pollingInterval) {
		this.appHandler = handler;
		this.context = context;
		this.elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		this.pollingInterval = pollingInterval;
		messageState = CHECK_HARDWARE_NONE;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	private synchronized void setState(int state) {
		if(messageState != state) {
			messageState = state;
			appHandler.obtainMessage(STATE_CHANGE, state, -1).sendToTarget();
		}
	}

	/**
	 * Gets the poller state.
	 *
	 * @return the poller state
	 */
	public synchronized int getPollerState() {
		return messageState;
	}

	/**
	 * Start polling.
	 */
	public synchronized void startPolling() {
		if (chPollerThread != null) {
			chPollerThread.cancel();
			chPollerThread = null;
		}

		chPollerThread = new ELMCheckHardwarePollerThread(elmFramework);
		chPollerThread.start();
		setState(CHECK_HARDWARE_POLLING);
	}

	/**
	 * Stop.
	 */
	public synchronized void stop() {
		if (chPollerThread != null) {
			chPollerThread.cancel();
			while(chPollerThread.isAlive()){
				//Wait
			}
			chPollerThread = null;
		}
		setState(CHECK_HARDWARE_NONE);
	}

	/**
	 * Sets the polling interval.
	 *
	 * @param pollingInterval the new polling interval
	 */
	public synchronized void setPollingInterval(int pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	/**
	 * Gets the polling interval.
	 *
	 * @return the polling interval
	 */
	public synchronized int getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * The Class ELMCheckHardwarePollerThread.
	 */
	private class ELMCheckHardwarePollerThread extends Thread {

		/** The elm framework. */
		private ELMFramework elmFramework;
		
		/** The continue polling. */
		private boolean continuePolling;

		/**
		 * Instantiates a new eLM check hardware poller thread.
		 *
		 * @param elmFramework the elm framework
		 */
		public ELMCheckHardwarePollerThread(ELMFramework elmFramework) {
			
			//Set the thread name
			setName("Check Hardware Poller");
			
			this.elmFramework = elmFramework;
			continuePolling = true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while(continuePolling) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_elmframework_chpoller),
					"Started polling hardware status");
				}
				
				try {
					Thread.sleep(pollingInterval);
				} catch (InterruptedException e) {
					//We don't really care...
				}
				
				//If the hardware is confirmed verified
				if(this.elmFramework.verifyHardwareVersion()) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_elmframework_chpoller),
							"Hardware Indicated Verified");
					}
					
					//Stop polling
					continuePolling = false;
					setState(CHECK_HARDWARE_VERIFIED);
				}
				
				//If the hardware is not supported
				else {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_elmframework_chpoller),
						"Hardware Indicated Not Verified");
					}
					
					//Set a failed state
					setState(CHECK_HARDWARE_FAILED);
				}
			}
		}
		
		/**
		 * Cancel.
		 */
		public void cancel(){
			continuePolling = false;
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_chpoller),
				"Cancel Polling");
			}
		}

	};
}
