package edu.unl.csce.obdme.hardware.obd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.hardware.elm.ELMException;
import edu.unl.csce.obdme.hardware.elm.ELMFramework;
import edu.unl.csce.obdme.hardware.obd.OBDPID.EVALS;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * The Class OBDFramework.
 */
public class OBDFramework {

	/** The configured protocol. */
	private ConcurrentHashMap<String, OBDMode> configuredProtocol;

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
	 * @throws ELMException 
	 */
	public boolean queryValidPIDS() throws ELMException {

		return OBDValidator.validate(this.elmFramework);

	}

	public void removeMode(String modeHex) {
		if(this.configuredProtocol.contains(modeHex)) {
			this.configuredProtocol.remove(modeHex);
		}
	}

	/**
	 * Checks if is pID supported.
	 *
	 * @param mode the mode
	 * @param pid the pid
	 * @return true, if is pID supported
	 */
	public boolean isPIDSupported(String mode, String pid) {

		//If the configured protocol contains the mode
		if (configuredProtocol.containsKey(mode)) {

			//If the configured protocol contains the pid
			if (configuredProtocol.get(mode).containsPID(pid)) {

				//Return if the configured protocol supports the pid
				return configuredProtocol.get(mode).getPID(pid).isSupported();
			}

			//If it's not listed, its not supported as far as we're concerned
			else {
				return false;
			}
		}

		//If the mode is not listed, the pid is not supported as far as we're concerned.
		else {
			return false;
		}
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
	private ConcurrentHashMap<String, OBDMode> parseOBDCommands(Context context, HashMap<String, List<String>> config) {

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
						if (config.containsKey(xrp.getAttributeValue(null, "hex"))) {
							parentMode = xrp.getAttributeValue(null, "hex");
							protocolStructure.put(new String(xrp.getAttributeValue(null, "hex")), 
									new OBDMode(xrp.getAttributeValue(null, "hex"), 
											xrp.getAttributeValue(null, "name")));
						}
					}

					//If a PID node, add it to the hashmap
					else if (startTagName.equals("pid")) {
						if (config.get(parentMode).contains(xrp.getAttributeValue(null, "hex"))){
							protocolStructure.get(parentMode).putPID(xrp.getAttributeValue(null, "hex"),
									new OBDPID(xrp.getAttributeValue(null, "hex"),
											Integer.parseInt(xrp.getAttributeValue(null, "return")),
											xrp.getAttributeValue(null, "unit"),
											xrp.getAttributeValue(null, "eval"),
											xrp.getAttributeValue(null, "name"),
											parentMode));
						}
						currentPID = xrp.getAttributeValue(null, "hex");
					}
				
					//If a supported PID node, add it to the hashmap in a supported mode
					else if (startTagName.equals("suported-pid")) {
						if (config.get(parentMode).contains(xrp.getAttributeValue(null, "hex"))){
							protocolStructure.get(parentMode).putPID(xrp.getAttributeValue(null, "hex"),
									new OBDPID(xrp.getAttributeValue(null, "hex"),
											Integer.parseInt(xrp.getAttributeValue(null, "return")),
											xrp.getAttributeValue(null, "unit"),
											xrp.getAttributeValue(null, "eval"),
											xrp.getAttributeValue(null, "name"),
											parentMode, true));
						}
						currentPID = xrp.getAttributeValue(null, "hex");
					}

					//If a code node (for a PID).  Add the code to the PID's code list.
					else if(startTagName.equals("code")){
						if(config.get(parentMode).contains(currentPID)){
							
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
						if(config.get(parentMode).contains(currentPID)) {
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
	 * @return the configuredProtocol
	 */
	public ConcurrentHashMap<String, OBDMode> getConfiguredProtocol() {
		return configuredProtocol;
	}

	/**
	 * @param configuredProtocol the configuredProtocol to set
	 */
	public void setConfiguredProtocol(ConcurrentHashMap<String, OBDMode> configuredProtocol) {
		this.configuredProtocol = configuredProtocol;
	}

}

