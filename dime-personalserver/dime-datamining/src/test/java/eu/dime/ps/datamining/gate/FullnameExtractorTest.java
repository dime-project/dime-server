package eu.dime.ps.datamining.gate;

import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.rdf.impl.ResourceModelImpl;
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

public class FullnameExtractorTest extends Assert {
	
	private static FullnameExtractor fullnameExtractor = new FullnameExtractor();

    @BeforeClass
    public static void setUpClass() throws Exception {
    	// disabling org.openrdf.rdf2go.RepositoryModel warnings
    	org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
    	java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
    }

	@Test
	public void testEmptyFullnameExtraction() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "");
		
		// won't do anything, but shouldn't throw any exception
		fullnameExtractor.extract(resourceModel);	
	}
	
	@Test
	public void testFullnameExtraction() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "Dr. John Doe Jr.");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("Dr.", newResourceModel.get(NCO.nameHonorificPrefix));
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("Doe", newResourceModel.get(NCO.nameFamily));
		assertEquals("Jr.", newResourceModel.get(NCO.nameHonorificSuffix));
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction2() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "John Doe-Smith");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("Doe-Smith", newResourceModel.get(NCO.nameFamily));
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction3() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "John O'Connell");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("O'Connell", newResourceModel.get(NCO.nameFamily));
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction4() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "John O'Connell-Smith");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("O'Connell-Smith", newResourceModel.get(NCO.nameFamily));
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction5() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "John O'Connell Smith");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("O'Connell Smith", newResourceModel.get(NCO.nameFamily));
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction6() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "Prof. John Smith");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("Prof.", newResourceModel.get(NCO.nameHonorificPrefix));
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("Smith", newResourceModel.get(NCO.nameFamily));
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction7() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "John Smith Sr.");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("Smith", newResourceModel.get(NCO.nameFamily));
		assertEquals("Sr.", newResourceModel.get(NCO.nameHonorificSuffix));
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction8() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "John Smith Sr.");
		resourceModel.set(NCO.nameHonorificSuffix, "Jr.");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("Smith", newResourceModel.get(NCO.nameFamily));
		assertEquals("Jr.", newResourceModel.get(NCO.nameHonorificSuffix)); //test to verify that original NCO.nameHonorSuffix property is not overridden by extracted entity i.e. Sr.
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction9() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "John Smith Sr.");
		resourceModel.set(NCO.nameGiven, "");		
		resourceModel.set(NCO.nameHonorificSuffix, "");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("John", newResourceModel.get(NCO.nameGiven));
		assertEquals("Smith", newResourceModel.get(NCO.nameFamily));
		assertEquals("Sr.", newResourceModel.get(NCO.nameHonorificSuffix)); //test to check that if NCO.nameGiven & NCO.nameHonorSuffix properties have an empty string value, they will be overridden by extracted entities i.e. John and Sr.
		assertEquals("male", newResourceModel.get(NCO.gender));
	}
	
	@Test
	public void testFullnameExtraction10() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "Ismael");
	
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("Ismael", newResourceModel.get(NCO.nameGiven)); //test to check if manual extraction works
	}
	
	@Test
	public void testFullnameExtraction11() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "Ismael Rivera");
		resourceModel.set(NCO.nameGiven, "");		
		resourceModel.set(NCO.nameFamily, "");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("Ismael", newResourceModel.get(NCO.nameGiven));
		assertEquals("Rivera", newResourceModel.get(NCO.nameFamily)); //test to check if manual extraction works even if properties have an empty string value
	}
	
	@Test
	public void testFullnameExtraction12() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "Ismael 'Joe' Rivera");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("Ismael", newResourceModel.get(NCO.nameGiven));
		assertEquals("'Joe'", newResourceModel.get(NCO.nameAdditional));
		assertEquals("Rivera", newResourceModel.get(NCO.nameFamily)); //test to check if manual extraction works
	}
	
	@Test
	public void testFullnameExtraction13() throws ModelRuntimeException, IOException, DataMiningException {
		// getting model
		Model model = RDF2Go.getModelFactory().createModel();
	    model.open();   
	 
	    URI name = model.createURI("urn:juan:personName4");	   	    
		ResourceModel resourceModel = new ResourceModelImpl(name);
		resourceModel.set(NCO.fullname, "Jane O' Donnell");
		
		ResourceModel newResourceModel = fullnameExtractor.extract(resourceModel);	
		assertEquals("Jane", newResourceModel.get(NCO.nameGiven));
		assertEquals("O' Donnell", newResourceModel.get(NCO.nameFamily)); 
		assertEquals("female", newResourceModel.get(NCO.gender));
	}
	
	
}
