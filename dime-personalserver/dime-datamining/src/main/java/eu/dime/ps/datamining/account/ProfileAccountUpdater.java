package eu.dime.ps.datamining.account;

import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.datamining.ProfileEnricher;
import eu.dime.ps.datamining.ProfileEnricherImpl;
import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

public class ProfileAccountUpdater extends AccountUpdaterBase implements AccountUpdater<PersonContact> {

	private static final Logger logger = LoggerFactory.getLogger(ProfileAccountUpdater.class);

	private final PimoService pimoService;
	private final ProfileEnricher profileEnricher;

	private final double DEFAULT_TRUST_VALUE = 0.5;
	
	public ProfileAccountUpdater(ResourceStore resourceStore, PimoService pimoService) throws DataMiningException {
		super(resourceStore);
		this.pimoService = pimoService;
		this.profileEnricher = ProfileEnricherImpl.getInstance();
	}

	public ProfileAccountUpdater(ResourceStore resourceStore, PimoService pimoService, ProfileEnricher profileEnricher) throws DataMiningException {
		super(resourceStore);
		this.pimoService = pimoService;
		this.profileEnricher = profileEnricher;
	}
	
	@Override
	public void update(URI accountUri, String path, PersonContact contact)
			throws AccountIntegrationException {
		logger.info("Updating "+path+" for "+accountUri+" with 1 contact.");

		URI accountPathGraph = getGraph(accountUri, path);
		
		// clears data in this path
		removeResources(accountUri, path);
		
		if (path.contains("@me")) {
			// user's profile
			PersonContact profile = (PersonContact) super.match(contact).castTo(PersonContact.class);
			profile.setPrefLabel(getProfilePrefLabel(profile, accountUri));
			
			// enrich profile (decomposing fullname, postal address, etc.)
			try {
				profile = profileEnricher.enrich(profile);
			} catch (DataMiningException e) {
				logger.error("Profile enrichment (fullname, postal address, etc.) failed for profile "+profile.asResource()+": "+e.getMessage(), e);
			}

			resourceStore.createOrUpdate(accountPathGraph, profile);
			if (!tripleStore.containsStatements(pimoService.getPimoUri(), pimoService.getUserUri(), PIMO.occurrence, profile)) {
				tripleStore.addStatement(pimoService.getPimoUri(), pimoService.getUserUri(), PIMO.occurrence, profile);
			}

			// links all resources to the account using nie:dataSource
			if (!tripleStore.containsStatements(accountPathGraph, profile.asResource(), NIE.dataSource, accountUri)) {
				tripleStore.addStatement(accountPathGraph, profile.asResource(), NIE.dataSource, accountUri);
			}
		} else {
			// someone's profile
			throw new RuntimeException("NOT YET IMPLEMENTED!");
		}
	}

	@Override
	public void update(URI accountUri, String path, Collection<PersonContact> contacts)
			throws AccountIntegrationException {
		logger.info("Updating "+path+" for "+accountUri+" with "+contacts.size()+" contacts.");

		URI accountPathGraph = getGraph(accountUri, path);

		// collection of resources with unique URIs (reusing them if the resource previously existed)
		Collection<? extends Resource> matchedContacts = match(contacts);
		
		// keeps a set with all existing contacts, to know if we do create or update and broadcast the proper event 
		Set<Resource> existing = new HashSet<Resource>(matchedContacts.size());
		for (Resource contact : matchedContacts) {
			if (resourceStore.exists(contact)) {
				existing.add(contact);
			}
		}
		
		// clears data in this path
		removeResources(accountUri, path);
		
		// creates a group for all contacts retrieved from the account
		PersonGroup accountGroup = resourceStore.find(PersonGroup.class).where(NIE.dataSource).is(accountUri).first();
		if (accountGroup == null) {
			accountGroup = (new ModelFactory()).getPIMOFactory().createPersonGroup();
			accountGroup.getModel().addStatement(accountGroup, NIE.dataSource, accountUri);
			accountGroup.setPrefLabel(getPersonGroupPrefLabel(accountUri));
			accountGroup.setCreator(pimoService.getUserUri());
		}

		// save all contacts metadata retrieved from the account (with matched URIs)
		logger.info("Adding " + contacts.size()+ " contacts fetched from " + accountUri + 
				" in group " + accountGroup + " (" + accountGroup.getPrefLabel() + ")");
		Person person = null;
		PersonContact contact = null;
		for (Resource resource : matchedContacts) {
			contact = (PersonContact) resource.castTo(PersonContact.class);
			
			// enrich profile (decomposing fullname, postal address, etc.)
			try {
				contact = profileEnricher.enrich(contact);
			} catch (DataMiningException e) {
				logger.error("Profile enrichment (fullname, postal address, etc.) failed for profile "+contact.asResource()+": "+e.getMessage(), e);
			}

			// links all resources to the account using nie:dataSource
			contact.getModel().addStatement(contact.asResource(), NIE.dataSource, accountUri);

			resourceStore.createOrUpdate(accountPathGraph, contact);
			
			person = pimoService.getOrCreatePersonForGroundingOccurrence(contact);
			if (!accountGroup.getModel().contains(accountGroup, PIMO.hasMember, person)) {
				accountGroup.getModel().addStatement(accountGroup, PIMO.hasMember, person);
			}
			if(!person.hasTrustLevel()){
				person.setTrustLevel(DEFAULT_TRUST_VALUE);
			}
			pimoService.createOrUpdate(person);
		}
		tripleStore.touchGraph(accountPathGraph);

		// save changes in account group
		pimoService.createOrUpdate(accountGroup);

		// TODO add path to Event, and send crawl completed for every crawl, also livepost, etc.
		if (AttributeMap.FRIEND_ALL.equals(path)) {
			BroadcastManager.getInstance().sendBroadcast(new Event(pimoService.getName(), Event.ACTION_CRAWL_COMPLETED, accountUri));
		}
	}

}
