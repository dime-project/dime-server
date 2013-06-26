package eu.dime.ps.semantic.rdf.impl;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.DateUtil;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NRL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.Class4Type;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.query.impl.BasicQuery;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * RDF store wrapper which allows to work with RDF resources instead of directly
 * with triples.
 * 
 * @author Ismael Rivera
 */
public class ResourceStoreImpl implements ResourceStore {

	private static final Logger logger = LoggerFactory.getLogger(ResourceStoreImpl.class);

    private final BroadcastManager broadcastManager; 

	private final TripleStore tripleStore;
	private final ModelSet underlyingModelSet;
	
	private final String name;
	
	public ResourceStoreImpl(TripleStore tripleStore) {
		this.tripleStore = tripleStore;
		this.underlyingModelSet = tripleStore.getUnderlyingModelSet();
		this.name = tripleStore.getName();
		this.broadcastManager = BroadcastManager.getInstance();
	}
	
	@Override
	public void dump() {
		System.out.println(tripleStore.serialize(Syntax.Trig));
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public TripleStore getTripleStore() {
		return tripleStore;
	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> Query<T> find(Class<T> returnType) {
		return new BasicQuery<T>(this, returnType);
	}

	@Override
	public <T extends Resource> Collection<URI> listAllResources(Class<T> returnType) {
		Collection<org.ontoware.rdf2go.model.node.Resource> identifiers = find(returnType).ids();
		Collection<URI> all = new ArrayList<URI>(identifiers.size());
		for (org.ontoware.rdf2go.model.node.Resource identifier : identifiers) {
			all.add(identifier.asURI());
		}
		return all;
	}

	@Override
	public Resource get(
			org.ontoware.rdf2go.model.node.Resource instanceIdentifier)
			throws NotFoundException {
		// forcing identifier to be a RDF2Go resource (in case an object of a subclass is passed)
		instanceIdentifier = instanceIdentifier.asResource();

		Resource resource = getAndCastTo(instanceIdentifier, guessClass(instanceIdentifier));
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}

	@Override
	public Resource get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			URI... properties) throws NotFoundException {
		// forcing identifier to be a RDF2Go resource (in case an object of a subclass is passed)
		instanceIdentifier = instanceIdentifier.asResource();

		Resource resource = getAndCastTo(instanceIdentifier, guessClass(instanceIdentifier), properties);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}
	
	@Override
	public <T extends Resource> T get(
			org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType) throws NotFoundException {
		// forcing identifier to be a RDF2Go resource (in case an object of a subclass is passed)
		instanceIdentifier = instanceIdentifier.asResource();

		T resource = getAndCastTo(Variable.ANY, instanceIdentifier, returnType);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}

	@Override
	public <T extends Resource> T get(
			org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType, URI... properties) throws NotFoundException {
		// forcing identifier to be a RDF2Go resource (in case an object of a subclass is passed)
		instanceIdentifier = instanceIdentifier.asResource();

		T resource = getAndCastTo(instanceIdentifier, returnType, properties);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}

	@Override
	public <T extends Resource> T get(URI graphUri,
			org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType) throws NotFoundException {
		// forcing identifier to be a RDF2Go resource (in case an object of a subclass is passed)
		instanceIdentifier = instanceIdentifier.asResource();

		T resource = getAndCastTo(graphUri, instanceIdentifier, returnType);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}

	@Override
	public <T extends Resource> T get(URI graphUri, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType, URI... properties) throws NotFoundException {
		// forcing identifier to be a RDF2Go resource (in case an object of a subclass is passed)
		instanceIdentifier = instanceIdentifier.asResource();

		T resource = getAndCastTo(graphUri, instanceIdentifier, returnType, properties);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}

	private <T extends Resource> T get(UriOrVariable graphUri,
			org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType) throws NotFoundException {
		T resource = getAndCastTo(graphUri, instanceIdentifier, returnType);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}

	@SuppressWarnings("unchecked")
	private <T extends Resource> T getAndCastTo(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType, URI... properties) throws NotFoundException {
		
		// check if resource identifier exists
		if (!exists(instanceIdentifier)) {
			throw new NotFoundException("Resource " + instanceIdentifier + " does not exist.");
		}
		
		// check if the resource is of the type requested
		assertType(instanceIdentifier, returnType);
		
		Model sinkModel = tripleStore.createModel();
		ModelUtils.fetch(underlyingModelSet, sinkModel, instanceIdentifier, true, true, null, null, false, properties);
		Resource resource = new Resource(sinkModel, instanceIdentifier, false);
		
		// if returnType is just Resource, we try to guess a more concrete type
		returnType = returnType.equals(Resource.class) ? (Class<T>) guessClass(instanceIdentifier) : returnType;

		return (T) resource.castTo(returnType);
	}
	
	private <T extends Resource> T getAndCastTo(UriOrVariable context, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType) throws NotFoundException {
		return getAndCastTo(context, instanceIdentifier, returnType, new URI[0]);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Resource> T getAndCastTo(UriOrVariable context, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType, URI... properties) throws NotFoundException {

		// check if resource identifier exists
		if (!exists(instanceIdentifier)) {
			throw new NotFoundException("Resource " + instanceIdentifier + " does not exist.");
		}
		
		// check if the resource is of the type requested
		assertType(instanceIdentifier, returnType);

		Model sinkModel = tripleStore.createModel();
		if (context == Variable.ANY || context == null) {
			ModelUtils.fetch(underlyingModelSet, sinkModel, instanceIdentifier, true);
		} else if (context instanceof URI) {
			ModelSet modelSet = RDF2Go.getModelFactory().createModelSet();
			modelSet.open();
			modelSet.addModel(underlyingModelSet.getModel((URI) context));
			ModelUtils.fetch(modelSet, sinkModel, instanceIdentifier, properties);
		} else {
			logger.warn("'context' parameter must be either Variable.ANY or a URI");
		}
		Resource resource = new Resource(sinkModel, instanceIdentifier.asResource(), false);

		// if returnType is just Resource, we try to guess a more concrete type
		returnType = returnType.equals(Resource.class) ? (Class<T>) guessClass(instanceIdentifier) : returnType;

		return (T) resource.castTo(returnType);
	}
	
	private <T extends Resource> void assertType(org.ontoware.rdf2go.model.node.Resource instanceIdentifier, Class<T> returnType)
			throws NotFoundException {
		try {
			String clazz = String.valueOf(returnType.getDeclaredField("RDFS_CLASS").get(returnType));
			URI type = new URIImpl(clazz);
			if (!type.equals(RDFS.Resource) && !isTypedAs(instanceIdentifier, type)) {
				throw new NotFoundException("Resource " + instanceIdentifier + " may exist, but it's not of type " + type);
			}
		} catch (IllegalAccessException e) {
			throw new NotFoundException("Class attribute 'RDFS_CLASS' of returnType is not accessible, object type cannot be verified.", e);
		} catch (NoSuchFieldException e) {
			throw new NotFoundException("Class attribute 'RDFS_CLASS' does not exist, object type cannot be verified.", e);
		}
	}
	
	private <T extends Resource> Class<T> guessClass(org.ontoware.rdf2go.model.node.Resource instanceIdentifier) {
		Class<T> clazz = null;
		
		// look for the Class of the RDF types of the resource (not inferred types)
		List<URI> types = tripleStore.getTypes(instanceIdentifier);
		for (URI type : types) {
			clazz = Class4Type.getClassForType(type);
			if (clazz != null) {
				break;
			}
		}
		
		// by default everything is a Resource
		if (clazz == null)	clazz = (Class<T>) Resource.class;

		return clazz;
	}
	
	@Override
	public ClosableIterable<Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			MalformedQueryException, ModelRuntimeException {
		return tripleStore.queryConstruct(query, querylanguage);
	}

	@Override
	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, MalformedQueryException,
			ModelRuntimeException {
		return tripleStore.querySelect(query, querylanguage);
	}

	@Override
	public boolean sparqlAsk(String query) throws ModelRuntimeException,
			MalformedQueryException {
		return tripleStore.sparqlAsk(query);
	}

	@Override
	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException, MalformedQueryException {
		return tripleStore.sparqlConstruct(query);
	}

	@Override
	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		return tripleStore.sparqlDescribe(query);
	}

	@Override
	public QueryResultTable sparqlSelect(String queryString)
			throws MalformedQueryException, ModelRuntimeException {
		return tripleStore.sparqlSelect(queryString);
	}

	@Override
	public void clear() {
		tripleStore.clear();
	}

	@Override
	public void clear(URI graph) {
		tripleStore.clear(graph);
	}

	@Override
	public boolean exists(org.ontoware.rdf2go.model.node.Resource instanceIdentifier) {
		// a resource exists iff there is a triple such <resource - rdf:type - ?>
		return tripleStore.containsStatements(Variable.ANY, instanceIdentifier, RDF.type, Variable.ANY);
	}

	@Override
	public boolean exists(Resource resource) {
		// a resource exists iff there is a triple such <resource - rdf:type - ?>
		URI context = resource.getModel().getContextURI();
		return tripleStore.containsStatements(context, resource.asResource(), RDF.type, Variable.ANY);
	}

	@Override
	public boolean exists(URI graph,
			org.ontoware.rdf2go.model.node.Resource instanceId) {
		// a resource exists iff there is a triple such <resource - rdf:type - ?> in a specific graph
		return tripleStore.containsStatements(graph, instanceId, RDF.type, Variable.ANY);
	}

	private void assertExist(org.ontoware.rdf2go.model.node.Resource instanceId,
			String message) throws NotFoundException {
		if (!exists(instanceId))
			throw new NotFoundException(instanceId+" does not exist: "+message);
	}
	
	private void assertNotExist(org.ontoware.rdf2go.model.node.Resource instanceId,
			String message) throws ResourceExistsException {
		if (exists(instanceId))
			throw new ResourceExistsException(instanceId+" already exists: "+message);
	}
	
	private void assertExist(URI graph, org.ontoware.rdf2go.model.node.Resource instanceId,
			String message) throws NotFoundException {
		if (!exists(graph, instanceId))
			throw new NotFoundException(instanceId+" does not exist in graph "+graph+": "+message);
	}
	
	private void assertNotExist(URI graph, org.ontoware.rdf2go.model.node.Resource instanceId,
			String message) throws ResourceExistsException {
		if (exists(graph, instanceId))
			throw new ResourceExistsException(instanceId+" already exists in graph "+graph+": "+message);
	}
	
	@Override
	public boolean isTypedAs(org.ontoware.rdf2go.model.node.Resource instanceId,
			URI type) throws NotFoundException {
		assertExist(instanceId, "cannot be of any type.");
		return tripleStore.isTypedAs(instanceId, type);
	}

	/* Saves the resource in the RDF store without checking if the resource
	   already exists or not */
	private void createWithoutAsserting(URI graph, Resource resource) {
		// relate resource with graph through nao:hasDataGraph
		if (graph != null) {
			resource.getModel().addStatement(resource, NAO.hasDataGraph, graph);
		}
		
		// set nao:created & nao:lastModified if missing
		Literal now = DateUtil.currentDateTimeAsLiteral();
		if (!resource.getModel().contains(resource, NAO.created, Variable.ANY)) {
			resource.getModel().addStatement(resource, NAO.created, now);
		}
		if (!resource.getModel().contains(resource, NAO.lastModified, Variable.ANY)) {
			resource.getModel().addStatement(resource, NAO.lastModified, now);
		}
		
		underlyingModelSet.addModel(resource.getModel(), graph);
	}

	/* Updates a resource, assuming is already in the RDF store */
	private void updateWithoutAsserting(UriOrVariable graph, Resource resource, boolean isPartial) {
		Model toRemove = RDF2Go.getModelFactory().createModel().open();
		
		// makes sure nao:hasDataGraph is specified
		if (graph != null && graph instanceof URI) {
			resource.getModel().addStatement(resource, NAO.hasDataGraph, (URI) graph);
		}
		
		// set nao:created & nao:lastModified if missing
		Literal now = DateUtil.currentDateTimeAsLiteral();
		if (!resource.getModel().contains(resource, NAO.lastModified, Variable.ANY)) {
			resource.getModel().addStatement(resource, NAO.lastModified, now);
		}

		try {
			// load current data for the resource
			Resource existing = get(graph, resource.asResource(), Resource.class);
	
			if (isPartial) {
				// remove all triples with the same properties as the passed resource
				ClosableIterator<Statement> statements = resource.getModel().findStatements(resource.asResource(), Variable.ANY, Variable.ANY);
				while (statements.hasNext()) {
					toRemove.addAll(existing.getModel().findStatements(resource.asResource(), statements.next().getPredicate(), Variable.ANY));
				}
				statements.close();
			} else {
				toRemove.addAll(existing.getModel().iterator());
			}
		} catch (NotFoundException e) {
			logger.debug("Updating non-existing resource "+resource+". It will just be saved anyway.");
		}
		
		// removes overridden predicates, and add new triples of the resource
		tripleStore.update(graph, toRemove.iterator(), resource.getModel().iterator());
	}

	@Override
	public void create(Resource resource) throws ResourceExistsException {
		assertNotExist(resource, "cannot be created again.");
		createWithoutAsserting(null, resource);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_ADD, resource));
	}

	@Override
	public void create(URI graph, Resource resource) throws ResourceExistsException {
		assertNotExist(graph, resource, "cannot be created again.");
		createWithoutAsserting(graph, resource);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_ADD, resource));
	}
	
	@Override
	public void update(Resource resource) throws NotFoundException {
		update(resource.getModel().getContextURI(), resource, false);
	}

	@Override
	public void update(URI graph, Resource resource) throws NotFoundException {
		update(graph, resource, false);
	}

	@Override
	public void update(Resource resource, boolean isPartial)
			throws NotFoundException {
		assertExist(resource, "cannot be updated.");
		URI context = resource.getModel().getContextURI();
		updateWithoutAsserting(context, resource, isPartial);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public void update(URI graph, Resource resource, boolean isPartial)
			throws NotFoundException {
		assertExist(graph, resource, "cannot be updated.");
		updateWithoutAsserting(graph, resource, isPartial);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public void update(Resource resource, URI property, Node value) throws NotFoundException {
		assertExist(resource, "cannot be updated.");
		
		URI context = resource.getModel().getContextURI();
		tripleStore.removeStatements(context, resource, property, Variable.ANY);
		tripleStore.addStatement(context, resource, property, value);
		
		resource = get(resource.asResource());
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public void createOrUpdate(URI graph, Resource resource) {
		createOrUpdate(graph, resource, false);
	}

	@Override
	public void createOrUpdate(URI graph, Resource resource, boolean isPartial) {
		if (exists(resource)) {
			updateWithoutAsserting(graph, resource, isPartial);
			broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
		} else {
			createWithoutAsserting(graph, resource);
			broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_ADD, resource));
		}
	}

	@Override
	public void remove(org.ontoware.rdf2go.model.node.Resource instanceIdentifier) throws NotFoundException {
		assertExist(instanceIdentifier, "cannot be removed.");
		Resource resource = get(instanceIdentifier);
		tripleStore.remove(instanceIdentifier);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_DELETE, resource));
	}

	@Override
	public void remove(URI graph, org.ontoware.rdf2go.model.node.Resource instanceIdentifier)
			throws NotFoundException {
		assertExist(graph, instanceIdentifier, "cannot be removed.");
		Resource resource = get(graph, instanceIdentifier, Resource.class);
		tripleStore.removeFromGraph(graph, instanceIdentifier);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_DELETE, resource));
	}

	@Override
	public void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			URI property, Literal value) throws NotFoundException {
		tripleStore.addStatement(graph, instanceIdentifier, property, value);
		Resource resource = get(graph, instanceIdentifier, Resource.class);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			URI property, org.ontoware.rdf2go.model.node.Resource value)
			throws NotFoundException {
		assertExist(instanceIdentifier, "cannot add metadata to inexisting resource.");
		tripleStore.addStatement(graph, instanceIdentifier, property, value);
		Resource resource = get(graph, instanceIdentifier, Resource.class);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, String value) throws NotFoundException {
		addValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource, URI property, String value,
			String languageTag) throws NotFoundException {
		addValue(graph, resource, property, tripleStore.createLiteral(value, languageTag));
	}

	@Override
	public void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, double value) throws NotFoundException {
		addValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, float value) throws NotFoundException {
		addValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, long value) throws NotFoundException {
		addValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void addValue(URI graph, org.ontoware.rdf2go.model.node.Resource resource,
			URI property, int value) throws NotFoundException {
		addValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void setValue(URI graph,
			org.ontoware.rdf2go.model.node.Resource instanceIdentifier, URI property,
			Literal value) throws NotFoundException {
		tripleStore.remove(graph, instanceIdentifier, property);
		tripleStore.addStatement(graph, instanceIdentifier, property, value);
		Resource resource = get(graph, instanceIdentifier, Resource.class);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public void setValue(URI graph,
			org.ontoware.rdf2go.model.node.Resource instanceIdentifier, URI property,
			org.ontoware.rdf2go.model.node.Resource value)
			throws NotFoundException {
		tripleStore.remove(graph, instanceIdentifier, property);
		tripleStore.addStatement(graph, instanceIdentifier, property, value);
		Resource resource = get(graph, instanceIdentifier, Resource.class);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public void setValue(URI graph,
			org.ontoware.rdf2go.model.node.Resource resource, URI property,
			String value, String languageTag) throws NotFoundException {
		setValue(graph, resource, property, tripleStore.createLiteral(value, languageTag));
	}

	@Override
	public void setValue(URI graph,
			org.ontoware.rdf2go.model.node.Resource resource, URI property,
			String value) throws NotFoundException {
		setValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void setValue(URI graph,
			org.ontoware.rdf2go.model.node.Resource resource, URI property,
			double value) throws NotFoundException {
		setValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void setValue(URI graph,
			org.ontoware.rdf2go.model.node.Resource resource, URI property,
			float value) throws NotFoundException {
		setValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void setValue(URI graph,
			org.ontoware.rdf2go.model.node.Resource resource, URI property,
			long value) throws NotFoundException {
		setValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void setValue(URI graph,
			org.ontoware.rdf2go.model.node.Resource resource, URI property,
			int value) throws NotFoundException {
		setValue(graph, resource, property, tripleStore.createLiteral(value));
	}

	@Override
	public void removeValue(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			URI property, org.ontoware.rdf2go.model.node.Resource value)
			throws NotFoundException {
		tripleStore.removeStatements(Variable.ANY, instanceIdentifier, property, value);
		Resource resource = get(instanceIdentifier);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public void removeValue(URI graph, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			URI property, org.ontoware.rdf2go.model.node.Resource value)
			throws NotFoundException {
		tripleStore.removeStatements(graph, instanceIdentifier, property, value);
		Resource resource = get(instanceIdentifier);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
	}

	@Override
	public <T extends Resource> T getByUUID(String uuid, Class<T> returnType) throws NotFoundException {
		URI identifier = new URIImpl("urn:uuid:" + uuid);
		T resource = get(identifier, returnType);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}
	
	@Override
	public <T extends Resource> T getByUUID(String uuid, Class<T> returnType, URI... properties) throws NotFoundException {
		URI identifier = new URIImpl("urn:uuid:" + uuid);
		T resource = get(identifier, returnType, properties);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_READ, resource));
		return resource;
	}

	public URI createGraph(URI graphType) {
		URI graph = new URIImpl("urn:uuid:" + UUID.randomUUID().toString());
		tripleStore.createGraph(graph, graphType);
		return graph;
	}
	
	public URI createGraph(Model graphModel) {
		return createGraph(new URIImpl("urn:uuid:" + UUID.randomUUID().toString()), graphModel);
	}

	public URI createGraph(URI graph, Model graphModel) {
		tripleStore.createGraph(graph);
		tripleStore.addAll(graph, graphModel.iterator());
		return graph;
	}

	@Override
	public void removeGraph(URI graph) {
		ClosableIterator<Statement> cIt = tripleStore.findStatements(Variable.ANY, Variable.ANY, NRL.coreGraphMetadataFor, graph);
		while (cIt.hasNext()) {
			URI metadataGraph = cIt.next().getSubject().asURI();
			tripleStore.removeStatements(metadataGraph, Variable.ANY, Variable.ANY, Variable.ANY);
			broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_GRAPH_DELETE, metadataGraph));
		}
		tripleStore.removeStatements(graph, Variable.ANY, Variable.ANY, Variable.ANY);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_GRAPH_DELETE, graph));
	}

	/**
	 * Returns the graph URI of the first graph which contains a triple stating
	 * the rdf:type of the resource (usually where the resource has been created).
	 * @param resource the resource whose graph URI needs to be found
	 * @return the URI of the graph in which the resource is defined
	 */
	protected URI findGraphForResource(org.ontoware.rdf2go.model.node.Resource resource) {
		URI graph = null;
		ClosableIterator<? extends Statement> cIt = tripleStore.findStatements(Variable.ANY, resource, RDF.type, Variable.ANY);
		while (cIt.hasNext()) {
			URI context = cIt.next().getContext();
			if (context != null) {
				graph = context;
				break;
			}
		}
		cIt.close();
		return graph;
	}

	@Override
	public void replaceUri(URI sourceUri, URI targetUri) {
		tripleStore.replaceUri(sourceUri, targetUri);
	}

}
