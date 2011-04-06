package edu.unl.csce.obdme.collector;

/**
 * The Class Acceleration.
 */
public class Acceleration {
	
	/** The x accel. */
	private volatile float xAccel;
	
	/** The y accel. */
	private volatile float yAccel;
	
	/** The z accel. */
	private volatile float zAccel;
	
	
	/**
	 * Instantiates a new acceleration.
	 */
	public Acceleration() {
		xAccel = 0;
		yAccel = 0;
		zAccel = 0;
	}
	
	/**
	 * Gets the x accel.
	 *
	 * @return the x accel
	 */
	public synchronized float getXAccel() {
		return xAccel;
	}

	/**
	 * Sets the x accel.
	 *
	 * @param xAccel the new x accel
	 */
	public synchronized void setXAccel(float xAccel) {
		this.xAccel = xAccel;
	}

	/**
	 * Gets the y accel.
	 *
	 * @return the y accel
	 */
	public synchronized float getYAccel() {
		return yAccel;
	}

	/**
	 * Sets the y accel.
	 *
	 * @param yAccel the new y accel
	 */
	public synchronized void setYAccel(float yAccel) {
		this.yAccel = yAccel;
	}

	/**
	 * Gets the z accel.
	 *
	 * @return the z accel
	 */
	public synchronized float getZAccel() {
		return zAccel;
	}

	/**
	 * Sets the z accel.
	 *
	 * @param zAccel the new z accel
	 */
	public synchronized void setZAccel(float zAccel) {
		this.zAccel = zAccel;
	}
}
