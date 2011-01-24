package edu.unl.csce.obdme.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtils {
	
	private static final String OBDME_ENCRYPTION_SALT = "[!@#$%]doYOUrememberWHENtwentyONEyearsWASold[^&*()]";
	private static final byte[] OBDME_ENCRYPTION_SALT_BYTES = OBDME_ENCRYPTION_SALT.getBytes();
	private static final String HEX_CHARS = "0123456789ABCDEF";
	
	public static final String MD5 = "MD5";
	public static final String SHA = "SHA";
	public static final String SHA_256 = "SHA-256";
	public static final String SHA_384 = "SHA-384";
	public static final String SHA_512 = "SHA-512";
	
	public static byte[] getOBDMeSaltBytes() {
		return OBDME_ENCRYPTION_SALT_BYTES;
	}	
	
	public static String encryptString(String valueToEncrypt, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest md = null;
		md = MessageDigest.getInstance(algorithm);		
		md.update(valueToEncrypt.getBytes());
		md.update(OBDME_ENCRYPTION_SALT_BYTES);
		return byteArrayToHexString(md.digest());
	}	
	
	public static String encryptPassword(String password) throws NoSuchAlgorithmException {
		return encryptString(password, SHA_256);		
	}
	
	public static String byteArrayToHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (byte byteToConvert : bytes) {
			sb.append(HEX_CHARS.charAt((byteToConvert & 0xf0) >> 4));
			sb.append(HEX_CHARS.charAt(byteToConvert & 0x0f));
		}
		return sb.toString();		
	}
}
