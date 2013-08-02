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

package eu.dime.ps.gateway.transformer;

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.gateway.transformer.impl.XSparqlTransformer;
import eu.dime.ps.semantic.model.dlpo.Comment;
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;

/**
 * Tests {@link TwitterXSparqlTest}.
 *
 * @author Keith Cortis
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/transformer-tests-context.xml")
public class TwitterXSparqlTest extends Assert {

	private XSparqlTransformer transformer;
	
	@Before
	public void setUp() throws Exception {
		transformer = new XSparqlTransformer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProfileDeserialize() throws Exception {		
		
		String serviceJSON = loadResource("transformer/twitter_profile.json");		
		String xml = FormatUtils.convertJSONToXML(serviceJSON, null);
		Collection<PersonContact> contacts = transformer.deserialize(xml, "twitter", "/profile/@me/@all", PersonContact.class);			
				
		// there should be 1 contact
		assertEquals(1, contacts.size());

		PersonContact contact = contacts.iterator().next();
		
		assertEquals("102624652", contact.getContactUID());
				
		PersonName name = contact.getAllPersonName().next();
		assertEquals("Keith Cortis", name.getFullname());
		
		//assertEquals(30, contact.getAllHobby_asNode_().count());
		assertEquals(1, contact.getAllPostalAddress_asNode_().count());
		
		assertEquals("kcortis",contact.getModel().findStatements(name, NCO.nickname, Variable.ANY).next().getObject().toString());
		assertEquals("http://a0.twimg.com/profile_images/1470511462/keith_cortis_normal.jpg", contact.getAllPhoto().next().asURI().toString());					
		assertEquals("MSc Student at Digital Enterprise Research Institute (DERI) Galway. Sports enthusiast & an avid Liverpool FC supporter.",contact.getModel().findStatements(contact.getResource(), NAO.description, Variable.ANY).next().getObject().asLiteral().getValue());
	}
	
	
	@Test
	public void testPersonDeserialize() throws Exception {		
		
		String serviceJSON = loadResource("transformer/twitter_person.json");		
		String xml = FormatUtils.convertJSONToXML(serviceJSON, null);
		Collection<PersonContact> contacts = transformer.deserialize(xml, "twitter", "/person/@me/@all", PersonContact.class);   
	     
		// there should be 20 contacts
		assertEquals(20, contacts.size());
		
		// checking information of one of the contacts
		boolean ryanFound = false;
		for (PersonContact c : contacts) {
			String contactUID = c.getContactUID();
			if ("431429137".equals(contactUID)) {
				ryanFound = true;
				PersonName name = c.getAllPersonName().next();
				assertEquals("Clint Dempsey", name.getFullname()); 
				assertEquals("clint_dempsey",c.getModel().findStatements(name, NCO.nickname, Variable.ANY).next().getObject().toString());
				assertEquals("http://a0.twimg.com/profile_images/1734541996/Bass_normal.jpg", c.getAllPhoto().next().asURI().toString());					
				assertEquals("Avid fisherman, dad of 2, reppin Nac for life. Oh, and I play a little football.",c.getModel().findStatements(c.getResource(), NAO.description, Variable.ANY).next().getObject().asLiteral().getValue());
				assertEquals("http://www.facebook.com/clintdempsey23",c.getAllUrl().next().asURI().toString());
				assertEquals(0, c.getAllPostalAddress_as().count());
				
				break;
			}
		}
		assertTrue(ryanFound);
	}

	
	@Test
	public void testLivePostsDeserialize() throws Exception {
		String serviceJSON = loadResource("transformer/twitter_livepost.json");		
		String xml = FormatUtils.convertJSONToXML(serviceJSON, null);
		Collection<Comment> comments = transformer.deserialize(xml, "twitter", "/livepost/@me/@all", Comment.class);	
	
		assertEquals(1, comments.size());
		
		boolean livepostFound = false;
		for (Comment comment : comments) {
			// check it's also a dlpo:LivePost explicitly
			comment.getModel().findStatements(comment.asResource(), RDF.type, DLPO.LivePost);

			String externalIdentifier = comment.getModel().findStatements(comment.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
			if ("276385635445665792".equals(externalIdentifier)) {
				livepostFound = true;
				String textualContent = comment.getModel().findStatements(comment.getResource(), DLPO.textualContent, Variable.ANY).next().getObject().asLiteral().getValue();
				assertEquals("@tedvickey interesting!!", textualContent);
		
				DatatypeLiteral timestamp = comment.getModel().findStatements(comment.getResource(), DLPO.timestamp, Variable.ANY).next().getObject().asDatatypeLiteral();
				assertEquals(XSD._dateTime, timestamp.getDatatype());
				assertEquals(new DateTime("2012-12-05T18:00:45.000Z"), new DateTime(timestamp.getValue()));
				
				Resource creator = comment.getModel().findStatements(comment.getResource(), NAO.creator, Variable.ANY).next().getObject().asResource();
				assertEquals("102624652",  comment.getModel().findStatements(creator, NCO.contactUID, Variable.ANY).next().getObject().asLiteral().getValue()); 
			     
				assertEquals("276380543178911745",comment.getAllReplyOf().next().getAllExternalIdentifier().next());	
				Resource replyCreator = comment.getAllReplyOf().next().getCreator().asResource();
				assertEquals("18091293",  comment.getModel().findStatements(replyCreator, NCO.contactUID, Variable.ANY).next().getObject().asLiteral().getValue());
				Resource relatedResource = comment.getModel().findStatements(comment.getResource(), DLPO.relatedResource, Variable.ANY).next().getObject().asResource();
				assertEquals("18091293",comment.getModel().findStatements(relatedResource, NCO.contactUID,Variable.ANY).next().getObject().asLiteral().getValue());
				
				break;
			}
		}
		assertTrue(livepostFound);
	} 
	
	@Test
	public void testLivePostsAllDeserialize() throws Exception {
	
		String serviceJSON = loadResource("transformer/twitter_livepost_all.json");		
		String xml = FormatUtils.convertJSONToXML(serviceJSON, null);
		Collection<Status> statuses = transformer.deserialize(xml, "twitter", "/livepost/@all", Status.class);	
	
		assertEquals(241, statuses.size());
		
		boolean livepostFound = false;
		for (Status status : statuses) {
			// check it's also a dlpo:LivePost explicitly
			status.getModel().findStatements(status.asResource(), RDF.type, DLPO.LivePost);
			
			String externalIdentifier = status.getModel().findStatements(status.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
			if ("278158924639772673".equals(externalIdentifier)) {
				livepostFound = true;
				String textualContent = status.getModel().findStatements(status.getResource(), DLPO.textualContent, Variable.ANY).next().getObject().asLiteral().getValue();
				assertEquals("Sad day. Been to visit some sick kids in hospital in Stoke and as I'm heading home just witnessed a horrific fatal accident on the M6.", textualContent);
		
				DatatypeLiteral timestamp = status.getModel().findStatements(status.getResource(), DLPO.timestamp, Variable.ANY).next().getObject().asDatatypeLiteral();
				assertEquals(XSD._dateTime, timestamp.getDatatype());
				assertEquals(new DateTime("2012-12-10T15:27:10.000Z"), new DateTime(timestamp.getValue()));
				
				Resource creator = status.getModel().findStatements(status.getResource(), NAO.creator, Variable.ANY).next().getObject().asResource();
				assertEquals("216264820",  status.getModel().findStatements(creator, NCO.contactUID, Variable.ANY).next().getObject().asLiteral().getValue());
				assertEquals("http://a0.twimg.com/profile_images/1268978942/Avatar_normal.jpg",  status.getModel().findStatements(creator, NCO.photo, Variable.ANY).next().getObject().asURI().toString()); 
				assertEquals("The Official Twitter Account of Michael Owen",  status.getModel().findStatements(creator, NAO.description, Variable.ANY).next().getObject().asLiteral().getValue()); 
				assertEquals("http://michaelowen.com",  status.getModel().findStatements(creator, NCO.websiteUrl, Variable.ANY).next().getObject().asURI().toString()); 
				
				break;
			}
		}
		assertTrue(livepostFound);
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
