package edu.unl.csce.obdme.obd;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class DataCollector.
 */
public class DataCollector extends Thread {    

	/** The log. */
	private Logger log;

	/** The obd communications object. */
	private OBDCommunication obdComm;

	/** The data manager. */
	private DataManager dataManager;

	/** The default collection frequency. */
	private final int DEFAULT_COLLECTION_FREQUENCY = 1;

	/** The collection frequency. */
	private int collectionFrequency;

	/**
	 * Instantiates a new data collector.
	 *
	 * @param obdComm the obd communications object
	 * @param dataManager the data manager
	 */
	public DataCollector(OBDCommunication obdComm, DataManager dataManager) {

		log = Logger.getLogger(DataCollector.class);
		this.obdComm = obdComm;
		this.dataManager = dataManager;
		this.setCollectionFrequency(DEFAULT_COLLECTION_FREQUENCY);

	}

	/**
	 * Run the data collection thread.
	 * This thread will pull data from all the available PID's and place them in the 
	 * latest data concurrent queue.
	 *
	 */
	public void run() {

		//Get the Valid PIDS
		EnumMap<PIDS, Boolean> validPIDS = this.obdComm.getValidPIDS();

		//Make sure that we have valid PID's to collect
		if (validPIDS.isEmpty()) {
			this.interrupt();
			log.error("Data Collector thread interrupted. No supported PID's.");
		}

		//While the thread hasn't been interrupted (paused)
		while (!this.isInterrupted()) {
			
			Iterator<Entry<PIDS, Boolean>> supportedPIDS = validPIDS.entrySet().iterator();

			while(supportedPIDS.hasNext()) {

				Entry<PIDS, Boolean> currentEntry = supportedPIDS.next();

				//If the PID is supported by the car, add it the the lastest data snapshot
				if(currentEntry.getValue()) {
					try {
						this.dataManager.updateLatestData(currentEntry.getKey().getPid(), 
								this.obdComm.readPIDValue(MODES.CURRENT_DATA, currentEntry.getKey()));
					} catch (Exception e) {
						log.error("Error during data collection", e);
					}
				}
			}
		}

		
		try {
			//Inform the data manager that we have updated all the PIDS in the latest data.
			try {
				this.dataManager.pushLatestDataToQueue();
			} catch (InterruptedException ie) {
				log.error("Error calling the data manager to add data queue item.", ie);
			}
			
			//Sleep according to the frequency
			Thread.sleep(Math.round(1000/this.collectionFrequency));
		} catch (InterruptedException ie) {
			log.error("Error sleeping in the data collection thread.", ie);
		}

	}

	/**
	 * Sets the collection frequency.
	 *
	 * @param collectionFrequency the collectionFrequency to set
	 */
	public void setCollectionFrequency(int collectionFrequency) {
		this.collectionFrequency = collectionFrequency;
	}

	/**
	 * Gets the collection frequency.
	 *
	 * @return the collectionFrequency
	 */
	public int getCollectionFrequency() {
		return collectionFrequency;
	}
}