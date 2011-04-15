package utilities.obd;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import models.obdmedb.obd.ObdPid;
import models.obdmedb.statistics.VehicleDataPoint.IObdPidHelper;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import play.Logger;
import play.vfs.VirtualFile;

public class ObdPidUtils implements IObdPidHelper {
	
	private static ObdPidUtils _instance;
	
	private ObdPidUtils(){}
	
	public static ObdPidUtils getInstance() {
		if (_instance == null) {
			_instance = new ObdPidUtils();
		}
		return _instance;
	}
	
	private static Document loadObdProtocolFile() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder docBuilder = null;
		Document doc = null;
		
		try {
			docBuilder = dbf.newDocumentBuilder();
			doc = docBuilder.parse(VirtualFile.fromRelativePath("/app/files/obd/obd_protocol.xml").getRealFile());
			return doc;
		} catch (ParserConfigurationException e) {
			Logger.error(e, "Shit, problem creating document builder in obd protocol utils.");
			return null;
		} catch (SAXException e) {
			Logger.error(e, "Problem loading obd protcol.");
			return null;
		} catch (IOException e) {
			Logger.error(e, "Problem loading obd protcol.");
			return null;
		}		
	}
	
	
	
	
	public static String getPidUnit(ObdPid obdPid) {
		XPathFactory xpathFact = XPathFactory.newInstance();
		XPath xpath = xpathFact.newXPath();
		
		String unitXPathExpression = "//mode[@hex='%s']/pid[@hex='%s']/@unit";
		try {
			XPathExpression unitExpression = xpath.compile(String.format(unitXPathExpression, obdPid.getMode(), obdPid.getPid()));
			return unitExpression.evaluate(loadObdProtocolFile(), XPathConstants.STRING).toString();
		} catch (XPathExpressionException e) {
			Logger.error(e, "Problem compiling unit xpath expression in obd protocol.");
			return null;
		}
	}
	
	public static String getPidDecimalFormat(ObdPid obdPid) {
		XPathFactory xpathFact = XPathFactory.newInstance();
		XPath xpath = xpathFact.newXPath();
		
		String unitXPathExpression = "//mode[@hex='%s']/pid[@hex='%s']/formula/@format";
		try {
			XPathExpression unitExpression = xpath.compile(String.format(unitXPathExpression, obdPid.getMode(), obdPid.getPid()));
			return unitExpression.evaluate(loadObdProtocolFile(), XPathConstants.STRING).toString();
		} catch (XPathExpressionException e) {
			Logger.error(e, "Problem compiling unit xpath expression in obd protocol.");
			return null;
		}
	}

	@Override
	public String getObdPidDecimalFormat(ObdPid obdPid) {
		return getPidDecimalFormat(obdPid);
	}

	@Override
	public String getObdPidUnit(ObdPid obdPid) {
		return getPidUnit(obdPid);
	}

}
