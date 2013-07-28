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

package eu.dime.ps.datamining.gate;

import eu.dime.ps.datamining.exceptions.DataMiningException;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.rdf.impl.ResourceModelImpl;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts named entities from a full postal address (e.g. '19 Lancaster Road, Nothingham, Scotland').
 * 
 * @author Keith Cortis
 * @author Ismael Rivera
 */
public class PostalAddressExtractor extends Base {

	private static final Logger logger = LoggerFactory.getLogger(PostalAddressExtractor.class);

	private static final String GAPP = "MatchingPipeline.gapp";
	
	private final CorpusController application;
	
	public PostalAddressExtractor() {
		super();

		try {
			// load the GATE pipeline for postal addresses
			application = loadApplication(GAPP);
		} catch (PersistenceException e) {
			throw new ExceptionInInitializerError(e);
		} catch (ResourceInstantiationException e) {
			throw new ExceptionInInitializerError(e);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public ResourceModel extract(ResourceModel postalAddress) throws DataMiningException {
		
		// copy content from original resource
		ResourceModel result = new ResourceModelImpl(postalAddress.getModel(), postalAddress.getIdentifier());
		
		String content = postalAddress.getString(NAO.prefLabel);
		if ((content == null) || (content.trim().isEmpty())) {
			logger.info("PostalAddress "+postalAddress.getIdentifier()+" doesn't specify the full postal address as the NAO.prefLabel; " +
					"skipping postal address extraction...");
			return result;
		}

		Document doc = null;
		try {
			// build a document from the textual address
			doc = Factory.newDocument(content);
			
			synchronized(application) {
				// put the document in the corpus
				Corpus corpus = Factory.newCorpus("PostalAddress"+System.currentTimeMillis());
				corpus.add(doc);
			
				// tell application what corpus to use
				application.setCorpus(corpus);

				// run the application
				application.execute();

				AnnotationSet defaultAnnotSet = doc.getAnnotations();		   
				Set<String> annotTypesRequired = new HashSet<String>();						
				annotTypesRequired.add("LocationStreet");
				annotTypesRequired.add("LocationCity");		
				annotTypesRequired.add("LocationRegion");
				annotTypesRequired.add("LocationPostCode");
				annotTypesRequired.add("LocationPOBox");
				annotTypesRequired.add("LocationCountry");
				annotTypesRequired.add("LocationProvince"); 
					
				AnnotationSet entityAnnSet = defaultAnnotSet.get(new HashSet<String>(annotTypesRequired));
				List<Annotation> entityAnnots = gate.Utils.inDocumentOrder(entityAnnSet);
				
				// extract annotations from the defined AnnotationSet
				for (Annotation annot : entityAnnots) {				
					String entityValue = null;
					String entityType = annot.getType();
				  			   
					if ((entityType.equals("LocationCountry")) && ((!(result.has(NCO.country))) || ((result.get(NCO.country)).equals("")))) {
					   	entityValue = annot.getFeatures().get("country").toString();	
					   	result.set(NCO.country, entityValue);
					} else if ((entityType.equals("LocationProvince")) && ((!(result.has(NCO.region))) || ((result.get(NCO.region)).equals("")))) {
					   	entityValue = annot.getFeatures().get("province").toString(); 				   
					   	result.set(NCO.region, entityValue);
					} else if ((entityType.equals("LocationCity")) && ((!(result.has(NCO.locality))) || ((result.get(NCO.locality)).equals("")))) { 
					   	entityValue = annot.getFeatures().get("city").toString(); 				 
					   	result.set(NCO.locality, entityValue);
					} else if ((entityType.equals("LocationStreet")) && ((!(result.has(NCO.streetAddress))) || ((result.get(NCO.streetAddress)).equals("")))) {
					   	entityValue = annot.getFeatures().get("street").toString();  				 
					   	result.set(NCO.streetAddress, entityValue);
					} else if ((entityType.equals("LocationPostCode")) && ((!(result.has(NCO.postalcode))) || ((result.get(NCO.postalcode)).equals("")))) {
					   	entityValue = annot.getFeatures().get("postcode").toString();				   
					   	result.set(NCO.postalcode, entityValue);
					} else if ((entityType.equals("LocationPOBox")) && ((!(result.has(NCO.pobox))) || ((result.get(NCO.pobox)).equals("")))) {
					   	entityValue = annot.getFeatures().get("pobox").toString();				   
					   	result.set(NCO.pobox, entityValue);
					} else if ((entityType.equals("LocationRegion")) && ((!(result.has(NCO.region))) || ((result.get(NCO.region)).equals("")))) {
					   	entityValue = annot.getFeatures().get("region").toString();   				   
					   	result.set(NCO.region, entityValue);
					} 
		
				}
		  
				// cleanup resources from corpus
				corpus.cleanup();
				corpus.clear();
			}
			
		} catch (ResourceInstantiationException e) {
			throw new DataMiningException("Failed to extract named entities from '"+content+"'", e);
		} catch (ExecutionException e) {
			throw new DataMiningException("Failed to extract named entities from '"+content+"'", e);
		} finally {
			if (doc != null) {
				// remove document so GATE doesn't hold any reference to it
				Factory.deleteResource(doc);
			}
		}
		
		return result;
	}
	
}
