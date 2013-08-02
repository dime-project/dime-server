/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

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
