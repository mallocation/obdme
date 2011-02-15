package edu.unl.csce.obdme.utils;


/**
 * The Class UnitConversion.
 */
public class UnitConversion {

	/**
	 * Convert to english.
	 *
	 * @param unit the unit
	 * @param value the value
	 * @return the double
	 */
	public static Double convertToEnglish(String unit, Double value) {

		if (unit.equals("C")) {
			return cToF(value);
		}
		else if (unit.equals("kPa")) {
			return kpaToPsi(value);
		}
		else if (unit.equals("km/h")) {
			return kphToMph(value);
		}
		else if (unit.equals("km")) {
			return kmToMi(value);
		}
		else if (unit.equals("Pa")) {
			return paToPsi(value);
		}
		else {
			return value;
		}

	}
	
	/**
	 * Gets the english unit.
	 *
	 * @param unit the unit
	 * @return the english unit
	 */
	public static String getEnglishUnit(String unit) {

		if (unit.equals("\u00B0C")) {
			return "\u00B0F";
		}
		else if (unit.equals("kPa")) {
			return "psi";
		}
		else if (unit.equals("km/h")) {
			return "mph";
		}
		else if (unit.equals("km")) {
			return "mi";
		}
		else if (unit.equals("Pa")) {
			return "psi";
		}
		else {
			return unit;
		}

	}

	/**
	 * C to f.
	 *
	 * @param value the value
	 * @return the double
	 */
	public static Double cToF(Double value) {
		return (9.0 / 5.0 ) * value + 32.0;
	}

	/**
	 * Kpa to psi.
	 *
	 * @param value the value
	 * @return the double
	 */
	public static Double kpaToPsi(Double value) {
		return value * 0.14503773773020923;
	}

	/**
	 * Kph to mph.
	 *
	 * @param value the value
	 * @return the double
	 */
	public static Double kphToMph(Double value) {
		return value * 0.621371192;
	}
	
	/**
	 * Km to mi.
	 *
	 * @param value the value
	 * @return the double
	 */
	public static Double kmToMi(Double value) {
		return value * 0.621371192;
	}
	
	/**
	 * Pa to psi.
	 *
	 * @param value the value
	 * @return the double
	 */
	public static Double paToPsi(Double value) {
		return value * 0.00014503773773020923;
	}
}
