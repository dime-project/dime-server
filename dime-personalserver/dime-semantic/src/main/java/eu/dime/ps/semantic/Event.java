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

package eu.dime.ps.semantic;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;

public class Event {

	public static final String ACTION_RESOURCE_READ = "eu.dime.ps.semantic.action.RESOURCE_READ";
	public static final String ACTION_RESOURCE_ADD = "eu.dime.ps.semantic.action.RESOURCE_ADD";
	public static final String ACTION_RESOURCE_MODIFY = "eu.dime.ps.semantic.action.RESOURCE_MODIFY";
	public static final String ACTION_RESOURCE_DELETE = "eu.dime.ps.semantic.action.RESOURCE_DELETE";
	public static final String ACTION_TRIPLE_ADD = "eu.dime.ps.semantic.action.TRIPLE_ADD";
	public static final String ACTION_TRIPLE_MODIFY = "eu.dime.ps.semantic.action.TRIPLE_MODIFY";
	public static final String ACTION_TRIPLE_DELETE = "eu.dime.ps.semantic.action.TRIPLE_DELETE";
	public static final String ACTION_GRAPH_ADD = "eu.dime.ps.semantic.action.GRAPH_ADD";
	public static final String ACTION_GRAPH_MODIFY = "eu.dime.ps.semantic.action.GRAPH_MODIFY";
	public static final String ACTION_GRAPH_DELETE = "eu.dime.ps.semantic.action.GRAPH_DELETE";
	public static final String ACTION_GRAPH_TOUCH = "eu.dime.ps.semantic.action.GRAPH_TOUCH";
	
	public static final String ACTION_PERSON_MERGE = "eu.dime.ps.semantic.action.ACTION_PERSON_MERGE";
	public static final String ACTION_PERSON_MATCH = "eu.dime.ps.semantic.action.ACTION_PERSON_MATCH";
	
	public static final String ACTION_CRAWL_COMPLETED = "eu.dime.ps.datamining.action.CRAWL_COMPLETED";

	private String context;
	private String action;
	private Resource identifier;
	private org.ontoware.rdfreactor.schema.rdfs.Resource data;
	private final long timestamp;
	
	private final String tenantId;
	
	/**
	 * Create an empty event.
	 */
	public Event(String tenantId) {
		this.tenantId = tenantId;
		this.timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Copy constructor.
	 * @param other event to copy
	 */
	public Event(String tenantId, Event other) {
		this(tenantId);
		this.context = other.getContext();
		this.action = other.getAction();
		this.identifier = other.getIdentifier();
		this.data = other.getData();
	}
	
	/**
	 * Create an event specifying action and data
	 * @param action
	 * @param data
	 */
	public Event(String tenantId, String action, Resource resourceIdentifier) {
		this(tenantId);
		this.action = action;
		this.identifier = resourceIdentifier;
	}
	
	/**
	 * Create an event specifying action and data
	 * @param action
	 * @param resource
	 */
	public Event(String tenantId, String action, org.ontoware.rdfreactor.schema.rdfs.Resource resource) {
		this(tenantId);
		this.action = action;
		this.identifier = resource.asResource();
		this.data = resource;
	}
	
	/**
	 * Create an event specifying its scope, action and resource identifier
	 * @param scope
	 * @param action
	 * @param resource
	 */
	public Event(String tenantId, String scope, String action, Resource resourceIdentifier) {
		this(tenantId);
		this.context = scope;
		this.action = action;
		this.identifier = resourceIdentifier;
	}

	/**
	 * Create an event specifying its scope, action and resource
	 * @param scope
	 * @param action
	 * @param resource
	 */
	public Event(String tenantId, String scope, String action, org.ontoware.rdfreactor.schema.rdfs.Resource resource) {
		this(tenantId);
		this.context = scope;
		this.action = action;
		this.identifier = resource.asResource();
		this.data = resource;
	}

	/**
	 * Returns the context or graph in which the changes or
	 * event was performed.
	 * @return the string identifier of the context
	 */
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * Returns the action performed (e.g. ACTION_RESOURCE_READ).
	 * @return the string identifier of the action
	 */
	public String getAction() {
		return this.action;
	}

	public Event setAction(String action) {
		this.action = action;
		return this;
	}

	/**
	 * Returns the identifier of the resource related to the event.
	 * @return the URI or blank node identifier
	 */
	public Resource getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Resource identifier) {
		this.identifier = identifier;
	}

	public boolean is(URI type) {
		return getTypes().contains(type);
	}
	
	/**
	 * Returns the type (RDFS class) of the resource.
	 * @return the URI of the RDFS class of the resource or null if unknown
	 */
	public List<URI> getTypes() {
		List<URI> types = new ArrayList<URI>();
		if (data != null) {
			data.getRDFSClassURI();
			ClosableIterator<Statement> typesIt = data.getModel().findStatements(data.asResource(), RDF.type, Variable.ANY);
			while (typesIt.hasNext()) {
				types.add(typesIt.next().getObject().asURI());
			}
			typesIt.close();
		}
		return types;
	}

	/**
	 * Returns the metadata object related to the event.
	 * @return the RDFReactor RDF object with the metadata
	 */
	public org.ontoware.rdfreactor.schema.rdfs.Resource getData() {
		return data;
	}

	public void setData(org.ontoware.rdfreactor.schema.rdfs.Resource data) {
		this.data = data;
	}

	/**
	 * Returns the timestamp of the event in milliseconds.
	 * @return the difference, measured in milliseconds, between the time the event occurred
	 * and midnight, January 1, 1970 UTC
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getTenant() {
		return tenantId;
	}

    public Long getTenantId(){
		return new Long(tenantId);
    }
}
