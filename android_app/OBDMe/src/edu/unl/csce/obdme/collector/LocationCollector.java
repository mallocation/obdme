package edu.unl.csce.obdme.collector;

import edu.unl.csce.obdme.R;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

/**
 * The Class LocationCollector.
 */
public class LocationCollector extends Thread{

	/** The context. */
	private Context context;

	/** The location manager. */
	private LocationManager locationManager;

	/** The current location. */
	private Location currentLocation;

	/** The provider status. */
	private boolean providerStatus;
	
	private LocationListener locationListener;

	/**
	 * Instantiates a new location collector.
	 *
	 * @param context the context
	 */
	public LocationCollector(Context context) {

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
			"Initalizing the Location Collector");
		}

		this.context = context;

		// Acquire a reference to the system Location Manager
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

	}

	/**
	 * Request location updates.
	 */
	public void requestLocationUpdates() {

		if(context.getResources().getBoolean(R.bool.debug)) {
			Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
			"Location Collector is requesting updates");
		}

		while(locationListener == null) {
		
		}
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				context.getResources().getInteger(R.integer.collector_locatio_min_time), 
				context.getResources().getInteger(R.integer.collector_locatio_min_distance), 
				locationListener);
	}

	/**
	 * Gets the current location.
	 *
	 * @return the current location
	 */
	public synchronized Location getCurrentLocation() {
		return currentLocation;
	}

	/**
	 * Sets the current location.
	 *
	 * @param currentLocation the new current location
	 */
	public synchronized void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	/**
	 * Sets the provider status.
	 *
	 * @param providerStatus the new provider status
	 */
	public void setProviderStatus(boolean providerStatus) {
		this.providerStatus = providerStatus;
	}

	/**
	 * Checks if is provider status.
	 *
	 * @return true, if is provider status
	 */
	public boolean isProviderStatus() {
		return providerStatus;
	}

	public void run() {
		
		// Define a listener that responds to location updates
		/** The location listener. */
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
							"Location Collector: Location updated");
				}
				//Called when a new location is found by the network location provider.
				setCurrentLocation(location);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			public void onProviderEnabled(String provider) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
							"Location provider was enabled. Provider is: " + provider);
				}
				setProviderStatus(true);
			}

			public void onProviderDisabled(String provider) {
				if(context.getResources().getBoolean(R.bool.debug)) {
					Log.d(context.getResources().getString(R.string.debug_tag_locationcollector),
							"Location provider was disabled. Provider is: " + provider);
				}
				setProviderStatus(false);
			}

		};
		
	}
}
