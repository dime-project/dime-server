package eu.dime.ps.controllers.infosphere;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;

/**
 * Tests {@link SharingManager}.
 * 
 * @author Ismael Rivera
 */
public class SharingManagerTest extends InfoSphereManagerTest {

	@Autowired
	private SharingManager sharingManager;
	
	@Test
	public void testAddAgentToDatabox() throws Exception {
		PrivacyPreference databox;
		Person ismael, simon;
		PersonGroup aGroup;

		// creates some agents to add to the databoxes
		ismael = modelFactory.getPIMOFactory().createPerson("urn:person:ismael");
		simon = modelFactory.getPIMOFactory().createPerson("urn:person:simon");
		aGroup = modelFactory.getPIMOFactory().createPersonGroup("urn:person:a-group");
		pimoService.createOrUpdate(ismael);
		pimoService.createOrUpdate(simon);
		pimoService.createOrUpdate(aGroup);
		
		// creates databox
		databox = modelFactory.getPPOFactory().createPrivacyPreference();
		databox.setPrefLabel("test");
		databox.setLabel(PrivacyPreferenceType.DATABOX.toString());
		pimoService.createOrUpdate(databox);

		// creates 'sharedThrough' di.me account 
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setCreator(pimoService.getUser());
		pimoService.createOrUpdate(account);

		// sharing databox with agents
		sharingManager.shareDatabox(databox.asURI().toString(), account.toString(),
				new String[]{ ismael.asURI().toString(), aGroup.asURI().toString() });
		
		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), ismael.asURI().toString()));
		assertFalse(sharingManager.hasAccessToDatabox(databox.asURI().toString(), simon.asURI().toString()));
		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), aGroup.asURI().toString()));
	}
	
	@Test(expected=InfosphereException.class)
	public void testAddInvalidAgentToDatabox() throws Exception {
		PrivacyPreference databox = modelFactory.getPPOFactory().createPrivacyPreference();
		databox.setPrefLabel("test");
		databox.setLabel(PrivacyPreferenceType.DATABOX.toString());
		pimoService.createOrUpdate(databox);

		// creates 'sharedThrough' di.me account 
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setCreator(pimoService.getUser());
		pimoService.createOrUpdate(account);

		// sharing databox with non-existing agent should fail
		sharingManager.shareDatabox(databox.asURI().toString(), account.toString(), new String[]{ "urn:non-existing-agent" });
	}
	
	@Test
	public void testFindPrivacyPreference() throws Exception {
		FileDataObject file = modelFactory.getNFOFactory().createFileDataObject();
		file.setPrefLabel("myfile");
		pimoService.createOrUpdate(file);
		
		Person ismael = modelFactory.getPIMOFactory().createPerson();
		pimoService.createOrUpdate(ismael);

		// creates 'sharedThrough' di.me account 
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setCreator(pimoService.getUser());
		pimoService.createOrUpdate(account);

		sharingManager.shareFile(file.toString(), account.toString(), new String[]{ ismael.toString() });
		
		PrivacyPreference ppFile = sharingManager.findPrivacyPreference(file.toString(), PrivacyPreferenceType.FILE);
		assertNotNull(ppFile);
		assertTrue(ppFile.hasAppliesToResource());
		assertEquals(file.asURI(), ppFile.getAllAppliesToResource().next().asURI());
		
		assertTrue(ppFile.hasAccessSpace());
		AccessSpace accessSpace = (AccessSpace) ppFile.getAllAccessSpace().next().castTo(AccessSpace.class);
		assertTrue(accessSpace.hasIncludes());
		assertEquals(ismael.asURI(), accessSpace.getAllIncludes().next().asURI());
	}
	
	@Test
	public void testAccountHasAccessToDatabox() throws Exception {
		// creates a person to add to the databox
		Person ismael = modelFactory.getPIMOFactory().createPerson();
		pimoService.createOrUpdate(ismael);
		
		// creates di.me accounts for the person
		Account ismaelAccount = modelFactory.getDAOFactory().createAccount();
		ismaelAccount.setAccountType(DimeServiceAdapter.NAME);
		ismaelAccount.setCreator(ismael);
		pimoService.createOrUpdate(ismaelAccount);

		// creates di.me account to use as 'sender'
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setCreator(pimoService.getUser());
		pimoService.createOrUpdate(account);

		// setting up databox and access spaces
		PrivacyPreference databox = modelFactory.getPPOFactory().createPrivacyPreference();
		databox.setLabel(PrivacyPreferenceType.DATABOX.toString());
		databox.setCreator(pimoService.getUser());
		
		AccessSpace space = modelFactory.getNSOFactory().createAccessSpace();
		space.setSharedThrough(account);
		space.addIncludes(ismaelAccount);
		databox.getModel().addAll(space.getModel().iterator());
		databox.addAccessSpace(space);
		
		// saving databox metadata
		pimoService.createOrUpdate(databox);

		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), ismaelAccount.asURI().toString()));
		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), ismael.asURI().toString()));
	}
	
	@Test
	public void testPersonHasAccessToDatabox() throws Exception {
		// creates some agents to add to the databox
		Person ismael = modelFactory.getPIMOFactory().createPerson();
		Person simon = modelFactory.getPIMOFactory().createPerson();
		pimoService.createOrUpdate(ismael);
		pimoService.createOrUpdate(simon);
		
		// creates di.me account to use as 'sender'
		Account accountA = modelFactory.getDAOFactory().createAccount();
		accountA.setAccountType(DimeServiceAdapter.NAME);
		accountA.setCreator(pimoService.getUser());
		Account accountB = modelFactory.getDAOFactory().createAccount();
		accountB.setAccountType(DimeServiceAdapter.NAME);
		accountB.setCreator(pimoService.getUser());
		pimoService.createOrUpdate(accountA);
		pimoService.createOrUpdate(accountB);

		// setting up databox and access spaces
		PrivacyPreference databox = modelFactory.getPPOFactory().createPrivacyPreference();
		databox.setLabel(PrivacyPreferenceType.DATABOX.toString());
		databox.setCreator(pimoService.getUser());
		
		AccessSpace spaceA = modelFactory.getNSOFactory().createAccessSpace();
		spaceA.setSharedThrough(accountA);
		spaceA.addIncludes(ismael);
		databox.getModel().addAll(spaceA.getModel().iterator());
		databox.addAccessSpace(spaceA);
		
		AccessSpace spaceB = modelFactory.getNSOFactory().createAccessSpace();
		spaceB.setSharedThrough(accountB);
		spaceB.addIncludes(simon);
		databox.getModel().addAll(spaceB.getModel().iterator());
		databox.addAccessSpace(spaceB);

		// saving databox metadata
		pimoService.createOrUpdate(databox);

		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), ismael.asURI().toString()));
		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), simon.asURI().toString()));
	}

	@Test
	public void testGroupMemberHasAccessToDatabox() throws Exception {
		// creates some agents to add to the databox
		Person ismael = modelFactory.getPIMOFactory().createPerson();
		Person simon = modelFactory.getPIMOFactory().createPerson();
		pimoService.createOrUpdate(ismael);
		pimoService.createOrUpdate(simon);
		
		// creates a group
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.addMember(ismael);
		group.addMember(simon);
		pimoService.createOrUpdate(group);

		// creates di.me account to use as 'sender'
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setCreator(pimoService.getUser());
		pimoService.createOrUpdate(account);

		// setting up databox and access spaces
		PrivacyPreference databox = modelFactory.getPPOFactory().createPrivacyPreference();
		databox.setLabel(PrivacyPreferenceType.DATABOX.toString());
		databox.setCreator(pimoService.getUser());
		
		AccessSpace space = modelFactory.getNSOFactory().createAccessSpace();
		space.setSharedThrough(account);
		space.addIncludes(group);
		databox.getModel().addAll(space.getModel().iterator());
		databox.addAccessSpace(space);
		
		// saving databox metadata
		pimoService.createOrUpdate(databox);

		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), group.asURI().toString()));
		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), ismael.asURI().toString()));
		assertTrue(sharingManager.hasAccessToDatabox(databox.asURI().toString(), simon.asURI().toString()));
	}
	

}
