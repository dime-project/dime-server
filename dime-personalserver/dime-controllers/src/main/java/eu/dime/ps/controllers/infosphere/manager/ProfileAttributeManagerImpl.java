/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.nie.DataSource;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.query.impl.BasicQuery;
import eu.dime.ps.semantic.query.impl.PimoQuery;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.storage.jfix.util.Arrays;

/**
 * Implements {@link ProfileAttributeManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class ProfileAttributeManagerImpl extends InfoSphereManagerBase<Resource> implements ProfileAttributeManager {

	public static final URI[] ATTRIBUTE_PROPERTIES = new URI[] {
		// nco:Role
		NCO.hasIMAccount, NCO.hasPhoneNumber, NCO.hasPostalAddress, NCO.hasEmailAddress,
		NCO.url, NCO.foafUrl, NCO.blogUrl, NCO.websiteUrl,
		// nco:Contact
		NCO.key, NCO.contactUID, NCO.hasLocation, NCO.note, NCO.representative,
		NCO.photo, NCO.sound, NCO.hasName,
		// nco:OrganizationContact
		NCO.logo,
		// nco:PersonContact
		NCO.hobby, NCO.hasAffiliation, NCO.gender, NCO.hasPersonName, NCO.hasBirthDate
	};

	@Override
	public Collection<Resource> getAll() throws InfosphereException {
		return getAll(Arrays.asList(ATTRIBUTE_PROPERTIES));
	}

	@Override
	public Collection<Resource> getAll(List<URI> properties) throws InfosphereException {
		return getAllByProfile(Variable.ANY, properties);
	}

	@Override
	public Collection<Resource> getAllByContainer(String containerId) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		URI container = new URIImpl(containerId);
		try {
			if (resourceStore.isTypedAs(container, PPO.PrivacyPreference)) {
				return getAllByPrivacyPreference(container);
			} else if (resourceStore.isTypedAs(container, NCO.Contact)) {
				return getAllByProfile(container, Arrays.asList(ATTRIBUTE_PROPERTIES));
			} else {
				throw new InfosphereException(containerId+" is neither a profile nor a profile card.");
			}
		} catch (NotFoundException e) {
			throw new InfosphereException(containerId+" is neither a profile nor a profile card.");
		}
	}
	
	private Collection<Resource> getAllByProfile(ResourceOrVariable profile, List<URI> properties)
			throws InfosphereException {
		Query<Resource> query = getResourceStore().find(Resource.class).distinct();
		boolean first = true;
		Object subject = null;
		
		if (profile instanceof Variable && profile == Variable.ANY) {
			subject = Query.ANY;
		} else if (profile instanceof org.ontoware.rdf2go.model.node.Resource) {
			subject = (org.ontoware.rdf2go.model.node.Resource) profile;
		} else {
			throw new InfosphereException("'profile' must be a valid resource identifier (blank node or URI), or Variable.ANY.");
		}
		
		for (URI attribute : properties) {
			if (first) {
				query.where(subject, attribute, PimoQuery.THIS);
				first = false;
			} else {
				query.orWhere(subject, attribute, PimoQuery.THIS);
			}
		}
		
		return query.distinct().results();
	}
	
	private Collection<Resource> getAllByPrivacyPreference(URI privacyPreference)
			throws InfosphereException {
		return getResourceStore().find(Resource.class)
				.distinct()
				.where(privacyPreference, PPO.appliesToResource).is(BasicQuery.THIS)
				.results();
	}

	@Override
	public Resource get(String attributeId) throws InfosphereException {
		return get(attributeId, new ArrayList<URI>(0));
	}

	@Override
	public Resource get(String attributeId, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			return resourceStore.get(new URIImpl(attributeId),
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("Profile attribute "+attributeId+" not found.");
		}
	}

	@Override
	public void add(Resource attribute) throws InfosphereException {
		authorize("add", attribute);
		
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		resourceStore.createOrUpdate(pimoService.getPimoUri(), attribute);
	}

	@Override
	public void update(Resource attribute) throws InfosphereException {
		authorize("update", attribute);

		ResourceStore resourceStore = getResourceStore();
		try {
			resourceStore.update(attribute, true);
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot update profile attribute "+attribute.asResource(), e);
		}
	}

	@Override
	public void remove(String attributeId) throws InfosphereException {
		authorize("remove", new URIImpl(attributeId));

		ResourceStore resourceStore = getResourceStore();
		try {
			resourceStore.remove(new URIImpl(attributeId));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot remove profile attribute "+attributeId, e);
		}
	}

	/**
	 * Overrides implementation in {@link InfoSphereManagerBase} because profile attributes don't specify a data source
	 * directly, instead the profile (PersonContact, etc.) which they belong to may be linked to a data source.
	 * 
	 * @param action the action to be performed
	 * @param attribute profile attribute on which the action will be performed
	 * @throws InfosphereException if the profile attribute was retrieved from a data source (operation not allowed)
	 */
	@Override
	protected final void authorize(String action, Resource attribute) throws InfosphereException {
		authorize(action, attribute.asURI());
	}

	/**
	 * Overrides implementation in {@link InfoSphereManagerBase} because profile attributes don't specify a data source
	 * directly, instead the profile (PersonContact, etc.) which they belong to may be linked to a data source.
	 * 
	 * @param action the action to be performed
	 * @param attributeId profile attribute identifier on which the action will be performed
	 * @throws InfosphereException if the profile attribute was retrieved from a data source (operation not allowed)
	 */
	@Override
	protected final void authorize(String action, URI attributeId) throws InfosphereException {
		if ("update".equals(action) || "remove".equals(action)) {
			DataSource dataSource = getResourceStore().find(DataSource.class)
					.where(Query.X, NIE.dataSource, Query.THIS)
					.where(Query.X, RDF.type, NCO.PersonContact)
					.where(Query.X, Query.ANY, attributeId)
					.first();
			if (dataSource != null)
				throw new InfosphereException(attributeId+" cannot be "+action+"d, it is a read-only resource retrieved from the data source "+dataSource);
		}
	}

}
