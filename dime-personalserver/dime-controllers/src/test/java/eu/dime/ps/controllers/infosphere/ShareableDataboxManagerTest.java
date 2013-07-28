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

package eu.dime.ps.controllers.infosphere;

import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.ShareableDataboxManager;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.model.ObjectFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * Tests {@link ShareableDataboxManager}.
 * 
 * @author Ismael Rivera
 */
public class ShareableDataboxManagerTest extends InfoSphereManagerTest {

	@Autowired
	private ShareableDataboxManager databoxManager;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		loadData("files-data.nt"); // load some files for adding to the databoxes
	}

	private Person createPerson(String name) throws Exception {
		Person person = ObjectFactory.buildPerson(name);
		pimoService.create(person);
		return person;
	}

	private Account createAccount(Person creator) throws Exception {
		Account account = ObjectFactory.buildAccount("Account " + System.currentTimeMillis(), DimeServiceAdapter.NAME, creator.asURI());
		pimoService.create(account);
		return account;
	}
	
	@Test
	public void testExistDatabox() throws Exception {
		// creates a di.me account for the owner of the PIM
		Person me = pimoService.getUser();
		Account account = createAccount(me);

		// creates Anna person, account and profile
		Person anna = modelFactory.getPIMOFactory().createPerson();
		Account annaAccount = modelFactory.getDAOFactory().createAccount();
		annaAccount.setPrefLabel("anna's account");
		annaAccount.setCreator(anna);
		PersonContact annaProfile = modelFactory.getNCOFactory().createPersonContact();
		anna.setGroundingOccurrence(annaProfile);
		annaProfile.getModel().addStatement(annaProfile, NIE.dataSource, annaAccount.asResource());
		resourceStore.createOrUpdate(pimoService.getPimoUri(), annaAccount);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), annaProfile);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), anna);

		// adds a databox to Anna's account
		DataObject file = modelFactory.getNIEFactory().createDataObject("file:/home/ismriv/example/dir1/motivation-long-tail.png");
		DataContainer databox = ObjectFactory.buildDatabox("anna's databox", new DataObject[]{ file }, anna.asURI(), true);
		databoxManager.add(databox, annaAccount.asURI().toString(), account.asURI().toString());
		
		// verify databox exists
		assertTrue(databoxManager.exist(databox.toString()));
	}
	
	@Test
	public void testAddDataboxToAccount() throws Exception {
		// creates a di.me account for the owner of the PIM
		Person me = pimoService.getUser();
		Account account = createAccount(me);

		// creates Anna person, account and profile
		Person anna = modelFactory.getPIMOFactory().createPerson();
		Account annaAccount = modelFactory.getDAOFactory().createAccount();
		annaAccount.setPrefLabel("anna's account");
		annaAccount.setCreator(anna);
		PersonContact annaProfile = modelFactory.getNCOFactory().createPersonContact();
		anna.setGroundingOccurrence(annaProfile);
		annaProfile.getModel().addStatement(annaProfile, NIE.dataSource, annaAccount.asResource());
		resourceStore.createOrUpdate(pimoService.getPimoUri(), annaAccount);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), annaProfile);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), anna);

		// adds a databox to Anna's account
		DataObject file = modelFactory.getNIEFactory().createDataObject("file:/home/ismriv/example/dir1/motivation-long-tail.png");
		DataContainer databox = ObjectFactory.buildDatabox("anna's databox", new DataObject[]{ file }, anna.asURI(), true);

		// this data shouldn't be stored even if it's provided
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.addIncludes(pimoService.getUser());
		databox.getModel().addStatement(databox, PPO.hasAccessSpace, accessSpace);
		databox.getModel().addAll(accessSpace.getModel().iterator());

		databoxManager.add(databox, annaAccount.asURI().toString(), account.asURI().toString());

		assertTrue(tripleStore.containsStatements(Variable.ANY, databox, RDF.type, NFO.DataContainer));
		assertTrue(tripleStore.containsStatements(Variable.ANY, databox, NIE.hasPart, file));
		assertTrue(tripleStore.containsStatements(Variable.ANY, databox, NAO.creator, anna));
		assertTrue(tripleStore.containsStatements(Variable.ANY, databox, NIE.dataSource, annaAccount));
		assertTrue(tripleStore.containsStatements(Variable.ANY, databox, NSO.sharedBy, annaAccount));
		assertTrue(tripleStore.containsStatements(Variable.ANY, databox, NSO.sharedWith, account));
		assertFalse(tripleStore.containsStatements(Variable.ANY, databox, PPO.hasAccessSpace, Variable.ANY));
		assertFalse(tripleStore.containsStatements(Variable.ANY, Variable.ANY, RDF.type, NSO.AccessSpace));
		assertFalse(tripleStore.containsStatements(Variable.ANY, Variable.ANY, NSO.includes, Variable.ANY));
		assertFalse(tripleStore.containsStatements(Variable.ANY, Variable.ANY, NSO.excludes, Variable.ANY));
	}

	@Test(expected=InfosphereException.class)
	public void testAddDataboxToOwnAccount() throws Exception {
		// creates a di.me account for the owner of the PIM
		Person me = pimoService.getUser();
		Account account = createAccount(me);

		// creates a second di.me account for the o person
		Account anotherAccount = createAccount(me);

		// adds a databox to my own account
		DataContainer databox = modelFactory.getNFOFactory().createDataContainer();
		databox.setPrefLabel("test databox");

		databoxManager.add(databox, account.asURI().toString(), anotherAccount.asURI().toString());
	}
	
	@Test
	public void testGet() throws Exception {
		DataObject file = modelFactory.getNIEFactory().createDataObject("file:/home/ismriv/example/dir1/motivation-long-tail.png");
		URI creator = pimoService.getUserUri();

		// creates a di.me account for the owner of the PIM
		Account accountSender = createAccount(pimoService.getUser());

		// creates a di.me account for the another person
		Person friend = createPerson("friend");
		Account accountFriend = createAccount(friend);

		// creates a databox giving access to the person itself
		DataContainer databoxA = ObjectFactory.buildDatabox("test databox A", new DataObject[]{ file }, creator, accountSender.asURI(), new Agent[]{ friend });
		pimoService.create(databoxA);

		// creates a databox giving access to a specific account of the person
		DataContainer databoxB = ObjectFactory.buildDatabox("test databox B", new DataObject[]{ file }, creator, accountSender.asURI(), new Agent[]{ accountFriend });
		pimoService.create(databoxB);

		assertEquals(databoxA, databoxManager.get(databoxA.toString(), accountFriend.toString()));
		assertEquals(databoxB, databoxManager.get(databoxB.toString(), accountFriend.toString()));
	}

	@Test
	public void testGetAll() throws Exception {
		DataObject file = modelFactory.getNIEFactory().createDataObject("file:/home/ismriv/example/dir1/motivation-long-tail.png");
		URI creator = pimoService.getUserUri();
		
		// creates a di.me account for the owner of the PIM
		Account accountSender = createAccount(pimoService.getUser());

		// creates a di.me account for the another person
		Person friend = createPerson("friend");
		Account accountFriend = createAccount(friend);

		// creates a databox giving access to the person itself
		DataContainer databoxA = ObjectFactory.buildDatabox("test databox A", new DataObject[]{ file }, creator, accountSender.asURI(), new Agent[]{ friend });
		pimoService.create(databoxA);

		// creates a databox giving access to a specific account of the person
		DataContainer databoxB = ObjectFactory.buildDatabox("test databox B", new DataObject[]{ file }, creator, accountSender.asURI(), new Agent[]{ accountFriend });
		pimoService.create(databoxB);

		Collection<DataContainer> databoxes = databoxManager.getAll(accountSender.toString(), accountFriend.toString());
		assertEquals(2, databoxes.size());
		assertTrue(databoxes.contains(databoxA));
		assertTrue(databoxes.contains(databoxB));
	}

}