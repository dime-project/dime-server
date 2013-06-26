package eu.dime.ps.semantic.service.context;

import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.impl.ResourceModelImpl;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DPO;
import ie.deri.smile.vocabulary.GEO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NRL;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.DCONFactory;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.NRLFactory;
import eu.dime.ps.semantic.model.ResourceFactory;
import eu.dime.ps.semantic.model.dcon.Aspect;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.impl.ResourceStoreImpl;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.util.DateUtils;
import eu.dime.ps.semantic.util.StringUtils;

/**
 * Provides all the required logic common for all LiveContextService implementations
 * independently of the update strategy adopted.
 * 
 * It sets constrains on what classes/types are allowed to be updated, and provides
 * methods to retrieve the current state of the previous and live context graphs.
 * 
 * DOES NOT provide an implementation for the sessions, it should be provided by classes
 * extending this class.
 * 
 * @author Ismael Rivera
 */
public abstract class LiveContextServiceBase implements LiveContextService {

    private static final Logger logger = LoggerFactory.getLogger(LiveContextServiceBase.class);
	
    protected static final URI[] ALLOWED_ASPECTS = new URI[] {
		DCON.Schedule,
		DCON.Peers,
		DCON.Environment,
		DCON.Attention,
		DCON.SpaTem,
		DCON.Connectivity,
		DCON.State
	};

	protected static final URI[] ALLOWED_TYPES = new URI[] {
		DCON.Schedule,
		DCON.Peers,
		DCON.Environment,
		DCON.Attention,
		DCON.SpaTem,
		DCON.Connectivity,
		DCON.State,
		DPO.Activity,
		DPO.Altitude,
		DPO.AttendingEvent,
		DPO.Availability,
		DPO.Brightness,
		DPO.Direction,
		DPO.EnvironmentalIndicator,
		DPO.Mood,
		DPO.Movement,
		DPO.Noise,
		DPO.NonQuantifiableComponent,
		DPO.OtherActivity,
		DPO.Place,
		DPO.PresenceComponent,
		DPO.QuantifiableComponent,
		DPO.Temperature,
		DPO.TimePeriod,
		DPO.Travelling,
		DPO.WeatherConditions,
		GEO.Point
	};

	protected final TripleStore tripleStore;
	protected final ResourceStore resourceStore;
	protected final PimoService pimoService;
	
	protected final NRLFactory nrlFactory;
	protected final DCONFactory dconFactory;

	protected final URI liveContextGraph;
	protected final URI previousContextGraph;

	public LiveContextServiceBase(PimoService pimoService) {
		this.pimoService = pimoService;
		this.tripleStore = pimoService.getTripleStore();
		this.resourceStore = new ResourceStoreImpl(this.tripleStore);
		this.liveContextGraph = new URIImpl(pimoService.getUserNamespace() + "graph:live-context");
		this.previousContextGraph = new URIImpl(pimoService.getUserNamespace() + "graph:previous-context");

		this.nrlFactory = (new ModelFactory()).getNRLFactory();
		this.dconFactory = (new ModelFactory()).getDCONFactory();
		
		initContextGraph(liveContextGraph);
		initContextGraph(previousContextGraph);
	}
	
	private void initContextGraph(URI graphUri) {
		if (!tripleStore.isTypedAs(graphUri, DCON.LiveContext)) {
			logger.info("Creating Live Context graph and its metadata graph");
			
			URI metadataGraphUri = new URIImpl(graphUri.toString()+"/metadata");
			tripleStore.addStatement(metadataGraphUri, metadataGraphUri, RDF.type, NRL.GraphMetadata);
			tripleStore.addStatement(metadataGraphUri, metadataGraphUri, NRL.coreGraphMetadataFor, graphUri);
			
			tripleStore.addStatement(metadataGraphUri, graphUri, RDF.type, DCON.LiveContext);
			DatatypeLiteral now = DateUtils.currentDateTimeAsLiteral();
			tripleStore.addStatement(metadataGraphUri, graphUri, NAO.created, now);
			tripleStore.addStatement(metadataGraphUri, graphUri, NAO.lastModified, now);
		}
	}
	
	@Override
	public Model getLiveContext() {
		return tripleStore.getModel(this.liveContextGraph);
	}
	
	@Override
	public Model getPreviousContext() {
		return tripleStore.getModel(this.previousContextGraph);
	}
	
	@Override
	public <T extends Aspect> T get(Class<T> returnType) {
		// looks for the first instance of a resource of that type and returns;
		// if it does not exist, creates one and returns
		T instance = null;
		try {
			instance = getFirstOccurrence(returnType);
		} catch (NotFoundException e) {
			URI typeUri = ResourceFactory.getUriOfClass(returnType);
			logger.info("No instance of type "+typeUri+" was found. A new one has been created.");
			instance = dconFactory.createResource(returnType);
			tripleStore.addAll(this.liveContextGraph, instance.getModel().iterator());
		}
		return instance;
	}
	
