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

package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.nco.Affiliation;
import eu.dime.ps.semantic.model.nco.AudioIMAccount;
import eu.dime.ps.semantic.model.nco.BbsNumber;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.CarPhoneNumber;
import eu.dime.ps.semantic.model.nco.CellPhoneNumber;
import eu.dime.ps.semantic.model.nco.Contact;
import eu.dime.ps.semantic.model.nco.ContactGroup;
import eu.dime.ps.semantic.model.nco.ContactList;
import eu.dime.ps.semantic.model.nco.ContactListDataObject;
import eu.dime.ps.semantic.model.nco.ContactMedium;
import eu.dime.ps.semantic.model.nco.DomesticDeliveryAddress;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.FaxNumber;
import eu.dime.ps.semantic.model.nco.Gender;
import eu.dime.ps.semantic.model.nco.Hobby;
import eu.dime.ps.semantic.model.nco.IMAccount;
import eu.dime.ps.semantic.model.nco.IMCapability;
import eu.dime.ps.semantic.model.nco.IMStatusType;
import eu.dime.ps.semantic.model.nco.InternationalDeliveryAddress;
import eu.dime.ps.semantic.model.nco.IsdnNumber;
import eu.dime.ps.semantic.model.nco.MessagingNumber;
import eu.dime.ps.semantic.model.nco.ModemNumber;
import eu.dime.ps.semantic.model.nco.Name;
import eu.dime.ps.semantic.model.nco.OrganizationContact;
import eu.dime.ps.semantic.model.nco.PagerNumber;
import eu.dime.ps.semantic.model.nco.ParcelDeliveryAddress;
import eu.dime.ps.semantic.model.nco.PcsNumber;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nco.PostalAddress;
import eu.dime.ps.semantic.model.nco.Role;
import eu.dime.ps.semantic.model.nco.VideoIMAccount;
import eu.dime.ps.semantic.model.nco.VideoTelephoneNumber;
import eu.dime.ps.semantic.model.nco.VoicePhoneNumber;

