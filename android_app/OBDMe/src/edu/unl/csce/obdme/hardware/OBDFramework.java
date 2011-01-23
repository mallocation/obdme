package edu.unl.csce.obdme.hardware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import edu.unl.csce.obdme.R;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * The Class OBDFramework.
 */
public class OBDFramework {

	/** The configured protocol. */
	private HashMap<String, OBDMode> configuredProtocol;

	/** The elm framework. */
	private ELMFramework elmFramework;

	/**
	 * Instantiates a new OBD framework.
	 *
	 * @param context the context
	 * @param parentELMFramework the parent elm framework
	 */
	public OBDFramework(Context context, ELMFramework parentELMFramework) {
		this.elmFramework = parentELMFramework;
		this.configuredProtocol = parseOBDCommands(context, readOBDConfig(context));
	}

	/**
	 * Query valid pids.
	 */
	public void queryValidPIDS() {

		OBDValidator.validate(this.configuredProtocol, this.elmFramework);

	}

	/**
	 * Read obd config.
	 *
	 * @param context the context
	 * @return the hash map
	 */
	private HashMap<String, List<String>> readOBDConfig(Context context) {
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
	 * Parses the obd commands.
	 *
	 * @param context the context
	 * @param config the config
	 * @return the hash map
	 */
	private HashMap<String, OBDMode> parseOBDCommands(Context context, HashMap<String, List<String>> config) {

		//Make the PID HashMap
		HashMap<String, OBDMode> protocolStructure = new HashMap<String, OBDMode>();

		try {
			//Setup the XML Pull Parser
			XmlResourceParser xrp = context.getResources().getXml(edu.unl.csce.obdme.R.xml.obd_protocol);
			int eventType = xrp.getEventType();

			//Local Vars
			String parentMode = null;
			boolean ignoreMode = false;

			//While we haven't reached the end of the document
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String startTagName = xrp.getName();

					//If the start of the protocol
					if (startTagName.equals("obd-protocol")) {
						protocolStructure = new HashMap<String, OBDMode>();
					}

					//If a mode node, add it to the hashmap
					else if (startTagName.equals("mode")) {
						//If this mode exists in the config, add it.
						if (config.containsKey(xrp.getAttributeValue(null, "hex"))) {
							ignoreMode = false;
							parentMode = xrp.getAttributeValue(null, "hex");
							protocolStructure.put(xrp.getAttributeValue(null, "hex"), 
									new OBDMode(xrp.getAttributeValue(null, "hex"), 
											xrp.getAttributeValue(null, "name")));
						}
						else {
							ignoreMode = true;
						}
					}

					//If a PID node, add it to the hashmap
					else if (startTagName.equals("pid") && !ignoreMode) {

						//If the PID exists in the config, add it to the OBDMode object
						if(config.get(parentMode).contains(xrp.getAttributeValue(null, "hex"))) {
							protocolStructure.get(parentMode).putPID(xrp.getAttributeValue(null, "hex"),
									new OBDPID(xrp.getAttributeValue(null, "hex"),
											Integer.parseInt(xrp.getAttributeValue(null, "return")),
											xrp.getAttributeValue(null, "unit"),
											xrp.getAttributeValue(null, "eval"),
											xrp.getAttributeValue(null, "name"),
											protocolStructure.get(parentMode)));
						}
					}
				}
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
}

