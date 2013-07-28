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

package eu.dime.ps.storage.jfix.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;

/**
 * Utilitities to ease the handling of java.io.File.
 */
public class Files {

	/**
	 * Creates a new file (and missing parent folders) if given file doesn't
	 * exist. If something goes wrong, a thrown IOException is wrapped within a
	 * RuntimeException.
	 */
	public static void createMissing(File file) {
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (java.io.IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Creates a temporary directory. If something goes wrong, a thrown
	 * IOException is wrapped within a RuntimeException.
	 */
	public static File createTempDirectory() {
		try {
			File directory = File.createTempFile("jfix-util-", "");
			directory.delete();
			directory.mkdirs();
			return directory;
		} catch (java.io.IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Renames given source to target. If File#renameTo(File) fails, the file
	 * content is copied from source to target.
	 */
	public static void rename(File source, File target) {
		target.delete();
		if (!source.renameTo(target)) {
			copy(source, target);
			source.delete();
		}
	}

	/**
	 * Copies source to target via NIO API. If source is null or doesn't exist,
	 * nothing happens.
	 */
	public static void copy(File source, File target) {
		if (source != null && source.exists()) {
			try {
				target.createNewFile();
				FileChannel sourceChannel = new FileInputStream(source)
						.getChannel();
				FileChannel targetChannel = new FileOutputStream(target)
						.getChannel();
				targetChannel.transferFrom(sourceChannel, 0,
						sourceChannel.size());
				sourceChannel.close();
				targetChannel.close();
			} catch (IOException e) {
				throw new RuntimeException(String.format(
						"Copy file from '%s' to '%s' failed (%s)",
						source.getAbsolutePath(), target.getAbsolutePath(),
						e.getMessage()), e);
			}
		}
	}

	/**
	 * Returns UTF-8 encoded Reader for given file.
	 */
	public static Reader newReader(File file) throws IOException {
		try {
			return new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Returns UTF-8 encoded Writer for given file.
	 */
	public static Writer newWriter(File file) throws IOException {
		try {
			return new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
