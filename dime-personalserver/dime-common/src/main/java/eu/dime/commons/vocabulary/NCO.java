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

package eu.dime.commons.vocabulary;

public interface NCO {
	
	public static final String NS_NCO = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#";

    /**
     * Label: VoicePhoneNumber 
     * Comment: A telephone number with voice communication capabilities. Class inspired by the TYPE=voice parameter of the TEL property defined in RFC 2426 sec. 3.3.1 
     */
    public static final String VoicePhoneNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#VoicePhoneNumber";

    /**
     * Label: VideoTelephoneNumber 
     * Comment: A Video telephone number. A class inspired by the TYPE=video parameter of the TEL property defined in RFC 2426 sec. 3.3.1 
     */
    public static final String VideoTelephoneNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#VideoTelephoneNumber";

    /**
     * Label: IMAccount 
     * Comment: An account in an Instant Messaging system. 
     */
    public static final String IMAccount = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount";

    /**
     * Label: IsdnNumber 
     * Comment: An ISDN phone number. Inspired by the (TYPE=isdn) parameter of the TEL property as defined in RFC 2426 sec  3.3.1. 
     */
    public static final String IsdnNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IsdnNumber";

    /**
     * Label: instant messaging status type 
     * Comment: The status type of an IMAccount. Based on the Connection_Presence_Type enumeration of the Telepathy project: http://telepathy.freedesktop.org/spec/Connection_Interface_Simple_Presence.html#Enum:Connection_Presence_Type 
     */
    public static final String IMStatusType = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMStatusType";

    /**
     * Label: PcsNumber 
     * Comment: Personal Communication Services Number. A class inspired by the TYPE=pcs parameter of the TEL property defined in RFC 2426 sec. 3.3.1 
     */
    public static final String PcsNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PcsNumber";

    /**
     * Label: ContactList 
     * Comment: A contact list, this class represents an addressbook or a contact list of an IM application. Contacts inside a contact list can belong to contact groups. 
     */
    public static final String ContactList = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactList";

    /**
     * Label: ContactGroup 
     * Comment: A group of Contacts. Could be used to express a group in an addressbook or on a contact list of an IM application. One contact can belong to many groups. 
     */
    public static final String ContactGroup = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactGroup";

    /**
     * Label: BbsNumber 
     * Comment: A Bulletin Board System (BBS) phone number. Inspired by the (TYPE=bbsl) parameter of the TEL property as defined in RFC 2426 sec  3.3.1. 
     */
    public static final String BbsNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#BbsNumber";

    /**
     * Label: Affiliation 
     * Comment: Aggregates three properties defined in RFC2426. Originally all three were attached directly to a person. One person could have only one title and one role within one organization. This class is intended to lift this limitation. 
     */
    public static final String Affiliation = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation";

    /**
     * Label: OrganizationContact 
     * Comment: A Contact that denotes on Organization. 
     */
    public static final String OrganizationContact = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#OrganizationContact";

    /**
     * Label: PhoneNumber 
     * Comment: A telephone number. 
     */
    public static final String PhoneNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PhoneNumber";

    /**
     * Label: Contact 
     * Comment: A Contact. A piece of data that can provide means to identify or communicate with an entity. 
     */
    public static final String Contact = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact";

    /**
     * Label: ModemNumber 
     * Comment: A modem phone number. Inspired by the (TYPE=modem) parameter of the TEL property as defined in RFC 2426 sec  3.3.1. 
     */
    public static final String ModemNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ModemNumber";

    /**
     * Label: Role 
     * Comment: A role played by a contact. Contacts that denote people, can have many roles (e.g. see the hasAffiliation property and Affiliation class). Contacts that denote Organizations or other Agents usually have one role.  Each role can introduce additional contact media. 
     */
    public static final String Role = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role";

    /**
     * Label: PagerNumber 
     * Comment: A pager phone number. Inspired by the (TYPE=pager) parameter of the TEL property as defined in RFC 2426 sec  3.3.1. 
     */
    public static final String PagerNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PagerNumber";

