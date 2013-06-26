package eu.dime.ps.controllers.trustengine;

import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.infosphere.manager.LivePostManagerImpl;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.trustengine.impl.TrustEngineUpdateService;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.service.impl.PimoService;

@Ignore //Tests unstable
public class TrustEngineUpdateServiceTestIt extends AbstractTrustEngineTest{

	private BroadcastManager broadCastManager = BroadcastManager.getInstance();
	
	@Autowired
	private TrustEngineUpdateService trustEngineUpdateService;
	
	@Autowired
	LivePostManagerImpl livePostManager;
	
	@Mock
	NotifierManager notifierManager;
	
	PrivacyPreferenceService privacyPreferenceService;
	
	private Person anna;
	private Person bob;
	
	private PersonGroup groupA;
	
	private URI pimoUri = new URIImpl("uri:pimo");
	
	Account sender;

	
	@Before
	public void init() throws Exception{
		MockitoAnnotations.initMocks(this);
		
		trustEngineUpdateService.setNotifyManager(notifierManager);
		
		TenantContextHolder.setTenant(Long.parseLong(connection.getName()));
		
		privacyPreferenceService = connection.getPrivacyPreferenceService();
		
		anna = buildPerson("Anna", 0.8);
		bob = buildPerson("Bob", 0.1);
		
		personManager.add(anna);
		personManager.add(bob);
		
		groupA = buildGroup("A");
		
		groupA.addMember(anna);
		groupA.addMember(bob);
		personGroupManager.add(groupA);
		
		PimoService pimoService = connection.getPimoService();

		sender = modelFactory.getDAOFactory().createAccount();
		sender.setAccountType(DimeServiceAdapter.NAME);
		sender.setCreator(pimoService.getUser());
		pimoService.createOrUpdate(sender);
	}
	
	@After
	public void tearDown(){
		TenantContextHolder.clear();
	}
		
	@Test
	public void testOnReceivePrivacyPreferenceModify(){
		PrivacyPreference pp = modelFactory.getPPOFactory().createPrivacyPreference();
		
		Event event = new Event("tenant", Event.ACTION_RESOURCE_MODIFY, pp);
		trustEngineUpdateService.onReceive(event);
		//FIXME: add assert stuff

	}
	@Test
	public void testOnReceivePrivacyPreferenceAdd() throws Exception{
		PrivacyPreference pp = modelFactory.getPPOFactory().createPrivacyPreference();
		pp.setLabel(PrivacyPreferenceType.DATABOX.toString());
		pp.getModel().addStatement(pp, RDF.type, PPO.PrivacyPreference);
		pp.getModel().addStatement(pp, PIMO.isDefinedBy, pimoUri);
		connection.getResourceStore().createOrUpdate(pimoUri, pp);
		
		share(pp, anna, bob);

		Event event = new Event("tenant", Event.ACTION_RESOURCE_MODIFY, pp);
		trustEngineUpdateService.onReceive(event);
		
		DataContainer ppAfter = connection.getResourceStore().get(pp.asURI(), DataContainer.class);
		PersonGroup groupAfter = connection.getResourceStore().get(groupA.asURI(), PersonGroup.class);

		Assert.assertTrue(ppAfter.getAllIsRelated_as().count() > 0);
		Assert.assertTrue(groupAfter.getAllIsRelated_as().count() > 0);
		Assert.assertTrue(ppAfter.getAllIsRelated().next().equals(groupAfter));
		Assert.assertTrue(groupAfter.getAllIsRelated().next().equals(ppAfter));
	}
	@Test
	public void testOnReceiveLivePostShare() throws Exception{
		LivePost lp = modelFactory.getDLPOFactory().createLivePost();
		livePostManager.add(lp);
		
		PrivacyPreference pp = createPP(lp, PrivacyPreferenceType.LIVEPOST.toString());
		share(pp, groupA.asResource());
		
		Event event = new Event("tenant", Event.ACTION_RESOURCE_MODIFY, lp);
		trustEngineUpdateService.onReceive(event);
		
		LivePost lpAfter = connection.getResourceStore().get(lp.asURI(), LivePost.class);
		PersonGroup groupAfter = connection.getResourceStore().get(groupA.asURI(), PersonGroup.class);
		
		Assert.assertTrue(lpAfter.getAllIsRelated_as().count() > 0);
		Assert.assertTrue(groupAfter.getAllIsRelated_as().count() > 0);
		Assert.assertTrue(lpAfter.getAllIsRelated().next().equals(groupAfter));
		Assert.assertTrue(groupAfter.getAllIsRelated().next().equals(lpAfter));

	}
	@Test
	public void testOnReceiveResourceShare() throws Exception{
		FileDataObject file = buildFile("dummy", 0.3);
		fileManager.add(file);
		
		PrivacyPreference pp = createPP(file, PrivacyPreferenceType.FILE.toString());
		share(pp, groupA.asResource());
		
		Event event = new Event("tenant", Event.ACTION_RESOURCE_MODIFY, file);
		trustEngineUpdateService.onReceive(event);
		
		FileDataObject fileAfter = connection.getResourceStore().get(file.asURI(), FileDataObject.class);
		PersonGroup groupAfter = connection.getResourceStore().get(groupA.asURI(), PersonGroup.class);
		
		Assert.assertTrue(fileAfter.getAllIsRelated_as().count() > 0);
		Assert.assertTrue(groupAfter.getAllIsRelated_as().count() > 0);
		Assert.assertTrue(fileAfter.getAllIsRelated().next().equals(groupAfter));
		Assert.assertTrue(groupAfter.getAllIsRelated().next().equals(fileAfter));
	}
	
	private void share(PrivacyPreference pp, Resource... agents) throws Exception{
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(sender);
		for (Resource agent : agents) {
			accessSpace.addIncludes(agent);
		}
		pp.getModel().addAll(accessSpace.getModel().iterator());
		pp.setAccessSpace(accessSpace);
		connection.getResourceStore().createOrUpdate(pimoUri, pp);
	}
	
	private PrivacyPreference createPP(Resource thing, String label) throws Exception{
		PrivacyPreference pp = modelFactory.getPPOFactory().createPrivacyPreference();

		pp = modelFactory.getPPOFactory().createPrivacyPreference();
		pp.getModel().addStatement(pp, PIMO.isDefinedBy, pimoUri);
		pp.setLabel(label);
		pp.setAppliesToResource(thing);
		connection.getResourceStore().createOrUpdate(pimoUri, pp);
		return pp;
	}

}

