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

package eu.dime.ps.controllers.infosphere;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManagerImpl;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.model.ObjectFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;

/**
 * Tests {@link ProfileCardManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class ProfileCardManagerTest extends InfoSphereManagerTest {

	@Autowired
	private ProfileCardManagerImpl profileCardManager;

	private Person me = null, someone = null;
	private Account myAccount = null, otherAccount = null, someoneAccount = null;
	private PersonName personName = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		me = profileCardManager.getMe();
		someone = ObjectFactory.buildPerson("John Doe");
		myAccount = ObjectFactory.buildAccount("My account", DimeServiceAdapter.NAME, me.asURI());
		otherAccount = ObjectFactory.buildAccount("My other account", DimeServiceAdapter.NAME, me.asURI());
		someoneAccount = ObjectFactory.buildAccount("Someone's account", DimeServiceAdapter.NAME, someone.asURI());
		personName = ObjectFactory.buildPersonName("Ismael Rivera");
		pimoService.create(myAccount);
		pimoService.create(someone);
		pimoService.create(someoneAccount);
		pimoService.create(personName);
	}

	
	@Test
	public void testExist() throws Exception {
		Resource[] attributes = new Resource[]{ personName };
		Account[] recipients = new Account[]{ someoneAccount };
		PrivacyPreference testCard = ObjectFactory.buildProfileCard("Test card", attributes, me.asURI(), myAccount.asURI(), recipients);
		profileCardManager.add(testCard);
		assertTrue(profileCardManager.exist(testCard.toString()));
	}

	@Test
	public void testAdd() throws Exception {
		Resource[] attributes = new Resource[]{ personName };
		Account[] recipients = new Account[]{ someoneAccount };
		PrivacyPreference testCard = ObjectFactory.buildProfileCard("Test card", attributes, me.asURI(), myAccount.asURI(), recipients);
		
		// persists profile card
		profileCardManager.add(testCard);
		
		PrivacyPreference card = profileCardManager.get(testCard.asURI().toString());
		assertEquals(testCard.asURI(), card.asURI());
		assertTrue(card.getAllLabel_as().asList().contains(PrivacyPreferenceType.PROFILECARD.toString()));
		assertEquals(testCard.getPrefLabel(), card.getPrefLabel());
		assertEquals(testCard.getCreator(), card.getCreator());
		assertEquals(testCard.getAllAppliesToResource_as().asList(), card.getAllAppliesToResource_as().asList());
		assertEquals(testCard.getAllAccessSpace_as().asList(), card.getAllAccessSpace_as().asList());
		
		URI accessSpace = card.getAllAccessSpace().next().asURI();
		assertEquals(myAccount.asURI(), ModelUtils.findObject(card.getModel(), accessSpace, NSO.sharedThrough));
		assertEquals(someoneAccount.asURI(), ModelUtils.findObject(card.getModel(), accessSpace, NSO.includes));
	}
	
	@Test
	public void testGetAllByPerson() throws Exception {
		Resource[] attributes = new Resource[]{ personName };
		Account[] recipients = new Account[]{ someoneAccount };
		PrivacyPreference testCard = ObjectFactory.buildProfileCard("Test card", attributes, me.asURI(), myAccount.asURI(), recipients);
		
		// persists profile card
		profileCardManager.add(testCard);
		Person me = profileCardManager.getMe();
		
		Collection<PrivacyPreference> cards = profileCardManager.getAllByPerson(me);
		assertEquals(testCard.asURI(), cards.iterator().next().asURI());
		assertTrue(cards.iterator().next().getAllLabel_as().asList().contains(PrivacyPreferenceType.PROFILECARD.toString()));
		assertEquals(testCard.getPrefLabel(), cards.iterator().next().getPrefLabel());
		assertEquals(testCard.getCreator(), cards.iterator().next().getCreator());
		assertEquals(testCard.getAllAppliesToResource_as().asList(), cards.iterator().next().getAllAppliesToResource_as().asList());
		assertEquals(testCard.getAllAccessSpace_as().asList(), cards.iterator().next().getAllAccessSpace_as().asList());
		
		URI accessSpace = cards.iterator().next().getAllAccessSpace().next().asURI();
		assertEquals(myAccount.asURI(), ModelUtils.findObject(cards.iterator().next().getModel(), accessSpace, NSO.sharedThrough));
		assertEquals(someoneAccount.asURI(), ModelUtils.findObject(cards.iterator().next().getModel(), accessSpace, NSO.includes));
	}

	@Test
	@ExpectedException(InfosphereException.class)
	public void testAddFailDueToOneAccountRestriction() throws Exception {
		Resource[] attributes = new Resource[]{ personName };
		Account[] recipients = new Account[]{ someoneAccount };
		PrivacyPreference testCard = ObjectFactory.buildProfileCard("Test card", attributes, me.asURI(), myAccount.asURI(), recipients);
		PrivacyPreference otherCard = ObjectFactory.buildProfileCard("Other card", attributes, me.asURI(), myAccount.asURI(), recipients);
		
		try {
			// persists profile card, should be successfully, otherwise test should fail
			profileCardManager.add(testCard);
		} catch(InfosphereException e) {
			fail(e.getMessage());
		}
		
		// try to persist another profile card for the same di.me account should raise an exception
		profileCardManager.add(otherCard);
	}

	@Test
	public void testUpdateSuccessfully() throws Exception {
		Account[] recipients = new Account[]{ someoneAccount };
		PrivacyPreference testCard = ObjectFactory.buildProfileCard("Test card", new Resource[]{}, me.asURI(), myAccount.asURI(), recipients);
		
		// persists profile card
		profileCardManager.add(testCard);
		
		// modifies profile card and persists update
		testCard.setPrefLabel("Updated card");
		testCard.setAppliesToResource(personName);
		profileCardManager.update(testCard);
		
		PrivacyPreference card = profileCardManager.get(testCard.asURI().toString());
		assertEquals(testCard.asURI(), card.asURI());
		assertTrue(card.getAllLabel_as().asList().contains(PrivacyPreferenceType.PROFILECARD.toString()));
		assertEquals(testCard.getPrefLabel(), card.getPrefLabel());
		assertEquals(testCard.getCreator(), card.getCreator());
		assertEquals(testCard.getAllAppliesToResource_as().asList(), card.getAllAppliesToResource_as().asList());
		assertEquals(testCard.getAllAccessSpace_as().asList(), card.getAllAccessSpace_as().asList());
		
		URI accessSpace = card.getAllAccessSpace().next().asURI();
		assertEquals(myAccount.asURI(), ModelUtils.findObject(card.getModel(), accessSpace, NSO.sharedThrough));
		assertEquals(someoneAccount.asURI(), ModelUtils.findObject(card.getModel(), accessSpace, NSO.includes));
	}

	@Test
	@ExpectedException(InfosphereException.class)
	public void testUpdateFailDueToOneAccountRestriction() throws Exception {
		Resource[] attributes = new Resource[]{ personName };
		Account[] recipients = new Account[]{ someoneAccount };
		PrivacyPreference testCardA = ObjectFactory.buildProfileCard("Test card A", attributes, me.asURI(), myAccount.asURI(), recipients);
		PrivacyPreference testCardB = ObjectFactory.buildProfileCard("Test card B", attributes, me.asURI(), otherAccount.asURI(), recipients);

		// persists both test profile cards
		profileCardManager.add(testCardA);
		profileCardManager.add(testCardB);

		// modifies sharedThrough of one of the cards, and attempt to persist changes
		URI accessSpace = ModelUtils.findObject(testCardB.getModel(), testCardB, PPO.hasAccessSpace).asURI();
		testCardB.getModel().removeStatements(accessSpace, NSO.sharedThrough, Variable.ANY);
		testCardB.getModel().addStatement(accessSpace, NSO.sharedThrough, myAccount.asURI());
		profileCardManager.add(testCardB);
	}

}