    /**
     * Label: ContactMedium 
     * Comment: A superclass for all contact media - ways to contact an entity represented by a Contact instance. Some of the subclasses of this class (the various kinds of telephone numbers and postal addresses) have been inspired by the values of the TYPE parameter of ADR and TEL properties defined in RFC 2426 sec. 3.2.1. and 3.3.1 respectively. Each value is represented by an appropriate subclass with two major exceptions TYPE=home and TYPE=work. They are to be expressed by the roles these contact media are attached to i.e. contact media with TYPE=home parameter are to be attached to the default role (nco:Contact or nco:PersonContact), whereas media with TYPE=work parameter should be attached to nco:Affiliation or nco:OrganizationContact. 
     */
    public static final String ContactMedium = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactMedium";

    /**
     * Label: Gender 
     * Comment: Gender. Instances of this class may include male and female. 
     */
    public static final String Gender = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Gender";

    /**
     * Label: MessagingNumber 
     * Comment: A number that can accept textual messages. 
     */
    public static final String MessagingNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#MessagingNumber";

    /**
     * Label: PersonContact 
     * Comment: A Contact that denotes a Person. A person can have multiple Affiliations. 
     */
    public static final String PersonContact = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact";

    /**
     * Label: ParcelDeliveryAddress 
     * Comment: Parcel Delivery Addresse. Class inspired by TYPE=parcel parameter of the ADR property defined in RFC 2426 sec. 3.2.1 
     */
    public static final String ParcelDeliveryAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ParcelDeliveryAddress";

    /**
     * Label: AudioIMAccount 
     * Comment: Deprecated in favour of nco:imCapabilityAudio. 
     */
    public static final String AudioIMAccount = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#AudioIMAccount";

    /**
     * Label: PostalAddress 
     * Comment: A postal address. A class aggregating the various parts of a value for the 'ADR' property as defined in RFC 2426 Sec. 3.2.1. 
     */
    public static final String PostalAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress";

    /**
     * Label: FaxNumber 
     * Comment: A fax number. Inspired by the (TYPE=fax) parameter of the TEL property as defined in RFC 2426 sec  3.3.1. 
     */
    public static final String FaxNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#FaxNumber";

    /**
     * Label: CarPhoneNumber 
     * Comment: A car phone number. Inspired by the (TYPE=car) parameter of the TEL property as defined in RFC 2426 sec  3.3.1. 
     */
    public static final String CarPhoneNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#CarPhoneNumber";

    /**
     * Label: ContactListDataObject 
     * Comment: An entity occuring on a contact list (usually interpreted as an nco:Contact) 
     */
    public static final String ContactListDataObject = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactListDataObject";

    /**
     * Label: InternationalDeliveryAddress 
     * Comment: International Delivery Addresse. Class inspired by TYPE=intl parameter of the ADR property defined in RFC 2426 sec. 3.2.1 
     */
    public static final String InternationalDeliveryAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#InternationalDeliveryAddress";

    /**
     * Label: VideoIMAccount 
     * Comment: Deprecated in favour of nco:imCapabilityVideo. 
     */
    public static final String VideoIMAccount = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#VideoIMAccount";

    /**
     * Label: EmailAddress 
     * Comment: An email address. The recommended best practice is to use mailto: uris for instances of this class. 
     */
    public static final String EmailAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#EmailAddress";

    /**
     * Label: CellPhoneNumber 
     * Comment: A cellular phone number. Inspired by the (TYPE=cell) parameter of the TEL property as defined in RFC 2426 sec  3.3.1. Usually a cellular phone can accept voice calls as well as textual messages (SMS), therefore this class has two superclasses. 
     */
    public static final String CellPhoneNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#CellPhoneNumber";

    /**
     * Label: DomesticDeliveryAddress 
     * Comment: Domestic Delivery Addresse. Class inspired by TYPE=dom parameter of the ADR property defined in RFC 2426 sec. 3.2.1 
     */
    public static final String DomesticDeliveryAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#DomesticDeliveryAddress";

