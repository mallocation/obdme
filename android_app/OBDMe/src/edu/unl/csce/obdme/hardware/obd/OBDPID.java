package edu.unl.csce.obdme.hardware.obd;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class OBDPID.
 */
public class OBDPID {

	/**
	 * The Enum EVALS.
	 */
	public static enum EVALS {
		
		/** The FORMULA. */
		FORMULA,
		
		/** The RAW. */
		RAW,
		
		/** The BI t_ encoded. */
		BIT_ENCODED,
		
		/** The BYT e_ encoded. */
		BYTE_ENCODED,
		
		/** The CHA r_ string. */
		CHAR_STRING
	}

	/** The pid hex. */
	private String pidHex;

	/** The pid value. */
	private int pidValue;

	/** The pid return. */
	private int pidReturn;

	/** The pid unit. */
	private String pidUnit;

	/** The pid eval. */
	private EVALS pidEval;

	/** The pid formula. */
	private String pidFormula;
	
	/** The pid compiled formula. */
	private OBDFormulas pidCompiledFormula;

	/** The pid name. */
	private String pidName;

	/** The bit encoded map. */
	private ConcurrentHashMap<Integer, String> bitEncodedMap;

	/** The byte encoded map. */
	private ConcurrentHashMap<Integer, String> byteEncodedMap;

	/** The supported. */
	private boolean supported;
	
	/** The collected. */
	private boolean collected;
	
	/** The displayed. */
	private boolean displayed;
	
	/** The pollable. */
	private boolean pollable;

	/** The parent mode. */
	private String parentMode;

	/** The parent mode value. */
	private int parentModeValue;
	
	/** The decimal format. */
	private DecimalFormat decimalFormat;

	/**
	 * Instantiates a new oBDPID.
	 *
	 * @param pidHex the pid hex
	 * @param pidReturn the pid return
	 * @param pidUnit the pid unit
	 * @param pidEval the pid eval
	 * @param pidName the pid name
	 * @param parent the parent
	 * @param pollable the pollable
	 */
	public OBDPID(String pidHex, int pidReturn, String pidUnit, String pidEval, String pidName, 
			String parent, String pollable) {
		this.pidHex = pidHex;
		this.pidValue = Integer.parseInt(pidHex,16);
		this.pidReturn = pidReturn;
		this.pidUnit = pidUnit;
		this.setPidEval(pidEval);
		this.pidName = pidName;
		this.parentMode = parent;
		this.parentModeValue = Integer.parseInt(parent,16);
		
		if (pollable.toString().equals("true")) {
			this.pollable = true;
		}
		
		else {
			this.pollable = false;
		}
		
		this.setCollected(false);
		this.supported = false;

		//Switch on the eval method to initialize variables 
		switch(this.pidEval) {
		
		//Formula
		case FORMULA:
			this.decimalFormat = new DecimalFormat();
			this.pidCompiledFormula = null;
		
		//Bit Encoded
		case BIT_ENCODED:
			this.bitEncodedMap = new ConcurrentHashMap<Integer, String>();
			break;

			//Byte Encoded
		case BYTE_ENCODED:
			this.byteEncodedMap = new ConcurrentHashMap<Integer, String>();
			break;
		}
	}

	/**
	 * Instantiates a new oBDPID.
	 *
	 * @param pidHex the pid hex
	 * @param pidReturn the pid return
	 * @param pidUnit the pid unit
	 * @param pidEval the pid eval
	 * @param pidName the pid name
	 * @param parent the parent
	 * @param pollable the pollable
	 * @param supported the supported
	 */
	public OBDPID(String pidHex, int pidReturn, String pidUnit, String pidEval, String pidName, 
			String parent,  String pollable, boolean supported) {
		this.pidHex = pidHex;
		this.pidValue = Integer.parseInt(pidHex,16);
		this.pidReturn = pidReturn;
		this.pidUnit = pidUnit;
		this.setPidEval(pidEval);
		this.pidName = pidName;
		this.parentMode = parent;
		
		if (pollable.toString().equals("true")) {
			this.pollable = true;
		}
		
		else {
			this.pollable = false;
		}
		
		this.setCollected(true);
		this.supported = supported;


		//Switch on the eval method to initialize variables 
		switch(this.pidEval) {
		
		//Formula
		case FORMULA:
			this.decimalFormat = new DecimalFormat();
			this.pidCompiledFormula = null;

		//Bit Encoded
		case BIT_ENCODED:
			this.bitEncodedMap = new ConcurrentHashMap<Integer, String>();
			break;

			//Byte Encoded
		case BYTE_ENCODED:
			this.byteEncodedMap = new ConcurrentHashMap<Integer, String>();
			break;
		}
	}

