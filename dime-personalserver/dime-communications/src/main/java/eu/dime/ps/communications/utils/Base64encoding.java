package eu.dime.ps.communications.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class Base64encoding {

	public static String encode(String id) throws UnsupportedEncodingException {
		return Base64.encodeBase64URLSafeString(id.getBytes("UTF-8"));
	}

	public static String decode(String encodedId) throws UnsupportedEncodingException {
		byte[] bytes = Base64.decodeBase64(encodedId);
		String decodedDbId = new String(bytes, "UTF-8");
		return decodedDbId;
	}

}