    /**
     * Label: imCapability 
     * Comment: Capabilities of a cetain IMAccount. 
     */
    public static final String IMCapability = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMCapability";

    /**
     * Label: Name 
     * Comment:  
     */
    public static final String Name = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Name";

    /**
     * Label: PersonName 
     * Comment:  
     */
    public static final String PersonName = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonName";

    /**
     * Label: BirthDate 
     * Comment:  
     */
    public static final String BirthDate = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#BirthDate";

    /**
     * Label: CustomAttribute 
     * Comment:  
     */
    public static final String CustomAttribute = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#CustomAttribute";

    /**
     * Label: region 
     * Comment: Region. Inspired by the fifth part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String region = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#region";

    /**
     * Label: key 
     * Comment: An encryption key attached to a contact. Inspired by the KEY property defined in RFC 2426 sec. 3.7.2 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String key = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#key";

    /**
     * Label: nameHonorificSuffix 
     * Comment: A suffix for the name of the Object represented by the given object. See documentation for the 'nameFamily' for details. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonName 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String nameHonorificSuffix = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameHonorificSuffix";

    /**
     * Label: url 
     * Comment: A uniform resource locator associated with the given role of a Contact. Inspired by the 'URL' property defined in RFC 2426 Sec. 3.6.8. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final String url = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#url";

    /**
     * Label: nameFamily 
     * Comment: The family name of an Object represented by this Contact. These applies to people that have more than one given name. The 'first' one is considered 'the' given name (see nameGiven) property. All additional ones are considered 'additional' names. The name inherited from parents is the 'family name'. e.g. For Dr. John Phil Paul Stevenson Jr. M.D. A.C.P. we have contact with: honorificPrefix: 'Dr.', nameGiven: 'John', nameAdditional: 'Phil', nameAdditional: 'Paul', nameFamily: 'Stevenson', honorificSuffix: 'Jr.', honorificSuffix: 'M.D.', honorificSuffix: 'A.C.P.'. These properties form an equivalent of the compound 'N' property as defined in RFC 2426 Sec. 3.1.2 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonName 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String nameFamily = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameFamily";

    /**
     * Label: contactUID 
     * Comment: A value that represents a globally unique  identifier corresponding to the individual or resource associated with the Contact. An equivalent of the 'UID' property defined in RFC 2426 Sec. 3.6.7 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String contactUID = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#contactUID";

    /**
     * Label: publisher 
     * Comment: An entity responsible for making the InformationElement available. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     */
    public static final String publisher = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#publisher";

    /**
     * Label: country 
     * Comment: A part of an address specyfing the country. Inspired by the seventh part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String country = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#country";

    /**
     * Label: nameHonorificPrefix 
     * Comment: A prefix for the name of the object represented by this Contact. See documentation for the 'nameFamily' property for details. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonName 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String nameHonorificPrefix = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameHonorificPrefix";

    /**
     * Label: extendedAddress 
     * Comment: An extended part of an address. This field might be used to express parts of an address that aren't include in the name of the Contact but also aren't part of the actual location. Usually the streed address and following fields are enough for a postal letter to arrive. Examples may include ('University of California Campus building 45', 'Sears Tower 34th floor' etc.) Inspired by the second part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String extendedAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#extendedAddress";

    /**
     * Label: hasIMAccount 
     * Comment: Indicates that an Instant Messaging account owned by an entity represented by this contact. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     */
    public static final String hasIMAccount = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasIMAccount";

    /**
     * Label: creator 
     * Comment: Creator of an information element, an entity primarily responsible for the creation of the content of the data object. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     */
    public static final String creator = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#creator";

    /**
     * Label: hasLocation 
     * Comment: Geographical location of the contact. Inspired by the 'GEO' property specified in RFC 2426 Sec. 3.4.2 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.w3.org/2003/01/geo/wgs84_pos#Point 
     */
    public static final String hasLocation = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasLocation";

