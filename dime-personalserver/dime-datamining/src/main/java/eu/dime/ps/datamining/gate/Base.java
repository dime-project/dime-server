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

package eu.dime.ps.datamining.gate;

import eu.dime.ps.datamining.util.JarUtils;
import gate.CorpusController;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Base {

	private static final Logger logger = LoggerFactory.getLogger(Base.class);

	private static final String GATE_HOME = "gate";
	private static final String GATE_PLUGINS_HOME = "plugins";
	private static final String GATE_USER_CONFIG = "gate-user.xml";
	private static final String GATE_SITE_CONFIG = "gate-site.xml";
	private static final String GATE_SESSION = "gate.session";
	
	private static File GATE_DIRECTORY;
	static {
		initGate(); // initialize GATE
	}
	
	private static final Map<String, CorpusController> applicationRegistry = new HashMap<String, CorpusController>();

	private static boolean gateInited = false;

	private static void initGate() {
		if (!gateInited) {
			try {
				// prepare GATE directory
				File sourceDir = new File(Base.class.getProtectionDomain().getCodeSource().getLocation().toURI());
				if (sourceDir.getName().endsWith(".jar")) {
					// will extract JAR contents to a temporary file (GATE needs a regular directory with all the resources)
					BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceDir));
					File destination = createTempDirectory();
					logger.info("Extracting JAR contents to temporary directory: "+destination.toURI());
					JarUtils.unjar(in, destination);
					GATE_DIRECTORY = new File(destination, GATE_HOME);
				} else {
					GATE_DIRECTORY = new File(new File(Base.class.getProtectionDomain().getCodeSource().getLocation().toURI()), GATE_HOME);
				}
				
				try {
					Gate.setGateHome(GATE_DIRECTORY);
					Gate.setSiteConfigFile(new File(GATE_DIRECTORY, GATE_SITE_CONFIG));
					Gate.setUserConfigFile(new File(GATE_DIRECTORY, GATE_USER_CONFIG));
					Gate.setUserSessionFile(new File(GATE_DIRECTORY, GATE_SESSION));
					Gate.setPluginsHome(new File(GATE_DIRECTORY, GATE_PLUGINS_HOME));
				} catch (IllegalStateException exn) {
					logger.info(exn.getMessage()
							+ " , using previously set value: "
							+ Gate.getGateHome().getAbsolutePath());
				}

				Gate.init();
			
				Iterator<URL> pluginItr = Gate.getKnownPlugins().iterator();
				while (pluginItr.hasNext()) {
					URL pluginURL = pluginItr.next();
					Gate.getCreoleRegister().registerDirectories(pluginURL);
				}

				gateInited = true;
				
			} catch (FileNotFoundException e) {
				throw new ExceptionInInitializerError(e);
			} catch (IOException e) {
				throw new ExceptionInInitializerError(e);
			} catch (URISyntaxException e) {
				throw new ExceptionInInitializerError(e);
			} catch (GateException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}
	
	protected CorpusController loadApplication(String gappFileName) throws IOException, PersistenceException, ResourceInstantiationException {
		File gappFile = new File(GATE_DIRECTORY, gappFileName);
		String canonicalPath = gappFile.getCanonicalPath();
		
		if (!applicationRegistry.containsKey(canonicalPath)) {
			applicationRegistry.put(canonicalPath, (CorpusController) PersistenceManager.loadObjectFromFile(gappFile));
		}
		
		return applicationRegistry.get(canonicalPath);
	}
	
	protected static File createTempDirectory() throws IOException {
		final File temp;
		
		temp = File.createTempFile("temp-", Long.toString(System.nanoTime()));
		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}
		
		if(!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return (temp);
	}
}
