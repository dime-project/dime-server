package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.nrl.*;

/**
 * A factory for the Java classes generated automatically for the NRL vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NRLFactory extends ResourceFactory {

	public AsymmetricProperty createAsymmetricProperty() {
		return new AsymmetricProperty(createModel(), generateUniqueURI(), true);
	}

	public AsymmetricProperty createAsymmetricProperty(URI resourceUri) {
		return new AsymmetricProperty(createModel(), resourceUri, true);
	}

	public AsymmetricProperty createAsymmetricProperty(String resourceUriString) {
		return new AsymmetricProperty(createModel(), new URIImpl(resourceUriString), true);
	}

	public Configuration createConfiguration() {
		return new Configuration(createModel(), generateUniqueURI(), true);
	}

	public Configuration createConfiguration(URI resourceUri) {
		return new Configuration(createModel(), resourceUri, true);
	}

	public Configuration createConfiguration(String resourceUriString) {
		return new Configuration(createModel(), new URIImpl(resourceUriString), true);
	}

	public Data createData() {
		return new Data(createModel(), generateUniqueURI(), true);
	}

	public Data createData(URI resourceUri) {
		return new Data(createModel(), resourceUri, true);
	}

	public Data createData(String resourceUriString) {
		return new Data(createModel(), new URIImpl(resourceUriString), true);
	}

	public DiscardableInstanceBase createDiscardableInstanceBase() {
		return new DiscardableInstanceBase(createModel(), generateUniqueURI(), true);
	}

	public DiscardableInstanceBase createDiscardableInstanceBase(URI resourceUri) {
		return new DiscardableInstanceBase(createModel(), resourceUri, true);
	}

	public DiscardableInstanceBase createDiscardableInstanceBase(String resourceUriString) {
		return new DiscardableInstanceBase(createModel(), new URIImpl(resourceUriString), true);
	}

	public DocumentGraph createDocumentGraph() {
		return new DocumentGraph(createModel(), generateUniqueURI(), true);
	}

	public DocumentGraph createDocumentGraph(URI resourceUri) {
		return new DocumentGraph(createModel(), resourceUri, true);
	}

	public DocumentGraph createDocumentGraph(String resourceUriString) {
		return new DocumentGraph(createModel(), new URIImpl(resourceUriString), true);
	}

	public ExternalViewSpecification createExternalViewSpecification() {
		return new ExternalViewSpecification(createModel(), generateUniqueURI(), true);
	}

	public ExternalViewSpecification createExternalViewSpecification(URI resourceUri) {
		return new ExternalViewSpecification(createModel(), resourceUri, true);
	}

	public ExternalViewSpecification createExternalViewSpecification(String resourceUriString) {
		return new ExternalViewSpecification(createModel(), new URIImpl(resourceUriString), true);
	}

	public FunctionalProperty createFunctionalProperty() {
		return new FunctionalProperty(createModel(), generateUniqueURI(), true);
	}

	public FunctionalProperty createFunctionalProperty(URI resourceUri) {
		return new FunctionalProperty(createModel(), resourceUri, true);
	}

	public FunctionalProperty createFunctionalProperty(String resourceUriString) {
		return new FunctionalProperty(createModel(), new URIImpl(resourceUriString), true);
	}

	public Graph createGraph() {
		return new Graph(createModel(), generateUniqueURI(), true);
	}

	public Graph createGraph(URI resourceUri) {
		return new Graph(createModel(), resourceUri, true);
	}

	public Graph createGraph(String resourceUriString) {
		return new Graph(createModel(), new URIImpl(resourceUriString), true);
	}

	public GraphMetadata createGraphMetadata() {
		return new GraphMetadata(createModel(), generateUniqueURI(), true);
	}

	public GraphMetadata createGraphMetadata(URI resourceUri) {
		return new GraphMetadata(createModel(), resourceUri, true);
	}

	public GraphMetadata createGraphMetadata(String resourceUriString) {
		return new GraphMetadata(createModel(), new URIImpl(resourceUriString), true);
	}

	public GraphView createGraphView() {
		return new GraphView(createModel(), generateUniqueURI(), true);
	}

	public GraphView createGraphView(URI resourceUri) {
		return new GraphView(createModel(), resourceUri, true);
	}

	public GraphView createGraphView(String resourceUriString) {
		return new GraphView(createModel(), new URIImpl(resourceUriString), true);
	}

	public InstanceBase createInstanceBase() {
		return new InstanceBase(createModel(), generateUniqueURI(), true);
	}

	public InstanceBase createInstanceBase(URI resourceUri) {
		return new InstanceBase(createModel(), resourceUri, true);
	}

	public InstanceBase createInstanceBase(String resourceUriString) {
		return new InstanceBase(createModel(), new URIImpl(resourceUriString), true);
	}

	public InverseFunctionalProperty createInverseFunctionalProperty() {
		return new InverseFunctionalProperty(createModel(), generateUniqueURI(), true);
	}

	public InverseFunctionalProperty createInverseFunctionalProperty(URI resourceUri) {
		return new InverseFunctionalProperty(createModel(), resourceUri, true);
	}

	public InverseFunctionalProperty createInverseFunctionalProperty(String resourceUriString) {
		return new InverseFunctionalProperty(createModel(), new URIImpl(resourceUriString), true);
	}

	public KnowledgeBase createKnowledgeBase() {
		return new KnowledgeBase(createModel(), generateUniqueURI(), true);
	}

	public KnowledgeBase createKnowledgeBase(URI resourceUri) {
		return new KnowledgeBase(createModel(), resourceUri, true);
	}

	public KnowledgeBase createKnowledgeBase(String resourceUriString) {
		return new KnowledgeBase(createModel(), new URIImpl(resourceUriString), true);
	}

	public Ontology createOntology() {
		return new Ontology(createModel(), generateUniqueURI(), true);
	}

	public Ontology createOntology(URI resourceUri) {
		return new Ontology(createModel(), resourceUri, true);
	}

	public Ontology createOntology(String resourceUriString) {
		return new Ontology(createModel(), new URIImpl(resourceUriString), true);
	}

	public ReflexiveProperty createReflexiveProperty() {
		return new ReflexiveProperty(createModel(), generateUniqueURI(), true);
	}

	public ReflexiveProperty createReflexiveProperty(URI resourceUri) {
		return new ReflexiveProperty(createModel(), resourceUri, true);
	}

	public ReflexiveProperty createReflexiveProperty(String resourceUriString) {
		return new ReflexiveProperty(createModel(), new URIImpl(resourceUriString), true);
	}

	public RuleViewSpecification createRuleViewSpecification() {
		return new RuleViewSpecification(createModel(), generateUniqueURI(), true);
	}

	public RuleViewSpecification createRuleViewSpecification(URI resourceUri) {
		return new RuleViewSpecification(createModel(), resourceUri, true);
	}

	public RuleViewSpecification createRuleViewSpecification(String resourceUriString) {
		return new RuleViewSpecification(createModel(), new URIImpl(resourceUriString), true);
	}

	public Schema createSchema() {
		return new Schema(createModel(), generateUniqueURI(), true);
	}

	public Schema createSchema(URI resourceUri) {
		return new Schema(createModel(), resourceUri, true);
	}

	public Schema createSchema(String resourceUriString) {
		return new Schema(createModel(), new URIImpl(resourceUriString), true);
	}

	public Semantics createSemantics() {
		return new Semantics(createModel(), generateUniqueURI(), true);
	}

	public Semantics createSemantics(URI resourceUri) {
		return new Semantics(createModel(), resourceUri, true);
	}

	public Semantics createSemantics(String resourceUriString) {
		return new Semantics(createModel(), new URIImpl(resourceUriString), true);
	}

	public SymmetricProperty createSymmetricProperty() {
		return new SymmetricProperty(createModel(), generateUniqueURI(), true);
	}

	public SymmetricProperty createSymmetricProperty(URI resourceUri) {
		return new SymmetricProperty(createModel(), resourceUri, true);
	}

	public SymmetricProperty createSymmetricProperty(String resourceUriString) {
		return new SymmetricProperty(createModel(), new URIImpl(resourceUriString), true);
	}

	public TransitiveProperty createTransitiveProperty() {
		return new TransitiveProperty(createModel(), generateUniqueURI(), true);
	}

	public TransitiveProperty createTransitiveProperty(URI resourceUri) {
		return new TransitiveProperty(createModel(), resourceUri, true);
	}

	public TransitiveProperty createTransitiveProperty(String resourceUriString) {
		return new TransitiveProperty(createModel(), new URIImpl(resourceUriString), true);
	}

	public ViewSpecification createViewSpecification() {
		return new ViewSpecification(createModel(), generateUniqueURI(), true);
	}

	public ViewSpecification createViewSpecification(URI resourceUri) {
		return new ViewSpecification(createModel(), resourceUri, true);
	}

	public ViewSpecification createViewSpecification(String resourceUriString) {
		return new ViewSpecification(createModel(), new URIImpl(resourceUriString), true);
	}

}