    /**
     * Label: phoneNumber 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PhoneNumber 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String phoneNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#phoneNumber";

    /**
     * Label: nickname 
     * Comment: A nickname of the Object represented by this Contact. This is an equivalent of the 'NICKNAME' property as defined in RFC 2426 Sec. 3.1.3. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Name 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String nickname = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nickname";

    /**
     * Label: imStatus 
     * Comment: Current status of the given IM account. When this property is set, the nco:imStatusType should also always be set. Applications should attempt to parse this property to determine the presence, only falling back to the nco:imStatusType property in the case that this property's value is unrecognised. Values for this property may include 'available', 'offline', 'busy' etc. The exact choice of them is unspecified, although it is recommended to follow the guidance of the Telepathy project when choosing a string identifier http://telepathy.freedesktop.org/spec/Connection_Interface_Simple_Presence.html#description 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String imStatus = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imStatus";

    /**
     * Label: instant messaging status type 
     * Comment: Current status type of the given IM account. When this property is set, the nco:imStatus property should also always be set. Applications should attempt to parse the nco:imStatus property to determine the presence, only falling back to this property in the case that the nco:imStatus property's value is unrecognised. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMStatusType 
     */
    public static final String imStatusType = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imStatusType";

    /**
     * Label: containsContact 
     * Comment: A property used to group contacts into contact groups. This 
    property was NOT defined in the VCARD standard. See documentation for the 
    'ContactList' class for details 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactList 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactListDataObject 
     */
    public static final String containsContact = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#containsContact";

    /**
     * Label: department 
     * Comment: Department. The organizational unit within the organization. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String department = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#department";

    /**
     * Label: imID 
     * Comment: Identifier of the IM account. Examples of such identifier might include ICQ UINs, Jabber IDs, Skype names etc. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String imID = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imID";

    /**
     * Label: addressLocation 
     * Comment: The geographical location of a postal address. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     * Range: http://www.w3.org/2003/01/geo/wgs84_pos#Point 
     */
    public static final String addressLocation = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#addressLocation";

    /**
     * Label: note 
     * Comment: A note about the object represented by this Contact. An equivalent for the 'NOTE' property defined in RFC 2426 Sec. 3.6.2 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String note = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#note";

    /**
     * Label: representative 
     * Comment: An object that represent an object represented by this Contact. Usually this property is used to link a Contact to an organization, to a contact to the representative of this organization the user directly interacts with. An equivalent for the 'AGENT' property defined in RFC 2426 Sec. 3.5.4 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     */
    public static final String representative = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#representative";

    /**
     * Label: nameAdditional 
     * Comment: Additional given name of an object represented by this contact. See documentation for 'nameFamily' property for details. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonName 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String nameAdditional = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameAdditional";

    /**
     * Label: nameGiven 
     * Comment: The given name for the object represented by this Contact. See documentation for 'nameFamily' property for details. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonName 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String nameGiven = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameGiven";

    /**
     * Label: fullname 
     * Comment: To specify the formatted text corresponding to the name of the object the Contact represents. An equivalent of the FN property as defined in RFC 2426 Sec. 3.1.1. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Name 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String fullname = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#fullname";

    /**
     * Label: streetAddress 
     * Comment: The streed address. Inspired by the third part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String streetAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#streetAddress";

    /**
     * Label: hasPhoneNumber 
     * Comment: A number for telephony communication with the object represented by this Contact. An equivalent of the 'TEL' property defined in RFC 2426 Sec. 3.3.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PhoneNumber 
     */
    public static final String hasPhoneNumber = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasPhoneNumber";

    /**
     * Label: photo 
     * Comment: Photograph attached to a Contact. The DataObject referred to by this property is usually interpreted as an nfo:Image. Inspired by the PHOTO property defined in RFC 2426 sec. 3.1.4 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String photo = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#photo";

    /**
     * Label: contributor 
     * Comment: An entity responsible for making contributions to the content of the InformationElement. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     */
    public static final String contributor = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#contributor";

