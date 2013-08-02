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

package eu.dime.ps.semantic.service;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.semantic.exception.NameNotUniqueException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.OntologyInvalidException;
import eu.dime.ps.semantic.model.nao.Tag;

public interface TaggingService {

	public Tag getOrCreateTag(String name) throws OntologyInvalidException;
	
	/**
	 * Creates a new tag with name. If there is already a tag with this name,
	 * no new tag will be created and a NameNotUniqueException will be thrown.
	 * 
	 * @param name name for the tag
	 * @return the uri of the newly created Tag. 
	 * @throws NameNotUniqueException
	 */
	public URI createTag(String name) throws NameNotUniqueException, OntologyInvalidException;
	
	/**
	 * Makes the passed thing to a NAO:Tag if possible.
	 * 
	 * @param thing the resource which should be a NAO:Tag
	 * @throws OntologyInvalidException
	 * @throws NameNotUniqueException this exception will be thrown, if there is already an other NAO:Tag which have
	 * the same label, as the passed thing.
	 */
	public void createTag(URI thing) throws NameNotUniqueException, OntologyInvalidException;
	
	/**
	 * Adds a tag to a resource. The tag must be of the type NAO:Tag.
	 * You can only tag things which are in the pimo. When you tag a
	 * resource which is not in the pimo a new pimo thing will be created
	 * and adds the resource as groundingOccurrence to this thing.
	 * Every so created resource will have the type PIMO:Document.
	 * If this is not acceptable, you have the create the resource as
	 * PIMO.Thing.
	 *  
	 * @param resource resource to be tagged. Can be a Thing or any resource.
     * If any resource, a thing will be created automatically.
	 * @param tag the tag for the resource. Tags, Things, or Resources
     * can be passed. if the passed <code>tag</code> is not a tag yet,
     * a new tag will be created using the {@link #createTag(URI)} method.
	 * @throws NotFoundException if the resource doesn't exist
	 * @throws OntologyInvalidException when the tag is not a tag and cannot be converted to one
	 */
	public void addTag(URI resource, URI tag) throws NameNotUniqueException, OntologyInvalidException, NotFoundException;
	
    /**
     * Remove the passed tag from the resource.
     * 
     * @param resource resource which is annotated with nao:hasTag to the tag
     * @param tag uri of the tag
	 * @throws NotFoundException if the resource doesn't exist
     */
	public void removeTag(URI resource, URI tag) throws NotFoundException;

}
