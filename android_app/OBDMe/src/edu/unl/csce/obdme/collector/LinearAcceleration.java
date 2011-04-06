package edu.unl.csce.obdme.collector;

/**
 * The Class LinearAcceleration.
 */
public class LinearAcceleration {
	
	/** The linear x accel. */
	private volatile float linearXAccel;
	
	/** The linear y accel. */
	private volatile float linearYAccel;
	
	/** The linear z accel. */
	private volatile float linearZAccel;
	
	/**
	 * Instantiates a new linear acceleration.
	 */
	public LinearAcceleration() {
		linearXAccel = 0;
		linearYAccel = 0;
		linearZAccel = 0;
	}
	
	/**
	 * Gets the linear x accel.
	 *
	 * @return the linear x accel
	 */
	public synchronized float getLinearXAccel() {
		return linearXAccel;
	}

	/**
	 * Sets the linear x accel.
	 *
	 * @param linearXAccel the new linear x accel
	 */
	public synchronized void setLinearXAccel(float linearXAccel) {
		this.linearXAccel = linearXAccel;
	}

	/**
	 * Gets the linear y accel.
	 *
	 * @return the linear y accel
	 */
	public synchronized float getLinearYAccel() {
		return linearYAccel;
	}

	/**
	 * Sets the linear y accel.
	 *
	 * @param linearYAccel the new linear y accel
	 */
	public synchronized void setLinearYAccel(float linearYAccel) {
		this.linearYAccel = linearYAccel;
	}

	/**
	 * Gets the linear z accel.
	 *
	 * @return the linear z accel
	 */
	public synchronized float getLinearZAccel() {
		return linearZAccel;
	}

	/**
	 * Sets the linear z accel.
	 *
	 * @param linearZAccel the new linear z accel
	 */
	public synchronized void setLinearZAccel(float linearZAccel) {
		this.linearZAccel = linearZAccel;
	}
}
