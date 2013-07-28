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
import ie.deri.smile.vocabulary.NCO;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts named entities from a person's fullname (e.g. 'Prof. Dr. Stefan Decker').
 * 
 * @author Keith Cortis
 * @author Ismael Rivera
 */
public class FullnameExtractor extends Base {

	private static final Logger logger = LoggerFactory.getLogger(FullnameExtractor.class);

	private static final String GAPP = "MatchingPipeline.gapp";
	
	private final CorpusController application;
	
	public FullnameExtractor() {
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
	
	public ResourceModel extract(ResourceModel personName) throws DataMiningException {

		// copy content from original resource
		ResourceModel result = new ResourceModelImpl(personName.getModel(), personName.getIdentifier());
		
		String content = personName.getString(NCO.fullname);
		if ((content == null) || (content.trim().isEmpty())) {
			logger.info("PersonName "+personName.getIdentifier()+" doesn't specify the contact's fullname as the NCO.fullname; " +
					"skipping fullname extraction...");
			return result;
		}

		Document doc = null;
		try {
			// build a document from the textual fullname
			doc = Factory.newDocument(content);

			synchronized(application) {
				// put the document in the corpus
				Corpus corpus = Factory.newCorpus("Fullname"+System.currentTimeMillis());
				corpus.add(doc);
			
				// tell application what corpus to use
				application.setCorpus(corpus);

				// run the application
				application.execute();
	
				AnnotationSet defaultAnnotSet = doc.getAnnotations();		   
				Set<String> annotTypesRequired = new HashSet<String>();	
				annotTypesRequired.add("PersonEntities"); 
								
				AnnotationSet entityAnnSet = defaultAnnotSet.get(new HashSet<String>(annotTypesRequired));
				List<Annotation> entityAnnots = gate.Utils.inDocumentOrder(entityAnnSet);
				
				// extract annotations from the defined AnnotationSet
				for (Annotation annot : entityAnnots) {				
					String entityType = annot.getType();
					String firstname = null;
					String surname = null;
					String prefix = null;
					String suffix = null; 
					String gender = null;
					
					if (entityType.equals("PersonEntities")) {   
						if ((!(annot.getFeatures().get("prefix").toString().isEmpty())) && ((!(result.has(NCO.nameHonorificPrefix))) || ((result.get(NCO.nameHonorificPrefix)).equals("")))) {
							prefix = annot.getFeatures().get("prefix").toString(); 
							result.set(NCO.nameHonorificPrefix, prefix); 
						} 
						if ((!(annot.getFeatures().get("firstname").toString().isEmpty())) && ((!(result.has(NCO.nameGiven))) || ((result.get(NCO.nameGiven)).equals("")))) {
							firstname = annot.getFeatures().get("firstname").toString(); 
							result.set(NCO.nameGiven, firstname);
						}					
						if ((!(annot.getFeatures().get("surname").toString().isEmpty())) && ((!(result.has(NCO.nameFamily))) || ((result.get(NCO.nameFamily)).equals("")))) {
							surname = annot.getFeatures().get("surname").toString();  
							result.set(NCO.nameFamily, surname);
						}									   	
						if ((!(annot.getFeatures().get("suffix").toString().isEmpty())) && ((!(result.has(NCO.nameHonorificSuffix))) || ((result.get(NCO.nameHonorificSuffix)).equals("")))) {
							suffix = annot.getFeatures().get("suffix").toString();  
							result.set(NCO.nameHonorificSuffix, suffix);
						}  
						if ((!(annot.getFeatures().get("gender").toString().isEmpty())) && ((!(result.has(NCO.gender))) || ((result.get(NCO.gender)).equals("")))) {
							gender = annot.getFeatures().get("gender").toString(); 
							result.set(NCO.gender, gender);
						}  
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
		
		//if NER does not recognise any name and surname, use this manual approach
		if (((!(result.has(NCO.nameGiven))) || ((result.get(NCO.nameGiven)).equals(""))) 
				&& ((!(result.has(NCO.nameAdditional))) || ((result.get(NCO.nameAdditional)).equals("")))
				&& ((!(result.has(NCO.nameFamily))) || ((result.get(NCO.nameFamily)).equals("")))) {		
		
			String[] fullname = content.split(" ");
			
			if (fullname.length == 1) {
				result.set(NCO.nameGiven, fullname[0]);
	 		} else if (fullname.length == 2) {
				result.set(NCO.nameGiven, fullname[0]);
				result.set(NCO.nameFamily, fullname[1]);
			} else if (fullname.length == 3) {
				result.set(NCO.nameGiven, fullname[0]);
				result.set(NCO.nameAdditional, fullname[1]);
				result.set(NCO.nameFamily, fullname[2]);
			}
		}
		
		return result;
	}
	
}
