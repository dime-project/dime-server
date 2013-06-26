package eu.dime.ps.gateway.transformer;

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.GEO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NIE;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ibm.icu.util.Calendar;

import eu.dime.ps.gateway.transformer.impl.XSparqlTransformer;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;

/**
 * Tests {@link GoogleplusXSparqlTest}.
 *
 * @author Keith Cortis
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/transformer-tests-context.xml")

public class GoogleplusXSparqlTest extends Assert {

	XSparqlTransformer transformer;
	
	@Before
	public void setUp() throws Exception {
		transformer = new XSparqlTransformer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProfileDeserialize() throws Exception {
		
		String serviceJSON = loadResource("transformer/googleplus_profile.json");		
		String xml = FormatUtils.convertJSONToXML(serviceJSON, "person");
				
		Collection<PersonContact> contacts = transformer.deserialize(xml, "googleplus", "/profile/@me/@all", PersonContact.class);	
	    
		// there should be 1 contact
		assertEquals(1, contacts.size());
		assertTrue(!contacts.isEmpty()); 
		
		PersonContact contact = contacts.iterator().next();
		
		assertEquals("105758250853721587989", contact.getContactUID());
		
		PersonName name = contact.getAllPersonName().next();
		assertEquals("Keith", name.getNameGiven());
		assertEquals("Cortis", name.getNameFamily());
		assertEquals("Keith Cortis",contact.getModel().findStatements(name, NAO.prefLabel, Variable.ANY).next().getObject().toString());	
		assertEquals("https://lh6.googleusercontent.com/-pBdNpRL7Bgg/AAAAAAABBBI/AAABBBAAAB8/Dz6GFrr3ryM/photo.jpg?sz=50", contact.getPrefSymbol().asURI().toString());
		assertEquals("Currently a Masters student at DERI Galway.",contact.getModel().findStatements(contact.getResource(), NAO.description, Variable.ANY).next().getObject().asLiteral().getValue());
		assertEquals("https://plus.google.com/105758250853721587989",contact.getModel().findStatements(contact.getResource(), NIE.url, Variable.ANY).next().getObject().asResource().toString());
		assertEquals("http://www.deri.ie/about/team/member/keith_cortis/", contact.getAllUrl().next().asURI().toString());				
		assertEquals("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#male",contact.getGender().asResource().toString());
		
		BirthDate date = contact.getAllBirthDate().next();
		assertEquals(1988,date.getBirthDate().get(Calendar.YEAR));	
		assertEquals(1,date.getBirthDate().get(Calendar.MONTH)+1);	
		assertEquals(4,date.getBirthDate().get(Calendar.DATE));	
		
		assertEquals(3, contact.getAllAffiliation_as().count());			
	}
	
	@Test
	@Ignore // FIXME XSPARQL query needs to be improved 
	public void testLivePostsDeserialize() throws Exception {
		
		String serviceJSON = loadResource("transformer/googleplus_livepost.json");
		String xml = FormatUtils.convertJSONToXML(serviceJSON, "response", "UTF-8");
				
		Collection<LivePost> liveposts = transformer.deserialize(xml, "googleplus", "/livepost/@me/@all", LivePost.class);
		
		assertEquals(15, liveposts.size());
				
		boolean livepostFound = false; 
		for (LivePost l : liveposts) {
			String externalIdentifier = l.getModel().findStatements(l.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
			if ("z12esd5oxyucdhk5v04clf3bwvtc0k".equals(externalIdentifier)) {
				livepostFound = true;
				
				String textualContent = l.getModel().findStatements(l.getResource(), DLPO.textualContent, Variable.ANY).next().getObject().asLiteral().getValue();
				assertEquals("Waiting for a healthy irish bfast...", textualContent);
		
				DatatypeLiteral timestamp = l.getModel().findStatements(l.getResource(), DLPO.timestamp, Variable.ANY).next().getObject().asDatatypeLiteral();
				assertEquals(XSD._dateTime, timestamp.getDatatype());
				assertEquals("2012-02-19T09:14:24.425Z", timestamp.getValue());				
				
				Resource creator = l.getModel().findStatements(l.getResource(), NAO.creator, Variable.ANY).next().getObject().asResource();
				assertEquals("1061352508",  l.getModel().findStatements(creator, NCO.contactUID, Variable.ANY).next().getObject().asLiteral().getValue()); 
				Resource creatorName = l.getModel().findStatements(creator, NCO.hasName, Variable.ANY).next().getObject().asResource();
				assertEquals("Keith Cortis", l.getModel().findStatements(creatorName, NAO.prefLabel, Variable.ANY).next().getObject().asLiteral().getValue()); 				
				
				List<LivePost> posts = l.getAllIsComposedOf_as().asList();
				boolean postFound = false;
				for (LivePost post : posts) {
					String postId = post.getModel().findStatements(post.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
					if ("126".equals(postId)) {
						postFound = true;
						assertEquals("Strandsend House",post.getModel().findStatements(post.getResource(), NAO.prefLabel, Variable.ANY).next().getObject().asLiteral().getValue());
						assertEquals("http://maps.google.com/maps/place?cid=14093267913099459020",post.getModel().findStatements(post.getResource(), DLPO.definingResource, Variable.ANY).next().getObject().asResource().toString());
						Resource relatedResource = post.getModel().findStatements(post.getResource(), DLPO.relatedResource, Variable.ANY).next().getObject().asResource();
						assertEquals("Strandsend House", post.getModel().findStatements(relatedResource, NAO.prefLabel, Variable.ANY).next().getObject().asLiteral().getValue());
						assertEquals("Strandsend House, Cahersiveen - Knight's Town", post.getModel().findStatements(relatedResource, NAO.description, Variable.ANY).next().getObject().asLiteral().getValue());
						assertEquals("51.96187973022461", post.getModel().findStatements(relatedResource, GEO.lat, Variable.ANY).next().getObject().asLiteral().getValue());
						assertEquals("-10.181063652038574", post.getModel().findStatements(relatedResource, GEO.lon, Variable.ANY).next().getObject().asLiteral().getValue());
						break;
					}					
				}
				assertTrue(postFound);								
				break;
			}
		}
		assertTrue(livepostFound);
	
	} 
	
	@Test
	public void testNoLivePostsDeserialize() throws Exception {
		
		String serviceJSON = loadResource("transformer/googleplus_no_livepost.json");
		String xml = FormatUtils.convertJSONToXML(serviceJSON,"person","UTF-8");
				
		Collection<LivePost> liveposts = transformer.deserialize(xml, "googleplus", "/livepost/@me/@all", LivePost.class);
			
		assertEquals(0, liveposts.size());
	
	} 
	
	
	private String loadResource(String resource) throws Exception {
		InputStream inputStream = null;
		StringWriter writer = new StringWriter();
		
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);
			IOUtils.copy(inputStream, writer, "UTF-8");
		} finally {
			if (inputStream != null) {
				try { inputStream.close(); } catch (IOException e) {}
			}
		}

		return writer.toString();
	}
	
}
