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

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NIE;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.Thing;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Base implementation of all common methods for any infosphere manager.
 * Subclasses may override any of them to add additional validations or any other logic,
 * apart from extending with specific methods for the corresponding entity.
 * 
 * @author Ismael Rivera
 */
public abstract class InfoSphereManagerBase<T extends Resource> extends ConnectionBase implements InfoSphereManager<T> {

	public InfoSphereManagerBase() {}

	@Override
	public Person getMe() throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			return pimoService.getUser();
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot find current user/PIM owner: "+e, e);
		}
	}

	@Override
	public boolean exist(String resourceId) throws InfosphereException {
		PimoService pimoService = getPimoService();
		return pimoService.exists(new URIImpl(resourceId));
	}

	@Override
	public <R extends Resource> R get(String resourceId, Class<R> returnType)
			throws InfosphereException {
		return get(resourceId, returnType, new ArrayList<URI>(0));
	}

	@Override
	public <R extends Resource> R get(String resourceId, Class<R> returnType, List<URI> properties)
			throws InfosphereException {
		try {
			return getResourceStore().get(new URIImpl(resourceId), returnType, properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot find resource "+resourceId+":"+e, e);
		}
	}

	@Override
	public void add(T entity) throws InfosphereException {
		authorize("add", entity);

		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		try {
			if (((Resource) entity) instanceof Thing) {
				pimoService.create(entity);
			} else {
				resourceStore.create(pimoService.getPimoUri(), entity);
				pimoService.getOrCreateThingForOccurrence(entity);
			}
		} catch (ResourceExistsException e) {
			throw new InfosphereException("cannot create "+entity.asResource(), e);
		}
	}

	@Override
	public void update(T entity) throws InfosphereException {
		authorize("update", entity);
		
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		try {
			if (((Resource) entity) instanceof Thing) {
				pimoService.update(entity, true);
			} else {
				resourceStore.update(pimoService.getPimoUri(), entity, true);
			}
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot update "+entity.asResource(), e);
		}
	}

	@Override
	public void update(T entity, boolean override) throws InfosphereException {
		authorize("update", entity);
		
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		try {
			if (((Resource) entity) instanceof Thing) {
				pimoService.update(entity, !override);
			} else {
				resourceStore.update(pimoService.getPimoUri(), entity, !override);
			}
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot update "+entity.asResource(), e);
		}
	}

	@Override
	public void remove(String entityId) throws InfosphereException {
		authorize("remove", new URIImpl(entityId));
		
		ResourceStore resourceStore = getResourceStore();
		try {
			resourceStore.remove(new URIImpl(entityId));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot remove "+entityId, e);
		}
	}
	
	/**
	 * Protects resources retrieved from any data source (online accounts, phone books, etc.) from being modified
	 * or deleted.
	 * 
	 * @param action the action to be performed
	 * @param entity resource on which the action will be performed
	 * @throws InfosphereException if the resource was retrieved from a data source (operation not allowed)
	 */
	protected void authorize(String action, T entity) throws InfosphereException {
		authorize(action, entity.asURI());
	}

	/**
	 * Protects resources retrieved from any data source (online accounts, phone books, etc.) from being modified
	 * or deleted.
	 * 
	 * @param action the action to be performed
	 * @param entityId resource identifier on which the action will be performed
	 * @throws InfosphereException if the resource was retrieved from a data source (operation not allowed)
	 */
	protected void authorize(String action, URI entityId) throws InfosphereException {
		if ("update".equals(action) || "remove".equals(action)) {
			Node dataSource = ModelUtils.findObject(getTripleStore(), entityId, NIE.dataSource);
			if (dataSource != null) {
				throw new InfosphereException(entityId+" cannot be "+action+"d, it is a read-only resource retrieved from the data source "+dataSource);
			}
		}
	}

}
