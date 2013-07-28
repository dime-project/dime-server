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

package eu.dime.ps.semantic.service.context;

import ie.deri.smile.util.DateUtil;
import ie.deri.smile.vocabulary.DCON;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.exception.OntologyInvalidException;
import eu.dime.ps.semantic.model.ResourceFactory;
import eu.dime.ps.semantic.model.dcon.Aspect;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;
import eu.dime.ps.semantic.util.DateUtils;

public class LiveContextSessionImpl implements LiveContextSession {

	private static final Logger logger = LoggerFactory.getLogger(LiveContextSessionImpl.class);

	private static final String DCON_PATH = "vocabularies/dcon/dcon.trig";
	
	private boolean autoCommit = true;
	private boolean closed = false;
	
	private Model liveContext;
	private UpdateStrategy updateStrategy;
	
	private List<URI> contextAttributes = new ArrayList<URI>();
	
	private List<Statement> toAdd = new ArrayList<Statement>();
	private List<Statement> toRemove = new ArrayList<Statement>();
	
	private URI dataSource = null;
	
	private final String tenant;
	private final BroadcastManager broadcastManager;
	
	public LiveContextSessionImpl(String tenant, Model liveContext, UpdateStrategy updateStrategy) {
		this.tenant = tenant;
		this.liveContext = liveContext;
		this.updateStrategy = updateStrategy;
		this.broadcastManager = BroadcastManager.getInstance();
		init();
	}
	
	public LiveContextSessionImpl(String tenant, Model liveContext, UpdateStrategy updateStrategy, URI dataSource) {
		this(tenant, liveContext, updateStrategy);
		this.dataSource = dataSource;
	}
	
	private void init() {
		InputStream in = LiveContextSessionImpl.class.getClassLoader().getResourceAsStream(DCON_PATH);
		if (in == null) {
            throw new RuntimeException(
            		new OntologyInvalidException("Cannot load DCON ontology file: " + DCON_PATH));
        }
		
		Model model = RDF2Go.getModelFactory().createModel().open();
		try {
			model.readFrom(in, Syntax.Trig);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load DCON ontology file: " + DCON_PATH, e);
		}
		
		// cache all subproperties of dcon:hasContextAttribute
		ClosableIterator<Statement> attIt = model.findStatements(Variable.ANY, RDFS.subPropertyOf, DCON.hasContextAttribute);
		while (attIt.hasNext()) {
			contextAttributes.add(attIt.next().getSubject().asURI());
		}
		attIt.close();
	}
	
	@Override
	public void commit() throws LiveContextException {
		if (closed) {
			throw new LiveContextException("This session has already been closed.");
		}

		long start = System.currentTimeMillis();
		try {
			// updates the live & previous context using a specific strategy
			updateStrategy.update(toAdd, toRemove);
			
			// get all subject/object URIs, and broadcast 'resource.modified' events
			Set<URI> modified = new HashSet<URI>();
			for (Statement stmt : toAdd) {
				if (stmt.getSubject() instanceof URI) {
					modified.add(stmt.getSubject().asURI());
				}
				if (stmt.getObject() instanceof URI) {
					modified.add(stmt.getObject().asURI());
				}
			}
			for (Statement stmt : toRemove) {
				if (stmt.getSubject() instanceof URI) {
					modified.add(stmt.getSubject().asURI());
				}
				if (stmt.getObject() instanceof URI) {
					modified.add(stmt.getObject().asURI());
				}
			}
			for (URI resource : modified) {
				broadcastManager.sendBroadcast(new Event(tenant, Event.ACTION_RESOURCE_MODIFY, resource));
			}
			
			// clear cache of latest changes (triples to add/remove)
			toAdd.clear();
			
			logger.info("Live Context Session was committed [tenant = "+tenant+", time = "+(System.currentTimeMillis() - start)+" ms]");
			
		} catch (ModelRuntimeException e) {
			throw new LiveContextException("An error occurred updating the live context: " + e.getMessage(), e);
		}
	}

	@Override
	public void rollback() throws LiveContextException {
		if (closed) {
			throw new LiveContextException("This session has already been closed.");
		}

		// removes all outstanding statements to remove or add
		toRemove.clear();
		toAdd.clear();
	}

