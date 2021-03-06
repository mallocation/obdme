package edu.unl.csce.obdme.hardware.elm;

import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.hardware.obd.OBDResponse;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * The Class ELMAutoConnectPoller.
 */
public class ELMAutoConnectPoller {

	/** The app handler. */
	private final Handler appHandler;

	/** The ac poller thread. */
	private ELMAutoConnectPollerThread acPollerThread;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The message state. */
	private int messageState;

	/** The context. */
	private Context context;

	/** The polling interval. */
	private int pollingInterval;

	/** The Constant STATE_CHANGE. */
	public static final int STATE_CHANGE = 135478392;

	/** The Constant AUTO_CONNECT_NONE. */
	public static final int AUTO_CONNECT_NONE = 0;

	/** The Constant AUTO_CONNECT_POLLING. */
	public static final int AUTO_CONNECT_POLLING = 1;

	/** The Constant AUTO_CONNECT_COMPLETE. */
	public static final int AUTO_CONNECT_COMPLETE = 2;

	/** The Constant AUTO_CONNECT_FAILED. */
	public static final int AUTO_CONNECT_FAILED = 3;

	/**
	 * Instantiates a new eLM auto connect poller.
	 *
	 * @param context the context
	 * @param handler the handler
	 */
	public ELMAutoConnectPoller(Context context, Handler handler) {
		this.appHandler = handler;
		this.context = context;
		this.elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		this.pollingInterval = 1000;
		messageState = AUTO_CONNECT_NONE;
	}

	/**
	 * Instantiates a new eLM auto connect poller.
	 *
	 * @param context the context
	 * @param handler the handler
	 * @param pollingInterval the polling interval
	 */
	public ELMAutoConnectPoller(Context context, Handler handler, int pollingInterval) {
		this.appHandler = handler;
		this.context = context;
		this.elmFramework = ((OBDMeApplication)context.getApplicationContext()).getELMFramework();
		this.pollingInterval = pollingInterval;
		messageState = AUTO_CONNECT_NONE;
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
		if (acPollerThread != null) {
			acPollerThread.cancel();
			acPollerThread = null;
		}

		acPollerThread = new ELMAutoConnectPollerThread(elmFramework);
		acPollerThread.start();
		setState(AUTO_CONNECT_POLLING);
	}

	/**
	 * Stop.
	 */
	public synchronized void stop() {
		if (acPollerThread != null) {
			acPollerThread.cancel();
			while(acPollerThread.isAlive()){
				//Wait
			}
			acPollerThread = null;
		}
		setState(AUTO_CONNECT_NONE);
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
	 * The Class ELMAutoConnectPollerThread.
	 */
	private class ELMAutoConnectPollerThread extends Thread {

		/** The elm framework. */
		private ELMFramework elmFramework;

		/** The continue polling. */
		private boolean continuePolling;

		/**
		 * Instantiates a new eLM auto connect poller thread.
		 *
		 * @param elmFramework the elm framework
		 */
		public ELMAutoConnectPollerThread(ELMFramework elmFramework) {
			
			//Set the thread name
			setName("Auto Connect Poller");
			
			this.elmFramework = elmFramework;
			continuePolling = true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {

			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
				"Started Polling");
			}

			while(continuePolling) {

				//Sleep for two seconds
				try {
					Thread.sleep(pollingInterval);
				} catch (InterruptedException e) {
					//We don't really care...
				}

				try {

					//Send a always supported PID
					OBDResponse result = elmFramework.sendOBDRequest(elmFramework.getConfiguredPID("01", "00"));

					//If the result is not null
					if(result != null) {

						//If this protocol was not loaded from a saved configuration
						if(!elmFramework.getObdFramework().isSavedConfiguration()) {
							if(context.getResources().getBoolean(R.bool.debug)) {
								Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
										"Querying Valid PIDS");
							}

							//Query the Valid PIDS
							if (elmFramework.getObdFramework().queryValidPIDS()){
								setState(AUTO_CONNECT_COMPLETE);
								continuePolling = false;
							}
						}
						
						//Otherwise
						else {
							if(context.getResources().getBoolean(R.bool.debug)) {
								Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
										"Saved protocol was loaded, not validating PIDS.");
							}
							setState(AUTO_CONNECT_COMPLETE);
							continuePolling = false;
						}
					}

					//If for some reason we weren't able to connect, try auto searching protocols.
				} catch (ELMUnableToConnectException e) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
						"Unable to connect exception.  Still waiting.");
					}

					//Auto search protocols
					elmFramework.autoSearchProtocols();

					//Change the status to connection failed.
					setState(AUTO_CONNECT_FAILED);

					//We have a general ELM exception. 
				} catch (ELMException elme) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.e(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
								elme.getMessage());
					}

					//Change the status to connection failed.
					setState(AUTO_CONNECT_FAILED);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/**
		 * Cancel.
		 */
		public void cancel(){
			continuePolling = false;
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
				"Cancel Polling");
			}
		}

	};
}
