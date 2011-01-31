package edu.unl.csce.obdme.http.request;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.unl.csce.obdme.encryption.EncryptionUtils;

public class RequestBuilder {
	
	public static String computeRequestSignature(String domain, String path, Map<String, String> requestParams, List<String> excludeParams) {
		MessageDigest md;
		
		try {
			md = MessageDigest.getInstance(EncryptionUtils.MD5);
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			return null;
		}
		
		/* First digest the domain/host i.e. obdme.com */
		md.update(domain.getBytes());
		
		
		/* Second digest the path i.e. /users/farmboy30@gmail.com */
		md.update(path.getBytes());
		
		
		/* Third digest all of the request parameters */		
		if (requestParams != null) {
			List<String> sortedParams = new ArrayList<String>(requestParams.keySet());
			Collections.sort(sortedParams, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}				
			});			
			for (String param : sortedParams) {
				if (excludeParams == null || !excludeParams.contains(param)) {
					md.update(param.getBytes());
					md.update(requestParams.get(param).getBytes());
				}				
			}	
		}
		
		/* Finally digest some special salt. */
		md.update(EncryptionUtils.getOBDMeSaltBytes());
		
		/* Return the digest as a hex string */
		return EncryptionUtils.byteArrayToHexString(md.digest());
	}	

}
