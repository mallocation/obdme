package edu.unl.csce.obdme.hardware;

import android.content.Context;
import android.os.Handler;

public class ELMIgnitionPoller {

	private final Handler appHandler;
	private ELMIgnitionPollerThread ignPollerThread;
	private ELMFramework elmFramework;
	private IGN_POLLER_STATE messageState;

	public static enum IGN_POLLER_STATE { NONE, INIT, POLLING, IGN_ON, IGN_OFF };
	private int MESSAGE_STATE_CHANGE = 0;

	public ELMIgnitionPoller(Context context, Handler handler, ELMFramework elmFramework) {
		messageState = IGN_POLLER_STATE.NONE;
		appHandler = handler;
		this.elmFramework = elmFramework;
	}

	private synchronized void setState(IGN_POLLER_STATE state) {
		if(messageState != state) {
			messageState = state;
			appHandler.obtainMessage(MESSAGE_STATE_CHANGE, state.ordinal(), -1).sendToTarget();
		}
	}

	public synchronized IGN_POLLER_STATE getState() {
		return messageState;
	}

	public synchronized void start() {

		if (ignPollerThread != null) {
			ignPollerThread.cancel();
			ignPollerThread = null;
		}
		setState(IGN_POLLER_STATE.INIT);
	}

	public synchronized void startPolling() {

		ignPollerThread = new ELMIgnitionPollerThread(elmFramework);
		ignPollerThread.start();
		setState(IGN_POLLER_STATE.POLLING);
	}

	public synchronized void ignitionOn() {
		setState(IGN_POLLER_STATE.IGN_ON);
	}

	public synchronized void ignitionOff() {
		setState(IGN_POLLER_STATE.IGN_OFF);
	}

	public synchronized void stop() {
		if (ignPollerThread != null) {
			ignPollerThread.cancel();
			ignPollerThread = null;
		}
		setState(IGN_POLLER_STATE.NONE);
	}

	private class ELMIgnitionPollerThread extends Thread {

		private ELMFramework elmFramework;

		public ELMIgnitionPollerThread(ELMFramework elmFramework) {
			this.elmFramework = elmFramework;
		}

		public void run() {
			while(true) {

				if(this.elmFramework.checkVehicleIgnition()) {
					ignitionOn();
				}
				else {
					ignitionOff();
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//We don't really care...
				}
			}
		}

		public void cancel() {

		}

	}
}
