package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.util.StringUtils;

/**
 * Implements {@link ProfileCardManager} using a RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class ProfileCardManagerImpl extends PrivacyPreferenceManager<PrivacyPreference> implements ProfileCardManager {

	private final ModelFactory modelFactory = new ModelFactory();
	
	public ProfileCardManagerImpl() {}
	
	@Override
	public Collection<PrivacyPreference> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<PrivacyPreference> getAll(List<URI> properties) throws InfosphereException {
		return getAll(PrivacyPreferenceType.PROFILECARD, properties);
	}
	
	@Override
	public PrivacyPreference getByLabel(String label) throws InfosphereException {
		PrivacyPreferenceService ppService = getPrivacyPreferenceService();
		ResourceStore resourceStore = getResourceStore();
		
		PrivacyPreference profileCard = null;
		profileCard = resourceStore.find(PrivacyPreference.class)
				.distinct()
				.where(RDFS.label).is(PrivacyPreferenceType.PROFILECARD.toString())
				.where(NAO.prefLabel).is(label)
				.first();
		
		// read AccessSpace metadata, etc.
		if (profileCard != null) {
			profileCard = ppService.get(profileCard.asURI());
		}

		return profileCard;
	}
	
	@Override
	public Collection<PrivacyPreference> getAllByPerson(Person person)
			throws InfosphereException {
		return getAllByPerson(person, new ArrayList<URI>(0));
	}

	@Override
	public Collection<PrivacyPreference> getAllByPerson(Person person, List<URI> properties)
			throws InfosphereException {
		return getAllByPerson(PrivacyPreferenceType.PROFILECARD, person, properties);
	}

	@Override
	public PrivacyPreference get(String profileCardId) throws InfosphereException {
		return get(profileCardId, new ArrayList<URI>(0));
	}
	
	@Override
	public PrivacyPreference get(String profileCardId, List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PrivacyPreference card = null;
		try {
			card = resourceStore.get(new URIImpl(profileCardId), PrivacyPreference.class,
					properties.toArray(new URI[properties.size()]));
			if (!resourceStore.sparqlAsk(
					"ASK { "+card.toSPARQL()+" "+RDFS.label.toSPARQL()+" \""+PrivacyPreferenceType.PROFILECARD.toString()+"\" .}")) {
				throw new InfosphereException("cannot get profile card "+profileCardId+"]:" +
						" privacy preference not defined as a profile card.");
			}
			prefetchAccessSpace(card);
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot get profile card "+profileCardId+": "+e, e);
		}
		return card;
	}

	@Override
	public void add(PrivacyPreference profileCard) throws InfosphereException {
		authorize("add", profileCard);

		profileCard.setLabel(PrivacyPreferenceType.PROFILECARD.toString());
		super.add(profileCard);
	}

	@Override
	public void update(PrivacyPreference profileCard) throws InfosphereException {
		authorize("update", profileCard);
		
		profileCard.setLabel(PrivacyPreferenceType.PROFILECARD.toString());
		super.update(profileCard);
	}

	@Override
	public PersonContact getProfile(String profileId, List<URI> properties) throws InfosphereException {
		PrivacyPreferenceService ppService = getPrivacyPreferenceService();
	
		PrivacyPreference card = ppService.get(new URIImpl(profileId));
		if (card == null) {
			throw new InfosphereException("No profile information was shared, profile card "+profileId+" does not exist.");
		}

		// return a profile build from a profile card
		return buildProfile(card, properties);
	}
	
	@Override
	public Collection<PersonContact> getAllProfile(String accountId, List<URI> properties) throws InfosphereException {
		PrivacyPreferenceService ppService = getPrivacyPreferenceService();
		ResourceStore resourceStore = getResourceStore();
		
		Collection<org.ontoware.rdf2go.model.node.Resource> profileCardUris = null;
		profileCardUris = resourceStore.find(PrivacyPreference.class)
				.distinct()
				.where(RDFS.label).is(PrivacyPreferenceType.PROFILECARD.toString())
				.where(PPO.hasAccessSpace).is(Query.X)
				.where(Query.X, NSO.sharedThrough, new URIImpl(accountId))
				.ids();

		List<PersonContact> profiles = new ArrayList<PersonContact>();
		for (org.ontoware.rdf2go.model.node.Resource cardUri : profileCardUris) {
			PrivacyPreference profileCard = ppService.get(cardUri.asURI());
			profiles.add(buildProfile(profileCard, properties));
		}

		return profiles;
	}
	
	protected PersonContact buildProfile(PrivacyPreference profileCard, List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact(profileCard.asURI());
		
		// iterate over the profile card attributes, and add them to the profile to return
		URI[] propertiesToFetch = properties.toArray(new URI[0]);
		ClosableIterator<Resource> attributesIt = profileCard.getAllAppliesToResource();
		while (attributesIt.hasNext()) {
			Resource attribute = attributesIt.next();
			
			try {
				// every attribute must be related to a profile by a specific property to
				// compose the new profile object to share, these properties need to be found out
				String query = StringUtils.strjoinNL(
						"PREFIX nco:"+NCO.NS_NCO.toSPARQL(),
						"SELECT ?property WHERE {",
						"  ?profile a nco:PersonContact ;",
						"    ?property "+attribute.toSPARQL()+" .",
						"}");
				ClosableIterator<QueryRow> rowIt = resourceStore.sparqlSelect(query).iterator();
				if (rowIt.hasNext()) {
					URI property = rowIt.next().getValue("property").asURI();
					
					if (property.equals(NCO.hasPersonName)) {
						Model nameModel = resourceStore.get(attribute.asURI(), propertiesToFetch).getModel();
						Node fullname = ModelUtils.findObject(nameModel, attribute, NCO.fullname);
						if (fullname != null) {
							profile.setPrefLabel(fullname.asLiteral().getValue());
						}
					}

					// relate profile and attribute
					profile.getModel().addStatement(profile, property, attribute);
					
					// add all metadata from the attribute to the profile model
					profile.getModel().addModel(resourceStore.get(attribute.asURI(), propertiesToFetch).getModel());
				}
				rowIt.close();

			} catch (NotFoundException e) {
				throw new InfosphereException("Profile information could not be retrieved from profile card "+profileCard, e);
			}
		}
		attributesIt.close();
		
		return profile;
	}

	@Override
	protected void authorize(String action, PrivacyPreference profileCard) throws InfosphereException {
		super.authorize(action, profileCard);
		
		// also check if no other profile card has been shared through the same di.me account
		// as the one specified in this one
		PimoService pimoService = getPimoService();
		ClosableIterator<Node> accessSpaceIt = profileCard.getAllAccessSpace_asNode();
		while (accessSpaceIt.hasNext()) {
			URI accessSpace = accessSpaceIt.next().asURI();
			
			// query sharedThrough in the profileCard model or PIM (i.e. in case of updates)
			Node sharedThrough = ModelUtils.findObject(profileCard.getModel(), accessSpace, NSO.sharedThrough);
			if (sharedThrough == null) {
				sharedThrough = ModelUtils.findObject(pimoService.getUserPIM(), accessSpace, NSO.sharedThrough);
				if (sharedThrough == null) {
					throw new InfosphereException("Profile card's access space " + accessSpace + " must specify a valid " +
							"di.me account for nso:sharedThrough.");
				}
			}
			
			Collection<org.ontoware.rdf2go.model.node.Resource> existing = pimoService.find(PrivacyPreference.class)
					.distinct()
					.where(RDFS.label).is(PrivacyPreferenceType.PROFILECARD.toString())
					.where(PPO.hasAccessSpace).is(Query.X)
					.where(Query.X, NSO.sharedThrough).is(sharedThrough)
					.ids();
			existing.remove(profileCard.asResource());
			if (!existing.isEmpty()) {
				accessSpaceIt.close();
				throw new InfosphereException("Profile card " + profileCard + " cannot be shared through the account " +
						sharedThrough + " because the account is already used by profile card "+existing.iterator().next());
			}
		}
		accessSpaceIt.close();
	}

}