    /**
     * Label: logo 
     * Comment: Logo of a company. Inspired by the LOGO property defined in RFC 2426 sec. 3.5.3 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#OrganizationContact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String logo = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#logo";

    /**
     * Label: websiteUrl 
     * Comment: A url of a website. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final String websiteUrl = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#websiteUrl";

    /**
     * Label: birthDate 
     * Comment: Birth date of the object represented by this Contact. An equivalent of the 'BDAY' property as defined in RFC 2426 Sec. 3.1.5. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#BirthDate 
     * Range: http://www.w3.org/2001/XMLSchema#date 
     */
    public static final String birthDate = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#birthDate";

    /**
     * Label: hasEmailAddress 
     * Comment: An address for electronic mail communication with the object specified by this contact. An equivalent of the 'EMAIL' property as defined in RFC 2426 Sec. 3.3.1. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#EmailAddress 
     */
    public static final String hasEmailAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasEmailAddress";

    /**
     * Label: postalcode 
     * Comment: Postal Code. Inspired by the sixth part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String postalcode = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#postalcode";

    /**
     * Label: org 
     * Comment: Name of an organization or a unit within an organization the object represented by a Contact is associated with. An equivalent of the 'ORG' property defined in RFC 2426 Sec. 3.5.5 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#OrganizationContact 
     */
    public static final String org = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#org";

    /**
     * Label: title 
     * Comment: The official title  the object represented by this contact in an organization. E.g. 'CEO', 'Director, Research and Development', 'Junior Software Developer/Analyst' etc. An equivalent of the 'TITLE' property defined in RFC 2426 Sec. 3.5.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String title = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#title";

    /**
     * Label: voiceMail 
     * Comment: Indicates if the given number accepts voice mail. (e.g. there is an answering machine). Inspired by TYPE=msg parameter of the TEL property defined in RFC 2426 sec. 3.3.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#VoicePhoneNumber 
     * Range: http://www.w3.org/2001/XMLSchema#boolean 
     */
    public static final String voiceMail = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#voiceMail";

    /**
     * Label: addressLocation 
     * Comment: Links a Contact with a ContactGroup it belongs to. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactGroup 
     */
    public static final String belongsToGroup = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#belongsToGroup";

    /**
     * Label: hasContactMedium 
     * Comment: A superProperty for all properties linking a Contact to an instance of a contact medium. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactMedium 
     */
    public static final String hasContactMedium = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasContactMedium";

    /**
     * Label: contactGroupName 
     * Comment: The name of the contact group. This property was NOT defined 
    in the VCARD standard. See documentation of the 'ContactGroup' class for 
    details 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactGroup 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String contactGroupName = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#contactGroupName";

    /**
     * Label: contactMediumComment 
     * Comment: A comment about the contact medium. (Deprecated in favor of nie:comment or nao:description - based on the context) 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactMedium 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String contactMediumComment = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#contactMediumComment";

    /**
     * Label: foafUrl 
     * Comment: The URL of the FOAF file. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final String foafUrl = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#foafUrl";

    /**
     * Label: emailAddress 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#EmailAddress 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String emailAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#emailAddress";

    /**
     * Label: locality 
     * Comment: Locality or City. Inspired by the fourth part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String locality = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#locality";

    /**
     * Label: sound 
     * Comment: Sound clip attached to a Contact. The DataObject referred to by this property is usually interpreted as an nfo:Audio. Inspired by the SOUND property defined in RFC 2425 sec. 3.6.6. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String sound = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#sound";

    /**
     * Label: imNickname 
     * Comment: A nickname attached to a particular IM Account. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String imNickname = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imNickname";

    /**
     * Label: hobby 
     * Comment: A hobby associated with a PersonContact. This property can be used to express hobbies and interests. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String hobby = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hobby";

    /**
     * Label: blogUrl 
     * Comment: A Blog url. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final String blogUrl = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#blogUrl";

    /**
     * Label: role 
     * Comment: Role an object represented by this contact represents in the organization. This might include 'Programmer', 'Manager', 'Sales Representative'. Be careful to avoid confusion with the title property. An equivalent of the 'ROLE' property as defined in RFC 2426. Sec. 3.5.2. Note the difference between nco:Role class and nco:role property. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String role = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#role";

    /**
     * Label: hasPostalAddress 
     * Comment: The default Address for a Contact. An equivalent of the 'ADR' property as defined in RFC 2426 Sec. 3.2.1. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     */
    public static final String hasPostalAddress = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasPostalAddress";

