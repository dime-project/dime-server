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

package eu.dime.ps.gateway.transformer;

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;

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
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.IMAccount;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nco.PostalAddress;

/**
 * Tests {@link LinkedInXSparqlTest}.
 *
 * @author Ismael Rivera
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/transformer-tests-context.xml")
public class LinkedInXSparqlTest extends Assert {

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
		String xml = loadResource("transformer/linkedin_profiles.xml");
		Collection<PersonContact> contacts = transformer.deserialize(xml, "linkedin", "/profile/@me/@all", PersonContact.class);
		assertEquals(1,contacts.size());
		
		PersonContact contact = contacts.iterator().next();

     	assertEquals("J7qb67bTP", contact.getContactUID());

		PersonName pName = contact.getAllPersonName().next();
		assertEquals("Juan", pName.getNameGiven());
		assertEquals("Martinez", pName.getNameFamily());
		assertEquals("Juan Martinez", pName.getFullname());
		
		assertEquals(2, contact.getAllHobby_as().count());
		
		assertEquals(2, contact.getAllPostalAddress_as().count());		
		PostalAddress address = contact.getAllPostalAddress().next();		
		assertEquals("Paseo de la Castellana, 121 28046 Madrid", contact.getModel().findStatements(address, NAO.prefLabel, Variable.ANY).next().getObject().asLiteral().getValue()); 
		
		assertEquals("http://media.linkedin.com/mpr/mprx/0_yq555n50SdvS0jaMptNn5qFTTW_8xZ7MOzlV5zqxYuBK8pZJr-vWIv8l7FixpxmvgcbUEtEdTYeM", contact.getAllPhoto().next().asURI().toString());			
		assertEquals(1, contact.getAllAffiliation_as().count());
		
		PhoneNumber number = contact.getAllPhoneNumber().next();
		assertEquals("+34 750 106 567",number.getPhoneNumber());
		assertEquals(1, contact.getAllPhoneNumber_as().count());
		
		assertEquals(0, contact.getAllIMAccount_as().count());	
		
		BirthDate date = contact.getAllBirthDate().next();
		assertEquals(1966,date.getBirthDate().get(Calendar.YEAR));	
		assertEquals(8,date.getBirthDate().get(Calendar.MONTH)+1);	
		assertEquals(5,date.getBirthDate().get(Calendar.DATE));	

		assertEquals("Juan Martinez", contact.getPrefLabel());
		assertEquals("http://media.linkedin.com/mpr/mprx/0_yq555n50SdvS0jaMptNn5qFTTW_8xZ7MOzlV5zqxYuBK8pZJr-vWIv8l7FixpxmvgcbUEtEdTYeM", contact.getPrefSymbol().asURI().toString());
	}

	@Test
	public void testPersonsDeserialize() throws Exception {
		String xml = loadResource("transformer/linkedin_persons.xml");
		Collection<PersonContact> contacts = transformer.deserialize(xml, "linkedin", "/person/@me/@all", PersonContact.class);
		
		// there should be 71 contacts
		assertEquals(71, contacts.size());
		
		// checking information of one of the contacts
		boolean pujaFound = false;
		for (PersonContact c : contacts) {
			if ("g_xvPyW6QW".equals(c.getContactUID())) {
				pujaFound = true;
				PersonName pName = c.getAllPersonName().next();
				assertEquals("Puja", pName.getNameGiven());
				assertEquals("Abbassi", pName.getNameFamily());
				assertEquals("Puja Abbassi", pName.getFullname());
				assertEquals("http://media.linkedin.com/mpr/mprx/0_527Sxe0v9KDU6TlfLwm-xI0JnPeU6XrfFDU-xIuWWnywJ8F7doxTAw4bZjHk5iAikfSPKuO0Rf3Q", c.getAllPhoto().next().asURI().toString());			
				assertEquals(8, c.getAllAffiliation_as().count());
				assertEquals(10, c.getAllHobby_as().count());
				
				assertEquals(2, c.getAllPostalAddress_as().count());	
				PostalAddress address = c.getAllPostalAddress().next();
				assertEquals("Cologne, Germany", c.getModel().findStatements(address, NAO.prefLabel, Variable.ANY).next().getObject().asLiteral().getValue()); 
								
				PhoneNumber number = c.getAllPhoneNumber().next();
				assertEquals("+491799120593",number.getPhoneNumber());
				assertEquals(1, c.getAllPhoneNumber_as().count());
				
				IMAccount imaccount = c.getAllIMAccount().next();
				assertEquals("skype",imaccount.getImAccountType());
				assertEquals("puja108",imaccount.getAllImID().next());	

				assertEquals(1, c.getAllIMAccount_as().count());	
				
				BirthDate date = c.getAllBirthDate().next();
				assertEquals(1982,date.getBirthDate().get(Calendar.YEAR));	
				assertEquals(8,date.getBirthDate().get(Calendar.MONTH)+1);	
				assertEquals(29,date.getBirthDate().get(Calendar.DATE));	

				assertEquals("Puja Abbassi", c.getPrefLabel());
				assertEquals("http://media.linkedin.com/mpr/mprx/0_527Sxe0v9KDU6TlfLwm-xI0JnPeU6XrfFDU-xIuWWnywJ8F7doxTAw4bZjHk5iAikfSPKuO0Rf3Q", c.getPrefSymbol().asURI().toString());
				
				break;
			}
		}
		assertTrue(pujaFound);
	}
	
	@Test
	public void testLivePostsDeserialize() throws Exception {
		String xml = loadResource("transformer/linkedin_livepost.xml");
		Collection<Status> liveposts = transformer.deserialize(xml, "linkedin", "/livepost/@me/@all", Status.class);
		assertEquals(1, liveposts.size());
		
		Status livepost = liveposts.iterator().next();

		String externalIdentifier = livepost.getModel().findStatements(livepost.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
		assertEquals("s644819790", externalIdentifier);

		String textualContent = livepost.getModel().findStatements(livepost.getResource(), DLPO.textualContent, Variable.ANY).next().getObject().asLiteral().getValue();
		assertEquals("just joined LinkedIn!", textualContent);
		
		DatatypeLiteral timestamp = livepost.getModel().findStatements(livepost.getResource(), DLPO.timestamp, Variable.ANY).next().getObject().asDatatypeLiteral();
		assertEquals(XSD._dateTime, timestamp.getDatatype());
		assertEquals("2011-10-17T11:21:58.000Z", timestamp.getValue());
	}
	
	@Test
	public void testLivePostsAllDeserialize() throws Exception {		
		String xml = loadResource("transformer/linkedin_livepost_all.xml");
		Collection<Status> liveposts = transformer.deserialize(xml, "linkedin", "/livepost/@all", Status.class);			
		assertEquals(10, liveposts.size());
		
		boolean livepostFound = false;
		for (LivePost l : liveposts) {
			String externalIdentifier = l.getModel().findStatements(l.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue();
			if ("UNIU661575775527025216070365184SHARE".equals(externalIdentifier)) {
				livepostFound = true;
				String textualContent = l.getModel().findStatements(l.getResource(), DLPO.textualContent, Variable.ANY).next().getObject().asLiteral().getValue();
				assertEquals("has the requirement to recruit a #Graphical User Interface Developer #GUI for an #igaming company, based #Malta http://bull.hn/l/3UEP/3", textualContent);
		
				DatatypeLiteral timestamp = l.getModel().findStatements(l.getResource(), DLPO.timestamp, Variable.ANY).next().getObject().asDatatypeLiteral();
				assertEquals(XSD._dateTime, timestamp.getDatatype());
				assertEquals("2011-10-04T16:24:58.675Z", timestamp.getValue());
								
				Resource creator = l.getModel().findStatements(l.getResource(), NAO.creator, Variable.ANY).next().getObject().asResource();
				assertEquals("R5O2ObhksA",  l.getModel().findStatements(creator, NCO.contactUID, Variable.ANY).next().getObject().asLiteral().getValue());
				assertEquals("http://media.linkedin.com/mpr/mprx/0_U-ft05b1VKJTAibmMP2T0F9OZ9wmPLbmJzRD06b7b1xYuTKacq0YlQ1TnZIlrGQCsADurTzfCxQ6",  l.getModel().findStatements(creator, NCO.photo, Variable.ANY).next().getObject().asURI().toString()); 
				assertEquals("Head of idemandHR (Recruitment Malta & Gibraltar)",  l.getModel().findStatements(creator, NAO.description, Variable.ANY).next().getObject().asLiteral().getValue()); 
							
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