	/**
	 * Evaluate response.
	 *
	 * @param byteResponse the byte response
	 * @return the object
	 * @throws Exception the exception
	 */
	public Object evaluateResponse(List<String> byteResponse) throws Exception {

		//Switch on the PID calculation type
		switch(this.pidEval) {
		case BIT_ENCODED:
			return bitEncodedEvaluator(byteResponse);

		case BYTE_ENCODED:
			return byteEncodedEvaluator(byteResponse);

		case CHAR_STRING:
			return charStringEvaluator(byteResponse);

		case FORMULA:
			if (this.pidCompiledFormula != null) {
				return this.pidCompiledFormula.formulaEval(byteResponse);
			}
			else{
				return formulaEvaluator(byteResponse);
			}

		case RAW:
			return rawEvaluator(byteResponse);

		default:
			return rawEvaluator(byteResponse);
		}

	}

	/**
	 * Formula evaluator.
	 *
	 * @param byteResponse the byte response
	 * @return the double
	 */
	private Double formulaEvaluator(List<String> byteResponse) {

		//Setup the operations and values stack
		Stack<String> operations  = new Stack<String>();
		Stack<Double> values = new Stack<Double>();

		//Tokenize the formula
		StringTokenizer tokenizedFormula = new StringTokenizer(this.pidFormula, " ", false);

		//While there are still elements to process
		while (tokenizedFormula.hasMoreElements()) {

			//Get the current token
			String currentToken = tokenizedFormula.nextToken().toString();

			//Starting a nested operation
			if (currentToken.equals("(")) {

			}

			//Push addition operation to the operations stack
			else if (currentToken.equals("+")) {
				operations.push(currentToken);
			}

			//Push subtraction operation to the operations stack
			else if (currentToken.equals("-")) {
				operations.push(currentToken);
			}

			//Push multiplication operation to the operations stack
			else if (currentToken.equals("*")) {
				operations.push(currentToken);
			}

			//Push division operation to the operations stack
			else if (currentToken.equals("/")) {
				operations.push(currentToken);
			}

			//If the formula indicates the first byte from the response
			else if (currentToken.equals("A")){

				//Parse the double value of the hex byte
				values.push((double)Integer.parseInt(byteResponse.get(0), 16));
			}

			//If the formula indicates the second byte from the response
			else if (currentToken.equals("B")){

				//Parse the double value of the hex byte
				values.push((double)Integer.parseInt(byteResponse.get(1), 16));
			}

			//If the formula indicates the third byte from the response
			else if (currentToken.equals("C")){

				//Parse the double value of the hex byte
				values.push((double)Integer.parseInt(byteResponse.get(2), 16));
			}

			//If the formula indicates the fourth byte from the response
			else if (currentToken.equals("D")){

				//Parse the double value of the hex byte
				values.push((double)Integer.parseInt(byteResponse.get(3), 16));
			}
			
			//Process the nested operations
			else if (currentToken.equals(")")) {

				String op = operations.pop();
				double currentValue = values.pop();

				//Pop the previous value from the stack and 
				//add it to the most recently popped value
				if (op.equals("+")) {
					currentValue = values.pop() + currentValue;
				}
				else if (op.equals("-")) {
					currentValue = values.pop() - currentValue;
				}
				else if (op.equals("*")) {
					currentValue = values.pop() * currentValue;
				}
				else if (op.equals("/")) {
					currentValue = values.pop() / currentValue;
				}
				values.push(currentValue);
			}

			//Otherwise, it is a formula value...
			else {

				//Add the formula value to the stack
				values.push(Double.parseDouble(currentToken));
			}
		}

		return values.pop();
	}

