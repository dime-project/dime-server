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

package eu.dime.ps.storage.dtos;

import ie.deri.smile.vocabulary.NCO;

import org.junit.Assert;
import org.junit.Test;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.dto.ProfileAttributeType;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.geo.Point;
import eu.dime.ps.semantic.model.ncal.AlarmAction;
import eu.dime.ps.semantic.model.nco.Affiliation;
import eu.dime.ps.semantic.model.nco.AudioIMAccount;
import eu.dime.ps.semantic.model.nco.BbsNumber;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.CarPhoneNumber;
import eu.dime.ps.semantic.model.nco.CellPhoneNumber;
import eu.dime.ps.semantic.model.nco.DomesticDeliveryAddress;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.FaxNumber;
import eu.dime.ps.semantic.model.nco.Gender;
import eu.dime.ps.semantic.model.nco.Hobby;
import eu.dime.ps.semantic.model.nco.IMAccount;
import eu.dime.ps.semantic.model.nco.IMCapability;
import eu.dime.ps.semantic.model.nco.InternationalDeliveryAddress;
import eu.dime.ps.semantic.model.nco.IsdnNumber;
import eu.dime.ps.semantic.model.nco.MessagingNumber;
import eu.dime.ps.semantic.model.nco.ModemNumber;
import eu.dime.ps.semantic.model.nco.Name;
import eu.dime.ps.semantic.model.nco.PagerNumber;
import eu.dime.ps.semantic.model.nco.ParcelDeliveryAddress;
import eu.dime.ps.semantic.model.nco.PcsNumber;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nco.PostalAddress;
import eu.dime.ps.semantic.model.nco.VideoIMAccount;
import eu.dime.ps.semantic.model.nco.VideoTelephoneNumber;
import eu.dime.ps.semantic.model.nco.VoicePhoneNumber;
import eu.dime.ps.dto.Type;

/**
 * Tests {@link ProfileAttributeType}.
 * 
 * @author Ismael Rivera
 */
public class ProfileAttributeTypeTest extends Assert {

	ModelFactory factory = new ModelFactory();

	@Test
	public void testUnsupportedType() {
		AlarmAction alarmAction = factory.getNCALFactory().createAlarmAction();
		assertNull(Type.get(alarmAction));
	}

	@Test
	public void testRDFTypeOrderIsIrrelevant() {
		// superclass type before subclass type
		Resource att1 = factory.getNCOFactory().createResource(NCO.PhoneNumber);
		att1.getModel().addStatement(att1, RDF.type, NCO.FaxNumber);
		assertEquals(ProfileAttributeType.FAX_NUMBER, ProfileAttributeType.get(att1));

		// subclass type before superclass type
		Resource att2 = factory.getNCOFactory().createResource(NCO.FaxNumber);
		att2.getModel().addStatement(att2, RDF.type, NCO.PhoneNumber);
		assertEquals(ProfileAttributeType.FAX_NUMBER, ProfileAttributeType.get(att2));
	}

