package eu.dime.ps.semantic.service;

import ie.deri.smile.matching.matcher.MongeElkanPersonMatcher;
import ie.deri.smile.rdf.util.ModelUtils;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.service.impl.PersonMatchingServiceImpl;

/**
 * Tests {@link PersonMatchingServiceImpl}.
 */
public final class PersonMatchingServiceTest extends SemanticTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonMatchingServiceTest.class);	

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.resourceStore.clear();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testPersonMatchingGeneration() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData4.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
		Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));
		PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore);
		List<PersonMatch> personMatches  = matchingService.match(newPersonURI);
		assertEquals(2,personMatches.size()); //2 persons which are being matched to
		
		// TODO add some assertions...
	}
	
	@Test
	public void testPersonMatchingNoMatch() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData2.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
		Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));
		PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore);
		List<PersonMatch> personMatches  = matchingService.match(newPersonURI);
		
		Resource annaURI = resourceStore.get(new URIImpl("urn:anna:annaUser"));
		assertEquals(annaURI,personMatches.get(0).getTarget());
		double annaScore = personMatches.get(0).getSimilarityScore();
		assertEquals(0.0, annaScore, 0.00001);
	}
	

	@Test
	public void testPersonMatching() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
	    Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));
	    PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore, 2, 1, 1, true);
	    List<PersonMatch> personMatches = matchingService.match(newPersonURI);	
		
		Resource juanURI = resourceStore.get(new URIImpl("urn:juan:juanUser"));
		assertEquals(juanURI,personMatches.get(1).getTarget());
		double juanScore = personMatches.get(1).getSimilarityScore();
		assertEquals(0.9000937035679817, juanScore, 0.00001);
		
		Resource annaURI = resourceStore.get(new URIImpl("urn:anna:annaUser"));
		assertEquals(annaURI,personMatches.get(0).getTarget());
		double annaScore = personMatches.get(0).getSimilarityScore();		
		assertEquals(0.609866693019867, annaScore, 0.00001); //Results Score Decimal Function 
	}	
		
	@Test
	public void testPersonMatchingWithThreshold() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
	    Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));
	    PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore, 2, 1, 1, true);
		List<PersonMatch> personMatches  = matchingService.match(newPersonURI, 0.80); 
		
		Resource juanURI = resourceStore.get(new URIImpl("urn:juan:juanUser"));
		assertEquals(juanURI,personMatches.get(0).getTarget()); //change array position due to change of method
		double juanScore = personMatches.get(0).getSimilarityScore();
		assertEquals(0.9000937035679817, juanScore, 0.00001); //Results Score Decimal Function
	}
	
	@Test
	public void testPersonMatchingWithDifferentApproach() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
	    Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));	   
	    PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore, 3, 1, 2, true); //Approach 2 used
		List<PersonMatch> personMatches  = matchingService.match(newPersonURI);
		
		Resource juanURI = resourceStore.get(new URIImpl("urn:juan:juanUser"));
		assertEquals(juanURI,personMatches.get(1).getTarget());
		double juanScore = personMatches.get(1).getSimilarityScore();
		assertEquals(0.9583333333333334,juanScore, 0.00001); //Results Score Decimal Function 
		
		Resource annaURI = resourceStore.get(new URIImpl("urn:anna:annaUser"));
		assertEquals(annaURI,personMatches.get(0).getTarget());
		double annaScore = personMatches.get(0).getSimilarityScore();		
		assertEquals(0.2, annaScore, 0.00001); //Results Score Decimal Function 
	}
	
	@Test
	public void testPersonMatchingWithThresholdAndDifferentApproach() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
	    Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));	   
	    PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore, 3, 1, 2, true); //Approach 2 used
	    List<PersonMatch> personMatches  = matchingService.match(newPersonURI, 0.8);
		
		Resource juanURI = resourceStore.get(new URIImpl("urn:juan:juanUser"));
		assertEquals(juanURI,personMatches.get(0).getTarget());
		double juanScore = personMatches.get(0).getSimilarityScore();
		assertEquals(0.9583333333333334,juanScore, 0.00001); //Results Score Decimal Function 
	}
	
	@Test
	public void testPersonMatchingWithLatestUpdate() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData3.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
	    Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));	   
	    PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore, 2, 1, 2); //Approach 2 used
	    List<PersonMatch> personMatches  = matchingService.match(newPersonURI, 0.8);		
	    
	    Resource juanURI = resourceStore.get(new URIImpl("urn:juan:juanUser")); //new user is matched against this user i.e. juan:juanUser
	    assertEquals(juanURI,personMatches.get(0).getTarget());
		double juanScore = personMatches.get(0).getSimilarityScore();
		assertEquals(0.9666666666666667,juanScore, 0.00001); //Results Score Decimal Function		
		
		Resource newPersonURI2 = resourceStore.get(new URIImpl("urn:shaunLINKEDIN:shaunLINKEDINUser"));	   
	    List<PersonMatch> personMatches2  = matchingService.match(newPersonURI2, 0);
		
		assertEquals(juanURI,personMatches2.get(2).getTarget());
		double juanScore2 = personMatches2.get(2).getSimilarityScore();
		assertEquals(0.6222222222222222,juanScore2, 0.00001); //Results Score Decimal Function		
	} 
	
	@Test
	public void testPersonMatchingWithSemanticMatching() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("pimo.trig"),
				Syntax.Trig, initial);		
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("nao.trig"),
				Syntax.Trig, initial);		
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData5.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
	    Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));	   
	    PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore, 2, 1, 1, true); //Approach 1 used and Semantic Extension is ON
		List<PersonMatch> personMatches  = matchingService.match(newPersonURI, 0.7); 
		
		Resource juanURI = resourceStore.get(new URIImpl("urn:juan:juanUser"));
		assertEquals(juanURI,personMatches.get(0).getTarget());
		double juanScore = personMatches.get(0).getSimilarityScore();
		assertEquals(0.9389161514100574,juanScore, 0.00001); //Results Score Decimal Function 
			
	}
	
	@Test
	public void testPersonMatchingWithSemanticMatching2() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("pimo.trig"),
				Syntax.Trig, initial);		
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("nao.trig"),
				Syntax.Trig, initial);		
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData6.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
	    Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));	   
	    PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore, 2, 1, 1, true); //Approach 1 used and Semantic Extension is ON
		List<PersonMatch> personMatches  = matchingService.match(newPersonURI, 0);
		
		Resource juanURI = resourceStore.get(new URIImpl("urn:juan:juanUser"));
		assertEquals(juanURI,personMatches.get(0).getTarget());
		double juanScore = personMatches.get(0).getSimilarityScore();
		assertEquals(0.8682947042253283,juanScore, 0.00001); //Results Score Decimal Function 
	} 
	
	@Test
	public void testPersonMatchingWithSemanticMatching3() throws ModelRuntimeException, IOException, NotFoundException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("pimo.trig"),
				Syntax.Trig, initial);		
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("nao.trig"),
				Syntax.Trig, initial);		
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/pimTestData6.ttl"),
				Syntax.Turtle, initial);
		resourceStore.getTripleStore().addAll(initial.iterator());
		
	    Resource newPersonURI = resourceStore.get(new URIImpl("urn:juanLINKEDIN:juanLINKEDINUser"));			
	    PersonMatchingService matchingService = new PersonMatchingServiceImpl(resourceStore, 2, 2, 2, true); //Approach 2 used and Semantic Extension is ON
		List<PersonMatch> personMatches  = matchingService.match(newPersonURI, 0);
		
		Resource juanURI = resourceStore.get(new URIImpl("urn:juan:juanUser"));
		assertEquals(juanURI,personMatches.get(0).getTarget());
		double juanScore = personMatches.get(0).getSimilarityScore();
		assertEquals(1.0,juanScore, 0.00001); //Results Score Decimal Function 
	} 
	
}