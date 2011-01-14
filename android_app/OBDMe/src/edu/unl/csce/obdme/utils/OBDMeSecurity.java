package edu.unl.csce.obdme.utils;

import java.security.MessageDigest;

public abstract class OBDMeSecurity {
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
