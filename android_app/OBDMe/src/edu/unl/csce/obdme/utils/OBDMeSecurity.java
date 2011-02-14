package edu.unl.csce.obdme.utils;

import java.security.MessageDigest;

/**
 * The Class OBDMeSecurity.
 */
public abstract class OBDMeSecurity {
	
	/**
	 * Encrypt.
	 *
	 * @param plaintext the plaintext
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String encrypt(String plaintext) throws Exception {
		MessageDigest msgDigest = null;
		msgDigest = MessageDigest.getInstance("SHA");
		msgDigest.update(plaintext.getBytes("UTF-8"));
		byte mdResult[] = msgDigest.digest(); //step 4
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<mdResult.length;i++) {
			String hex = Integer.toHexString(0xFF & mdResult[i]);
			if(hex.length() == 1) {
				hexString.append('0');
			}
			 hexString.append(hex);
		}
		String mdString = hexString.toString();
		return mdString;
	}
}