	@Test
	public void testEmailAddressType() {
		EmailAddress attribute = factory.getNCOFactory().createEmailAddress();
		assertEquals(ProfileAttributeType.EMAIL_ADDRESS, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testPhoneNumberType() {
		PhoneNumber attribute = factory.getNCOFactory().createPhoneNumber();
		assertEquals(ProfileAttributeType.PHONE_NUMBER, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testPhoneNumberSubtypes() {
		BbsNumber att1 = factory.getNCOFactory().createBbsNumber();
		assertEquals(ProfileAttributeType.BBS_NUMBER, ProfileAttributeType.get(att1));

		CarPhoneNumber att2 = factory.getNCOFactory().createCarPhoneNumber();
		assertEquals(ProfileAttributeType.CAR_PHONE_NUMBER, ProfileAttributeType.get(att2));

		CellPhoneNumber att3 = factory.getNCOFactory().createCellPhoneNumber();
		assertEquals(ProfileAttributeType.CELL_PHONE_NUMBER, ProfileAttributeType.get(att3));

		FaxNumber att4 = factory.getNCOFactory().createFaxNumber();
		assertEquals(ProfileAttributeType.FAX_NUMBER, ProfileAttributeType.get(att4));

		IsdnNumber att5 = factory.getNCOFactory().createIsdnNumber();
		assertEquals(ProfileAttributeType.ISDN_NUMBER, ProfileAttributeType.get(att5));

		MessagingNumber att6 = factory.getNCOFactory().createMessagingNumber();
		assertEquals(ProfileAttributeType.MESSAGING_NUMBER, ProfileAttributeType.get(att6));

		ModemNumber att7 = factory.getNCOFactory().createModemNumber();
		assertEquals(ProfileAttributeType.MODEM_NUMBER, ProfileAttributeType.get(att7));

		PagerNumber att8 = factory.getNCOFactory().createPagerNumber();
		assertEquals(ProfileAttributeType.PAGER_NUMBER, ProfileAttributeType.get(att8));

		PcsNumber att9 = factory.getNCOFactory().createPcsNumber();
		assertEquals(ProfileAttributeType.PCS_NUMBER, ProfileAttributeType.get(att9));

		VideoTelephoneNumber att10 = factory.getNCOFactory().createVideoTelephoneNumber();
		assertEquals(ProfileAttributeType.VIDEO_TELEPHONE_NUMBER, ProfileAttributeType.get(att10));

		VoicePhoneNumber att11 = factory.getNCOFactory().createVoicePhoneNumber();
		assertEquals(ProfileAttributeType.VOICE_PHONE_NUMBER, ProfileAttributeType.get(att11));
	}

	@Test
	public void testIMAccountType() {
		IMAccount attribute = factory.getNCOFactory().createIMAccount();
		assertEquals(ProfileAttributeType.IM_ACCOUNT, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testIMAccountSubtypes() {
		AudioIMAccount att1 = factory.getNCOFactory().createAudioIMAccount();
		assertEquals(ProfileAttributeType.AUDIO_IM_ACCOUNT, ProfileAttributeType.get(att1));
		
		VideoIMAccount att2 = factory.getNCOFactory().createVideoIMAccount();
		assertEquals(ProfileAttributeType.VIDEO_IM_ACCOUNT, ProfileAttributeType.get(att2));
	}

	@Test
	public void testIMCapabilityType() {
		IMCapability attribute = factory.getNCOFactory().createIMCapability();
		assertEquals(ProfileAttributeType.IM_CAPABILITY, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testPostalAddressType() {
		PostalAddress attribute = factory.getNCOFactory().createPostalAddress();
		assertEquals(ProfileAttributeType.POSTAL_ADDRESS, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testPostalAddressSubtypes() {
		DomesticDeliveryAddress att1 = factory.getNCOFactory().createDomesticDeliveryAddress();
		assertEquals(ProfileAttributeType.DOMESTIC_DELIVERY_ADDRESS, ProfileAttributeType.get(att1));
		
		InternationalDeliveryAddress att2 = factory.getNCOFactory().createInternationalDeliveryAddress();
		assertEquals(ProfileAttributeType.INTERNATIONAL_DELIVERY_ADDRESS, ProfileAttributeType.get(att2));
		
		ParcelDeliveryAddress att3 = factory.getNCOFactory().createParcelDeliveryAddress();
		assertEquals(ProfileAttributeType.PARCEL_DELIVERY_ADDRESS, ProfileAttributeType.get(att3));
	}

	@Test
	public void testNameType() {
		Name attribute = factory.getNCOFactory().createName();
		assertEquals(ProfileAttributeType.NAME, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testNameSubtypes() {
		PersonName attribute = factory.getNCOFactory().createPersonName();
		assertEquals(ProfileAttributeType.PERSON_NAME, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testAffiliationType() {
		Affiliation attribute = factory.getNCOFactory().createAffiliation();
		assertEquals(ProfileAttributeType.AFFILIATION, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testBirthDateType() {
		BirthDate attribute = factory.getNCOFactory().createBirthDate();
		assertEquals(ProfileAttributeType.BIRTH_DATE, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testGenderType() {
		Gender attribute = factory.getNCOFactory().createGender();
		assertEquals(ProfileAttributeType.GENDER, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testHobbyType() {
		Hobby attribute = factory.getNCOFactory().createHobby();
		assertEquals(ProfileAttributeType.HOBBY, ProfileAttributeType.get(attribute));
	}

	@Test
	public void testPointType() {
		Point attribute = factory.getGEOFactory().createPoint();
		assertEquals(ProfileAttributeType.POINT, ProfileAttributeType.get(attribute));
	}

}