	@Override
	public boolean getAutoCommit() throws LiveContextException {
		return autoCommit;
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws LiveContextException {
		if (closed) {
			throw new LiveContextException("This session has already been closed.");
		}
		
		this.autoCommit = autoCommit;
	}

	@Override
	public void close() throws LiveContextException {
		closed = true;
	}

	@Override
	public void set(Class<? extends Aspect> aspect, URI property, URI... elementUris)
			throws LiveContextException {
		toRemove(liveContext.findStatements(findAspect(aspect), property, Variable.ANY));
		_add(aspect, property, elementUris);
		commitIfAuto();
	}

	@Override
	public void set(Class<? extends Aspect> aspect, URI property, Resource... elements)
			throws LiveContextException {
		toRemove(liveContext.findStatements(findAspect(aspect), property, Variable.ANY));
		_add(aspect, property, elements);
		commitIfAuto();
	}

	@Override
	public void set(URI resource, URI property, Object... values) throws LiveContextException {
		toRemove(liveContext.findStatements(resource, property, Variable.ANY));
		for (Object value : values)
			toAdd.add(liveContext.createStatement(resource, property, literal(value)));
		commitIfAuto();
	}

	@Override
	public void add(Class<? extends Aspect> aspect, URI property, URI... elementUris) throws LiveContextException {
		_add(aspect, property, elementUris);
		commitIfAuto();
	}

	@Override
	public void add(Class<? extends Aspect> aspect, URI property, Resource... elements) throws LiveContextException {
		_add(aspect, property, elements);
		commitIfAuto();
	}

	@Override
	public void add(URI resource, URI property, Object... values) throws LiveContextException {
		for (Object value : values) {
			toAdd.add(liveContext.createStatement(resource, property, literal(value)));
		}
		commitIfAuto();
	}

	private void _add(Class<? extends Aspect> aspect, URI property, URI... elements) throws LiveContextException {
		URI aspectUri = findAspect(aspect);
		
		for (URI element : elements) {
			// link aspect with element
			toAdd.add(liveContext.createStatement(aspectUri, property, element));
			
			// add an empty Observation (required for recordedAt & recordedBy)
			URI observation = new URIImpl(element+"-"+System.currentTimeMillis());
			toAdd.add(new StatementImpl(liveContext.getContextURI(), element, DCON.hasObservation, observation));
			toAdd.add(new StatementImpl(liveContext.getContextURI(), observation, RDF.type, DCON.Observation));

			// keep provenance and timestamp of recorded element data
			toAdd.add(liveContext.createStatement(observation, DCON.recordedAt, DateUtils.currentDateTimeAsLiteral()));
			toAdd.add(liveContext.createStatement(observation, DCON.recordedBy, dataSource));
		}
	}
	
	private void _add(Class<? extends Aspect> aspect, URI property, Resource... elements) throws LiveContextException {
		URI aspectUri = findAspect(aspect);
		
		for (Resource element : elements) {
			URI elementUri = element.asURI();
			
			// link aspect with element
			toAdd.add(liveContext.createStatement(aspectUri, property, elementUri));

			// add an Observation instance for the Element
			URI observation = new URIImpl(elementUri+"#"+System.currentTimeMillis());
			toAdd.add(new StatementImpl(liveContext.getContextURI(), elementUri, DCON.hasObservation, observation));
			toAdd.add(new StatementImpl(liveContext.getContextURI(), observation, RDF.type, DCON.Observation));
			
			// keep provenance and timestamp of recorded Observation data
			toAdd.add(liveContext.createStatement(observation, DCON.recordedAt, DateUtils.currentDateTimeAsLiteral()));
			toAdd.add(liveContext.createStatement(observation, DCON.recordedBy, dataSource));

			// add extra metadata about the element, splitting basic metadata (attach to the Element) from
			// context metadata (attach to an Observation)
			ClosableIterator<Statement> statements = element.getModel().iterator();
			while (statements.hasNext()) {
				Statement stmt = statements.next();
				URI predicate = stmt.getPredicate();
				
				if (contextAttributes.contains(predicate)) {
					toAdd.add(new StatementImpl(liveContext.getContextURI(), observation, predicate, stmt.getObject()));
				} else {
					toAdd.add(stmt);
				}
			}
			statements.close();
		}
	}
	
	@Override
	public void remove(Class<? extends Aspect> aspect, URI property) throws LiveContextException {
		URI aspectUri = findAspect(aspect);
		
		ClosableIterator<Statement> elementsIt = liveContext.findStatements(aspectUri, property, Variable.ANY);
		while (elementsIt.hasNext()) {
			Node element = elementsIt.next().getObject();
			if (element instanceof org.ontoware.rdf2go.model.node.Resource) {
				
				// check if the element is linked from another aspects, or with a different property
				boolean isShared = false;
				ClosableIterator<Statement> relationsIt = liveContext.findStatements(Variable.ANY, Variable.ANY, element);
				while (relationsIt.hasNext()) {
					Statement stmt = relationsIt.next();
					if (!stmt.getSubject().equals(aspectUri) || !stmt.getPredicate().equals(property)) {
						isShared = true;
						break;
					}
				}
				relationsIt.close();

				// if element is shared, only the relation with the specified aspect is removed, otherwise
				// all metadata of the element is removed, and also all observations
				if (isShared) {
					toRemove(liveContext.findStatements(aspectUri, property, element));
				} else {
					toRemove(liveContext.findStatements(Variable.ANY, Variable.ANY, element));
					
					// TODO remove all observations for this element
					
					toRemove(liveContext.findStatements(element.asResource(), Variable.ANY, Variable.ANY));
				}
			} else {
				toRemove(liveContext.findStatements(Variable.ANY, Variable.ANY, element));
			}
		}
		elementsIt.close();
		
		commitIfAuto();
	}

	@Override
	public void remove(Class<? extends Aspect> aspect, URI property, URI... elementUris)
			throws LiveContextException {
		// TODO get the functionality from the above remove(), and also use it when passing elements as URIs
		URI aspectUri = findAspect(aspect);
		for (URI elementUri : elementUris) {
			toRemove(liveContext.findStatements(aspectUri, property, elementUri));
		}
		commitIfAuto();
	}
	
	@Override
	public void remove(URI resource, URI property) throws LiveContextException {
		toRemove(liveContext.findStatements(resource, property, Variable.ANY));
		commitIfAuto();
	}

	@Override
	public void remove(URI resource, URI property, Object... values) throws LiveContextException {
		for (Object value : values) {
			toRemove(liveContext.findStatements(resource, property, literal(value)));
		}
		commitIfAuto();
	}

	private void toRemove(ClosableIterator<? extends Statement> statements) {
		while (statements.hasNext()) {
			toRemove.add(statements.next());
		}
		statements.close();
	}
	
	private void commitIfAuto() throws LiveContextException {
		if (autoCommit) commit();
	}
	
	private Literal literal(Object value) throws LiveContextException {
		if (value instanceof String)        return new DatatypeLiteralImpl((String) value, XSD._string);
		else if (value instanceof Date)     return new DatatypeLiteralImpl(DateUtil.dateTimeToString((Date) value), XSD._dateTime);
		else if (value instanceof Calendar) return new DatatypeLiteralImpl(DateUtil.dateTimeToString((Calendar) value), XSD._dateTime);
		else if (value instanceof Boolean)  return new DatatypeLiteralImpl(((Boolean) value).toString(), XSD._boolean);
		else if (value instanceof Integer)  return new DatatypeLiteralImpl(((Integer) value).toString(), XSD._integer);
		else if (value instanceof Long)     return new DatatypeLiteralImpl(((Long) value).toString(), XSD._long);
		else if (value instanceof Float)    return new DatatypeLiteralImpl(((Long) value).toString(), XSD._float);
		else if (value instanceof Double)   return new DatatypeLiteralImpl(((Long) value).toString(), XSD._double);
		else                                throw new LiveContextException(value + " of type " + value.getClass().getSimpleName() + " is not accepted.");
	}

	private URI findAspect(Class<? extends Aspect> aspectClass) throws LiveContextException {
		URI aspectUri = null;
		ClosableIterator<? extends Statement> iterator = null;
		try {
			URI aspectType = ResourceFactory.getUriOfClass(aspectClass);
			iterator = liveContext.findStatements(Variable.ANY, RDF.type, aspectType);
			if (iterator.hasNext()) {
				aspectUri = iterator.next().getSubject().asURI();
			} else {
				logger.debug("No instance of aspect "+aspectType+" was found in the live context, creating one...");
				aspectUri = new URIImpl("urn:uuid:"+UUID.randomUUID());
				liveContext.addStatement(aspectUri, RDF.type, aspectType);
			}
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return aspectUri;
	}
	
}