	@Override
	public List<URI> getAspects() {
		List<URI> results = new ArrayList<URI>();
		String query = StringUtils.strjoinNL(
				"PREFIX rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
				"PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>",
				"PREFIX dcon:	<http://www.semanticdesktop.org/ontologies/2011/10/05/dcon#>",
				"SELECT DISTINCT ?aspect WHERE {",
				"  GRAPH "+liveContextGraph.toSPARQL()+" { ?aspect rdf:type ?class . }",
				"  ?class rdfs:subClassOf dcon:Aspect . ",
				"}");
		ClosableIterator<QueryRow> rows = tripleStore.sparqlSelect(query).iterator();
		while (rows.hasNext()) {
			results.add(rows.next().getValue("aspect").asURI());
		}
		rows.close();
		return results;
	}
	
	@Override
	public <T extends Resource> T get(URI elementUri, Class<T> returnType)
			throws NotFoundException {
		T element = resourceStore.get(this.liveContextGraph, elementUri, returnType);
		
		// remove <?, dcon:hasObservation, ?> triples, they are for internal use only 
		element.getModel().removeStatements(element, DCON.hasObservation, Variable.ANY);
		
		return element;
	}

	@Override
	public <T extends Resource> T get(URI elementUri, Class<T> returnType, URI datasource)
			throws NotFoundException {
		T element = resourceStore.get(this.liveContextGraph, elementUri, returnType);
		
		// merge data attached to the Observation, corresponding to the specified data source
		ClosableIterator<Statement> observationIt = element.getModel().findStatements(element, DCON.hasObservation, Variable.ANY);
		while (observationIt.hasNext()) {
			Statement stmt = observationIt.next();
			URI observationUri = stmt.getObject().asURI();
			Model metadata = resourceStore.get(this.liveContextGraph, observationUri, Resource.class).getModel();
			ResourceModel observation = new ResourceModelImpl(metadata, observationUri);
			
			URI recordedBy = observation.getURI(DCON.recordedBy);
			if (recordedBy.equals(datasource)) {
				// add Observation metadata to the Element
				ClosableIterator<Statement> metadataIt = metadata.iterator();
				while (metadataIt.hasNext()) {
					Statement st = metadataIt.next();
					URI subject = st.getSubject().asURI();
					URI predicate = st.getPredicate();
					Node object = st.getObject();
					
					// remove metadata not meant to be in the Element
					if (!object.equals(observationUri) &&
							!predicate.equals(RDF.type) &&
							!predicate.equals(DCON.recordedAt) &&
							!predicate.equals(DCON.recordedBy)) {
						element.getModel().addStatement(elementUri, predicate, object);
					}
				}
				
				// only one observation for each data source
				break;
			}
		}
		observationIt.close();
		
		// remove <?, dcon:hasObservation, ?> triples, they are for internal use only 
		element.getModel().removeStatements(element, DCON.hasObservation, Variable.ANY);
		
		return element;
	}

	@Override
	public List<URI> getElements() {
		List<URI> results = new ArrayList<URI>();
		String query = StringUtils.strjoinNL(
				"PREFIX rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
				"PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>",
				"PREFIX dcon:	<http://www.semanticdesktop.org/ontologies/2011/10/05/dcon#>",
				"SELECT DISTINCT ?element WHERE {",
				"  GRAPH "+liveContextGraph.toSPARQL()+" { ?aspect rdf:type ?class ; ?property ?element .}",
				"  ?class rdfs:subClassOf dcon:Aspect . ",
				"  ?property rdfs:subPropertyOf dcon:hasContextElement .",
				"}");
		ClosableIterator<QueryRow> rows = tripleStore.sparqlSelect(query).iterator();
		while (rows.hasNext()) {
			results.add(rows.next().getValue("element").asURI());
		}
		rows.close();
		return results;
	}
	
	/**
	 * Retrieves the first occurrence for a given type of resource.
	 * @param returnType the type of the occurrence to retrieve
	 * @return the resource instance representing the occurrence
	 * @throws NotFoundException
	 */
	protected <T extends Resource> T getFirstOccurrence(Class<T> returnType)
			throws NotFoundException {
		T result = null;
		ClosableIterator<? extends Statement> iterator = null;
		try {
			URI typeUri = ResourceFactory.getUriOfClass(returnType);
			iterator = tripleStore.findStatements(this.liveContextGraph, Variable.ANY, RDF.type, typeUri);
			if (iterator.hasNext()) {
				result = resourceStore.get(iterator.next().getSubject(), returnType);
			} else {
				iterator.close();
				throw new NotFoundException("No instance of type "+typeUri+" was found in graph "+this.liveContextGraph);
			}
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return result;
	}
 
	/**
	 * Returns true if the 'instanceUri' is an instance of the class 'aspectType'
	 * defined in the live context graph
	 */
	protected boolean isValidAspect(URI instanceUri, URI aspectType) {
		return tripleStore.containsStatements(liveContextGraph, instanceUri, RDF.type, aspectType);
	}
	
	@Override
	public String toString() {
		return tripleStore.getModel(previousContextGraph).serialize(Syntax.Trig)
				+ tripleStore.getModel(liveContextGraph).serialize(Syntax.Trig);
	}

}