	/**
	 * Char string evaluator.
	 *
	 * @param byteResponse the byte response
	 * @return the string
	 */
	private String charStringEvaluator(List<String> byteResponse) {

		//Make an iterator for the reponse string 
		Iterator<String> byteResponseItr = byteResponse.iterator();

		//Make a string buffer to compose the response string
		StringBuffer responseCharString = new StringBuffer();

		while (byteResponseItr.hasNext()) {

			//Get the next byte
			String currentByte = byteResponseItr.next();

			//Parse the string to a hex value and then typecast it to a character
			char charValue = (char) Integer.parseInt(currentByte, 16);

			//Add the character to the response string
			responseCharString.append(charValue);
		}

		//Return the string
		return responseCharString.toString();
	}

	/**
	 * Bit encoded evaluator.
	 *
	 * @param byteResponse the byte response
	 * @return the list
	 */
	public List<String> bitEncodedEvaluator(List<String> byteResponse) {

		if (this.bitEncodedMap != null) {
			//Make an iterator for the reponse string 
			Iterator<String> responseValueItr = byteResponse.iterator();

			//Make a string buffer to compose the bitstring
			StringBuffer encodedBits = new StringBuffer();

			while (responseValueItr.hasNext()) {

				//Get the bitstring for the fist byte
				String bitString = Integer.toBinaryString(Integer.parseInt(responseValueItr.next(),16));

				//If it's less that 8, add the preceeding 0's
				if (bitString.length() < 8) {
					String precedingZeros = "";

					//For each empty space to the left of the first one, add a zero
					for (int i = 0; i < 8 - bitString.length(); i++) {
						precedingZeros += "0";
					}

					//Compose the preceeding zeros with the bitstring
					bitString = precedingZeros + bitString;
				}

				//Add this bit string to the string buffer, move onto the next response byte
				encodedBits.append(bitString);
			}

			//Convert the composed bitstring to a imutable string
			String bitString = encodedBits.toString();

			//Set up the decided values
			List<String> decodedValues = new ArrayList<String>();

			//For each bit, starting at index 0 (MSB), append the bit value to the decoded values list
			for (int index = 0; index < bitString.length(); index++) {
				if (bitString.charAt(index) == '1') {
					if (this.bitEncodedMap.containsKey(index)) {
						decodedValues.add(this.bitEncodedMap.get(index));
					}
				} 
			}

			return decodedValues;

		}

		else {
			return null;
		}
	}

	/**
	 * Byte encoded evaluator.
	 *
	 * @param byteResponse the byte response
	 * @return the string
	 */
	public String byteEncodedEvaluator(List<String> byteResponse) {

		//Get the value of the response byte
		Integer byteValue = Integer.parseInt(byteResponse.get(0).toString(), 16);

		//If there is a byte encoded map
		if (this.byteEncodedMap != null) {

			//And there is a value for our decoded byte
			if (this.byteEncodedMap.containsKey(byteValue)) {

				//Return it
				return this.byteEncodedMap.get(byteValue);
			}
		}
		
		return null;
	}

	/**
	 * Raw evaluator.
	 *
	 * @param byteResponse the byte response
	 * @return the integer
	 */
	public Integer rawEvaluator(List<String> byteResponse) {

		//Make an iterator for the reponse string 
		Iterator<String> byteResponseItr = byteResponse.iterator();

		//Make a string buffer to compose the response string
		StringBuffer compositeResponseString = new StringBuffer();

		while (byteResponseItr.hasNext()) {

			//Get the next byte
			String currentByte = byteResponseItr.next();

			//Add the character to the response string
			compositeResponseString.append(currentByte);
		}

		//Get the hex value of the composite response string
		Integer responseValue = Integer.parseInt(compositeResponseString.toString(), 16);

		//Return the integer value
		return responseValue;

	}

	/**
	 * Gets the pid hex.
	 *
	 * @return the pid hex
	 */
	public String getPidHex() {
		return pidHex;
	}

	/**
	 * Sets the pid hex.
	 *
	 * @param pidHex the new pid hex
	 */
	public void setPidHex(String pidHex) {
		this.pidHex = pidHex;
	}

	/**
	 * Gets the pid unit.
	 *
	 * @return the pid unit
	 */
	public String getPidUnit() {
		return pidUnit;
	}

