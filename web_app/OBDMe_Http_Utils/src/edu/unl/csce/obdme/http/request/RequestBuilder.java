package edu.unl.csce.obdme.http.request;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import edu.unl.csce.obdme.encryption.EncryptionUtils;

public class RequestBuilder {
	
	public static String computeRequestSignature(Map<String, String> requestParams) {
		MessageDigest md;
		
		try {
			md = MessageDigest.getInstance(EncryptionUtils.SHA);
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			return "";
		}		
		
		for (String param : requestParams.keySet()) {
			md.update(param.getBytes());
			md.update(requestParams.get(param).getBytes());
		}
		
		md.update(EncryptionUtils.getOBDMeSaltBytes());
		
		return EncryptionUtils.byteArrayToHexString(md.digest());
	}
	

}
