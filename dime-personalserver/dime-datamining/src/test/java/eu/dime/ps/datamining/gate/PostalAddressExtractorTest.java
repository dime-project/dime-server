/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.rdf.impl.ResourceModelImpl;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.datamining.exceptions.DataMiningException;

public class PostalAddressExtractorTest extends Assert {
	
	private static PostalAddressExtractor postalAddressExtractor = new PostalAddressExtractor();

    @BeforeClass
    public static void setUpClass() throws Exception {
    	// disabling org.openrdf.rdf2go.RepositoryModel warnings
    	org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
    	java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
    }

	@Test	
	public void testEmptyPostalAddressExtraction() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "");
		
		// won't do anything, but shouldn't throw any exception
		postalAddressExtractor.extract(resourceModel);	
	}
	
	@Test
	public void testPostalAddressExtraction() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "Galway, Ireland");
		
		ResourceModel newResourceModel = postalAddressExtractor.extract(resourceModel);	
		assertEquals("Galway", newResourceModel.get(NCO.locality));
		assertEquals("Ireland", newResourceModel.get(NCO.country));		
	}
	
	@Test
	public void testPostalAddressExtraction2() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "Belfast, Northern Ireland, United Kingdom");
		
		ResourceModel newResourceModel = postalAddressExtractor.extract(resourceModel);	
		assertEquals("Belfast", newResourceModel.get(NCO.locality));
		assertEquals("Northern Ireland", newResourceModel.get(NCO.country));		
	}
	
	@Test
	public void testPostalAddressExtraction3() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "Cumbria, England");
		
		ResourceModel newResourceModel = postalAddressExtractor.extract(resourceModel);	
		assertEquals("Cumbria", newResourceModel.get(NCO.region));
		assertEquals("England", newResourceModel.get(NCO.country));		
	}
	
	@Test
	public void testPostalAddressExtraction4() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "Unit - 1019 PO Box 7169 Poole BH15 9EL");
		
		ResourceModel newResourceModel = postalAddressExtractor.extract(resourceModel);	
		assertEquals("PO Box 7169", newResourceModel.get(NCO.pobox));
		assertEquals("BH15 9EL", newResourceModel.get(NCO.postalcode));	
	}
	
	@Test
	public void testPostalAddressExtraction5() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "10b St Marys Road, Barnet, NW11 9UG, LONDON, Great Britain");
		
		ResourceModel newResourceModel = postalAddressExtractor.extract(resourceModel);	
		assertEquals("10b St Marys Road", newResourceModel.get(NCO.streetAddress));
		assertEquals("NW11 9UG", newResourceModel.get(NCO.postalcode));
		assertEquals("LONDON", newResourceModel.get(NCO.locality));	
		assertEquals("Great Britain", newResourceModel.get(NCO.country));	
	}
	
	@Test
	public void testPostalAddressExtraction6() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "Mississippi, USA");
		
		ResourceModel newResourceModel = postalAddressExtractor.extract(resourceModel);	
		assertEquals("Mississippi", newResourceModel.get(NCO.region));
		assertEquals("USA", newResourceModel.get(NCO.country));	
	}
	
	@Test
	public void testPostalAddressExtraction7() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "Mississippi, USA");
		resourceModel.set(NCO.country, "United States");
		
		ResourceModel newResourceModel = postalAddressExtractor.extract(resourceModel);	
		assertEquals("Mississippi", newResourceModel.get(NCO.region));
		assertEquals("United States", newResourceModel.get(NCO.country)); //test to verify that original NCO.country property is not overridden by extracted entity i.e. USA
	}
	
	@Test
	public void testPostalAddressExtraction8() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI addr = model.createURI("urn:juan:loc36");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(addr);
		resourceModel.set(NAO.prefLabel, "Mississippi, USA");
		resourceModel.set(NCO.country, "");
		
		ResourceModel newResourceModel = postalAddressExtractor.extract(resourceModel);	
		assertEquals("Mississippi", newResourceModel.get(NCO.region));
		assertEquals("USA", newResourceModel.get(NCO.country)); //test to check that if NCO.country property is an empty string, it will be overridden by extracted entity i.e. USA
	}
	
}
