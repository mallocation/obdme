package edu.unl.csce.obdme.collector;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.database.OBDMeDatabaseHelper;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;

/**
 * The Class ELMIgnitionPoller.
 */
public class DataCollector {

	/** The app handler. */
	private final Handler appHandler;

	/** The dc thread. */
	private DataCollectorThread dcThread;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/** The message state. */
	private int messageState;

	/** The context. */
	private Context context;

	/** The Constant MESSAGE_STATE_CHANGE. */
	public static final int MESSAGE_STATE_CHANGE = 0;

	/** The Constant COLLECTOR_NONE. */
	public static final int COLLECTOR_NONE = 0;
	
	/** The Constant COLLECTOR_POLLING. */
	public static final int COLLECTOR_POLLING = 1;
	
	/** The Constant COLLECTOR_PAUSED. */
	public static final int COLLECTOR_PAUSED = 2;

	/**
	 * Instantiates a new data collector.
	 *
	 * @param context the context
	 * @param handler the handler
	 * @param elmFramework the elm framework
	 */
	public DataCollector(Context context, Handler handler, ELMFramework elmFramework) {
		messageState = COLLECTOR_NONE;
		appHandler = handler;
		this.elmFramework = elmFramework;
		this.context = context;
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
		if (dcThread != null) {
			dcThread.cancel();
			dcThread = null;
		}

		dcThread = new DataCollectorThread();
		dcThread.start();
		setState(COLLECTOR_POLLING);
	}

	/**
	 * Pause polling.
	 */
	public synchronized void pausePolling() {
		if (dcThread != null) {
			dcThread.pause();
		}
		setState(COLLECTOR_PAUSED);
	}

	/**
	 * Stop.
	 */
	public synchronized void stop() {
		if (dcThread != null) {
			dcThread.cancel();
			while(dcThread.isAlive()){
				//Wait
			}
			dcThread = null;
		}
		setState(COLLECTOR_NONE);
	}

	/**
	 * The Class DataCollectorThread.
	 */
	private class DataCollectorThread extends Thread {

		/** The continue polling. */
		private boolean continuePolling;
		
		/** The sqldb. */
		private SQLiteDatabase sqldb;

		/**
		 * Instantiates a new data collector thread.
		 */
		public DataCollectorThread() {
			continuePolling = true;
			OBDMeDatabaseHelper dbh = new OBDMeDatabaseHelper(context, elmFramework);
			this.sqldb = dbh.getWritableDatabase();
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

				//For all the modes that exist in the configured protocol
				for ( String currentMode : elmFramework.getObdFramework()
						.getConfiguredProtocol().keySet() ) {

					//For all the pids that exist in the configured protocol
					for ( String currentPID : elmFramework.getObdFramework()
							.getConfiguredProtocol().get(currentMode).pidKeySet()) {

						//If it's not supported
						if (!elmFramework.getObdFramework().getConfiguredProtocol().get(currentMode)
								.getPID(currentPID).isSupported()) {

							//Remove it
							elmFramework.getObdFramework().getConfiguredProtocol().get(currentMode)
							.removePID(currentPID);
						}
					}
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

		/**
		 * Pause.
		 */
		public void pause(){
			continuePolling = false;
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.d(context.getResources().getString(R.string.debug_tag_elmframework_acpoller),
				"Pausing Polling");
			}
		}

	};
}
