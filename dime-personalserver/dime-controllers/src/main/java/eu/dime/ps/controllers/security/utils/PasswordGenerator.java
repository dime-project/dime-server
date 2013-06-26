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
