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

import ie.deri.smile.vocabulary.NIE;

import java.util.Collection;

import org.junit.Test;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileAttributeManagerImpl;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nie.DataSource;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * Tests {@link ProfileAttributeManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class ProfileAttributeManagerTest extends InfoSphereManagerTest {

	@Autowired
	private ProfileAttributeManagerImpl profileAttributeManager;

	@Autowired
	private PersonManager personManager;

	@Autowired
	private ProfileManager profileManager;
	
	@Test
	public void testExist() throws Exception {
		PersonName name = modelFactory.getNCOFactory().createPersonName();
		name.setFullname("Ismael Rivera");
		profileAttributeManager.add(name);
		assertTrue(profileAttributeManager.exist(name.toString()));
	}

	@Test
	public void testAddAndGet() throws Exception {
		Person person = modelFactory.getPIMOFactory().createPerson();
		PersonName name = modelFactory.getNCOFactory().createPersonName();
		name.setFullname("Ismael Rivera");
		PhoneNumber phone = modelFactory.getNCOFactory().createPhoneNumber();
		phone.setPhoneNumber("123123123");
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		profile.addPersonName(name);
		profile.addPhoneNumber(phone);
		
		personManager.add(person);
		profileManager.add(person, profile);
		profileAttributeManager.add(name);
		profileAttributeManager.add(phone);
		
		Collection<Resource> profileAttributes = profileAttributeManager.getAllByContainer(profile.asURI().toString());
		assertEquals(2, profileAttributes.size());
		assertTrue(profileAttributes.contains(name));
		assertTrue(profileAttributes.contains(phone));
	}
	
	@Test
	public void testUpdate() throws Exception {
		PersonName name = modelFactory.getNCOFactory().createPersonName();
		name.setFullname("Ismael Rivera");
		PhoneNumber phone = modelFactory.getNCOFactory().createPhoneNumber();
		phone.setPhoneNumber("123123123");
		
		profileAttributeManager.add(name);
		profileAttributeManager.add(phone);
		
		PersonName nameRead = (PersonName) profileAttributeManager.get(name.asURI().toString()).castTo(PersonName.class);
		PhoneNumber phoneRead = (PhoneNumber) profileAttributeManager.get(phone.asURI().toString()).castTo(PhoneNumber.class);
		assertEquals("Ismael Rivera", nameRead.getFullname());
		assertEquals("123123123", phoneRead.getPhoneNumber());

		nameRead.setFullname("I. Rivera");
		phoneRead.setPhoneNumber("789789789");
		profileAttributeManager.update(nameRead);
		profileAttributeManager.update(phoneRead);

		PersonName nameUpdated = (PersonName) profileAttributeManager.get(nameRead.asURI().toString()).castTo(PersonName.class);
		PhoneNumber phoneUpdated = (PhoneNumber) profileAttributeManager.get(phoneRead.asURI().toString()).castTo(PhoneNumber.class);
		assertEquals("I. Rivera", nameUpdated.getFullname());
		assertEquals("789789789", phoneUpdated.getPhoneNumber());
	}

	@Test
	public void testUpdateProfileKeepRelationToProfile() throws Exception {
		PersonName name = modelFactory.getNCOFactory().createPersonName();
		name.setFullname("Ismael Rivera");
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		profile.addPersonName(name);
		
		profileManager.add(profile);
		profileAttributeManager.add(name);
		
		PersonName nameRead = (PersonName) profileAttributeManager.get(name.asURI().toString()).castTo(PersonName.class);
		assertEquals("Ismael Rivera", nameRead.getFullname());
		profile = profileManager.get(profile.toString());
		assertEquals(1, profile.getAllPersonName_asNode_().count());
		assertEquals(nameRead.asURI(), profile.getAllPersonName_asNode().next());

		nameRead.setFullname("I. Rivera");
		profileAttributeManager.update(nameRead);

		PersonName nameUpdated = (PersonName) profileAttributeManager.get(nameRead.asURI().toString()).castTo(PersonName.class);
		assertEquals("I. Rivera", nameUpdated.getFullname());
		profile = profileManager.get(profile.toString());
		assertEquals(1, profile.getAllPersonName_asNode_().count());
		assertEquals(nameUpdated.asURI(), profile.getAllPersonName_asNode().next());
	}

	@Test
	public void testRemove() throws Exception {
		Person person = modelFactory.getPIMOFactory().createPerson();
		PersonName name = modelFactory.getNCOFactory().createPersonName();
		name.setFullname("Ismael Rivera");
		PhoneNumber phone = modelFactory.getNCOFactory().createPhoneNumber();
		phone.setPhoneNumber("123123123");
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		profile.addPersonName(name);
		profile.addPhoneNumber(phone);
		
		personManager.add(person);
		profileManager.add(person, profile);
		profileAttributeManager.add(name);
		profileAttributeManager.add(phone);
		
		assertEquals(2, profileAttributeManager.getAllByContainer(profile.asURI().toString()).size());
		profileAttributeManager.remove(name.asURI().toString());
		assertEquals(1, profileAttributeManager.getAllByContainer(profile.asURI().toString()).size());
		profileAttributeManager.remove(phone.asURI().toString());
		assertEquals(0, profileAttributeManager.getAllByContainer(profile.asURI().toString()).size());
	}

	@Test(expected=InfosphereException.class)
	public void testUpdateFromDataSource() throws Exception {
		DataSource twitter = buildDataSource("Twitter");
		PersonName name = modelFactory.getNCOFactory().createPersonName();
		name.setFullname("Ismael Rivera");
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		profile.addPersonName(name);
		profile.getModel().addStatement(profile, NIE.dataSource, twitter);

		resourceStore.create(twitter);
		resourceStore.create(name);
		resourceStore.create(profile);

		try {
			assertNotNull(profileAttributeManager.get(name.toString()));
		} catch (InfosphereException e) {
			fail(e.getMessage());
		}
		name.setFullname("Juanito");
		profileAttributeManager.update(name);
	}

	@Test(expected=InfosphereException.class)
	public void testRemoveFromDataSource() throws Exception {
		DataSource twitter = buildDataSource("Twitter");
		PersonName name = modelFactory.getNCOFactory().createPersonName();
		name.setFullname("Ismael Rivera");
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		profile.addPersonName(name);
		profile.getModel().addStatement(profile, NIE.dataSource, twitter);

		resourceStore.create(twitter);
		resourceStore.create(name);
		resourceStore.create(profile);
		
		try {
			assertNotNull(profileAttributeManager.get(name.toString()));
		} catch (InfosphereException e) {
			fail(e.getMessage());
		}
		profileAttributeManager.remove(name.toString());
	}

}
