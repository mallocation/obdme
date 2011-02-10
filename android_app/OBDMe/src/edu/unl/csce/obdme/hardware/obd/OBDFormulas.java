package edu.unl.csce.obdme.hardware.obd;

import java.util.List;

/**
 * The Enumerator MODES.
 */
public enum OBDFormulas {
	
	/** The FORMUL a_1. */
	FORMULA_1,
	
	/** The FORMUL a_2. */
	FORMULA_2,
	
	/** The FORMUL a_3. */
	FORMULA_3,
	
	/** The FORMUL a_4. */
	FORMULA_4,
	
	/** The FORMUL a_5. */
	FORMULA_5,
	
	/** The FORMUL a_6. */
	FORMULA_6,
	
	/** The FORMUL a_7. */
	FORMULA_7,
	
	/** The FORMUL a_8. */
	FORMULA_8,
	
	/** The FORMUL a_9. */
	FORMULA_9,
	
	/** The FORMUL a_10. */
	FORMULA_10,
	
	/** The FORMUL a_11. */
	FORMULA_11,
	
	/** The FORMUL a_12. */
	FORMULA_12,
	
	/** The FORMUL a_13. */
	FORMULA_13,
	
	/** The FORMUL a_14. */
	FORMULA_14,
	
	/** The FORMUL a_15. */
	FORMULA_15,
	
	/** The FORMUL a_16. */
	FORMULA_16;
	
	/**
	 * Formula eval.
	 *
	 * @param bytes the bytes
	 * @return the double
	 * @throws Exception the exception
	 */
	double formulaEval(List<String> bytes) throws Exception{
		
		double A;
		double B;
		
        switch(this) {
        	case FORMULA_1:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		return (A* 100.0) / 255.0;
        		
        	case FORMULA_2:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		return A - 40.0;
        		
        	case FORMULA_3:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		return (A - 128.0) * (100.0 / 128.0);
        		
        	case FORMULA_4:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		return A * 3.0;
        		
        	case FORMULA_5:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		return A;
        		
        	case FORMULA_6:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return ((A * 256.0) + B) / 4.0;
        		
        	case FORMULA_7:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		return (A / 2.0) - 64.0;
        		
        	case FORMULA_8:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return ((A * 256.0) + B) / 100.0;
        		
        	case FORMULA_9:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return (A * 256.0) + B;
        		
        	case FORMULA_10:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return (((A * 256.0) + B) * 10.0) / 128.0;
        		
        	case FORMULA_11:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return ((A * 256.0) + B) * 10.0;
        		
        	case FORMULA_12:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		return ( 100.0 * A) / 255.0;
        		
        	case FORMULA_13:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return (((A * 256.0) + B) / 10.0) - 40.0;
        		
        	case FORMULA_14:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return ((A * 256.0) + B) / 1000.0;
        		
        	case FORMULA_15:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return ((A * 256.0) + B) * (100.0 / 255.0);
        		
        	case FORMULA_16:
        		A = (double)Integer.parseInt(bytes.get(0), 16);
        		B = (double)Integer.parseInt(bytes.get(1), 16);
        		return ((A * 256.0) + B) / 32768.0;
        }
        throw new AssertionError("Unknown enumeration: " + this);
    }

}
