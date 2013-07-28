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

package eu.dime.ps.poc;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.service.exception.PimoConfigurationException;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.util.DateUtils;

public class TestDataLoader {

	private static final Logger logger = LoggerFactory.getLogger(TestDataLoader.class);
	
	private static final String CONFIG_FILE = "poc-data/%persona%/%persona%-pimo.trig";
	private static final String[] DATA_FILES = new String[] {
			"poc-data/%persona%/%persona%-profile.trig",
			"poc-data/%persona%/%persona%-resources.ttl",
			"poc-data/%persona%/%persona%-databoxes.ttl",
			"poc-data/%persona%/%persona%-liveposts.ttl",
			"poc-data/%persona%/%persona%-situations.trig",
	};

	/**
	 * Loads pre-defined data for a persona into the PIMO service (triple store). PIMO configuration
	 * and owner (pimo:Person) will be preserved, but all other previous data will be removed.
	 * 
	 * @param persona
	 * @param pimoService
	 * @throws Exception
	 */
	public void preparePimoService(String persona, PimoService pimoService) throws Exception {
		logger.debug("Preparing PIMO Service for "+persona+"...");

		if ("juan".equals(persona) || "anna".equals(persona) || "norbert".equals(persona)) {
			
			// read config file
			String configFile = CONFIG_FILE.replaceAll("%persona%", persona);
			Model configModel = RDF2Go.getModelFactory().createModel().open();
			try {
				ModelUtils.loadFromInputStream(
						this.getClass().getClassLoader().getResourceAsStream(configFile),
						Syntax.Trig,
						configModel);
			} catch (ModelRuntimeException e) {
				logger.error("PimoService cannot been initialized: "+e.getMessage(), e);
				throw new PimoConfigurationException();
			} catch (IOException e) {
				logger.error("PimoService cannot been initialized: "+e.getMessage(), e);
				throw new PimoConfigurationException();
			}
			
			// load all files in a memory model set
			Model pimoModel = RDF2Go.getModelFactory().createModel().open();
			ModelSet otherModel = RDF2Go.getModelFactory().createModelSet();
			otherModel.open();
			for (String template : DATA_FILES) {
				String dataFile = template.replaceAll("%persona%", persona);
				logger.debug("loading " + dataFile);
				try {
					InputStream is = this.getClass().getClassLoader().getResourceAsStream(dataFile);
					Syntax syntax = guessSyntax(dataFile);
					if (syntax.equals(Syntax.Trig)) { // keeps the data in the corresponding graphs
						ModelSet sinkModelSet = RDF2Go.getModelFactory().createModelSet();
						sinkModelSet.open();
						ModelUtils.loadFromInputStream(is, syntax, sinkModelSet);
						otherModel.addAll(sinkModelSet.iterator());
					} else { // data not in a graph is loaded into the PIM by default
						ModelUtils.loadFromInputStream(is, syntax, pimoModel);
					}
				} catch (Exception e) {
					logger.debug(dataFile + " couldn't be loaded: " + e.getMessage(), e);
				}
			}
			
			// add creation/last-update dates to PIMO Things
			// add missing pimo:isDefinedBy user's PIM to all pimo Things in the test data
			DatatypeLiteral now = DateUtils.currentDateTimeAsLiteral();
			ClosableIterator<? extends Statement> thingsIt = otherModel.findStatements(Variable.ANY, Variable.ANY, RDF.type, PIMO.Thing);
			while (thingsIt.hasNext()) {
				Statement stmt = thingsIt.next();
				URI context = stmt.getContext();
				Resource subject = stmt.getSubject();
				
				if (!otherModel.containsStatements(Variable.ANY, subject, NAO.created, Variable.ANY)) {
					otherModel.addStatement(context, subject, NAO.created, now);
				}
				if (!otherModel.containsStatements(Variable.ANY, subject, NAO.lastModified, Variable.ANY)) {
					otherModel.addStatement(context, subject, NAO.lastModified, now);
				}
				
				if (!otherModel.containsStatements(Variable.ANY, stmt.getSubject(), PIMO.isDefinedBy, Variable.ANY)) {
					otherModel.addStatement(pimoService.getPimoUri(), stmt.getSubject(), PIMO.isDefinedBy, pimoService.getPimoUri());
				}
			}
			thingsIt.close();
	
			// add creation/last-update dates to NIE Information Elements
			ClosableIterator<? extends Statement> ieIt = otherModel.findStatements(Variable.ANY, Variable.ANY, RDF.type, NIE.InformationElement);
			while (ieIt.hasNext()) {
				Statement stmt = ieIt.next();
				URI context = stmt.getContext();
				Resource subject = stmt.getSubject();
	
				if (!otherModel.containsStatements(Variable.ANY, subject, NIE.created, Variable.ANY)) {
					otherModel.addStatement(context, subject, NIE.created, now);
				}
				if (!otherModel.containsStatements(Variable.ANY, subject, NIE.lastModified, Variable.ANY)) {
					otherModel.addStatement(context, subject, NIE.lastModified, now);
				}
			}
			ieIt.close();
			
			// clear triple store, re-initialize the pimo and adds all data for the user
			pimoService.getTripleStore().clear();
			pimoService.initialize(configModel);
			pimoService.getTripleStore().addAll(pimoService.getPimoUri(), pimoModel.iterator());
			pimoService.getTripleStore().addAll(otherModel.iterator());
			
			// FIXME is this still needed? or should it be done in a different way?
			// need to register all owner's accounts in the DNS resolver
			// this is done when an Account instance is created, so we broadcast that event
			Collection<Account> accounts = pimoService.find(Account.class)
					.distinct()
					.where(NAO.creator).is(pimoService.getUserUri())
					.results();
			for (Account account : accounts) {
				logger.info("Broadcasting 'account created' event for account "+account);
				BroadcastManager.getInstance().sendBroadcast(new Event(pimoService.getName(), Event.ACTION_RESOURCE_ADD, account));
			}
			
		} else {
			logger.debug("Persona '"+persona+"' has no pre-defined data. Nothing to load in the triple store.");
		}

	}
	
	private Syntax guessSyntax(String file) {
		if (file.endsWith(".nt")) {
			return Syntax.Ntriples;
		} else if (file.endsWith(".ttl")) {
			return Syntax.Turtle;
		} else if (file.endsWith(".trix")) {
			return Syntax.Trix;
		} else if (file.endsWith(".trig")) {
			return Syntax.Trig;
		} else {
			return Syntax.RdfXml;
		}
	}
	
}