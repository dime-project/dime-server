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

package eu.dime.ps.semantic.rdf;

import ie.deri.smile.rdf.TripleStore;

import java.util.Collection;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.query.Queryable;

public interface ResourceStore extends Queryable {

	void clear();
	void clear(URI graph);
	
	/**
	 * Print the whole content of this triple store to System.out.
	 */
	void dump();

	String getName();

	TripleStore getTripleStore();
	
	<T extends org.ontoware.rdfreactor.schema.rdfs.Resource> Query<T> find(Class<T> returnType);

	<T extends Resource> Collection<URI> listAllResources(Class<T> returnType);

	boolean exists(org.ontoware.rdf2go.model.node.Resource instanceIdentifier);
	boolean exists(URI graph, org.ontoware.rdf2go.model.node.Resource instanceIdentifier);
	boolean exists(Resource resource);

	boolean isTypedAs(org.ontoware.rdf2go.model.node.Resource instanceId, URI type) throws NotFoundException;

	void create(Resource resource) throws ResourceExistsException;
	
	/**
	 * Creates or saves a Resource in the persistent store.
	 * 
	 * @param resource the resource to be created or saved
	 * @param graph the graph in which the resource will be created
	 * @throws ResourceExistsException if the a resource with the same URI
	 *         already exists
	 */
	void create(URI graph, Resource resource) throws ResourceExistsException;
	
	void update(Resource resource) throws NotFoundException;

	/**
	 * Same as {@link update(Resource, URI, boolean)}, except the update
	 * is complete by default.
	 * 
	 * @param resource
	 * @param graph
	 * @throws NotFoundException
	 */
	void update(URI graph, Resource resource) throws NotFoundException;

	void update(Resource resource, boolean isPartial) throws NotFoundException;

	/**
	 * Updates/modifies a Resource in the persistent store. If only
	 * part of the resource needs to be updated, <em>isPartial</em> flag
	 * must be set to true. If <em>isPartial</em> is set to false,
	 * all known data about the resource will be deleted, and the data
	 * will be persisted.
	 * 
	 * @param resource the resource to be updated
	 * @param graph the graph in which the resource will be updated
	 * @param isPartial indicates if the update is complete or partial
	 * @throws NotFoundException if the resource does not exist
	 */
	void update(URI graph, Resource resource, boolean isPartial) throws NotFoundException;
	
	void update(Resource resource, URI property, Node value) throws NotFoundException;

	void createOrUpdate(URI graph, Resource resource);
	void createOrUpdate(URI graph, Resource resource, boolean isPartial);
	
	void remove(org.ontoware.rdf2go.model.node.Resource instanceId) throws NotFoundException;
	void remove(URI graph, org.ontoware.rdf2go.model.node.Resource instanceId) throws NotFoundException;
	
	/**
	 * Adds a new value for a property of a resource.
	 * 
	 * @param resource the resource identifier
	 * @param property the URI of the property
	 * @param value the Literal representing the value
	 * @throws NotFoundException if the resource does not exist
	 */
	void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, Literal value) throws NotFoundException;

	/**
	 * Same as {@link #addValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except the object/value is another resource.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, org.ontoware.rdf2go.model.node.Resource value)
			throws NotFoundException;
	
	/**
	 * Same as {@link #addValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)}, except a
	 * xsd:String datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, String value) throws NotFoundException;
	
	/**
	 * Same as {@link #addValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a language tag literal is created with the passed value and
	 * language tag as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource, URI property,
			String value, String languageTag) throws NotFoundException;
	
	/**
	 * Same as {@link #addValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a xsd:double datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource, URI property,
			double value) throws NotFoundException;
	
	/**
	 * Same as {@link #addValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)}, except a
	 * xsd:float datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource, URI property,
			float value) throws NotFoundException;
	
	/**
	 * Same as {@link #addValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a xsd:long datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource, URI property,
			long value) throws NotFoundException;
	
	/**
	 * Same as {@link #addValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a xsd:integer datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource, URI property,
			int value) throws NotFoundException;
	
	/**
	 * Sets/changes the value for a property of a resource. If previous
	 * statements exist for the property, they are deleted and a new
	 * one is added with the new value.
	 * 
	 * @param resource the resource identifier
	 * @param property the URI of the property
	 * @param value the Literal representing the value
	 * @throws NotFoundException if the resource does not exist
	 */
	void setValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, Literal value) throws NotFoundException;
	
	/**
	 * Same as {@link #setValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except the object value is another resource.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void setValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, org.ontoware.rdf2go.model.node.Resource value) throws NotFoundException;
	
	/**
	 * Same as {@link #setValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a language tag literal is created with the passed value and
	 * language tag as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void setValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, String value, String languageTag) throws NotFoundException;
	
	/**
	 * Same as {@link #setValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a xsd:String datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void setValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, String value) throws NotFoundException;
	
	/**
	 * Same as {@link #setValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a xsd:double datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void setValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, double value) throws NotFoundException;
	
	/**
	 * Same as {@link #setValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a xsd:float datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void setValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, float value) throws NotFoundException;
	
	/**
	 * Same as {@link #setValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a xsd:long datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void setValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, long value) throws NotFoundException;
	
	/**
	 * Same as {@link #setValue(org.ontoware.rdf2go.model.node.Resource, URI, Literal)},
	 * except a xsd:int datatype literal is created with the passed value
	 * as the literal.
	 * 
	 * @param resource
	 * @param property
	 * @param value
	 * @throws NotFoundException
	 */
	void setValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, int value) throws NotFoundException;

	void removeValue(org.ontoware.rdf2go.model.node.Resource resource,
			URI property, org.ontoware.rdf2go.model.node.Resource value)
			throws NotFoundException;

	void removeValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, org.ontoware.rdf2go.model.node.Resource value)
			throws NotFoundException;

	<T extends Resource> T getByUUID(String uuid, Class<T> returnType) throws NotFoundException;
	<T extends Resource> T getByUUID(String uuid, Class<T> returnType, URI... properties) throws NotFoundException;

	URI createGraph(URI graphType);

	URI createGraph(Model graphModel);

	URI createGraph(URI graph, Model graphModel);

//	URI createGraph(Model graphModel, Model metadataGraphModel);

//	URI createGraph(URI graph, Model graphModel, Model metadataGraphModel);

	/**
	 * Remove all contents from a graph, including the graph definition and
	 * any graph containing metadata for the given graph.
	 * @param graph the graph to be removed
	 */
	void removeGraph(URI graph);

	/**
	 * Replace a resource URI with another one. If the newUri
	 * is already taken by a resource, that resource and its 
	 * metadata will remain.
	 * @param sourceUri resource URI to change/replace its URI
	 * @param targetUri new URI to replace the resource URI
	 */
	void replaceUri(URI sourceUri, URI targetUri);
	
}
