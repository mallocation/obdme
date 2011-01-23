package edu.unl.csce.obdme.hardware.elm;

import android.content.Context;
import android.os.Handler;

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
	 * Ignition on.
	 */
	private synchronized void ignitionOn() {
		setState(IGNITION_ON);
	}

	/**
	 * Ignition off.
	 */
	private synchronized void ignitionOff() {
		setState(IGNITION_OFF);
	}

	/**
	 * Stop.
	 */
	public synchronized void stop() {
		if (ignPollerThread != null) {
			ignPollerThread.cancel();
			ignPollerThread = null;
		}
		setState(IGNITION_NONE);
	}

	/**
	 * The Class ELMIgnitionPollerThread.
	 */
	private class ELMIgnitionPollerThread extends Thread {

		/** The elm framework. */
		private ELMFramework elmFramework;

		/**
		 * Instantiates a new eLM ignition poller thread.
		 *
		 * @param elmFramework the elm framework
		 */
		public ELMIgnitionPollerThread(ELMFramework elmFramework) {
			this.elmFramework = elmFramework;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while(true) {

				if(this.elmFramework.checkVehicleIgnition()) {
					if (getPollerState() != IGNITION_ON) {
						ignitionOn();
					}
				}
				else {
					if (getPollerState() != IGNITION_OFF){
						ignitionOff();
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//We don't really care...
				}
			}
		}

		/**
		 * Cancel.
		 */
		public void cancel() {

		}

	};
}
