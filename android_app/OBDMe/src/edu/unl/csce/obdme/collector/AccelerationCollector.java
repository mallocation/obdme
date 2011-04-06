package edu.unl.csce.obdme.collector;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import edu.unl.csce.obdme.R;

/**
 * The Class AccelerationCollector.
 */
public class AccelerationCollector extends Thread{

	/** The context. */
	private Context context;

	/** The sensor manager. */
	private SensorManager sensorManager;

	/** The sensor listener. */
	private SensorEventListener sensorListener;

	/** The linear acceleration. */
	private LinearAcceleration linearAcceleration;
	
	/** The acceleration. */
	private Acceleration acceleration; 

	/**
	 * Instantiates a new acceleration collector.
	 *
	 * @param context the context
	 */
	public AccelerationCollector(Context context) {

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
			"Initalizing the Location Collector");
		}

		this.context = context;

		// Acquire a reference to the system Sensor Manager
		sensorManager =(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

	}

	/**
	 * Request accelerometer updates.
	 */
	public void requestAccelerometerUpdates() {

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
			"Location Collector is requesting updates");
		}

		while(sensorListener == null) {

		}
		List<Sensor> sensorList = sensorManager.getSensorList(SensorManager.SENSOR_ACCELEROMETER);
		sensorManager.registerListener(sensorListener, sensorList.get(0), SensorManager.SENSOR_DELAY_UI);
	}

	/**
	 * Gets the linear acceleration.
	 *
	 * @return the linear acceleration
	 */
	public synchronized LinearAcceleration getLinearAcceleration() {
		synchronized(linearAcceleration) {
			LinearAcceleration returnLA = linearAcceleration;
			linearAcceleration = new LinearAcceleration();
			return returnLA;
		}
	}
	
	/**
	 * Gets the acceleration.
	 *
	 * @return the acceleration
	 */
	public synchronized Acceleration getAcceleration() {
		synchronized(acceleration) {
			Acceleration returnA = acceleration;
			acceleration = new Acceleration();
			return returnA;
		}
	}


	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		// Define a listener that responds to location updates
		/** The location listener. */
		sensorListener = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
							"Sensor Listener Event: " + sensor.getName() + " Accuracy Changed to " + accuracy);
				}

			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
							"Sensor Listener Event: Sensor Event For " + event.sensor.getName());
				}
				
				final float alpha = (float) 0.8;
				float gravity[] = new float[3];

				gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
				gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
				gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

				synchronized(linearAcceleration) {
					linearAcceleration.setLinearXAccel(event.values[0] - gravity[0]);
					linearAcceleration.setLinearYAccel(event.values[1] - gravity[1]);
					linearAcceleration.setLinearZAccel(event.values[2] - gravity[2]);
				}
				
				synchronized(acceleration) {
					acceleration.setXAccel(event.values[0]);
					acceleration.setYAccel(event.values[1]);
					acceleration.setZAccel(event.values[2]);
				}
			}

		};

	}

}
