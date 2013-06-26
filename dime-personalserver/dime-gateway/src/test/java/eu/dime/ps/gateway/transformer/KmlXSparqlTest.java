package eu.dime.ps.gateway.transformer;

import ie.deri.smile.vocabulary.GEO;
import ie.deri.smile.vocabulary.NAO;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.transformer.impl.XSparqlTransformer;

import eu.dime.ps.semantic.model.geo.Point;
import eu.dime.ps.semantic.model.nfo.Placemark;

/**
 * Tests {@link KmlXSparqlTest}.
 *
 * @author Keith Cortis
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/transformer-tests-context.xml")
public class KmlXSparqlTest extends Assert {

	XSparqlTransformer transformer;
	
	@Before
	public void setUp() throws Exception {
		transformer = new XSparqlTransformer();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testPlacemarksDeserialize() throws Exception {
		String xml = loadResource("transformer/kml_place_all.kml");
		
		Collection<Placemark> placemarks = transformer.deserialize(xml, "kml", AttributeMap.PLACE_ALL, Placemark.class);		
				
		// there should be 43 placemarks
		assertEquals(43, placemarks.size());
		
		// checking information of one of the placemarks
		boolean deriFound = false;
		for (Placemark p : placemarks) {
			String externalIdentifier = p.getModel().findStatements(p.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
			
			if ("184".equals(externalIdentifier)) {
				deriFound = true;			
				assertEquals("DERI building", p.getPrefLabel());
				String description = p.getModel().findStatements(p.getResource(), NAO.description, Variable.ANY).next().getObject().asLiteral().getValue(); 
				assertEquals("The building where everybody from DERI is working. It is located in the IDA business park. None of the events of the conference will happen here. (But good to have it as a reference ;) ",description);				
				
				String lat = p.getModel().findStatements(p.getResource(), GEO.lat , Variable.ANY).next().getObject().asLiteral().getValue(); 
				assertEquals("53.289936",lat);
				String lon = p.getModel().findStatements(p.getResource(), GEO.lon, Variable.ANY).next().getObject().asLiteral().getValue(); 
			    assertEquals("-9.074240",lon);			
				break;
			}
		}
		assertTrue(deriFound);
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
