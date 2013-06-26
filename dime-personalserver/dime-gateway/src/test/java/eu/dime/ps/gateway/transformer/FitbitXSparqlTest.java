package eu.dime.ps.gateway.transformer;

import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.DCON;

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
import eu.dime.ps.semantic.model.nao.Party;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.IMAccount;
import eu.dime.ps.semantic.model.nco.Name;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nco.PostalAddress;
import eu.dime.ps.semantic.model.dpo.Activity;

/**
 * Tests {@link FitbitXSparqlTest}.
 *
 * @author Keith Cortis
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/transformer-tests-context.xml")

public class FitbitXSparqlTest extends Assert {

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
		
		String xml = loadResource("transformer/fitbit_profile.xml");		
					
		Collection<PersonContact> contacts = transformer.deserialize(xml, "fitbit", "/profile/@me/@all", PersonContact.class);	
	    
		// there should be 1 contact
		assertEquals(1, contacts.size());
		assertTrue(!contacts.isEmpty()); 
		
		PersonContact contact = contacts.iterator().next();
		
		assertEquals("2295YW", contact.getContactUID());
		
		PersonName name = contact.getAllPersonName().next();
		assertEquals("Fitbit User", name.getFullname());
		assertEquals("Nick",contact.getModel().findStatements(name, NCO.nickname, Variable.ANY).next().getObject().toString());
		assertEquals("Nick",contact.getModel().findStatements(name, NAO.prefLabel, Variable.ANY).next().getObject().toString());	
		assertEquals("http://www.fitbit.com/images/profile/defaultProfile_100_male.gif", contact.getPrefSymbol().asURI().toString());
		assertEquals("I live in San Francisco.",contact.getModel().findStatements(contact.getResource(), NAO.description, Variable.ANY).next().getObject().asLiteral().getValue());
		assertEquals("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#male",contact.getGender().asResource().toString());
		
		PostalAddress address = contact.getAllPostalAddress().next();
		assertEquals("US",address.getCountry());
		assertEquals("CA",address.getRegion());
		assertEquals("San Francisco",address.getLocality());
		
		BirthDate date = contact.getAllBirthDate().next();
		assertEquals(1971,date.getBirthDate().get(Calendar.YEAR));	
		assertEquals(2,date.getBirthDate().get(Calendar.MONTH)+1);	
		assertEquals(18,date.getBirthDate().get(Calendar.DATE));	
		
	}
	
	@Test
	public void testPersonsDeserialize() throws Exception {
		String xml = loadResource("transformer/fitbit_person.xml");
		Collection<PersonContact> contacts = transformer.deserialize(xml, "fitbit", "/person/@me/@all", PersonContact.class);
		
		// there should be 2 contacts
		assertEquals(2, contacts.size());
		
		// checking information of one of the contacts
		boolean annaFound = false;
		for (PersonContact contact : contacts) {
			if ("23K9KQ".equals(contact.getContactUID())) {
				annaFound = true;
				
				PersonName name = contact.getAllPersonName().next();
				assertEquals("Anna Alford", name.getFullname());
				assertEquals("Annie",contact.getModel().findStatements(name, NCO.nickname, Variable.ANY).next().getObject().toString());
				assertEquals("Anna Alf.",contact.getModel().findStatements(name, NAO.prefLabel, Variable.ANY).next().getObject().toString());	
				assertEquals("http://www.fitbit.com/images/profile/defaultProfile_100_female.gif", contact.getPrefSymbol().asURI().toString());
				assertEquals("Strategist and technology enthusiast.",contact.getModel().findStatements(contact.getResource(), NAO.description, Variable.ANY).next().getObject().asLiteral().getValue());
				assertEquals("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#female",contact.getGender().asResource().toString());
				
				PostalAddress address = contact.getAllPostalAddress().next();
				assertEquals("IE",address.getCountry());
				assertEquals("Galway",address.getLocality());
				
				BirthDate date = contact.getAllBirthDate().next();
				assertEquals(1977,date.getBirthDate().get(Calendar.YEAR));	
				assertEquals(5,date.getBirthDate().get(Calendar.MONTH)+1);	
				assertEquals(12,date.getBirthDate().get(Calendar.DATE));	
				
				break;
			}
		}
		assertTrue(annaFound);
	}
	
	@Test
	public void testNoPersonsDeserialize() throws Exception {
		String xml = loadResource("transformer/fitbit_no_person.xml");		
        Collection<PersonContact> contacts = transformer.deserialize(xml, "fitbit", "/person/@me/@all", PersonContact.class);
		
		// there should be 0 contacts
		assertEquals(0, contacts.size());		
	}
	
	@Test
	public void testActivitiesDeserialize() throws Exception {
		String xml = loadResource("transformer/fitbit_activity.xml");
		Collection<Activity> activities = transformer.deserialize(xml, "fitbit", "/activity/@me/@all", Activity.class);
		
		// there should be 5 activities
		assertEquals(5, activities.size());
		
		// checking information of one of the contacts
		boolean activityFound = false;
		for (Activity activity : activities) {
			if ("18120".equals(activity.getModel().findStatements(activity.getResource(), NAO.externalIdentifier, Variable.ANY).next().getObject().asLiteral().getValue())) {
				activityFound = true;
				
				assertEquals("Sailing, boat and board sailing, windsurfing, ice sailing, general",activity.getPrefLabel());
				assertEquals("1291",activity.getModel().findStatements(activity.getResource(), DCON.caloriesExpended, Variable.ANY).next().getObject().asLiteral().getValue());
				assertEquals("3723000",activity.getModel().findStatements(activity.getResource(), DCON.duration, Variable.ANY).next().getObject().asLiteral().getValue());
				assertEquals("0",activity.getModel().findStatements(activity.getResource(), DCON.distanceCovered, Variable.ANY).next().getObject().asLiteral().getValue());
				
				break;
			}
		}
		assertTrue(activityFound);
	}
	
	@Test
	public void testNoActivitiesDeserialize() throws Exception {
		String xml = loadResource("transformer/fitbit_no_activity.xml");
		Collection<Activity> activities = transformer.deserialize(xml, "fitbit", "/activity/@me/@all", Activity.class);
		
		// there should be 0 activities
		assertEquals(0, activities.size());
		
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
