/*
    Copyright (C) 2010 maik.jablonski@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.dime.ps.storage.jfix.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Common utility methods to encrypt/decrypt numbers and strings.
 */
public class Crypts {

	private static final String BASE_62_DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final char[] HEX_CHARACTERS = { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Returns true if password is longer than 8 characters and contains lower
	 * and uppercase letters, digits and symbols.
	 */
	public static boolean isStrongPassword(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}
		if (!password.matches(".*[a-z]+.*")) {
			return false;
		}
		if (!password.matches(".*[A-Z]+.*")) {
			return false;
		}
		if (!password.matches(".*[\\d]+.*")) {
			return false;
		}
		if (!password.matches(".*[^a-zA-Z\\d]+.*")) {
			return false;
		}
		return true;
	}

	/**
	 * Encrypts griven string with given pass-phrase with DES.
	 */
	public static String cipher(String msg, byte[] passPhrase) {
		try {
			KeySpec keySpec = new DESKeySpec(passPhrase);
			SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(
					keySpec);
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return toHexString(cipher.doFinal(msg.getBytes()));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Decrypts griven string with given pass-phrase with DES.
	 */
	public static String decipher(String msg, byte[] passPhrase) {
		try {
			KeySpec keySpec = new DESKeySpec(passPhrase);
			SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(
					keySpec);
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(fromHexString(msg)));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Creates a md5 encoded in base64 for given input.
	 */
	public static String md5(byte[] input) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(input);
			return eu.dime.ps.storage.jfix.util.Base64.encodeBytes(md5.digest());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a random pass-phrase suitable for DES-encryption.
	 */
	public static byte[] createRandomPassPhrase() {
		byte[] passPhrase = String.valueOf(Math.random()).getBytes();
		if (passPhrase.length < 8) {
			return createRandomPassPhrase();
		}
		return passPhrase;
	}

	/**
	 * Converts given byte-array into hexadecimal.
	 */
	public static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_CHARACTERS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_CHARACTERS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * Converts given hexadecimal into byte-array.
	 */
	public static byte[] fromHexString(String s) {
		int stringLength = s.length();
		if ((stringLength % 2) != 0) {
			throw new IllegalArgumentException(
					"Even number of characters required");
		}
		byte[] b = new byte[stringLength / 2];
		for (int i = 0, j = 0; i < stringLength; i += 2, j++) {
			int high = charToNibble(s.charAt(i));
			int low = charToNibble(s.charAt(i + 1));
			b[j] = (byte) ((high << 4) | low);
		}
		return b;
	}

	private static int charToNibble(char c) {
		if ('0' <= c && c <= '9') {
			return c - '0';
		} else if ('a' <= c && c <= 'f') {
			return c - 'a' + 0xa;
		} else if ('A' <= c && c <= 'F') {
			return c - 'A' + 0xa;
		} else {
			throw new IllegalArgumentException("Invalid hex character: " + c);
		}
	}

	/**
	 * Decodes basic HTTP-Authorization-Header (encoded user:password) by
	 * applying BASE64Decoder to given string.
	 */
	public static String decodeBasicAuthorization(String authorizationHeader) {
		try {
			if (authorizationHeader == null) {
				return null;
			}
			String userpassEncoded = authorizationHeader.substring(6);
			return new String(Base64.decode(userpassEncoded));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Converts given decimalNumber into base62-encoded string.
	 */
	public static String toBase62(int decimalNumber) {
		String result = decimalNumber == 0 ? "0" : "";
		while (decimalNumber != 0) {
			int mod = decimalNumber % 62;
			result = BASE_62_DIGITS.substring(mod, mod + 1) + result;
			decimalNumber = decimalNumber / 62;
		}
		return result;
	}

	/**
	 * Converts given base62Number (encoded as string) to integer.
	 */
	public static int fromBase62(String base62Number) {
		int result = 0;
		for (int pos = base62Number.length(), multiplier = 1; pos > 0; pos--) {
			result += BASE_62_DIGITS.indexOf(base62Number.substring(pos - 1,
					pos)) * multiplier;
			multiplier *= 62;
		}
		return result;

	}
}
