package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DRMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.FindableModelSet;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.drmo.Rule;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * Implements {@link RuleManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class RuleManagerImpl extends InfoSphereManagerBase<Rule> implements RuleManager {

	private static final Logger logger = LoggerFactory.getLogger(RuleManagerImpl.class);
	
	private static final URI[] PROPERTIES_TO_FOLLOW = new URI[] {
		DRMO.triggers, DRMO.isComposedOf, DRMO.hasConstraint, DRMO.hasConstraintOnGraph,
		DRMO.hasConstraintOnSubject, DRMO.hasConstraintOnProperty, DRMO.hasConstraintOnObject,
		DRMO.hasPropertyOperator, DRMO.hasNegation, // TODO hasNegation should be included?
		DRMO.and, DRMO.or, DRMO.succeededBy, DRMO.precededBy 
	};
	
	@Override
	public Rule get(String ruleId) throws InfosphereException {
		return get(ruleId, new ArrayList<URI>(0));
	}

	@Override
	public Rule get(String ruleId, List<URI> properties) throws InfosphereException {
		TripleStore tripleStore = getTripleStore();
		ResourceStore resourceStore = getResourceStore();
		
		URI ruleUri = new URIImpl(ruleId);
		if (resourceStore.exists(ruleUri)) { 
			Model ruleModel = RDF2Go.getModelFactory().createModel().open();
			fetch(tripleStore, ruleModel, ruleUri);
			return new Rule(ruleModel, ruleUri, false);
		} else {
			throw new InfosphereException("Cannot find Rule "+ruleId);
		}
	}
	
	// fetch rule metadata and recursively all conditions, actions, etc.
	private void fetch(FindableModelSet modelSet, Model sinkModel, URI rule) {
		Set<URI> toFetch = new HashSet<URI>();
		toFetch.add(rule);
		fetch(modelSet, sinkModel, new HashSet<URI>(), toFetch);
	}
	
	private void fetch(FindableModelSet modelSet, Model sinkModel, Set<URI> known, Set<URI> toFetch) {
		known.addAll(toFetch);
		Set<URI> newResources = new HashSet<URI>();
		for (URI resource : toFetch) {
			ModelUtils.fetch(modelSet, sinkModel, resource, false, false, new URI[0], PROPERTIES_TO_FOLLOW, false);
			ClosableIterator<Statement> statements = sinkModel.findStatements(Variable.ANY, Variable.ANY, Variable.ANY);
			while (statements.hasNext()) {
				URI subject = statements.next().getSubject().asURI();
				if (!known.contains(subject)) {
					newResources.add(subject);
				}
			}
			statements.close();
		}
		if (!newResources.isEmpty()) {
			fetch(modelSet, sinkModel, known, newResources);
		}
	}

	@Override
	public Collection<Rule> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}

	@Override
	public Collection<Rule> getAll(List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		Collection<Resource> ruleUris = resourceStore.find(Rule.class).distinct().ids();
		Collection<Rule> results = new ArrayList<Rule>(ruleUris.size());
		for (Resource ruleUri : ruleUris) {
			try {
				results.add(get(ruleUri.toString(), properties));
			} catch (InfosphereException e) {
				logger.error("Skipping rule " + ruleUri + ": it won't be included in the collection.", e);
			}
		}
		return results;
	}
	
	@Override
	public void update(Rule entity) throws InfosphereException {
		// TODO implement!
	}

	@Override
	public void update(Rule entity, boolean override) throws InfosphereException {
		// TODO implement!
	}

	@Override
	public void remove(String ruleId) throws InfosphereException {
		TripleStore tripleStore = getTripleStore();
		Rule rule = get(ruleId);
		
		// remove the rule and all metadata for its conditions, actions, etc. 
		ClosableIterator<Statement> toDelete = rule.getModel().iterator();
		while (toDelete.hasNext()) {
			Statement stmt = toDelete.next();
			tripleStore.removeStatements(stmt.getContext(), stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
		}
		toDelete.close();
	}

}