/**
 * A factory for the Java classes generated automatically for the NCO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NCOFactory extends ResourceFactory {

	public Affiliation createAffiliation() {
		return new Affiliation(createModel(), generateUniqueURI(), true);
	}

	public Affiliation createAffiliation(URI resourceUri) {
		return new Affiliation(createModel(), resourceUri, true);
	}

	public Affiliation createAffiliation(String resourceUriString) {
		return new Affiliation(createModel(), new URIImpl(resourceUriString), true);
	}

	public AudioIMAccount createAudioIMAccount() {
		return new AudioIMAccount(createModel(), generateUniqueURI(), true);
	}

	public AudioIMAccount createAudioIMAccount(URI resourceUri) {
		return new AudioIMAccount(createModel(), resourceUri, true);
	}

	public AudioIMAccount createAudioIMAccount(String resourceUriString) {
		return new AudioIMAccount(createModel(), new URIImpl(resourceUriString), true);
	}

	public BbsNumber createBbsNumber() {
		return new BbsNumber(createModel(), generateUniqueURI(), true);
	}

	public BbsNumber createBbsNumber(URI resourceUri) {
		return new BbsNumber(createModel(), resourceUri, true);
	}

	public BbsNumber createBbsNumber(String resourceUriString) {
		return new BbsNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public BirthDate createBirthDate() {
		return new BirthDate(createModel(), generateUniqueURI(), true);
	}

	public BirthDate createBirthDate(URI resourceUri) {
		return new BirthDate(createModel(), resourceUri, true);
	}

	public BirthDate createBirthDate(String resourceUriString) {
		return new BirthDate(createModel(), new URIImpl(resourceUriString), true);
	}

	public CarPhoneNumber createCarPhoneNumber() {
		return new CarPhoneNumber(createModel(), generateUniqueURI(), true);
	}

	public CarPhoneNumber createCarPhoneNumber(URI resourceUri) {
		return new CarPhoneNumber(createModel(), resourceUri, true);
	}

	public CarPhoneNumber createCarPhoneNumber(String resourceUriString) {
		return new CarPhoneNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public CellPhoneNumber createCellPhoneNumber() {
		return new CellPhoneNumber(createModel(), generateUniqueURI(), true);
	}

	public CellPhoneNumber createCellPhoneNumber(URI resourceUri) {
		return new CellPhoneNumber(createModel(), resourceUri, true);
	}

	public CellPhoneNumber createCellPhoneNumber(String resourceUriString) {
		return new CellPhoneNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public Contact createContact() {
		return new Contact(createModel(), generateUniqueURI(), true);
	}

	public Contact createContact(URI resourceUri) {
		return new Contact(createModel(), resourceUri, true);
	}

	public Contact createContact(String resourceUriString) {
		return new Contact(createModel(), new URIImpl(resourceUriString), true);
	}

	public ContactGroup createContactGroup() {
		return new ContactGroup(createModel(), generateUniqueURI(), true);
	}

	public ContactGroup createContactGroup(URI resourceUri) {
		return new ContactGroup(createModel(), resourceUri, true);
	}

	public ContactGroup createContactGroup(String resourceUriString) {
		return new ContactGroup(createModel(), new URIImpl(resourceUriString), true);
	}

	public ContactList createContactList() {
		return new ContactList(createModel(), generateUniqueURI(), true);
	}

	public ContactList createContactList(URI resourceUri) {
		return new ContactList(createModel(), resourceUri, true);
	}

	public ContactList createContactList(String resourceUriString) {
		return new ContactList(createModel(), new URIImpl(resourceUriString), true);
	}

	public ContactListDataObject createContactListDataObject() {
		return new ContactListDataObject(createModel(), generateUniqueURI(), true);
	}

	public ContactListDataObject createContactListDataObject(URI resourceUri) {
		return new ContactListDataObject(createModel(), resourceUri, true);
	}

	public ContactListDataObject createContactListDataObject(String resourceUriString) {
		return new ContactListDataObject(createModel(), new URIImpl(resourceUriString), true);
	}

	public ContactMedium createContactMedium() {
		return new ContactMedium(createModel(), generateUniqueURI(), true);
	}

	public ContactMedium createContactMedium(URI resourceUri) {
		return new ContactMedium(createModel(), resourceUri, true);
	}

	public ContactMedium createContactMedium(String resourceUriString) {
		return new ContactMedium(createModel(), new URIImpl(resourceUriString), true);
	}

	public DomesticDeliveryAddress createDomesticDeliveryAddress() {
		return new DomesticDeliveryAddress(createModel(), generateUniqueURI(), true);
	}

	public DomesticDeliveryAddress createDomesticDeliveryAddress(URI resourceUri) {
		return new DomesticDeliveryAddress(createModel(), resourceUri, true);
	}

	public DomesticDeliveryAddress createDomesticDeliveryAddress(String resourceUriString) {
		return new DomesticDeliveryAddress(createModel(), new URIImpl(resourceUriString), true);
	}

	public EmailAddress createEmailAddress() {
		return new EmailAddress(createModel(), generateUniqueURI(), true);
	}

	public EmailAddress createEmailAddress(URI resourceUri) {
		return new EmailAddress(createModel(), resourceUri, true);
	}

	public EmailAddress createEmailAddress(String resourceUriString) {
		return new EmailAddress(createModel(), new URIImpl(resourceUriString), true);
	}

	public FaxNumber createFaxNumber() {
		return new FaxNumber(createModel(), generateUniqueURI(), true);
	}

	public FaxNumber createFaxNumber(URI resourceUri) {
		return new FaxNumber(createModel(), resourceUri, true);
	}

	public FaxNumber createFaxNumber(String resourceUriString) {
		return new FaxNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public Gender createGender() {
		return new Gender(createModel(), generateUniqueURI(), true);
	}

	public Gender createGender(URI resourceUri) {
		return new Gender(createModel(), resourceUri, true);
	}

	public Gender createGender(String resourceUriString) {
		return new Gender(createModel(), new URIImpl(resourceUriString), true);
	}

	public Hobby createHobby() {
		return new Hobby(createModel(), generateUniqueURI(), true);
	}

	public Hobby createHobby(URI resourceUri) {
		return new Hobby(createModel(), resourceUri, true);
	}

	public Hobby createHobby(String resourceUriString) {
		return new Hobby(createModel(), new URIImpl(resourceUriString), true);
	}

	public IMAccount createIMAccount() {
		return new IMAccount(createModel(), generateUniqueURI(), true);
	}

	public IMAccount createIMAccount(URI resourceUri) {
		return new IMAccount(createModel(), resourceUri, true);
	}

	public IMAccount createIMAccount(String resourceUriString) {
		return new IMAccount(createModel(), new URIImpl(resourceUriString), true);
	}

	public IMCapability createIMCapability() {
		return new IMCapability(createModel(), generateUniqueURI(), true);
	}

	public IMCapability createIMCapability(URI resourceUri) {
		return new IMCapability(createModel(), resourceUri, true);
	}

	public IMCapability createIMCapability(String resourceUriString) {
		return new IMCapability(createModel(), new URIImpl(resourceUriString), true);
	}

	public IMStatusType createIMStatusType() {
		return new IMStatusType(createModel(), generateUniqueURI(), true);
	}

	public IMStatusType createIMStatusType(URI resourceUri) {
		return new IMStatusType(createModel(), resourceUri, true);
	}

	public IMStatusType createIMStatusType(String resourceUriString) {
		return new IMStatusType(createModel(), new URIImpl(resourceUriString), true);
	}

	public InternationalDeliveryAddress createInternationalDeliveryAddress() {
		return new InternationalDeliveryAddress(createModel(), generateUniqueURI(), true);
	}

	public InternationalDeliveryAddress createInternationalDeliveryAddress(URI resourceUri) {
		return new InternationalDeliveryAddress(createModel(), resourceUri, true);
	}

	public InternationalDeliveryAddress createInternationalDeliveryAddress(String resourceUriString) {
		return new InternationalDeliveryAddress(createModel(), new URIImpl(resourceUriString), true);
	}

	public IsdnNumber createIsdnNumber() {
		return new IsdnNumber(createModel(), generateUniqueURI(), true);
	}

	public IsdnNumber createIsdnNumber(URI resourceUri) {
		return new IsdnNumber(createModel(), resourceUri, true);
	}

	public IsdnNumber createIsdnNumber(String resourceUriString) {
		return new IsdnNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public MessagingNumber createMessagingNumber() {
		return new MessagingNumber(createModel(), generateUniqueURI(), true);
	}

	public MessagingNumber createMessagingNumber(URI resourceUri) {
		return new MessagingNumber(createModel(), resourceUri, true);
	}

	public MessagingNumber createMessagingNumber(String resourceUriString) {
		return new MessagingNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public ModemNumber createModemNumber() {
		return new ModemNumber(createModel(), generateUniqueURI(), true);
	}

	public ModemNumber createModemNumber(URI resourceUri) {
		return new ModemNumber(createModel(), resourceUri, true);
	}

	public ModemNumber createModemNumber(String resourceUriString) {
		return new ModemNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public Name createName() {
		return new Name(createModel(), generateUniqueURI(), true);
	}

	public Name createName(URI resourceUri) {
		return new Name(createModel(), resourceUri, true);
	}

	public Name createName(String resourceUriString) {
		return new Name(createModel(), new URIImpl(resourceUriString), true);
	}

	public OrganizationContact createOrganizationContact() {
		return new OrganizationContact(createModel(), generateUniqueURI(), true);
	}

	public OrganizationContact createOrganizationContact(URI resourceUri) {
		return new OrganizationContact(createModel(), resourceUri, true);
	}

	public OrganizationContact createOrganizationContact(String resourceUriString) {
		return new OrganizationContact(createModel(), new URIImpl(resourceUriString), true);
	}

	public PagerNumber createPagerNumber() {
		return new PagerNumber(createModel(), generateUniqueURI(), true);
	}

	public PagerNumber createPagerNumber(URI resourceUri) {
		return new PagerNumber(createModel(), resourceUri, true);
	}

	public PagerNumber createPagerNumber(String resourceUriString) {
		return new PagerNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public ParcelDeliveryAddress createParcelDeliveryAddress() {
		return new ParcelDeliveryAddress(createModel(), generateUniqueURI(), true);
	}

	public ParcelDeliveryAddress createParcelDeliveryAddress(URI resourceUri) {
		return new ParcelDeliveryAddress(createModel(), resourceUri, true);
	}

	public ParcelDeliveryAddress createParcelDeliveryAddress(String resourceUriString) {
		return new ParcelDeliveryAddress(createModel(), new URIImpl(resourceUriString), true);
	}

	public PcsNumber createPcsNumber() {
		return new PcsNumber(createModel(), generateUniqueURI(), true);
	}

	public PcsNumber createPcsNumber(URI resourceUri) {
		return new PcsNumber(createModel(), resourceUri, true);
	}

	public PcsNumber createPcsNumber(String resourceUriString) {
		return new PcsNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public PersonContact createPersonContact() {
		return new PersonContact(createModel(), generateUniqueURI(), true);
	}

	public PersonContact createPersonContact(URI resourceUri) {
		return new PersonContact(createModel(), resourceUri, true);
	}

	public PersonContact createPersonContact(String resourceUriString) {
		return new PersonContact(createModel(), new URIImpl(resourceUriString), true);
	}

	public PersonName createPersonName() {
		return new PersonName(createModel(), generateUniqueURI(), true);
	}

	public PersonName createPersonName(URI resourceUri) {
		return new PersonName(createModel(), resourceUri, true);
	}

	public PersonName createPersonName(String resourceUriString) {
		return new PersonName(createModel(), new URIImpl(resourceUriString), true);
	}

	public PhoneNumber createPhoneNumber() {
		return new PhoneNumber(createModel(), generateUniqueURI(), true);
	}

	public PhoneNumber createPhoneNumber(URI resourceUri) {
		return new PhoneNumber(createModel(), resourceUri, true);
	}

	public PhoneNumber createPhoneNumber(String resourceUriString) {
		return new PhoneNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public PostalAddress createPostalAddress() {
		return new PostalAddress(createModel(), generateUniqueURI(), true);
	}

	public PostalAddress createPostalAddress(URI resourceUri) {
		return new PostalAddress(createModel(), resourceUri, true);
	}

	public PostalAddress createPostalAddress(String resourceUriString) {
		return new PostalAddress(createModel(), new URIImpl(resourceUriString), true);
	}

	public Role createRole() {
		return new Role(createModel(), generateUniqueURI(), true);
	}

	public Role createRole(URI resourceUri) {
		return new Role(createModel(), resourceUri, true);
	}

	public Role createRole(String resourceUriString) {
		return new Role(createModel(), new URIImpl(resourceUriString), true);
	}

	public VideoIMAccount createVideoIMAccount() {
		return new VideoIMAccount(createModel(), generateUniqueURI(), true);
	}

	public VideoIMAccount createVideoIMAccount(URI resourceUri) {
		return new VideoIMAccount(createModel(), resourceUri, true);
	}

	public VideoIMAccount createVideoIMAccount(String resourceUriString) {
		return new VideoIMAccount(createModel(), new URIImpl(resourceUriString), true);
	}

	public VideoTelephoneNumber createVideoTelephoneNumber() {
		return new VideoTelephoneNumber(createModel(), generateUniqueURI(), true);
	}

	public VideoTelephoneNumber createVideoTelephoneNumber(URI resourceUri) {
		return new VideoTelephoneNumber(createModel(), resourceUri, true);
	}

	public VideoTelephoneNumber createVideoTelephoneNumber(String resourceUriString) {
		return new VideoTelephoneNumber(createModel(), new URIImpl(resourceUriString), true);
	}

	public VoicePhoneNumber createVoicePhoneNumber() {
		return new VoicePhoneNumber(createModel(), generateUniqueURI(), true);
	}

	public VoicePhoneNumber createVoicePhoneNumber(URI resourceUri) {
		return new VoicePhoneNumber(createModel(), resourceUri, true);
	}

	public VoicePhoneNumber createVoicePhoneNumber(String resourceUriString) {
		return new VoicePhoneNumber(createModel(), new URIImpl(resourceUriString), true);
	}

}