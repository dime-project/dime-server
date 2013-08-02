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

package eu.dime.ps.semantic.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonMatchingConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonMatchingConfiguration.class);

	private static final boolean DEFAULT_ENABLED = false;
	private static final double DEFAULT_THRESHOLD = 0.90;
	private static final int DEFAULT_MATCHING_TECHNIQUE = 1;
	private static final int DEFAULT_WEIGHTING_SCHEME = 1;
	private static final int DEFAULT_WEIGHTING_APPROACH = 1;
	private static final boolean DEFAULT_SEMANTIC_EXTENSION = false;
	
	public static boolean ENABLED = DEFAULT_ENABLED;
	public static double THRESHOLD = DEFAULT_THRESHOLD;
	public static int MATCHING_TECHNIQUE = DEFAULT_MATCHING_TECHNIQUE;
	public static int WEIGHTING_SCHEME = DEFAULT_WEIGHTING_SCHEME;
	public static int WEIGHTING_APPROACH = DEFAULT_WEIGHTING_APPROACH;
	public static boolean SEMANTIC_EXTENSION = DEFAULT_SEMANTIC_EXTENSION;

	static {
		Properties properties = new Properties();
		InputStream in = PersonMatchingConfiguration.class.getClassLoader().getResourceAsStream("matching.properties");
		try {
			properties.load(in);
			ENABLED = Boolean.parseBoolean(properties.getProperty("ENABLED", Boolean.toString(DEFAULT_ENABLED)));
			THRESHOLD = Double.parseDouble(properties.getProperty("THRESHOLD", Double.toString(DEFAULT_THRESHOLD)));
			MATCHING_TECHNIQUE = Integer.parseInt(properties.getProperty("MATCHING_TECHNIQUE", Integer.toString(DEFAULT_MATCHING_TECHNIQUE)));
			WEIGHTING_SCHEME = Integer.parseInt(properties.getProperty("WEIGHTING_SCHEME", Integer.toString(DEFAULT_WEIGHTING_SCHEME)));
			WEIGHTING_APPROACH = Integer.parseInt(properties.getProperty("WEIGHTING_APPROACH", Integer.toString(DEFAULT_WEIGHTING_APPROACH)));
			SEMANTIC_EXTENSION = Boolean.parseBoolean(properties.getProperty("SEMANTIC_EXTENSION", Boolean.toString(DEFAULT_SEMANTIC_EXTENSION)));
		} catch (IOException e) {
			logger.error("Configuration values from 'matching.properties' couldn't be read, using default values instead: "+e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
	}
}
