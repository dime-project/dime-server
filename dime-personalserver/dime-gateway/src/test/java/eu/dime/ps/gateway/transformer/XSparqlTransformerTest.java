package eu.dime.ps.gateway.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.gateway.transformer.impl.XSparqlTransformer;
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.semantic.model.ncal.Event;
import eu.dime.ps.semantic.model.nco.PersonContact;

/**
 * Tests {@link XSparqlTransformer}.
 * 
 * @author Will Fleury
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/transformer-tests-context.xml")
public class XSparqlTransformerTest extends Assert {
	
	protected Transformer transformer;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	protected final String serviceIdentifier = "linkedin";
	protected final String path = "/person/@me/@all";
	protected final String testXML = "transformer/linkedin_persons.xml";
	
	@Before
	public void setUp() throws Exception {
		transformer = new XSparqlTransformer();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDeserialize() throws Exception {
		//load the xml data fresh for each test to ensure no cross test contamination.
		String serviceXml = loadResource(testXML);

		//Given a single input retreive all of the instances of the specified
		//type which are contained in the data.
		Collection<PersonContact> contacts = 
		transformer.deserialize(serviceXml, serviceIdentifier, path, PersonContact.class);
		
		assertEquals(71, contacts.size());
				
		serviceXml = loadResource("transformer/linkedin_livepost.xml");
				
		Collection<Status> messages = 
				transformer.deserialize(serviceXml, serviceIdentifier, 
				"/livepost/@me/@all", Status.class);
				
		assertTrue(!messages.isEmpty());
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testMissingTransformerException() throws Exception {
		//load the xml data fresh for each test to ensure no cross test contamination.
		String serviceXml = loadResource(testXML);
		
		//test that it throws and exception when the service type is unknown
		transformer.deserialize(serviceXml, "unknownservice", path, Resource.class);
	}
	
	@Test(expected=TransformerException.class)
	public void testTransformerException() throws Exception {
		//test that it throws the correct exception when the data is bad format..
		transformer.deserialize("bad data...", serviceIdentifier, path, Resource.class);
	}
	  
	@Test
	public void testDeserializeCollection() throws Exception {
		//load the xml data fresh for each test to ensure no cross test contamination.
		String serviceXml = loadResource(testXML);
		
		Collection<Collection<PersonContact>> contacts = 
		transformer.deserializeCollection(Arrays.asList(serviceXml, serviceXml), 
		serviceIdentifier, path, PersonContact.class);
		
		assertEquals(2, contacts.size());
		
		//make sure that they return the same things since the same xml doc
		//is passed..
		Iterator<Collection<PersonContact>> iter = contacts.iterator();
		
		Iterator<PersonContact> iter1 = iter.next().iterator();
		Iterator<PersonContact> iter2 = iter.next().iterator();
		assertEquals(
				iter1.next().getAllPersonName().next().getNameFamily(),
				iter2.next().getAllPersonName().next().getNameFamily());
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testSerialize() throws Exception {
		//should throw unsupported operation exception as its not available
		transformer.serialize(null, serviceIdentifier, path);
	}
	
	@Ignore
	@Test
	public void testAmetic() throws Exception {
		//get the json data
		String serviceJSON = loadResource("transformer/ametic_events.json");
		String xml = FormatUtils.convertAmeticJSONToXML(serviceJSON);
		
		Collection<Event> events = transformer.deserialize(xml, "ameticdummyadapter", 
				"/event/@me/173", Event.class);
				
		assertTrue(!events.isEmpty());		
				
		serviceJSON = loadResource("transformer/ametic_events_all.json");
		xml = FormatUtils.convertAmeticJSONToXML(serviceJSON);
		
		events = transformer.deserialize(xml, "ameticdummyadapter", 
				"/event/@all", Event.class);
		
		assertTrue(!events.isEmpty()); 
	}
	
	private String loadResource(String resource) throws Exception {
		InputStream xmlStream = null;
		StringWriter writer = new StringWriter();
		
		try {
			xmlStream = this.getClass().getClassLoader().getResourceAsStream(resource);
			IOUtils.copy(xmlStream, writer, "UTF-8");
		} finally {
			if (xmlStream != null) {
				try { xmlStream.close(); } catch (IOException e) {}
			}
		}

		return writer.toString();
	}
	
}