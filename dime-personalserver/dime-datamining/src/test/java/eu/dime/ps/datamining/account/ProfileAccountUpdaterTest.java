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

package eu.dime.ps.datamining.account;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.impl.TripleStoreImpl;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.FOAF;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.datamining.ProfileEnricher;
import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.pimo.Thing;
import eu.dime.ps.semantic.query.impl.BasicQuery;
import eu.dime.ps.semantic.rdf.RepositoryFactory;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.impl.ResourceStoreImpl;
import eu.dime.ps.semantic.rdf.impl.SesameMemoryRepositoryFactory;
import eu.dime.ps.semantic.service.impl.PimoService;

@ContextConfiguration(locations = { "classpath*:**/datamining-tests-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ProfileAccountUpdaterTest extends TestCase {

	@Autowired
	private ModelFactory modelFactory;
	
	private RepositoryFactory repositoryFactory;

	@Autowired
	private TripleStore tripleStore;

	@Autowired
	private ResourceStore resourceStore;

	@Autowired
	private PimoService pimoService;
	
	private ProfileAccountUpdater updater;

    @BeforeClass
    public static void setUpClass() throws Exception {
    	// disabling org.openrdf.rdf2go.RepositoryModel warnings
    	org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
    	java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
    }

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		pimoService.clear();
		repositoryFactory = new SesameMemoryRepositoryFactory();

		ProfileEnricher mockEnricher = new MockProfileEnricher();
		updater = new ProfileAccountUpdater(resourceStore, pimoService, mockEnricher);
	}

	/**
	 * Creates and initializes a test triple store
	 */
	protected ResourceStore createResourceStore(InputStream is, Syntax syntax) throws RepositoryException, IOException {
		TripleStore tripleStore = null;
		Model model = null;
		tripleStore = new TripleStoreImpl("12345", repositoryFactory.get("12345"));
		model = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(is, syntax, model);
		tripleStore.addAll(model.iterator());
		return new ResourceStoreImpl(tripleStore);
	}

	@Test
	public void testAddContacts() throws Exception {
		// adds connections from LinkedIn, should:
		// - create pimo:Persons for each
		// - relate with the pimo user via foaf:knows
		// - create a group for the account, connections are members
		
		// creates the service account
		Account linkedin = modelFactory.getDAOFactory().createAccount("urn:service:linkedin");
		linkedin.setPrefLabel("LinkedIn");
		pimoService.create(linkedin);

		// loads an example of RDF data for a service request (as coming from the transformer)
		ResourceStore data = createResourceStore(
				this.getClass().getClassLoader().getResourceAsStream("account/linkedin-persons.ttl"),
				Syntax.Turtle);
		Collection<PersonContact> contacts = data.find(PersonContact.class).distinct().results();
		
		// update the semantic store with the contacts (and should match with person "Kenneth Chircop")
		updater.update(linkedin.asURI(), "/connections", contacts);

		PersonGroup accountGroup = resourceStore.find(PersonGroup.class).where(NIE.dataSource).is(linkedin).first();
		assertNotNull(accountGroup);
		
		List<Agent> members = accountGroup.getAllMembers_as().asList();
		assertEquals(2, members.size());
		
		List<URI> uris = new LinkedList<URI>();
		for (Thing m : members) uris.add(m.asURI());
		Person anna = resourceStore.find(Person.class)
				.where(PIMO.groundingOccurrence).is(BasicQuery.X)
				.where(BasicQuery.X, NAO.externalIdentifier).is("9V7WOs5KHr")
				.first();
		assertTrue(uris.contains(anna.asURI()));
		assertTrue(tripleStore.containsStatements(pimoService.getPimoUri(), pimoService.getUserUri(), FOAF.knows, anna));
		
		Person norbert = resourceStore.find(Person.class)
				.where(PIMO.groundingOccurrence).is(BasicQuery.X)
				.where(BasicQuery.X, NAO.externalIdentifier).is("RYWVyGcfzR")
				.first();
		assertTrue(uris.contains(norbert.asURI()));
		assertTrue(tripleStore.containsStatements(pimoService.getPimoUri(), pimoService.getUserUri(), FOAF.knows, norbert));
	}
	
	@Test
	public void testRepeatedUpdate() throws Exception {
		// loads an example of RDF data for a service request (as coming from the transformer)
		ResourceStore initialStore = createResourceStore(
				this.getClass().getClassLoader().getResourceAsStream("account/linkedin-connections-initial.ttl"),
				Syntax.Turtle);
		ResourceStore updatedStore = createResourceStore(
				this.getClass().getClassLoader().getResourceAsStream("account/linkedin-connections-updated.ttl"),
				Syntax.Turtle);

		Collection<PersonContact> initialContacts = initialStore.find(PersonContact.class).distinct().results();
		Collection<PersonContact> updatedContacts = updatedStore.find(PersonContact.class).distinct().results();

		// creates the service account
		Account linkedin = modelFactory.getDAOFactory().createAccount("urn:service:linkedin");
		resourceStore.createOrUpdate(pimoService.getPimoUri(), linkedin);

		updater.update(linkedin.asURI(), "/connections", initialContacts);
		PersonContact initialContact = resourceStore.find(PersonContact.class).where(NAO.externalIdentifier).is("7N49Odr3Dk").first();
		PersonName initialContactName = resourceStore.get(initialContact.getAllPersonName().next().asURI(), PersonName.class);
		assertEquals("Charlie", initialContactName.getNameGiven());
		
		// adds a triple in another graph (not the account one), to check that this should be in
		// the store after the update
		tripleStore.addStatement(pimoService.getPimoUri(), initialContact, NAO.hasTag, new URIImpl("urn:sometag"));
		
		updater.update(linkedin.asURI(), "/connections", updatedContacts);
		PersonContact updatedContact = resourceStore.find(PersonContact.class).where(NAO.externalIdentifier).is("7N49Odr3Dk").first();
		PersonName updatedContactName = resourceStore.get(updatedContact.getAllPersonName().next().asURI(), PersonName.class);
		assertEquals(initialContact.asURI(), updatedContact.asURI());
		assertEquals("Carmelo", updatedContactName.getNameGiven());
		
		assertTrue(tripleStore.containsStatements(pimoService.getPimoUri(), updatedContact, NAO.hasTag, new URIImpl("urn:sometag")));
	}

	private class MockProfileEnricher implements ProfileEnricher {
		@Override
		public PersonContact enrich(PersonContact profile) throws DataMiningException {
			return profile;
		}
	}
	
}

