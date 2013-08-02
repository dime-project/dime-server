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

import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PPO;

import java.util.Calendar;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;

public class ObjectFactory {

	private static final ModelFactory modelFactory = new ModelFactory();
	
	public static Person buildPerson(String name) {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);
		return person;
	}

	public static PersonGroup buildPersonGroup(String name, Person...members) {
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.setPrefLabel(name);
		for (Person member : members) {
			group.addMember(member);
		}
		return group;
	}

	public static Account buildAccount(String name, String accountType, URI creator) {
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setPrefLabel(name);
		account.setAccountType(accountType);
		account.setCreator(creator);
		return account;
	}
	
	public static PrivacyPreference buildProfileCard(String label, Resource[] attributes, URI creator) {
		PrivacyPreference profileCard = modelFactory.getPPOFactory().createPrivacyPreference();
		profileCard.getModel().addStatement(profileCard, RDFS.label, PrivacyPreferenceType.PROFILECARD.toString());
		profileCard.setPrefLabel(label);
		profileCard.setCreator(creator);

		for (Resource attribute : attributes) {
			profileCard.addAppliesToResource(attribute);
			profileCard.getModel().addAll(attribute.getModel().iterator());
		}
		
		return profileCard;
	}

	public static PrivacyPreference buildProfileCard(String label, Resource[] attributes, URI creator, URI sender, Account... recipients) {
		PrivacyPreference profileCard = buildProfileCard(label, attributes, creator);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(sender);
		for (Account recipient : recipients) {
			accessSpace.addIncludes(recipient);
		}
		
		profileCard.setAccessSpace(accessSpace);
		profileCard.getModel().addAll(accessSpace.getModel().iterator());
		
		return profileCard;
	}

	public static PersonContact buildPersonContact(PersonName personName) {
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		
		profile.addPersonName(personName);
		profile.getModel().addAll(personName.getModel().iterator());
		
		return profile;
	}
	
	public static PersonContact buildPersonContact(PersonName personName, EmailAddress emailAddress, PhoneNumber phoneNumber) {
		PersonContact profile = buildPersonContact(personName);
		
		profile.addEmailAddress(emailAddress);
		profile.getModel().addAll(emailAddress.getModel().iterator());
		
		profile.addPhoneNumber(phoneNumber);
		profile.getModel().addAll(phoneNumber.getModel().iterator());
		
		return profile;
	}
	
	public static PersonName buildPersonName(String name) {
		PersonName personName = modelFactory.getNCOFactory().createPersonName();
		personName.setFullname(name);
		return personName;
	}

	public static EmailAddress buildEmailAddress(String email) {
		EmailAddress emailAddress = modelFactory.getNCOFactory().createEmailAddress();
		emailAddress.setEmailAddress(email);
		return emailAddress;
	}

	public static PhoneNumber buildPhoneNumber(String phone) {
		PhoneNumber phoneNumber = modelFactory.getNCOFactory().createPhoneNumber();
		phoneNumber.setPhoneNumber(phone);
		return phoneNumber;
	}

	public static DataContainer buildDatabox(String label, DataObject[] dataObjects, URI creator) {
		return buildDatabox(label, dataObjects, creator, false);
	}

	public static DataContainer buildDatabox(String label, DataObject[] dataObjects, URI creator, boolean isPrivacyPreference) {
		DataContainer databox = modelFactory.getNFOFactory().createDataContainer();
		databox.setPrefLabel(label);
		databox.setCreator(creator);
		
		if (isPrivacyPreference) { 
			databox.getModel().addStatement(databox, RDF.type, PPO.PrivacyPreference);
			databox.getModel().addStatement(databox, RDFS.label, PrivacyPreferenceType.DATABOX.toString());
			for (DataObject dataObject : dataObjects) {
				databox.getModel().addStatement(databox, PPO.appliesToResource, dataObject);
			}
		}
		
		for (DataObject dataObject : dataObjects) {
			databox.getModel().addStatement(databox, NIE.hasPart, dataObject);
		}
		
		return databox;
	}

	public static DataContainer buildDatabox(String label, DataObject[] dataObjects, URI creator, URI sender, Agent... recipients) {
		DataContainer databox = buildDatabox(label, dataObjects, creator, true);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(sender);
		for (Agent recipient : recipients) {
			accessSpace.addIncludes(recipient);
		}

		databox.getModel().addStatement(databox, PPO.hasAccessSpace, accessSpace);
		databox.getModel().addAll(accessSpace.getModel().iterator());

		return databox;
	}
	
	public static FileDataObject buildFileDataObject(String name, URI creator) {
		FileDataObject fdo = modelFactory.getNFOFactory().createFileDataObject();
		fdo.setFileName(name);
		fdo.setCreator(creator);
		return fdo;
	}
	
	public static FileDataObject buildFileDataObject(String name, URI creator, Calendar fileLastModified) {
		FileDataObject fdo = buildFileDataObject(name, creator);
		fdo.setFileLastModified(fileLastModified);
		return fdo;
	}
	
	public static LivePost buildLivePost(String text, URI creator) {
		LivePost livepost = modelFactory.getDLPOFactory().createLivePost();
		livepost.setTextualContent(text);
		livepost.setCreator(creator);
		return livepost;
	}

	public static LivePost buildLivePost(String text, URI creator, Calendar timestamp) {
		LivePost livepost = buildLivePost(text, creator);
		livepost.setTimestamp(timestamp);
		return livepost;
	}

}