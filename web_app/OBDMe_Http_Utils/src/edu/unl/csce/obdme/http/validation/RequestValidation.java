package edu.unl.csce.obdme.http.validation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import edu.unl.csce.obdme.http.encryption.EncryptionUtils;
import edu.unl.csce.obdme.http.request.ParamConstants;

public class RequestValidation {
	
	public static boolean isValidHttpRequest(Map<String, String> requestParams) {
		
		//Need to have a signature		
		if (!requestParams.containsKey(ParamConstants.OBDME_REQUEST_SIGNATURE_PARAM)) {
			return false;
		}		
		
		MessageDigest md;
		
		try {
			md = MessageDigest.getInstance(EncryptionUtils.SHA);
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			return false;
		}
		
		for (String param : requestParams.keySet()) {
			if (!param.equals(ParamConstants.OBDME_REQUEST_SIGNATURE_PARAM)) {
				md.update(param.getBytes());
				md.update(requestParams.get(param).getBytes());
			}			
		}
		
		md.update(EncryptionUtils.getOBDMeSaltBytes());
		
		String computedSignature = EncryptionUtils.byteArrayToHexString(md.digest());
		String sentSignature = requestParams.get(ParamConstants.OBDME_REQUEST_SIGNATURE_PARAM);	
		
		return computedSignature.equals(sentSignature);
	}
}
