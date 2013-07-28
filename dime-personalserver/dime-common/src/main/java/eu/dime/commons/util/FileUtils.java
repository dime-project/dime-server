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

package eu.dime.commons.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contains convenient methods to calculate file checksums/hashes, and
 * converting them to readable strings.
 * 
 * @author Ismael Rivera (ismael.rivera@deri.org)
 */
public class FileUtils {

	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	/**
	 * Fast method to convert a byte array to a readable String.
	 * 
	 * @param buf
	 * @return
	 */
	public static String asHex(byte[] buf) {
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}

	/**
	 * Calculates the hash of a file using a given algorithm or hash function.
	 * 
	 * @param filePath file to calculate its hash
	 * @param algorithm cryptographic hash function
	 * @return the hash of the file
	 * @throws IOException if file not found or error while reading file
	 */
	public static String doHash(MessageDigest algorithm, String filePath) throws IOException {
		return doHash(algorithm, new FileInputStream(filePath));
	}

	public static String doHash(MessageDigest algorithm, InputStream stream) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(stream);
		DigestInputStream dis = new DigestInputStream(bis, algorithm);

		// read the file and update the hash calculation
		while (dis.read() != -1);

		// get the hash value as byte array
		byte[] hash = algorithm.digest();

		return asHex(hash);
	}
	
	/**
	 * Calculates a hash of a file using the SHA-1 cryptographic hash function.
	 * 
	 * @param filePath file to calculate its hash 
	 * @return the hash of the file
	 * @throws IOException if file not found or error while reading file
	 */
	public static String doSHA1Hash(String filePath) throws IOException {
		try {
			return doHash(MessageDigest.getInstance("SHA1"), filePath);
		} catch (NoSuchAlgorithmException e) {
			// this shouldn't happen, we throw up an unchecked exception
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Same as {@link #doSHA1Hash(String)}, except the URI of the file 
	 * is accepted, i.e.: file:/Users/example/myfile.txt
	 */
	public static String doSHA1Hash(URI fileUri) throws IOException {
		try {
			File file = new File(fileUri);
			return doHash(MessageDigest.getInstance("SHA1"), new FileInputStream(file));
		} catch (NoSuchAlgorithmException e) {
			// this shouldn't happen, we throw up an unchecked exception
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Same as {@link #doSHA1Hash(String)}, except the input stream is passed.
	 */
	public static String doSHA1Hash(InputStream stream) throws IOException {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("SHA1");
			BufferedInputStream bis = new BufferedInputStream(stream);
			DigestInputStream dis = new DigestInputStream(bis, algorithm);
	
			// read the file and update the hash calculation
			while (dis.read() != -1);
	
			// get the hash value as byte array
			byte[] hash = algorithm.digest();
	
			return asHex(hash);
		} catch (NoSuchAlgorithmException e) {
			// this shouldn't happen, we throw up an unchecked exception
			throw new RuntimeException(e);
		} 
	}

	/**
	 * Calculates a hash of a file using the MD5 cryptographic hash function.
	 *  
	 * @param filePath file to calculate its hash 
	 * @return the hash of the file
	 * @throws IOException if file not found or error while reading file
	 */
	public static String doMD5Hash(String filePath) throws IOException {
		try {
			return doHash(MessageDigest.getInstance("MD5"), filePath);
		} catch (NoSuchAlgorithmException e) {
			// this shouldn't happen, we throw up an unchecked exception
			throw new RuntimeException(e);
		}
	}

	/**
	 * Compares if two input streams provide exactly the same data.
	 * @param i1
	 * @param i2
	 * @return
	 * @throws IOException
	 */
	public static boolean equals(InputStream i1, InputStream i2) throws IOException {
	    ReadableByteChannel ch1 = Channels.newChannel(i1);
	    ReadableByteChannel ch2 = Channels.newChannel(i2);

	    ByteBuffer buf1 = ByteBuffer.allocateDirect(1024);
	    ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);

	    try {
	        while (true) {
	            int n1 = ch1.read(buf1);
	            int n2 = ch2.read(buf2);

	            if (n1 == -1 || n2 == -1) return n1 == n2;

	            buf1.flip();
	            buf2.flip();

	            for (int i = 0; i < Math.min(n1, n2); i++)
	                if (buf1.get() != buf2.get())
	                    return false;

	            buf1.compact();
	            buf2.compact();
	        }

	    } finally {
	        if (i1 != null) i1.close();
	        if (i2 != null) i2.close();
	    }
	}

}