	/**
	 * Sets the pid unit.
	 *
	 * @param pidUnit the new pid unit
	 */
	public void setPidUnit(String pidUnit) {
		this.pidUnit = pidUnit;
	}

	/**
	 * Gets the pid eval.
	 *
	 * @return the pid eval
	 */
	public EVALS getPidEval() {
		return pidEval;
	}

	/**
	 * Sets the pid eval.
	 *
	 * @param pidEval the new pid eval
	 */
	public void setPidEval(String pidEval) {

		//If this PID response if calculated by a formula
		if (pidEval.toString().equals("FORMULA")) {
			this.pidEval = EVALS.FORMULA;
		}

		//Else if this PID response is unencoded
		else if (pidEval.toString().equals("RAW")) {
			this.pidEval = EVALS.RAW;
		}

		//Else if this PID response is bit encoded
		else if (pidEval.toString().equals("BIT_ENCODED")) {
			this.pidEval = EVALS.BIT_ENCODED;
		}

		//Else if this PID response is byte encoded
		else if (pidEval.toString().equals("BYTE_ENCODED")) {
			this.pidEval = EVALS.BYTE_ENCODED;
		}

		//Else if this PID response is a character string
		else if (pidEval.toString().equals("CHAR_STRING")) {
			this.pidEval = EVALS.CHAR_STRING;
		}
	}

	/**
	 * Gets the pid name.
	 *
	 * @return the pid name
	 */
	public String getPidName() {
		return pidName;
	}

	/**
	 * Sets the pid name.
	 *
	 * @param pidName the new pid name
	 */
	@SuppressWarnings("unused")
	private void setPidName(String pidName) {
		this.pidName = pidName;
	}

	/**
	 * Sets the pid return.
	 *
	 * @param pidReturn the new pid return
	 */
	@SuppressWarnings("unused")
	private void setPidReturn(int pidReturn) {
		this.pidReturn = pidReturn;
	}

	/**
	 * Gets the pid return.
	 *
	 * @return the pid return
	 */
	public int getPidReturn() {
		return pidReturn;
	}

	/**
	 * Sets the supported.
	 *
	 * @param supported the new supported
	 */
	public void setSupported(boolean supported) {
		this.supported = supported;
	}

	/**
	 * Checks if is supported.
	 *
	 * @return true, if is supported
	 */
	public boolean isSupported() {
		return supported;
	}

	/**
	 * Sets the pid value.
	 *
	 * @param pidValue the new pid value
	 */
	@SuppressWarnings("unused")
	private void setPidValue(int pidValue) {
		this.pidValue = pidValue;
	}

	/**
	 * Gets the pid value.
	 *
	 * @return the pid value
	 */
	public int getPidValue() {
		return pidValue;
	}

	/**
	 * Sets the parent mode.
	 *
	 * @param parentMode the new parent mode
	 */
	@SuppressWarnings("unused")
	private void setParentMode(String parentMode) {
		this.parentMode = parentMode;
	}

	/**
	 * Gets the parent mode.
	 *
	 * @return the parent mode
	 */
	public String getParentMode() {
		return parentMode;
	}

	/**
	 * Gets the pid formula.
	 *
	 * @return the pid formula
	 */
	public String getPidFormula() {
		if (this.getPidEval() == EVALS.FORMULA) {
			return pidFormula;
		}
		else {
			return null;
		}

	}

	/**
	 * Sets the pid formula.
	 *
	 * @param pidFormula the new pid formula
	 */
	public void setPidFormula(String pidFormula) {
		if (this.getPidEval() == EVALS.FORMULA) {
			this.pidFormula = pidFormula;
		}
	}

	/**
	 * Gets the bit encoded map.
	 *
	 * @return the bit encoded map
	 */
	public ConcurrentHashMap<Integer, String> getBitEncodedMap() {
		return bitEncodedMap;
	}

	/**
	 * Sets the bit encoded map.
	 *
	 * @param bitEncodedMap the bit encoded map
	 */
	public void setBitEncodedMap(ConcurrentHashMap<Integer, String> bitEncodedMap) {
		this.bitEncodedMap = bitEncodedMap;
	}

	/**
	 * Adds the bit encoding.
	 *
	 * @param index the index
	 * @param value the value
	 */
	public void addBitEncoding(String index, String value) {
		if (getBitEncodedMap() != null) {
			getBitEncodedMap().put(Integer.parseInt(index), value);
		}
	}

