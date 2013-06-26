package com.ontotext.kim.gate;

import gate.AnnotationSet;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * The Semantic Enrichment PR allows adding new data to semantic annotations 
 * by querying external RDF (Linked Data) repositories.
 *	
 * <p>
 * Here a semantic annotation is an annotation that is linked to an RDF entity by having 
 * the URI of the entity in the "inst" feature of the annotation. For all such annotation 
 * of a given type, this PR runs a SPARQL query against the defined repository and puts 
 * a comma-separated list of the values mentioned in the query output in the 
 * "connections" feature of the same annotation.
 * 
 * @author mnozchev
 */
@CreoleResource(
        name="Semantic Enrichment PR", 
        comment="The Semantic Enrichment PR allows adding new data to semantic annotations by querying external RDF (Linked Data) repositories.",
        helpURL="http://nmwiki.ontotext.com/lkb_gazetteer/semantic_encrichment_pr.html")
public class SesameEnrichment extends AbstractLanguageAnalyser {

  private static final Logger log = Logger.getLogger(SesameEnrichment.class);
	private static final long serialVersionUID = 13010L;
	
	// Initialized during init()
	private RepositoryConnection conn;
	private Repository rep;
	
	// Fields for creole parameters
	private String repositoryUrl;
	private String inputASName;
	private Set<String> annTypes = new HashSet<String>(Arrays.asList("Lookup"));
	private boolean deleteOnNoRelations = true;
  private String query = 
    "SELECT ?Person WHERE { " +
    "?Person <http://dbpedia.org/ontology/birthplace> ?BirthPlace . " +
    "?BirthPlace <http://www.geonames.org/ontology#parentFeature> <%s> . " +
    "?Person a <http://sw.opencyc.org/2008/06/10/concept/en/Entertainer> .} LIMIT 100";
  
	// Output buffer, reused between executions
	private final StringBuilder outputData = new StringBuilder(2000);
	
	@Override
	public Resource init() throws ResourceInstantiationException {
		try {
			rep = new HTTPRepository(repositoryUrl);
			conn = rep.getConnection();
		}
		catch (RepositoryException e) {
			throw new ResourceInstantiationException(e);
		}
		return this;
	}

	@Override
	public void execute() throws ExecutionException {		
		AnnotationSet input = document.getAnnotations(inputASName);
		Set<gate.Annotation> deathRow = new HashSet<gate.Annotation>();
		
		for (gate.Annotation ann : input.get(annTypes)) {
			Object instFeature = ann.getFeatures().get("inst");
			if (!(instFeature instanceof String))
				continue;
			String instQuery = String.format(query, instFeature);
			try {
				outputData.setLength(0);
				TupleQuery tq = conn.prepareTupleQuery(QueryLanguage.SPARQL, instQuery);
				TupleQueryResult tqr = tq.evaluate();	
				if (!tqr.hasNext() && deleteOnNoRelations) {
					deathRow.add(ann);
				}
				populateResults(outputData, tqr);
				if (outputData.length() > 0) {
					ann.getFeatures().put("connections", outputData.toString());
				}
			} catch (MalformedQueryException e) {
				log.warn(String.format("Created invalid query [%s] for entity [%s] (in brackets). Parser reported: %s", instQuery, instFeature, e.getMessage()));
			} catch (Exception e) {
				log.warn(String.format("Error executing query [%s] for entity [%s] (in brackets)", instQuery, instFeature), e);
			}			
		}
		
		if (deleteOnNoRelations) {
			input.removeAll(deathRow);
		}
		
	}

	private void populateResults(StringBuilder outputData, TupleQueryResult tqr)
			throws QueryEvaluationException {
		try {
			while (tqr.hasNext()) {
				BindingSet bs = tqr.next();
				for (Binding val : bs)
					outputData.append(val.getValue().stringValue()).append(",");						
			}
		}
		finally {
			tqr.close();
		}
	}
	
	@Override
	public void reInit() throws ResourceInstantiationException {
		cleanup();
		init();
	}
	
	@Override
	public synchronized void cleanup() {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
			if (rep != null) {
				rep.shutDown();
				rep = null;
			}
		}
		catch (RepositoryException e) {
			log.error("Could not close connection.", e);
		}
	}
	
	public String getRepositoryUrl() {
    return repositoryUrl;
  }

	@CreoleParameter(comment="The URL of a Sesame 2 HTTP repository. Supports generic SPARQL endpoints as well.", defaultValue="http://factforge.net/sparql")
  public void setRepositoryUrl(String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }

  public String getInputASName() {
		return inputASName;
	}
  
  @Optional
  @RunTime
  @CreoleParameter(comment="Input annotation set to be scanned for semantic annotations")
	public void setInputASName(String inputASName) {
		this.inputASName = inputASName;
	}
  
  @RunTime
  @CreoleParameter(comment="Types of annotations that will be enriched.")  
	public void setAnnotationTypes(List<String> annTypes) {
    if (annTypes != null)
      this.annTypes = new HashSet<String>(annTypes);
	}

	public List<String> getAnnotationTypes() {
		return new ArrayList<String>(annTypes);
	}

	@Optional
	@RunTime
	@CreoleParameter(comment="The sparql query pattern. The query will be processed like this - String.format(query, uriFromAnnotation), so you can use parameters like %s or %1$s")
	public void setQuery(String query) {
		this.query = query.replace("\\", "");
	}

	public String getQuery() {
		return query;
	}

  @Optional
  @RunTime
  @CreoleParameter(comment="Whether we want to delete the annotation that weren't encriched.", defaultValue="false")  
	public void setDeleteOnNoRelations(Boolean deleteOnNoRelations) {
		if (deleteOnNoRelations != null)
			this.deleteOnNoRelations = deleteOnNoRelations;
	}

	public Boolean getDeleteOnNoRelations() {
		return deleteOnNoRelations;
	}	
	
	public String getVersion() {
		return this.getClass().getPackage().getImplementationVersion();		
	}
	
	@CreoleParameter(comment="The version of the Gazetteer_LKB build. Read-only",defaultValue="to be loaded from jar manifest")
	@Optional
	public void setVersion(String v) {
		
	}
}
