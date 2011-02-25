package edu.unl.csce.obdme.collector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.unl.csce.obdme.OBDMe;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.api.ObdMeService;

/**
 * The Class DataUploader.
 */
public class DataUploader extends Thread {

	/** The Constant STATE_CHANGE. */
	public static final int STATE_CHANGE = 113513131;

	/** The Constant UPLOADER_NONE. */
	public static final int UPLOADER_NONE = 0;

	/** The Constant UPLOADER_WAITING. */
	public static final int UPLOADER_WAITING = 1;

	/** The Constant UPLOADER_UPLOADING. */
	public static final int UPLOADER_UPLOADING = 2;

	/** The Constant COLLECTOR_NEW_DATA. */
	public static final int COLLECTOR_NEW_DATA = 0;

	/** The message state. */
	private int messageState;

	/** The web framework. */
	private ObdMeService webFramework;

	/** The context. */
	private Context context;

	/** The shared prefs. */
	private SharedPreferences sharedPrefs;

	/** The app handler. */
	private Handler appHandler;
	
	/** The scheduled executor. */
	private ScheduledExecutorService scheduledExecutor;


	/**
	 * Instantiates a new data uploader.
	 *
	 * @param context the context
	 * @param handler the handler
	 * @param webFramework the web framework
	 */
	public DataUploader(Context context, Handler handler, ObdMeService webFramework) {
		this.webFramework = webFramework;
		this.appHandler = handler;
		this.context = context;

		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datauploader),
			"Initializing the Data Uploader with a Handler");
		}
		
		this.scheduledExecutor = Executors.newScheduledThreadPool(
				context.getResources().getInteger(R.integer.uploader_thread_pool_size));
		
		this.scheduledExecutor.scheduleWithFixedDelay(new DataUploadTask(context, webFramework, dataUploadHander), 
				15, 
				context.getResources().getInteger(R.integer.uploader_thread_sucessive_delay),
				TimeUnit.SECONDS);

		messageState = UPLOADER_WAITING;
	}

	/**
	 * Instantiates a new data uploader.
	 *
	 * @param context the context
	 * @param webFramework the web framework
	 */
	public DataUploader(Context context, ObdMeService webFramework) {
		messageState = UPLOADER_NONE;
		this.webFramework = webFramework;
		this.context = context;

		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		
		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_datauploader),
			"Initializing the Data Uploader without a Handler");
		}
		
		this.scheduledExecutor = Executors.newScheduledThreadPool(
				context.getResources().getInteger(R.integer.uploader_thread_pool_size));
		
		this.scheduledExecutor.scheduleWithFixedDelay(new DataUploadTask(context, webFramework, dataUploadHander), 15, 30, TimeUnit.SECONDS);

		messageState = UPLOADER_WAITING;
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
	 * Gets the uploader state.
	 *
	 * @return the uploader state
	 */
	public synchronized int getUploaderState() {
		return messageState;
	}

	/**
	 * Sets the app handler.
	 *
	 * @param appHandler the new app handler
	 */
	public void setAppHandler(Handler appHandler) {
		this.appHandler = appHandler;
	}
	
	private final Handler dataUploadHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case 0:
				if((Boolean)msg.obj) {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_datauploader),
						"Graph push succeeded.");
					}
				} 
				else {
					if(context.getResources().getBoolean(R.bool.debug)) {
						Log.d(context.getResources().getString(R.string.debug_tag_datauploader),
						"Graph push failed.");
					}
				}
				break;



			}
		}
	};


}
