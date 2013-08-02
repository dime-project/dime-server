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

package eu.dime.ps.datamining.account;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.impl.TripleStoreImpl;
import ie.deri.smile.rdf.util.ModelUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.semantic.rdf.RepositoryFactory;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.impl.ResourceStoreImpl;
import eu.dime.ps.semantic.rdf.impl.SesameMemoryRepositoryFactory;
import eu.dime.ps.semantic.service.impl.PimoService;

@ContextConfiguration(locations = { "classpath*:**/datamining-tests-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class StreamAccountUpdaterTest extends TestCase {

	@Autowired
	private ModelFactory modelFactory;
	
	private RepositoryFactory repositoryFactory;

	@Autowired
	private TripleStore tripleStore;
	
	@Autowired
	private ResourceStore resourceStore;
	
	@Autowired
	private PimoService pimoService;

	private StreamAccountUpdater streamAccountUpdater;
	
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
		streamAccountUpdater = new StreamAccountUpdater(resourceStore);
	}

	/**
	 * Creates and initializes a test triple store
	 */
	protected ResourceStore createResourceStore(InputStream is, Syntax syntax) throws RepositoryException, IOException {
		Model model = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(is, syntax, model);
		
		TripleStore tripleStore = new TripleStoreImpl("12345", repositoryFactory.get("12345"));
		tripleStore.addAll(model.iterator());
		
		return new ResourceStoreImpl(tripleStore);
	}

	@Test
	public void testAddTwoStreams() throws Exception {
		// creates the service account
		Account linkedin = modelFactory.getDAOFactory().createAccount("urn:service:linkedin");
		linkedin.setPrefLabel("LinkedIn");
		resourceStore.createOrUpdate(null, linkedin);

		ResourceStore data = null;
		Collection<LivePost> messages = new ArrayList<LivePost>();
		
		// contains the 10 latest posts
		data = createResourceStore(
				this.getClass().getClassLoader().getResourceAsStream("account/liveposts-call1.ttl"),
				Syntax.Turtle);
		for (Status status : data.find(Status.class).distinct().results()) {
			messages.add((Status) status.castTo(Status.class));
		}
		streamAccountUpdater.update(linkedin.asURI(), "/liveposts", messages);
		assertEquals(10, resourceStore.find(Status.class).distinct().results().size());

		// 10 latest post (3 new, 7 also included in the previous call)
		messages.clear();
		data = createResourceStore(
				this.getClass().getClassLoader().getResourceAsStream("account/liveposts-call2.ttl"),
				Syntax.Turtle);
		for (Status status : data.find(Status.class).distinct().results()) {
			messages.add((Status) status.castTo(Status.class));
		}
		streamAccountUpdater.update(linkedin.asURI(), "/liveposts", messages);
		assertEquals(13, resourceStore.find(Status.class).distinct().results().size());
	}
	
}