    /**
     * Label: imAccountType 
     * Comment: Type of the IM account. This may be the name of the service that provides the IM functionality. Examples might include Jabber, ICQ, MSN etc 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String imAccountType = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imAccountType";

    /**
     * Label: pobox 
     * Comment: Post office box. This is the first part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String pobox = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#pobox";

    /**
     * Label: hasAffiliation 
     * Comment: Links a PersonContact with an Affiliation. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation 
     */
    public static final String hasAffiliation = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasAffiliation";

    /**
     * Label: gender 
     * Comment: Gender of the given contact. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Gender 
     */
    public static final String gender = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#gender";

    /**
     * Label: imStatusMessage 
     * Comment: A feature common in most IM systems. A message left by the user for all his/her contacts to see. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String imStatusMessage = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imStatusMessage";

    /**
     * Label: start 
     * Comment: Start datetime for the role, such as: the datetime of joining a project or organization, datetime of starting employment, datetime of marriage 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String start = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#start";

    /**
     * Label: end 
     * Comment: End datetime for the role, such as: the datetime of leaving a project or organization, datetime of ending employment, datetime of divorce. If absent or set to a date in the future, the role is currently active. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String end = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#end";

    /**
     * Label: hasIMCapability 
     * Comment: Indicates that an IMAccount has a certain capability. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMCapability 
     */
    public static final String hasIMCapability = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasIMCapability";

    /**
     * Label: isKnownBy 
     * Comment: Indicates the local IMAccount by which this IMAccount is accessed. This does not imply membership of a contact list. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     */
    public static final String isAccessedBy = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#isAccessedBy";

    /**
     * Label: publishesPresenceTo 
     * Comment: Indicates that this IMAccount publishes its presence information to the other IMAccount. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     */
    public static final String publishesPresenceTo = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#publishesPresenceTo";

    /**
     * Label: requestedPresenceSubscriptionTo 
     * Comment: Indicates that this IMAccount has requested a subscription to the presence information of the other IMAccount. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     */
    public static final String requestedPresenceSubscriptionTo = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#requestedPresenceSubscriptionTo";

    /**
     * Label: isBlocked 
     * Comment: Indicates that this IMAccount has been blocked. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount 
     * Range: http://www.w3.org/2001/XMLSchema#boolean 
     */
    public static final String isBlocked = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#isBlocked";

    /**
     * Label: customAttribute 
     * Comment:  
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     */
    public static final String customAttribute = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#customAttribute";

    /**
     * Label: hasName 
     * Comment:  
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Name 
     */
    public static final String hasName = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasName";

    /**
     * Label: hasPersonName 
     * Comment:  
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonName 
     */
    public static final String hasPersonName = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasPersonName";

    /**
     * Label: hasBirthDate 
     * Comment:  
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#BirthDate 
     */
    public static final String hasBirthDate = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasBirthDate";

    /**
     * Label: age 
     * Comment:  
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#BirthDate 
     * Range: http://www.w3.org/2001/XMLSchema#nonNegativeInteger 
     */
    public static final String age = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#age";

    /**
     * Label: hasCustomAttribute 
     * Comment:  
     * Comment: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact 
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#CustomAttribute 
     */
    public static final String hasCustomAttribute = "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasCustomAttribute";

}
