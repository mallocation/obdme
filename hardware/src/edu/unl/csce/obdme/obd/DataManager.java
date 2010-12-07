package edu.unl.csce.obdme.obd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import edu.unl.csce.obdme.bluetooth.CommunicationInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class DataManager.
 */
public class DataManager {

	/** The log. */
	private Logger log;

	/** The collector. */
	private DataCollector collector;

	/** The collection frequency. */
	private int collectionFrequency;

	/** The OBD communications object. */
	protected OBDCommunication obdComm;

	/** The latest data. */
	protected ConcurrentHashMap<String, String> latestData;

	/** The data queue. */
	protected BlockingQueue<HashMap<String, String>> dataQueue;

	/**
	 * Instantiates a new data manager.
	 *
	 * @param commInterface the communications interface (bluetooth)
	 */
	public DataManager(CommunicationInterface commInterface) {
		this.obdComm = new OBDCommunication(commInterface);
		this.latestData = new ConcurrentHashMap<String, String>();
		this.dataQueue = new LinkedBlockingQueue<HashMap<String, String>>();
		log = Logger.getLogger(DataManager.class);
		this.collectionFrequency = 1;
	}
	
	/**
	 * Gets the data collection queue size.
	 *
	 * @return the data collection queue size
	 */
	public int getQueueSize() {
		return this.dataQueue.size();
	}
	
	/**
	 * Purge the data collection queue.
	 */
	public void purgeQueue() {
		this.latestData.clear();
	}

	/**
	 * Gets a data entry from the collection queue.
	 *
	 * @return the data entry
	 * @throws Exception the exception thrown if the queue is empty
	 */
	public HashMap<String, String> getQueueData() throws Exception {
		if (this.dataQueue.isEmpty()) {
			throw new Exception("The data queue is empty!");
		}
			
		return this.dataQueue.poll();
	}

	/**
	 * Gets a data entry from the collection queue in an amount.
	 *
	 * @param numberOfEntries the number of entries
	 * @return data entries
	 * @throws Exception the exception thrown if the queue is empty
	 */
	public List<HashMap<String, String>> getQueueData(int numberOfEntries) throws Exception {
		
		int returnCount = numberOfEntries;
		if (this.dataQueue.size() < numberOfEntries) {
			returnCount = this.dataQueue.size();
		}

		List<HashMap<String, String>> returnDataList = new ArrayList<HashMap<String, String>>(returnCount);

		for (int i = 0; i < returnCount; i++) {
			returnDataList.add(this.dataQueue.poll());
		}

		return returnDataList;
	}

	/**
	 * Start data collector thread.  This starts the data collector at the set frequency
	 */
	public void startDataCollector() {
		this.collector = new DataCollector(this.obdComm, this);
		log.info("Starting the data collector thread.");
		this.collector.setCollectionFrequency(this.collectionFrequency);
		this.collector.run();
	}

	/**
	 * Pause data collector.
	 */
	public void pauseDataCollector() {
		log.info("Inturrupting the data collector thread.");
		this.collector.interrupt();
	}
	
	/**
	 * Update latest data.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void updateLatestData(String key, String value) {
		this.latestData.put(key, value);
	}
	
	/**
	 * Push latest data to queue.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	public void pushLatestDataToQueue() throws InterruptedException {
		
		HashMap<String, String> dataSnapshot = new HashMap<String, String>();
			
		for (Iterator<String> keyItr = this.latestData.keySet().iterator(); keyItr.hasNext();) {
			String currentKey = keyItr.next();
			dataSnapshot.put(currentKey, this.latestData.get(currentKey));
		}
		
		this.dataQueue.put(dataSnapshot);
	}
	
	/**
	 * Sets the collection frequency.  This will not change the frequency for a running thread.
	 *
	 * @param collectionFrequency the collection frequency to set
	 */
	public void setCollectionFrequency(int collectionFrequency) {
		this.collectionFrequency = collectionFrequency;
		this.collector.setCollectionFrequency(this.collectionFrequency);
	}

	/**
	 * Gets the collection frequency.
	 *
	 * @return the collection frequency
	 */
	public int getCollectionFrequency() {
		return collectionFrequency;
	}

}
