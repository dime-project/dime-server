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
package eu.dime.ps.gateway.util;

/**
 * Decoder of usernames used in HTTP Basic Authorization.
 */
public class UsernameDecoder {

	/**
	 * Decodes a username which unsafe characters have been replaced by
	 * {@link UsernameEncoder#encode(String)}.
	 * 
	 * <p>For example, an encoded username such as <i>myuniqueid+example.org+8080</i>
	 * containing a domain and port, would be decoded to <i>myuniqueid@example.org:8080</i></p>
	 * 
	 * @param username encoded username
	 * @return decoded username with the original unsafe characters
	 * @see UsernameEncoder
	 */
	public static String decode(String username) {
		if (username == null) {
			return null;
		}
		
		String decoded = username.replaceFirst("\\+", "@").replaceFirst("\\+", ":");
		if (decoded.contains("+")) {
			throw new IllegalArgumentException("username '" + username + "' contains too many + characters.");
		}
		
		return decoded;
	}
	
}
