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

package eu.dime.ps.controllers.automation;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.SituationManager;
import eu.dime.ps.controllers.notification.NotifierManagerMock;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.model.nfo.Document;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.service.impl.PimoService;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class RuleExecutorTest extends TestCase {
	
	@Autowired
	protected ConnectionProvider connectionProvider;

	@Autowired
	protected TripleStore tripleStore;

	@Autowired
	protected PimoService pimoService;
	
	@Autowired
	protected FileManager fileManager;
	
	@Autowired
	protected SituationManager situationManager;

	protected ModelFactory modelFactory = new ModelFactory();

	private double PETER_TRUST_LEVEL = 0.7;
	private Person peter = null;
	private Account peterAccount = null;
	
	@Before
	public void setUp() throws Exception {
		// de-registers any other rule executor before each test, so that there aren't
		// multiple rule executors processing events
		BroadcastManager.getInstance().unregisterReceivers();

		// clear up all previous data, start clean each test
		pimoService.clear();
		loadData();
	}

	protected void loadData() throws ResourceExistsException, ModelRuntimeException, IOException {
		pimoService.create(this.buildProfile("John Doe", 0.8));
		pimoService.create(this.buildProfile("Mary Doe", 0.3));
		pimoService.create(this.buildProfile("Anna Doe", 0.9));
		pimoService.create(this.buildProfile("Paul Doe", 0.45));
		
		peter = this.buildProfile("Peter Doe", PETER_TRUST_LEVEL);
		peterAccount = this.buildAccount("Peter@di.me", peter.asURI());
		pimoService.create(peter);
		pimoService.create(peterAccount);
	}
	
	@Test
	public void testDocumentCreated() throws Exception {
		// load rule definition
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("rules/documentCreated.ttl"),
				Syntax.Turtle, sinkModel);
		pimoService.getTripleStore().addAll(pimoService.getPimoUri(), sinkModel.iterator());
		sinkModel.close();
		
		// construct RuleExecutor
		final NotifierManagerMock notifierManager = new NotifierManagerMock();
		final RuleExecutor executor = new RuleExecutor();
		executor.setConnectionProvider(connectionProvider);
		executor.setNotifierManager(notifierManager);

		final Document doc = modelFactory.getNFOFactory().createDocument();
		doc.setPrefLabel("Test Document");
		tripleStore.addAll(pimoService.getPimoUri(), doc.getModel().iterator());
		
		// should send user notification (to notifier manager)
		int size = notifierManager.internal.size();
		final BroadcastManager bm = BroadcastManager.getInstance();
		bm.sendBroadcastSync(new Event(pimoService.getName(), Event.ACTION_RESOURCE_ADD, doc));
		assertEquals(size + 1, notifierManager.internal.size());
	}
	
	@Test
	public void testDocumentSharedWith() throws Exception {
		// load rule definition
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("rules/documentSharedWith.ttl"),
				Syntax.Turtle, sinkModel);
		pimoService.getTripleStore().addAll(pimoService.getPimoUri(), sinkModel.iterator());
		sinkModel.close();
		
		// construct RuleExecutor
		final RuleExecutor executor = new RuleExecutor();
		executor.setConnectionProvider(connectionProvider);
		executor.setNotifierManager(null);
		
		final Document doc = modelFactory.getNFOFactory().createDocument();
		doc.setPrefLabel("MyFile.doc");
		doc.addSharedWith(peterAccount);
		doc.setIsDefinedBy(pimoService.getPimoUri());
		tripleStore.addAll(pimoService.getPimoUri(), doc.getModel().iterator());
		
		// should increase trust level value
		final BroadcastManager bm = BroadcastManager.getInstance();
		bm.sendBroadcastSync(new Event(pimoService.getName(), Event.ACTION_RESOURCE_MODIFY, doc));
		Node trustLevel = ModelUtils.findObject(tripleStore, peter, NAO.trustLevel);
		double value = Double.parseDouble(trustLevel.asDatatypeLiteral().getValue());
		assertTrue(value > PETER_TRUST_LEVEL);
	}

	@Test
	public void testUsingFileManagerDocumentSharedWith() throws Exception {
		// load rule definition
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("rules/documentSharedWith.ttl"),
				Syntax.Turtle, sinkModel);
		pimoService.getTripleStore().addAll(pimoService.getPimoUri(), sinkModel.iterator());
		sinkModel.close();
		
		TenantContextHolder.setTenant(Long.parseLong(pimoService.getName()));
		
		final FileDataObject doc = modelFactory.getNFOFactory().createFileDataObject();
		doc.getModel().addStatement(doc, RDF.type, NFO.Document);
		doc.setPrefLabel("MyFile.doc");
		fileManager.add(doc);
		
		doc.addSharedWith(peterAccount);
		fileManager.update(doc);

		// delaying the creation of the RuleExecutor, so it doesn't pick any
		// of the events sent by the file manager
		Thread.sleep(300); 
		
		final RuleExecutor executor = new RuleExecutor();
		executor.setConnectionProvider(connectionProvider);
		executor.setNotifierManager(null);
		
		// should increase trust level value
		final BroadcastManager bm = BroadcastManager.getInstance();
		bm.sendBroadcastSync(new Event(pimoService.getName(), Event.ACTION_RESOURCE_MODIFY, doc));
		Node trustLevel = ModelUtils.findObject(tripleStore, peter, NAO.trustLevel);
		double value = Double.parseDouble(trustLevel.asDatatypeLiteral().getValue());
		assertTrue(value > PETER_TRUST_LEVEL);
	}

	@Test
	public void testPersonHasSituationActivated() throws Exception {
		// load rule definition
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("rules/personHasSituationActivated.ttl"),
				Syntax.Turtle, sinkModel);
		pimoService.getTripleStore().addAll(pimoService.getPimoUri(), sinkModel.iterator());
		sinkModel.close();
		
		TenantContextHolder.setTenant(Long.parseLong(pimoService.getName()));

		final Situation situation = modelFactory.getDCONFactory().createSituation("urn:uuid:47189f0f-3c67-49a9-b7ed-0f5af1b0944c");
		situation.setPrefLabel("@Conference");
		situationManager.add(situation);
		tripleStore.addStatement(pimoService.getPimoUri(), pimoService.getUserUri(), DCON.hasSituation, situation);

		// delaying the creation of the RuleExecutor, so it doesn't pick any
		// of the events sent by the situation manager
		Thread.sleep(300); 

		// construct RuleExecutor
		final NotifierManagerMock notifierManager = new NotifierManagerMock();
		final RuleExecutor executor = new RuleExecutor();
		executor.setConnectionProvider(connectionProvider);
		executor.setNotifierManager(notifierManager);
		
		// should send user notification (to notifier manager)
		int size = notifierManager.internal.size();
		final BroadcastManager bm = BroadcastManager.getInstance();
		bm.sendBroadcastSync(new Event(pimoService.getName(), Event.ACTION_RESOURCE_MODIFY, pimoService.getUser()));
		assertEquals(size + 1, notifierManager.internal.size());
	}

	@Test
	@Ignore
	public void testSituationActivated() throws Exception {
		// load rule definition
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("rules/situationActivated.ttl"),
				Syntax.Turtle, sinkModel);
		pimoService.getTripleStore().addAll(pimoService.getPimoUri(), sinkModel.iterator());
		sinkModel.close();
		
		// construct RuleExecutor
		final NotifierManagerMock notifierManager = new NotifierManagerMock();
		final RuleExecutor executor = new RuleExecutor();
		executor.setConnectionProvider(connectionProvider);
		executor.setNotifierManager(notifierManager);
		
		final Situation situation = modelFactory.getDCONFactory().createSituation();
		situation.setPrefLabel("@Conference");
		tripleStore.addAll(pimoService.getPimoUri(), situation.getModel().iterator());
		tripleStore.addStatement(pimoService.getPimoUri(), pimoService.getUserUri(), DCON.hasSituation, situation);
		
		// should send user notification (to notifier manager)
		int size = notifierManager.internal.size();
		final BroadcastManager bm = BroadcastManager.getInstance();
		bm.sendBroadcastSync(new Event(pimoService.getName(), Event.ACTION_RESOURCE_MODIFY, situation));
		assertEquals(size + 1, notifierManager.internal.size());
	}
	
	private Person buildProfile(String name, double trustValue) {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);
		person.setTrustLevel(trustValue);
		return person;
	}

	private Account buildAccount(String name, URI creator) {
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setPrefLabel(name);
		account.setAccountType("di.me");
		account.setCreator(creator);
		return account;
	}

}
