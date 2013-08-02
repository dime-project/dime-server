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
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.semantic.model.nao.Party;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;

/**
 * Tests {@link FacebookXSparqlTest}.
 *
 * @author Keith Cortis
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/transformer-tests-context.xml")
public class FacebookXSparqlTest extends Assert {

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
		
		String serviceJSON = loadResource("transformer/facebook_profiles.json");		
		String xml = FormatUtils.convertJSONToXML(serviceJSON, null);
		Collection<PersonContact> contacts = transformer.deserialize(xml, "facebook", "/profile/@me/@all", PersonContact.class);	
	                
		// there should be 1 contact
		assertEquals(1, contacts.size());
		assertTrue(!contacts.isEmpty());   
		
		PersonContact contact = contacts.iterator().next();
		
		assertEquals("1003264926", contact.getContactUID());
		
		PersonName name = contact.getAllPersonName().next();
		assertEquals("Keith", name.getNameGiven());
		assertEquals("Cortis", name.getNameFamily());
		assertEquals("Keith Cortis", name.getFullname()); 
		assertEquals("keithcortis",contact.getModel().findStatements(name, NCO.nickname, Variable.ANY).next().getObject().toString());
		
		assertEquals(4, contact.getAllHobby_as().count());
		
		assertEquals("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc4/41731_1003264926_9901454_q.jpg", contact.getAllPhoto().next().asURI().toString());			
		assertEquals("https://www.facebook.com/keithcortis", contact.getModel().findStatements(contact.getResource(), NIE.url, Variable.ANY).next().getObject().asResource().toString());			
		assertEquals(7, contact.getAllAffiliation_as().count());
		assertEquals(2, contact.getAllPostalAddress_as().count());
		assertEquals("keithcortis@msn.com",contact.getAllEmailAddress().next().getEmailAddress());
		assertEquals("Hi there, I'm an addict, yes a football addict!",contact.getModel().findStatements(contact.getResource(), NAO.description, Variable.ANY).next().getObject().asLiteral().getValue());
		assertEquals("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#male",contact.getGender().asResource().toString());
				
		BirthDate date = contact.getAllBirthDate().next();
		assertEquals(1988,date.getBirthDate().get(Calendar.YEAR));	
		assertEquals(1,date.getBirthDate().get(Calendar.MONTH)+1);	
		assertEquals(4,date.getBirthDate().get(Calendar.DATE));	
				
	}
	
	@Test
	public void testPersonsDeserialize() throws Exception {
		
		String serviceJSON = loadResource("transformer/facebook_persons.json");
		String xml = FormatUtils.convertJSONToXML(serviceJSON, null);
		
		Collection<PersonContact> contacts = transformer.deserialize(xml, "facebook", "/person/@me/@all", PersonContact.class);	
	     
		// there should be 21 contacts
		assertEquals(21, contacts.size());
		assertTrue(!contacts.isEmpty());  
		
		// checking information of one of the contacts
		boolean daveFound = false;
		for (PersonContact c : contacts) {
			String contactUID = c.getContactUID();
			if ("503150961".equals(contactUID)) {
				daveFound = true;
				PersonName name = c.getAllPersonName().next();				
				
				assertEquals("Dave", name.getNameGiven());
				assertEquals("Briffa", name.getNameFamily());
				assertEquals("Dave Briffa", name.getFullname());
				assertEquals("davidbriffa",c.getModel().findStatements(name, NCO.nickname, Variable.ANY).next().getObject().toString());
				
				assertEquals(3, c.getAllHobby_as().count());
				
				assertEquals("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/275210_503150961_1985882673_q.jpg", c.getAllPhoto().next().asURI().toString());			
				assertEquals("http://www.facebook.com/davidbriffa", c.getModel().findStatements(c.getResource(), NIE.url, Variable.ANY).next().getObject().asResource().toString());			
				assertEquals(1, c.getAllAffiliation_as().count());
				assertEquals(2, c.getAllPostalAddress_as().count());
				assertEquals("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#male",c.getGender().asResource().toString());
				assertEquals("http://www.davidbriffa.info",c.getAllUrl().next().asURI().toString());		
				
				BirthDate date = c.getAllBirthDate().next();
				assertEquals(1986,date.getBirthDate().get(Calendar.YEAR));	
				assertEquals(9,date.getBirthDate().get(Calendar.MONTH)+1);	
				assertEquals(27,date.getBirthDate().get(Calendar.DATE));	
						
				break;
			} 
		}
		assertTrue(daveFound);
	}
	
	@Test
	public void testPersonsDeserialize2() throws Exception {
		
		String serviceJSON = loadResource("transformer/facebook_persons.json");
		String xml = FormatUtils.convertJSONToXML(serviceJSON, null);
		
		Collection<PersonContact> contacts = transformer.deserialize(xml, "facebook", "/person/@me/@all", PersonContact.class);	
	     
		// there should be 21 contacts
		assertEquals(21, contacts.size());
		assertTrue(!contacts.isEmpty());  
		
		// checking information of one of the contacts
		boolean marFound = false;
		for (PersonContact c : contacts) {
			String contactUID = c.getContactUID();
		    if ("500968384".equals(contactUID)) {
				marFound = true;
				
				PersonName name = c.getAllPersonName().next();				
				assertEquals("Elena",c.getModel().findStatements(name, NCO.nameAdditional, Variable.ANY).next().getObject().toString());
				
				assertFalse(c.getAllUrl().hasNext());		
			}
		}
		assertTrue(marFound);
	}


	
	@Test
	public void testLivePostsDeserialize() throws Exception {
		
		String serviceJSON = loadResource("transformer/facebook_livepost.json");
		String xml = FormatUtils.convertJSONToXML(serviceJSON, null);
				
		Collection<Status> liveposts = transformer.deserialize(xml, "facebook", "/livepost/@me/@all", Status.class);
			
		assertEquals(6, liveposts.size());
		
		boolean livepostFound = false; 
		for (Status l : liveposts) {
			String externalIdentifier = l.getModel().findStatements(l.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
			if ("3160579005741".equals(externalIdentifier)) {
				livepostFound = true;
				
				String textualContent = l.getModel().findStatements(l.getResource(), DLPO.textualContent, Variable.ANY).next().getObject().asLiteral().getValue();
				assertEquals("St paddys in dublin with some awesome Maltesers..", textualContent);
		
				DatatypeLiteral timestamp = l.getModel().findStatements(l.getResource(), DLPO.timestamp, Variable.ANY).next().getObject().asDatatypeLiteral();
				assertEquals(XSD._dateTime, timestamp.getDatatype());
				assertEquals("2012-03-17T00:22:53+0000", timestamp.getValue());				
				
				Resource creator = l.getModel().findStatements(l.getResource(), NAO.creator, Variable.ANY).next().getObject().asResource();
				assertEquals("1003264926",  l.getModel().findStatements(creator, NCO.contactUID, Variable.ANY).next().getObject().asLiteral().getValue()); 
				Resource creatorName = l.getModel().findStatements(creator, NCO.hasName, Variable.ANY).next().getObject().asResource();
				assertEquals("Keith Cortis", l.getModel().findStatements(creatorName, NCO.fullname, Variable.ANY).next().getObject().asLiteral().getValue()); 
			     						
				List<Party> favourites = l.getAllFavouritedBy_as().asList();
				boolean favouriteFound = false;
				for (Party f : favourites) {
					String favouriteId = f.getModel().findStatements(f.getResource(), NCO.contactUID, Variable.ANY).next().getObject().asLiteral().getValue();
					if ("588832317".equals(favouriteId)) {
						favouriteFound = true;
						Resource favouritedName = l.getModel().findStatements(f.getResource(), NCO.hasName, Variable.ANY).next().getObject().asResource();
						assertEquals("Judie Attard", l.getModel().findStatements(favouritedName, NCO.fullname, Variable.ANY).next().getObject().asLiteral().getValue());
					}					
				}
				assertTrue(favouriteFound);
								
				List<LivePost> comments = l.getAllReply_as().asList();
				boolean replyFound = false;
				for (LivePost c : comments) {
					String commentId = c.getModel().findStatements(c.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
					if ("3160579005741_3693768".equals(commentId)) {
						replyFound = true;
						assertEquals("enjoy amicos! =)", c.getAllTextualContent().next());
						
						DatatypeLiteral commentTimestamp = c.getModel().findStatements(c.getResource(), DLPO.timestamp, Variable.ANY).next().getObject().asDatatypeLiteral();
						assertEquals(XSD._dateTime, commentTimestamp.getDatatype());
						assertEquals("2012-03-17T08:50:55+0000", commentTimestamp.getValue());	
						
						Resource commentCreator = c.getModel().findStatements(c.getResource(), NAO.creator, Variable.ANY).next().getObject().asResource();
						assertEquals("639367035",c.getModel().findStatements(commentCreator,NCO.contactUID,Variable.ANY).next().getObject().asLiteral().getValue());
					    Resource commentCreatorName = c.getModel().findStatements(commentCreator, NCO.hasName, Variable.ANY).next().getObject().asResource();
						assertEquals("Ian Zerafa", c.getModel().findStatements(commentCreatorName, NCO.fullname, Variable.ANY).next().getObject().asLiteral().getValue()); 			
					}					
				}
				assertTrue(replyFound);
							
				break;
			}
		}
		assertTrue(livepostFound);

	} 
	
	/*
	//will be implemented once the encoding issue is solved - NCNames cannot start with the character 30
	@Test
	public void testLivePostsAllDeserialize() throws Exception {
		
		String serviceJSON = loadResource("transformer/facebook_livepost_all.json");
		String xml = convertJSONToXML(serviceJSON,"person","UTF-8");
		
		Collection<Status> liveposts = transformer.deserialize(xml, "facebook", "/livepost/@all", Status.class);
			
		assertEquals(25, liveposts.size());
		
	} 
	*/

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
