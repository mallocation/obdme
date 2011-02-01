package edu.unl.csce.obdme.hardware.obd.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.hardware.obd.OBDMode;
import edu.unl.csce.obdme.hardware.obd.OBDPID;
import edu.unl.csce.obdme.hardware.obd.OBDPID.EVALS;

/**
 * The Class OBDConfigurationManager.
 */
public abstract class OBDConfigurationManager {

	/**
	 * Read obd config.
	 *
	 * @param context the context
	 * @return the hash map
	 */
	public static HashMap<String, List<String>> readDefaultOBDConfig(Context context) {
		//Make the configuration HashMap
		HashMap<String, List<String>> configurationStructure = new HashMap<String, List<String>>(2);

		try {
			//Setup the XML Pull Parser
			XmlResourceParser xrp = context.getResources().getXml(edu.unl.csce.obdme.R.xml.obd_config);
			int eventType = xrp.getEventType();

			//Local Vars
			String parentMode = null;

			//While we haven't reached the end of the document
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String startTagName = xrp.getName();

					//If the start of the config
					if (startTagName.equals("obd-config")) {
						configurationStructure = new HashMap<String, List<String>>();
					}

					//If a mode node, add it to the hashmap
					if(startTagName.equals("mode")) {
						parentMode = xrp.getAttributeValue(null, "hex");
						configurationStructure.put(xrp.getAttributeValue(null, "hex"), new ArrayList<String>());
					}

					//If a pid node, add it to the hashmap
					if (startTagName.equals("pid")) {
						configurationStructure.get(parentMode).add(xrp.getAttributeValue(null, "hex"));
					}
				}
				eventType = xrp.next();
			}
		} catch (NotFoundException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: Config file not found");
			}
		} catch (XmlPullParserException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: An XML Pull Parser error occured");
			}
		} catch (IOException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: A general IO exception occured.");
			}
		}

		return configurationStructure;
	}

	/**
	 * Check for saved protocol.
	 *
	 * @param context the context
	 * @param VIN the vIN
	 * @return true, if successful
	 */
	public static boolean checkForSavedProtocol(Context context, String VIN) {

		//Initalize a file object where the config "should" be
		File configFile = new File(context.getExternalFilesDir(null), VIN + ".xml");

		//If the config file exists,
		if (configFile.exists()) {

			//Return True
			return true;
		}

		//Otherwise
		else {

			//Return false
			return false;
		}

	}

	/**
	 * Parses the saved protocol.
	 *
	 * @param context the context
	 * @param VIN the vIN
	 * @return the oBD configuration
	 */
	public static OBDConfiguration parseSavedConfiguration(Context context, String VIN) {

		//Initalize a file object where the config "should" be
		File configFile = new File(context.getExternalFilesDir(null), VIN + ".xml");

		//If the config file exists,
		if (configFile.exists()) {

		}

		//Make the configuration HashMap
		OBDConfiguration configurationStructure = new OBDConfiguration("");

		try {

			//Open the file for reading
			FileInputStream inputStream = new FileInputStream(configFile);

			//Setup the XML Pull parser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setValidating(false);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(inputStream, null);

			//Setup the XML Pull Parser
			int eventType = xpp.getEventType();

			//Local Vars
			String parentMode = null;

			//While we haven't reached the end of the document
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String startTagName = xpp.getName();

					//If the start of the config
					if (startTagName.equals("obd-config")) {
						configurationStructure.setVIN(xpp.getAttributeValue(null, "vin"));
					}

					//If a mode node, add it to the hashmap
					if(startTagName.equals("mode")) {
						parentMode = xpp.getAttributeValue(null, "hex");
						configurationStructure.putMode(xpp.getAttributeValue(null, "hex"));
					}

					//If a pid node, add it to the hashmap
					if (startTagName.equals("pid")) {
						configurationStructure.getMode(parentMode).putPID(
								new OBDConfigurationPID(xpp.getAttributeValue(null, "hex"), 
										xpp.getAttributeValue(null, "enabled")));
					}
				}
				eventType = xpp.next();
			}
		} catch (NotFoundException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: Config file not found");
			}
		} catch (XmlPullParserException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: An XML Pull Parser error occured");
			}
		} catch (IOException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: A general IO exception occured.");
			}
		}

		return configurationStructure;
	}

	/**
	 * Parses the full obd protocol.
	 *
	 * @param context the context
	 * @return the concurrent hash map
	 */
	public static ConcurrentHashMap<String, OBDMode> parseFullOBDProtocol(Context context) {

		//Make the PID HashMap
		ConcurrentHashMap<String, OBDMode> protocolStructure = new ConcurrentHashMap<String, OBDMode>();

		try {
			//Setup the XML Pull Parser
			XmlResourceParser xrp = context.getResources().getXml(edu.unl.csce.obdme.R.xml.obd_protocol);
			int eventType = xrp.getEventType();

			//Local Vars
			String parentMode = null;
			String currentPID = null;

			//While we haven't reached the end of the document
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String startTagName = xrp.getName();

					//If the start of the protocol
					if (startTagName.equals("obd-protocol")) {
						protocolStructure = new ConcurrentHashMap<String, OBDMode>();
					}

					//If a mode node, add it to the hashmap
					else if (startTagName.equals("mode")) {
						parentMode = xrp.getAttributeValue(null, "hex");
						protocolStructure.put(new String(xrp.getAttributeValue(null, "hex")), 
								new OBDMode(xrp.getAttributeValue(null, "hex"), 
										xrp.getAttributeValue(null, "name")));
					}

					//If a PID node, add it to the hashmap
					else if (startTagName.equals("pid")) {
						//Get the PID Hex value since we read it multiple times
						String pidHex = xrp.getAttributeValue(null, "hex");

						//Create a new PID with the parameters specified in the XML
						protocolStructure.get(parentMode).putPID(pidHex,
								new OBDPID(pidHex, Integer.parseInt(xrp.getAttributeValue(null, "return")),
										xrp.getAttributeValue(null, "unit"),
										xrp.getAttributeValue(null, "eval"),
										xrp.getAttributeValue(null, "name"),
										parentMode,
										xrp.getAttributeValue(null, "pollable")));

						//Save the current PID
						currentPID = pidHex;
					}

					//If a supported PID node, add it to the hashmap in a supported mode
					else if (startTagName.equals("suported-pid")) {
						//Get the PID Hex value since we read it multiple times
						String pidHex = xrp.getAttributeValue(null, "hex");

						//Create a new PID with the parameters specified in the XML
						protocolStructure.get(parentMode).putPID(pidHex,
								new OBDPID(pidHex, Integer.parseInt(xrp.getAttributeValue(null, "return")),
										xrp.getAttributeValue(null, "unit"),
										xrp.getAttributeValue(null, "eval"),
										xrp.getAttributeValue(null, "name"),
										parentMode,
										xrp.getAttributeValue(null, "pollable"), true));

						//Save the current PID
						currentPID = pidHex;
					}

					//If a code node (for a PID).  Add the code to the PID's code list.
					else if(startTagName.equals("code")){
						//If the code is bit encoded
						if (protocolStructure.get(parentMode).getPID(currentPID).getPidEval() == EVALS.BIT_ENCODED) {
							protocolStructure.get(parentMode).getPID(currentPID).addBitEncoding(
									xrp.getAttributeValue(null, "bit"),
									xrp.getAttributeValue(null, "value"));
						}

						//If the code is byte encoded
						else if (protocolStructure.get(parentMode).getPID(currentPID).getPidEval() == EVALS.BYTE_ENCODED) {
							protocolStructure.get(parentMode).getPID(currentPID).addByteEncoding(
									xrp.getAttributeValue(null, "byte-value"),
									xrp.getAttributeValue(null, "value"));
						}
					}

					//If a formula node (for a PID).  set the current PID's formula 
					else if(startTagName.equals("formula")){
						protocolStructure.get(parentMode).getPID(currentPID).setPidFormula(
								xrp.getAttributeValue(null, "value"));
					}
				}

				//Get the next node
				eventType = xrp.next();
			}
		} catch (NumberFormatException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: Numer format exception");
			}
		} catch (NotFoundException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: Not Found Exception");
			}
		} catch (XmlPullParserException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: XML Pull Parser Exception");
			}
		} catch (IOException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: A general IO exception occured.");
			}
		}

		return protocolStructure;

	}

	/**
	 * Parses the saved obd protocol.
	 *
	 * @param context the context
	 * @param config the config
	 * @return the concurrent hash map
	 */
	public static ConcurrentHashMap<String, OBDMode> parseSavedOBDProtocol(Context context, OBDConfiguration config) {

		//Make the PID HashMap
		ConcurrentHashMap<String, OBDMode> protocolStructure = new ConcurrentHashMap<String, OBDMode>();

		try {
			//Setup the XML Pull Parser
			XmlResourceParser xrp = context.getResources().getXml(edu.unl.csce.obdme.R.xml.obd_protocol);
			int eventType = xrp.getEventType();

			//Local Vars
			String parentMode = null;
			String currentPID = null;

			//While we haven't reached the end of the document
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String startTagName = xrp.getName();

					//If the start of the protocol
					if (startTagName.equals("obd-protocol")) {
						protocolStructure = new ConcurrentHashMap<String, OBDMode>();
					}

					//If a mode node, add it to the hashmap
					else if (startTagName.equals("mode")) {

						String currentMode = xrp.getAttributeValue(null, "hex");

						//If the currentPID is supported in the saved configuration
						if (config.containsMode(currentMode)) {
							protocolStructure.put(new String(currentMode), new OBDMode(currentMode, 
									xrp.getAttributeValue(null, "name")));
						}

						parentMode = currentMode;
					}

					//If a PID node, add it to the hashmap
					else if (startTagName.equals("pid")) {

						//Get the PID Hex value since we read it multiple times
						String pidHex = xrp.getAttributeValue(null, "hex");

						//If the currentPID is supported in the saved configuration
						if(config.querySupportedPID(parentMode, pidHex)) {

							//Create a new PID with the parameters specified in the XML
							protocolStructure.get(parentMode).putPID(pidHex,
									new OBDPID(pidHex, Integer.parseInt(xrp.getAttributeValue(null, "return")),
											xrp.getAttributeValue(null, "unit"),
											xrp.getAttributeValue(null, "eval"),
											xrp.getAttributeValue(null, "name"),
											parentMode,
											xrp.getAttributeValue(null, "pollable")));

							//Set the enabled status for the newly created PID
							protocolStructure.get(parentMode).getPID(pidHex).setEnabled(
									config.getMode(parentMode).getPID(pidHex).isEnabled());
						}

						//Save the current PID
						currentPID = pidHex;
					}

					//If a supported PID node, add it to the hashmap in a supported mode
					else if (startTagName.equals("suported-pid")) {
						//Get the PID Hex value since we read it multiple times
						String pidHex = xrp.getAttributeValue(null, "hex");

						//If the currentPID is supported in the saved configuration
						if(config.querySupportedPID(parentMode, pidHex)) {

							//Create a new PID with the parameters specified in the XML
							protocolStructure.get(parentMode).putPID(pidHex,
									new OBDPID(pidHex, Integer.parseInt(xrp.getAttributeValue(null, "return")),
											xrp.getAttributeValue(null, "unit"),
											xrp.getAttributeValue(null, "eval"),
											xrp.getAttributeValue(null, "name"),
											parentMode,
											xrp.getAttributeValue(null, "pollable"), true));

							//Set the enabled status for the newly created PID
							protocolStructure.get(parentMode).getPID(pidHex).setEnabled(
									config.getMode(parentMode).getPID(pidHex).isEnabled());
						}

						//Save the current PID
						currentPID = pidHex;
					}

					//If a code node (for a PID).  Add the code to the PID's code list.
					else if(startTagName.equals("code")){

						//If the currentPID is supported in the saved configuration
						if(config.querySupportedPID(parentMode, currentPID)) {

							//If the code is bit encoded
							if (protocolStructure.get(parentMode).getPID(currentPID).getPidEval() == EVALS.BIT_ENCODED) {
								protocolStructure.get(parentMode).getPID(currentPID).addBitEncoding(
										xrp.getAttributeValue(null, "bit"),
										xrp.getAttributeValue(null, "value"));
							}

							//If the code is byte encoded
							else if (protocolStructure.get(parentMode).getPID(currentPID).getPidEval() == EVALS.BYTE_ENCODED) {
								protocolStructure.get(parentMode).getPID(currentPID).addByteEncoding(
										xrp.getAttributeValue(null, "byte-value"),
										xrp.getAttributeValue(null, "value"));
							}
						}
					}

					//If a formula node (for a PID).  set the current PID's formula 
					else if(startTagName.equals("formula")){

						//If the currentPID is supported in the saved configuration
						if(config.querySupportedPID(parentMode, currentPID)) {

							//Add the formula to the current PID
							protocolStructure.get(parentMode).getPID(currentPID).setPidFormula(
									xrp.getAttributeValue(null, "value"));
						}
					}
				}

				//Get the next node
				eventType = xrp.next();
			}
		} catch (NumberFormatException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: Numer format exception");
			}
		} catch (NotFoundException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: Not Found Exception");
			}
		} catch (XmlPullParserException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: XML Pull Parser Exception");
			}
		} catch (IOException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: A general IO exception occured.");
			}
		}

		return protocolStructure;

	}

	/**
	 * Parses the obd commands.
	 *
	 * @param context the context
	 * @return the concurrent hash map
	 */
	public static ConcurrentHashMap<String, OBDMode> readModeAndPIDOBDProtocol(Context context) {

		//Make the PID HashMap
		ConcurrentHashMap<String, OBDMode> protocolStructure = new ConcurrentHashMap<String, OBDMode>();

		try {
			//Setup the XML Pull Parser
			XmlResourceParser xrp = context.getResources().getXml(edu.unl.csce.obdme.R.xml.obd_protocol);
			int eventType = xrp.getEventType();

			//Local Vars
			String parentMode = null;

			//While we haven't reached the end of the document
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String startTagName = xrp.getName();

					//If the start of the protocol
					if (startTagName.equals("obd-protocol")) {
						protocolStructure = new ConcurrentHashMap<String, OBDMode>();
					}

					//If a mode node, add it to the hashmap
					else if (startTagName.equals("mode")) {
						String currentMode = xrp.getAttributeValue(null, "hex");

						protocolStructure.put(new String(currentMode), new OBDMode(currentMode, 
								xrp.getAttributeValue(null, "name")));

						parentMode = currentMode;
					}

					//If a PID node, add it to the hashmap
					else if (startTagName.equals("pid")) {
						//Get the PID Hex value since we read it multiple times
						String pidHex = xrp.getAttributeValue(null, "hex");

						//Create a new PID with the parameters specified in the XML
						protocolStructure.get(parentMode).putPID(pidHex,
								new OBDPID(pidHex, Integer.parseInt(xrp.getAttributeValue(null, "return")),
										xrp.getAttributeValue(null, "unit"),
										xrp.getAttributeValue(null, "eval"),
										xrp.getAttributeValue(null, "name"),
										parentMode,
										xrp.getAttributeValue(null, "pollable"), true));
					}

					//If a supported PID node, add it to the hashmap in a supported mode
					else if (startTagName.equals("suported-pid")) {
						//Get the PID Hex value since we read it multiple times
						String pidHex = xrp.getAttributeValue(null, "hex");

						//Create a new PID with the parameters specified in the XML
						protocolStructure.get(parentMode).putPID(pidHex,
								new OBDPID(pidHex, Integer.parseInt(xrp.getAttributeValue(null, "return")),
										xrp.getAttributeValue(null, "unit"),
										xrp.getAttributeValue(null, "eval"),
										xrp.getAttributeValue(null, "name"),
										parentMode,
										xrp.getAttributeValue(null, "pollable"), true));

					}
				}

				//Get the next node
				eventType = xrp.next();
			}
		} catch (NumberFormatException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: Numer format exception");
			}
		} catch (NotFoundException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: Not Found Exception");
			}
		} catch (XmlPullParserException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseprotocol),
				"Error parsing OBD Protocol: XML Pull Parser Exception");
			}
		} catch (IOException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: A general IO exception occured.");
			}
		}

		return protocolStructure;

	}

	/**
	 * Write obd configuration.
	 *
	 * @param context the context
	 * @param configuredProtocol the configured protocol
	 * @param VIN the vIN
	 */
	public static void writeOBDConfiguration(Context context, ConcurrentHashMap<String, OBDMode> configuredProtocol, String VIN) {

		try {

			//Get the saved configuration output file
			File outputFile = new File(context.getExternalFilesDir(null), VIN + ".xml");

			//If the file exists, delete it
			if (outputFile.exists()) {
				outputFile.delete();
			}

			//Create the new file and make an output stream for it
			outputFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(outputFile);

			//Initialize the XML serializer
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(outputStream, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));

			//Turn on document indenting
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

			//Create the OBD configuration tag with the VIN attached
			serializer.startTag(null, "obd-config");
			serializer.attribute(null, "vin", VIN);

			//For each mode
			for (String modeHex : configuredProtocol.keySet()) {

				//Create the mode start tag
				serializer.startTag(null, "mode");
				serializer.attribute(null, "hex", modeHex);

				//For each PID
				for(String pidHex : configuredProtocol.get(modeHex).pidKeySet()) {

					//If the PID is supported
					if (configuredProtocol.get(modeHex).getPID(pidHex).isSupported()) {

						//Star the PID tag
						serializer.startTag(null, "pid");

						//Save the attributes of the current PID
						serializer.attribute(null, "hex", pidHex);
						serializer.attribute(null, "name", configuredProtocol.get(modeHex).getPID(pidHex).getPidName());
						serializer.attribute(null, "supported", new Boolean(configuredProtocol.get(modeHex).getPID(pidHex).isSupported()).toString());
						serializer.attribute(null, "enabled", new Boolean(configuredProtocol.get(modeHex).getPID(pidHex).isEnabled()).toString());

						//End the PID tag
						serializer.endTag(null, "pid");
					}
				}

				//End the mode tag
				serializer.endTag(null, "mode");

			}

			//End the document, flush it to the file, and close the file
			serializer.endDocument();
			serializer.flush();
			outputStream.close();

		} catch (FileNotFoundException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error writing current obd configuration: File not found exception occured.");
			}
		} catch (IllegalArgumentException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: Illegal argument exception occured.");
			}
		} catch (IllegalStateException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: Illegal state exception occured.");
			}
		} catch (IOException e) {
			if(context.getResources().getBoolean(R.bool.debug)) {
				Log.e(context.getResources().getString(R.string.debug_tag_obdframework_parseconfig),
				"Error parsing obd configuration: A general IO exception occured.");
			}
		}

	}

}
