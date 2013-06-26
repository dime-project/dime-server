package eu.dime.ps.communications.util;

import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.junit.Test;

import eu.dime.ps.communications.utils.Base64encoding;

public class Base64encodingTest extends Assert {

	private static final String PLAIN = "http://www.google.com";
	private static final String ENCODED = "aHR0cDovL3d3dy5nb29nbGUuY29t";

	@Test
	public void testEncoding() throws UnsupportedEncodingException {
		String encoded = Base64encoding.encode(PLAIN);
		assertEquals(ENCODED, encoded);
	}
	
	@Test
	public void testDecoding() throws UnsupportedEncodingException {
		String decoded = Base64encoding.decode("aHR0cDovL3d3dy5nb29nbGUuY29t");
		assertEquals(PLAIN, decoded);
	}

}
