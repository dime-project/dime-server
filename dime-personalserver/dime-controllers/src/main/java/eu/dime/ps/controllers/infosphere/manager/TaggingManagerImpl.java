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

package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.NAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NameNotUniqueException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.OntologyInvalidException;
import eu.dime.ps.semantic.model.nao.Tag;
import eu.dime.ps.semantic.service.TaggingService;

/**
 * Implements {@link TaggingManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class TaggingManagerImpl extends InfoSphereManagerBase<Tag> implements TaggingManager {

	@Override
	public Tag get(String tagId) throws InfosphereException {
		return get(tagId, new ArrayList<URI>(0));
	}

	@Override
	public Tag get(String tagId, List<URI> properties) throws InfosphereException {
		try {
			return getResourceStore().get(new URIImpl(tagId), Tag.class,
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot find tag "+tagId, e);
		}
	}

	@Override
	public Collection<Tag> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}

	@Override
	public Collection<Tag> getAll(List<URI> properties) throws InfosphereException {
		return getResourceStore().find(Tag.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.results();
	}

	@Override
	public Collection<Tag> getAllByResource(Resource resource) throws InfosphereException {
		return getResourceStore().find(Tag.class)
				.distinct()
				.where(NAO.isTagFor).is(resource)
				.results();
	}

	@Override
	public Collection<Tag> getAllByLabelLike(String label) throws InfosphereException {
		return getResourceStore().find(Tag.class)
				.distinct()
				.where(NAO.prefLabel).like(label, false)
				.results();
	}

	@Override
	public Collection<Tag> add(Resource resource, String... labels) throws InfosphereException {
		TaggingService taggingService = getPimoService();
		List<Tag> tags = new ArrayList<Tag>();
		
		for (String label : labels) {
			try {
				Tag tag = taggingService.getOrCreateTag(label);
				taggingService.addTag(resource.asURI(), tag.asURI());
				tags.add(tag);
			} catch (OntologyInvalidException e) {
				throw new InfosphereException("Could not add the tag '"+label+"' to "+resource, e);
			} catch (NameNotUniqueException e) {
				throw new InfosphereException("Could not add the tag '"+label+"' to "+resource, e);
			} catch (NotFoundException e) {
				throw new InfosphereException("Could not add the tag '"+label+"' to "+resource, e);
			}
		}

		return tags;
	}

}
