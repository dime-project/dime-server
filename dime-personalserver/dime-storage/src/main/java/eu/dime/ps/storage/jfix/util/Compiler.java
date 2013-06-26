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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;

/**
 * A Java-Compiler which can compile Java-Source-Files on the fly and load them
 * via an custom ClassLoader at runtime. If a source-file is changed, it is
 * recompiled and the class gets reloaded automatically.
 * 
 * - To add an file to the compiler, use {@link #add(File sourceFile)}.
 * 
 * - To create an instance of the compiled class, use
 * {@link #newInstance(String classname)}.
 * 
 * A shortcut for add/newInstance is {@link #eval(File)}.
 */
public class Compiler {

	private static final Pattern CLASSNAME_PATTERN = Pattern.compile(
			"public\\s+class\\s+([^\\s]+)", Pattern.DOTALL | Pattern.MULTILINE);

	public static void main(String[] args) {

	}

	private final List<File> sourceFiles = new CopyOnWriteArrayList();
	private ClassLoader runtimeClassLoader;

	public Compiler() {
	}

	/**
	 * Add given file to the compiler for compilation.
	 */
	public void add(File file) {
		if (!sourceFiles.contains(file)) {
			for (File sourceFile : new ArrayList<File>(sourceFiles)) {
				if (sourceFile.getName().equals(file.getName())) {
					sourceFiles.remove(sourceFile);
					break;
				}
			}
			sourceFiles.add(file);
			reset();
		}
	}

	/**
	 * Remove given file from the compiler.
	 */
	public void remove(File file) {
		if (sourceFiles.contains(file)) {
			sourceFiles.remove(file);
			reset();
		}
	}

	/**
	 * Check if compiler contains given file already.
	 */
	public boolean contains(File file) {
		return sourceFiles.contains(file);
	}

	/**
	 * Add given file to compiler, compile it and create a new instance
	 * directly.
	 */
	public Object eval(File file) {
		add(file);
		return newInstance(file.getName().replace(".java", ""));
	}

	/**
	 * Create java file for given source, compile it and return a new instance.
	 */
	public Object eval(String source) {
		try {
			Matcher matcher = CLASSNAME_PATTERN.matcher(source);
			if (matcher.find()) {
				File tmpDirectory = Files.createTempDirectory();
				tmpDirectory.deleteOnExit();
				File sourceFile = new File(tmpDirectory, matcher.group(1)
						+ ".java");
				sourceFile.deleteOnExit();
				FileUtils.writeStringToFile(sourceFile, source, "UTF-8");
				return eval(sourceFile);
			} else {
				return new RuntimeException("No class declaration.");
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Return new instance of given classname.
	 */
	public Object newInstance(String classname) {
		try {
			return loadClass(classname).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Return class-object for given classname.
	 */
	public Class loadClass(String classname) throws ClassNotFoundException {
		if (isSourceFileModified()) {
			compile();
			reset();
		}
		if (runtimeClassLoader == null) {
			reload();
		}
		return runtimeClassLoader.loadClass(classname);
	}

	private boolean isSourceFileModified() {
		for (File sourceFile : sourceFiles) {
			File classFile = new File(sourceFile.getAbsolutePath().replace(
					".java", ".class"));
			classFile.deleteOnExit();
			if (classFile.lastModified() < sourceFile.lastModified()) {
				return true;
			}
		}
		return false;
	}

	private void compile() {
		String[] args = new String[2 + sourceFiles.size()];
		args[0] = "-classpath";
		args[1] = buildClasspath();
		for (int i = 0; i < sourceFiles.size(); i++) {
			args[2 + i] = sourceFiles.get(i).getAbsolutePath();
		}
		ByteArrayOutputStream errors = new ByteArrayOutputStream();
		if (ToolProvider.getSystemJavaCompiler().run(null, null, errors, args) != 0) {
			throw new RuntimeException(errors.toString());
		}
	}

	private void reload() {
		List<URL> classLoaderDirectories = new ArrayList();
		for (int i = 0; i < sourceFiles.size(); i++) {
			try {
				URL sourceURL = sourceFiles.get(i).getParentFile().toURI()
						.toURL();
				if (!classLoaderDirectories.contains(sourceURL)) {
					classLoaderDirectories.add(sourceURL);
				}
			} catch (MalformedURLException e) {
				// should not happen
			}
		}
		runtimeClassLoader = new URLClassLoader(
				classLoaderDirectories.toArray(new URL[] {}), Thread
						.currentThread().getContextClassLoader());
	}

	private void reset() {
		runtimeClassLoader = null;
	}

	private String buildClasspath() {
		StringBuilder sb = new StringBuilder();
		for (ClassLoader classloader = Thread.currentThread()
				.getContextClassLoader(); classloader != null; classloader = classloader
				.getParent()) {
			if (classloader instanceof URLClassLoader) {
				for (URL url : ((URLClassLoader) classloader).getURLs()) {
					if (sb.length() > 0) {
						sb.append(File.pathSeparatorChar);
					}
					try {
						// URLDecoder needed to unquote %20
						// see:
						// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4466485
						sb.append(URLDecoder.decode(url.getFile(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// should not happen
					}
				}
			}
		}
		return sb.toString();
	}

	protected void finalize() throws Throwable {
		try {
			List<File> filesToDelete = new ArrayList<File>();
			for (File file : sourceFiles) {
				File parentDirectory = file.getParentFile();
				for (File fileToDelete : parentDirectory.listFiles()) {
					filesToDelete.add(fileToDelete);
				}
				filesToDelete.add(parentDirectory);
			}
			for (File file : filesToDelete) {
				file.delete();
			}
		} finally {
			super.finalize();
		}
	}
}
