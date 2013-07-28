/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.controllers.security.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

/**
 * Utilities class for generating random passwords.
 * 
 * @author Ismael Rivera
 */
public class PasswordGenerator {

	/**
	 * Creates an alphanumeric password between 15 and 20 characters long.
	 * @return the generated password
	 */
	public static String getRandomPassword() {
		return RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(6) + 15);
	}
	
}