	/**
	 * Gets the bit encoding.
	 *
	 * @param index the index
	 * @return the bit encoding
	 */
	public String getBitEncoding(int index) {
		if (getBitEncodedMap().containsKey(index)) {
			return getBitEncodedMap().get(index);
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the byte encoded map.
	 *
	 * @return the byte encoded map
	 */
	public ConcurrentHashMap<Integer, String> getByteEncodedMap() {
		return byteEncodedMap;
	}

	/**
	 * Sets the byte encoded map.
	 *
	 * @param byteEncodedMap the byte encoded map
	 */
	public void setByteEncodedMap(ConcurrentHashMap<Integer, String> byteEncodedMap) {
		this.byteEncodedMap = byteEncodedMap;
	}

	/**
	 * Adds the byte encoding.
	 *
	 * @param byteValue the byte value
	 * @param value the value
	 */
	public void addByteEncoding(String byteValue, String value) {
		if (getByteEncodedMap() != null) {
			getByteEncodedMap().put(Integer.parseInt(byteValue), value);
		}
	}

	/**
	 * Gets the byte encoding.
	 *
	 * @param index the index
	 * @return the byte encoding
	 */
	public String getByteEncoding(int index) {
		if (getByteEncodedMap().containsKey(index)) {
			return getByteEncodedMap().get(index);
		}
		else {
			return null;
		}
	}

	/**
	 * Checks if is pollable.
	 *
	 * @return true, if is pollable
	 */
	public boolean isPollable() {
		return pollable;
	}

	/**
	 * Sets the parent mode value.
	 *
	 * @param parentModeValue the new parent mode value
	 */
	public void setParentModeValue(int parentModeValue) {
		this.parentModeValue = parentModeValue;
	}

	/**
	 * Gets the parent mode value.
	 *
	 * @return the parent mode value
	 */
	public int getParentModeValue() {
		return parentModeValue;
	}

	/**
	 * Sets the decimal format.
	 *
	 * @param patternFormat the new decimal format
	 */
	public void setDecimalFormat(String patternFormat) {
		this.decimalFormat.applyPattern(patternFormat);
	}

	/**
	 * Gets the decimal format.
	 *
	 * @return the decimal format
	 */
	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	/**
	 * Sets the pid compiled formula.
	 *
	 * @param pidCompiledFormula the new pid compiled formula
	 */
	public void setPidCompiledFormula(OBDFormulas pidCompiledFormula) {
		this.pidCompiledFormula = pidCompiledFormula;
	}
	
	/**
	 * Sets the pid compiled formula.
	 *
	 * @param pidCompiledFormula the new pid compiled formula
	 */
	public void setPidCompiledFormula(String pidCompiledFormula) {
		
		if(pidCompiledFormula.equals("FORMULA_1")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_1;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_2")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_2;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_3")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_3;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_4")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_4;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_5")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_5;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_6")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_6;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_7")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_7;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_8")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_8;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_9")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_9;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_10")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_10;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_11")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_11;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_12")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_12;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_13")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_13;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_14")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_14;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_15")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_15;
		}
		
		else if(pidCompiledFormula.equals("FORMULA_16")) {
			this.pidCompiledFormula = OBDFormulas.FORMULA_16;
		}
		else {
			this.pidCompiledFormula = OBDFormulas.FORMULA_1;
		}

	}

	/**
	 * Gets the pid compiled formula.
	 *
	 * @return the pid compiled formula
	 */
	public OBDFormulas getPidCompiledFormula() {
		return pidCompiledFormula;
	}

	/**
	 * Sets the collected.
	 *
	 * @param collected the new collected
	 */
	public void setCollected(boolean collected) {
		this.collected = collected;
	}

	/**
	 * Checks if is collected.
	 *
	 * @return true, if is collected
	 */
	public boolean isCollected() {
		return collected;
	}

	/**
	 * Sets the displayed.
	 *
	 * @param displayed the new displayed
	 */
	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	/**
	 * Checks if is displayed.
	 *
	 * @return true, if is displayed
	 */
	public boolean isDisplayed() {
		return displayed;
	}

}
