package eu.dime.ps.semantic.service.context;

import ie.deri.smile.rdf.util.ModelUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.model.dcon.Aspect;

/**
 * 
 * 
 * bla bla ...
 * 
 * @author Ismael Rivera
 */
public class ElementBasedStrategy implements UpdateStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ElementBasedStrategy.class);
	
	private final String allElementsQuery;
	private final Set<URI> allProperties = new HashSet<URI>();

    private Model previousContext;
    private Model liveContext;
    
	public ElementBasedStrategy(Model previousContext, Model liveContext) {
		this.previousContext = previousContext;
		this.liveContext = liveContext;
		
		// building the SELECT query for fetching all elements from a graph
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		try {
			ModelUtils.loadFromInputStream(
					this.getClass().getClassLoader().getResourceAsStream("vocabularies/dcon/dcon.trig"),
					Syntax.Trig, sinkModel);
		} catch (ModelRuntimeException e) {
			logger.error("Couldn't access DCON ontology, the updates on the context will not operate propertly", e);
		} catch (IOException e) {
			logger.error("Couldn't access DCON ontology, the updates on the context will not operate propertly", e);
		}

		String query = "SELECT ?property WHERE { ?property <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://www.semanticdesktop.org/ontologies/2011/10/05/dcon#hasContextElement> . }";
		ClosableIterator<QueryRow> results = sinkModel.sparqlSelect(query).iterator();
		StringBuilder allElementsQuery = new StringBuilder("SELECT DISTINCT ?element WHERE {");
		while (results.hasNext()) {
			QueryRow result = results.next();
			allProperties.add(result.getValue("property").asURI());
			allElementsQuery.append("{ ?aspect ").append(result.getValue("property").toSPARQL()).append(" ?element . }");
			if (results.hasNext())
				allElementsQuery.append(" UNION ");
		}
		allElementsQuery.append("}");
		results.close();
		sinkModel.close();
		this.allElementsQuery = allElementsQuery.toString();
	}
	
	@Override
	public void update(List<Statement> toAdd, List<Statement> toRemove) {
		
		// loads statements to be added to a temporary model, so it can be queried
		Model newData = RDF2Go.getModelFactory().createModel().open();
		for (Statement s : toAdd) {
			newData.addStatement(s);
		}
		
		// extracts all URIs of Element resources from the statements to add/remove
		Set<URI> elements = new HashSet<URI>();
		elements.addAll(findElements(toAdd));
		elements.addAll(findElements(toRemove));
		
		logger.debug("Updating live context... elements to be updated: "+StringUtils.join(elements, ", "));

		for (URI element : elements) {

			// if an element is being modified/deleted, the current data from the live context
			// for that element is copied over to the previous context (removing the existing if any!)
			if (liveContext.contains(Variable.ANY, Variable.ANY, element)) {
				previousContext.removeStatements(Variable.ANY, Variable.ANY, element);
				previousContext.removeStatements(element, Variable.ANY, Variable.ANY);
				previousContext.addAll(liveContext.findStatements(Variable.ANY, Variable.ANY, element));
				previousContext.addAll(liveContext.findStatements(element, Variable.ANY, Variable.ANY));
			}
			
			// however, if a new element is inserted in the live context, all other elements for that aspect
			// are copied over to the previous context if they are not there yet
			else {
				// get all elements attached to a specific aspect in the live context
				URI aspect = newData.findStatements(Variable.ANY, Variable.ANY, element).next().getSubject().asURI();
				String query = "SELECT DISTINCT ?element WHERE { "+aspect.toSPARQL()+" ?p ?element . }";
				Set<URI> existing = new HashSet<URI>();
				ClosableIterator<QueryRow> results = liveContext.sparqlSelect(query).iterator();
				while (results.hasNext()) {
					existing.add(results.next().getValue("element").asURI());
				}
				results.close();
				
				for (URI e : existing) {
					// if it doesn't exist in the previous context, its metadata is copied over;
					// nothing is done otherwise
					if (!previousContext.contains(aspect, Variable.ANY, e)) {
						previousContext.addAll(liveContext.findStatements(aspect, Variable.ANY, e));
						previousContext.addAll(liveContext.findStatements(e, Variable.ANY, Variable.ANY));
					}
				}
			}
		}
		
		// not needed anymore, closing...
		newData.close();
		
		// makes changes to live context (add/remove statements)
		liveContext.removeAll(toRemove.iterator());
		liveContext.addAll(toAdd.iterator());
	}
	
	// retrieve all elements being updated in the statement list:
	// - either because they are related to aspects in the live context
	// - or because an statement contains such a relation: <aspect> ? <element>
	private Set<URI> findElements(List<Statement> statements) {
		Set<URI> allElements = new HashSet<URI>();

		ClosableIterator<QueryRow> results = liveContext.sparqlSelect(allElementsQuery).iterator();
		while (results.hasNext()) {
			allElements.add(results.next().getValue("element").asURI());
		}
		results.close();
		
		// finds subjects of the triples that are elements in the live context; or attached to
		// an aspect in the given statement
		Set<URI> elements = new HashSet<URI>();
		for (Statement statement : statements) {
			URI subject = statement.getSubject().asURI();
			URI predicate = statement.getPredicate();
			Node object = statement.getObject();
			
			if (allElements.contains(subject)) {
				elements.add(subject);
			} else if (allProperties.contains(predicate) && object instanceof URI) {
				elements.add(object.asURI());
			}
		}
		
		return elements;
	}

}
