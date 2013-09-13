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
 * Encoder of usernames to be used for HTTP Basic Authorization.
 */
public class UsernameEncoder {

	/**
	 * <p>Replaces unsafe characters from the username to not conflict with the passed authorization
	 * information passed in the URL (https://username:password@www.example.com/path)</p>
	 * 
	 * <p>For example, a username consisting of an id, a domain and a port such as 
	 * <i>myuniqueid@example.org:8080</i> would be encoded to <i>myuniqueid+example.org+8080</i></p>
	 * 
	 * @param username username to encode or replace unsafe characters
	 * @return the username where all @ and : are replaced by +
	 * @see UsernameDecoder
	 */
	public static String encode(String username) {
		return username == null ? null : username.replace('@', '+').replace(':', '+');
	}
	
}
