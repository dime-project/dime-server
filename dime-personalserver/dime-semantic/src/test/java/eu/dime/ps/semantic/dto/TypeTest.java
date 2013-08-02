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

package eu.dime.ps.semantic.dto;

import ie.deri.smile.vocabulary.NFO;

import org.junit.Assert;
import org.junit.Test;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.dime.ps.dto.Type;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.ddo.Device;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.ncal.AlarmAction;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nfo.Placemark;
import eu.dime.ps.semantic.model.pimo.Location;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.pimo.SocialEvent;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;

/**
 * Tests {@link Type}.
 * 
 * @author Ismael Rivera
 */
public class TypeTest extends Assert {

	ModelFactory factory = new ModelFactory();

	@Test
	public void testToStringReturnsTypeLabel() {
		assertEquals(Type.PRIVACY_PREFERENCE_LIVESTREAM.getLabel(), Type.PRIVACY_PREFERENCE_LIVESTREAM.toString());
	}
	
	@Test
	public void testUnsupportedType() {
		AlarmAction alarmAction = factory.getNCALFactory().createAlarmAction();
		assertNull(Type.get(alarmAction));
	}

	@Test
	public void testPrivacyPreferenceProfileCardType() {
		PrivacyPreference preference = factory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.PROFILECARD.toString());
		assertEquals(Type.PRIVACY_PREFERENCE_PROFILECARD, Type.get(preference));
	}

	@Test
	public void testPrivacyPreferenceLivePostType() {
		PrivacyPreference preference = factory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.toString());
		assertEquals(Type.PRIVACY_PREFERENCE_LIVEPOST, Type.get(preference));
	}

	@Test
	public void testPrivacyPreferenceFileType() {
		PrivacyPreference preference = factory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.FILE.toString());
		assertEquals(Type.PRIVACY_PREFERENCE_FILE, Type.get(preference));
	}

	@Test
	public void testAccountType() {
		Account account = factory.getDAOFactory().createAccount();
		assertEquals(Type.ACCOUNT, Type.get(account));
	}

	@Test
	public void testDataboxType() {
		PrivacyPreference databox = factory.getPPOFactory().createPrivacyPreference();
		databox.getModel().addStatement(databox, RDF.type, NFO.DataContainer);
		databox.setLabel(PrivacyPreferenceType.DATABOX.toString());
		assertEquals(Type.DATABOX, Type.get(databox));
	}

	@Test
	public void testDeviceType() {
		Device device = factory.getDDOFactory().createDevice();
		assertEquals(Type.DEVICE, Type.get(device));
	}

	@Test
	public void testEventType() {
		SocialEvent event = factory.getPIMOFactory().createSocialEvent();
		assertEquals(Type.EVENT, Type.get(event));
	}

	@Test
	public void testPersonGroupType() {
		PersonGroup group = factory.getPIMOFactory().createPersonGroup();
		assertEquals(Type.GROUP, Type.get(group));
	}

	@Test
	public void testFileDataObjectType() {
		FileDataObject resource = factory.getNFOFactory().createFileDataObject();
		assertEquals(Type.FILE_DATA_OBJECT, Type.get(resource));
	}

	@Test
	public void testLivePostType() {
		LivePost livepost = factory.getDLPOFactory().createLivePost();
		assertEquals(Type.LIVEPOST, Type.get(livepost));
	}

	@Test
	public void testLocationType() {
		Location location = factory.getPIMOFactory().createLocation();
		assertEquals(Type.LOCATION, Type.get(location));
	}

	@Test
	public void testPersonType() {
		Person person = factory.getPIMOFactory().createPerson();
		assertEquals(Type.PERSON, Type.get(person));
	}
	
	@Test
	public void testPlacemarkType() {
		Placemark placemark = factory.getNFOFactory().createPlacemark();
		assertEquals(Type.PLACEMARK, Type.get(placemark));
	}

	@Test
	public void testProfileType() {
		PersonContact contact = factory.getNCOFactory().createPersonContact();
		assertEquals(Type.PROFILE, Type.get(contact));
	}

}
