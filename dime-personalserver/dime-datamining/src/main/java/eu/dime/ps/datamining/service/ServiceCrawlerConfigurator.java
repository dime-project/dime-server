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

package eu.dime.ps.datamining.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Loads the settings for the service crawlers. The default configuration is
 * loaded from XML files, one per type of service adapter.
 * 
 * TODO: If the user is ever able to change the configuration of the crawlers
 * through the UI, this should allow to change the settings and persist the
 * changes either in the XML files or in a database, and load them back at startup.
 * 
 * @author Ismael Rivera
 */
public class ServiceCrawlerConfigurator {

	private static final Logger logger = LoggerFactory.getLogger(ServiceCrawlerConfigurator.class);
	
	private final Map<String, List<JobConfiguration>> config;

	private static ServiceCrawlerConfigurator INSTANCE;
	
	private ServiceCrawlerConfigurator() {
		this.config = new HashMap<String, List<JobConfiguration>>();
		
		// Pertaining to decision in daily call on 17.07.2013: Disable livepost crawling functionality.
		
		// FIXME on the staging server (linux) the config XML files are not found, now setting the config programmatically
		// and will fix later
		config.put("Facebook", new ArrayList<JobConfiguration>());
		config.get("Facebook").add(new JobConfiguration("/person/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
		config.get("Facebook").add(new JobConfiguration("/profile/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
//		config.get("Facebook").add(new JobConfiguration("/livepost/@me/@all", eu.dime.ps.semantic.model.dlpo.LivePost.class, "0 0/5 * * * ?"));
		config.put("Fitbit", new ArrayList<JobConfiguration>());
//		config.get("Fitbit").add(new JobConfiguration("/person/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
//		config.get("Fitbit").add(new JobConfiguration("/profile/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
		config.get("Fitbit").add(new JobConfiguration("/activity/@me/@all", eu.dime.ps.semantic.model.dpo.Activity.class, "0 0/5 * * * ?"));
		config.put("GooglePlus", new ArrayList<JobConfiguration>());
		config.get("GooglePlus").add(new JobConfiguration("/profile/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
//		config.get("GooglePlus").add(new JobConfiguration("/livepost/@me/@all", eu.dime.ps.semantic.model.dlpo.LivePost.class, "0 0/5 * * * ?"));
		config.put("LinkedIn", new ArrayList<JobConfiguration>());
		config.get("LinkedIn").add(new JobConfiguration("/person/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
		config.get("LinkedIn").add(new JobConfiguration("/profile/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
//		config.get("LinkedIn").add(new JobConfiguration("/livepost/@me/@all", eu.dime.ps.semantic.model.dlpo.LivePost.class, "0 0/5 * * * ?"));
//		config.get("LinkedIn").add(new JobConfiguration("/livepost/@all", eu.dime.ps.semantic.model.dlpo.LivePost.class, "0 0/5 * * * ?"));
		config.put("Twitter", new ArrayList<JobConfiguration>());
		config.get("Twitter").add(new JobConfiguration("/person/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
		config.get("Twitter").add(new JobConfiguration("/profile/@me/@all", eu.dime.ps.semantic.model.nco.PersonContact.class, "0 0/30 * * * ?"));
//		config.get("Twitter").add(new JobConfiguration("/livepost/@me/@all", eu.dime.ps.semantic.model.dlpo.LivePost.class, "0 0/5 * * * ?"));
//		config.get("Twitter").add(new JobConfiguration("/livepost/@all", eu.dime.ps.semantic.model.dlpo.LivePost.class, "0 0/5 * * * ?"));
	}
	
	public static ServiceCrawlerConfigurator getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ServiceCrawlerConfigurator();
		return INSTANCE;
	}
	
	public List<JobConfiguration> getJobConfigurations(String adapterName) throws ConfigurationNotFoundException, ConfigurationParsingException {
		// try to load configuration file if data not found in config map
		if (!config.containsKey(adapterName)) {
			try {
				config.put(adapterName, loadConfig(adapterName));
			} catch (FileNotFoundException e) {
				// next time the config for this adapter is requested, it's known
				// it wasn't found, and no config is returned
				config.put(adapterName, null);
				throw new ConfigurationNotFoundException("Crawling configuration not found for adapter '"+adapterName+"'", e);
			}
		}
		
		List<JobConfiguration> jobConfig = config.get(adapterName);
		if (jobConfig == null) {
			throw new ConfigurationNotFoundException("Crawling configuration not found for adapter '"+adapterName+"'");
		}
		
		return jobConfig;
	}
	
	/**
	 * Returns null if config not found for given adapter name
	 * @param adapterName
	 * @return
	 */
	private List<JobConfiguration> loadConfig(String adapterName) throws FileNotFoundException, ConfigurationParsingException {
		List<JobConfiguration> jobs = new ArrayList<JobConfiguration>();

		// reads XML file for the adapter in directory 'crawler'
		String configFile = "crawler" + File.separator + adapterName + ".xml";
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(configFile);
		
		// stream = null if file not found
		if (stream == null) {
			throw new FileNotFoundException("Crawling config file '"+configFile+"' not found.");
		}

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			logger.debug("Parsing "+configFile);
			Document doc = builder.parse(new InputSource(stream));
			
			String adapter = null;
			NodeList adapterElement = doc.getElementsByTagName("adapter");
			if (adapterElement.getLength() == 1) {
				adapter = adapterElement.item(0).getTextContent();
			} else {
				throw new IllegalArgumentException("The config file must contain only one 'adapter' element.");
			}
			
			NodeList jobsElement = doc.getElementsByTagName("job");
			for (int jobsIndex = 0; jobsIndex < jobsElement.getLength(); jobsIndex++) {
				Node jobElement = jobsElement.item(jobsIndex);
				JobConfiguration job = new JobConfiguration();
				for (int jobIndex = 0; jobIndex < jobElement.getChildNodes().getLength(); jobIndex++) {
					Node element = jobElement.getChildNodes().item(jobIndex);
					if ("cron".equals(element.getNodeName())) {
						job.setCronSchedule(element.getTextContent());
					} else if ("path".equals(element.getNodeName())) {
						job.setPath(element.getTextContent());
					} else if ("type".equals(element.getNodeName())) {
						Class<? extends Resource> type = (Class<? extends Resource>) this.getClass().getClassLoader().loadClass(element.getTextContent());
						job.setType(type);
					}
				}
				
				jobs.add(job);
				logger.debug("Added crawler config [cron="+job.getCronSchedule()+", path="+job.getPath()+"] for adapter '"+adapter+"'");
			}

			// returning config results if parsing went ok
			return jobs;

		} catch (ParserConfigurationException e) {
			throw new ConfigurationParsingException("Crawling config could not be processed for adapter '"+adapterName+"'", e);
		} catch (SAXException e) {
			throw new ConfigurationParsingException("Crawling config could not be processed for adapter '"+adapterName+"'", e);
		} catch (IOException e) {
			throw new ConfigurationParsingException("Crawling config could not be processed for adapter '"+adapterName+"'", e);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationParsingException("Crawling config could not be processed for adapter '"+adapterName+"'", e);
		}
		
	}

}
