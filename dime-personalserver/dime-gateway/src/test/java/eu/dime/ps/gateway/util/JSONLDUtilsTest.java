package eu.dime.ps.gateway.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NIE;

import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.jsonldjava.core.JSONLDProcessingError;

import eu.dime.ps.dto.Databox;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nfo.DataContainer;

/**
 * Tests {@link JSONLDUtils}.
 * 
 * @author Ismael Rivera
 */
public class JSONLDUtilsTest {

	@Test
	public void shouldDeserializeJSONLD() throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		String json = "{`@graph`:[{`@id`:`urn:uuid:4182dcd4-a510-4b0c-8145-271a3dcfe455`,`@type`:[`http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactMedium`,`http://www.w3.org/2000/01/rdf-schema#Resource`,`http://www.semanticdesktop.org/ontologies/2007/03/22/nco#EmailAddress`],`http://www.semanticdesktop.org/ontologies/2007/03/22/nco#emailAddress`:`test@email.com`},{`@id`:`urn:uuid:d030b056-cddd-4a8b-9828-a54dbca6495e`,`@type`:`http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact`,`http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasEmailAddress`:{`@id`:`urn:uuid:4182dcd4-a510-4b0c-8145-271a3dcfe455`},`http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefLabel`:`Test`}]}".replace('`', '"');
		PersonContact resource = JSONLDUtils.deserialize(json, PersonContact.class);

		Model metadata = resource.getModel();
		URI profile = new URIImpl("urn:uuid:d030b056-cddd-4a8b-9828-a54dbca6495e");
		URI email = new URIImpl("urn:uuid:4182dcd4-a510-4b0c-8145-271a3dcfe455");
		
		assertEquals(profile, profile.asURI());
		assertTrue("Must contain a nco:PersonContact resource", metadata.contains(profile, RDF.type, NCO.PersonContact));
		assertEquals("Test", ModelUtils.findObject(metadata, profile, NAO.prefLabel).asLiteral().getValue());
		assertTrue("Must contain a nco:EmailAddress resource", metadata.contains(email, RDF.type, NCO.EmailAddress));
		assertEquals("test@email.com", ModelUtils.findObject(metadata, email, NCO.emailAddress).asLiteral().getValue());
	}
	
	@Test
	public void shouldDeserializedJSONLDWithContext() throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		String json = "{`@context`:{`nao`:`http://www.semanticdesktop.org/ontologies/2007/08/15/nao#`,`nfo`:`http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#`,`nie`:`http://www.semanticdesktop.org/ontologies/2007/01/19/nie#`,`nco`:`http://www.semanticdesktop.org/ontologies/2007/03/22/nco#`,`dlpo`:`http://www.semanticdesktop.org/ontologies/2011/10/05/dlpo#`,`ppo`:`http://vocab.deri.ie/ppo#`},`@graph`:[{`@id`:`urn:uuid:4182dcd4-a510-4b0c-8145-271a3dcfe455`,`@type`:[`nco:ContactMedium`,`http://www.w3.org/2000/01/rdf-schema#Resource`,`nco:EmailAddress`],`nco:emailAddress`:`test@email.com`},{`@id`:`urn:uuid:6c329b90-737d-46cd-ae2f-eb86cddea172`,`@type`:`nco:PersonContact`,`nco:hasEmailAddress`:{`@id`:`urn:uuid:4182dcd4-a510-4b0c-8145-271a3dcfe455`},`nco:hasPersonName`:{`@id`:`urn:uuid:ca45458d-c054-4cf1-add3-db4ab2c2acc4`},`nao:prefLabel`:`Test Test`},{`@id`:`urn:uuid:ca45458d-c054-4cf1-add3-db4ab2c2acc4`,`@type`:[`http://www.w3.org/2000/01/rdf-schema#Resource`,`nco:Name`,`nco:PersonName`],`nco:fullname`:`Test Test`,`nco:nameFamily`:`Test`,`nco:nameGiven`:`Test`,`nco:nickname`:`test`}]}".replace('`', '"');
		PersonContact resource = JSONLDUtils.deserialize(json, PersonContact.class);

		Model metadata = resource.getModel();
		URI profile = new URIImpl("urn:uuid:6c329b90-737d-46cd-ae2f-eb86cddea172");
		URI email = new URIImpl("urn:uuid:4182dcd4-a510-4b0c-8145-271a3dcfe455");
		
		assertEquals(profile, profile.asURI());
		assertTrue("Must contain a nco:PersonContact resource", metadata.contains(profile, RDF.type, NCO.PersonContact));
		assertEquals("Test Test", ModelUtils.findObject(metadata, profile, NAO.prefLabel).asLiteral().getValue());
		assertTrue("Must contain a nco:EmailAddress resource", metadata.contains(email, RDF.type, NCO.EmailAddress));
		assertEquals("test@email.com", ModelUtils.findObject(metadata, email, NCO.emailAddress).asLiteral().getValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseErrorTwoObjectsSameType() throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		String json = "{`@context`:{`nao`:`http://www.semanticdesktop.org/ontologies/2007/08/15/nao#`,`nfo`:`http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#`,`nie`:`http://www.semanticdesktop.org/ontologies/2007/01/19/nie#`,`nco`:`http://www.semanticdesktop.org/ontologies/2007/03/22/nco#`,`dlpo`:`http://www.semanticdesktop.org/ontologies/2011/10/05/dlpo#`,`ppo`:`http://vocab.deri.ie/ppo#`},`@graph`:[{`@id`:`urn:uuid:4182dcd4-a510-4b0c-8145-271a3dcfe455`,`@type`:[`nco:PersonContact`,`http://www.w3.org/2000/01/rdf-schema#Resource`]},{`@id`:`urn:uuid:6c329b90-737d-46cd-ae2f-eb86cddea172`,`@type`:`nco:PersonContact`}]}".replace('`', '"');
		JSONLDUtils.deserialize(json, PersonContact.class);
	}
	
	@Test
	public void shouldSerializeAndDeserialize() throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		ModelFactory factory = new ModelFactory();
		URI file = new URIImpl("urn:file1");
		DataContainer databox = factory.getNFOFactory().createDataContainer();
		databox.addPart(file);
		databox.setPrefLabel("My Databox");
		
		String json = JSONLDUtils.serializeAsString(databox);
		DataContainer otherDatabox = JSONLDUtils.deserialize(json, DataContainer.class);
		
		assertEquals(databox.asURI(), otherDatabox.asURI());
		assertEquals("My Databox", ModelUtils.findObject(otherDatabox.getModel(), otherDatabox, NAO.prefLabel).asLiteral().getValue());
		assertEquals(file, ModelUtils.findObject(otherDatabox.getModel(), otherDatabox, NIE.hasPart).asURI());
	}
	
	@Test
	public void shouldSerializePrivacyLevelToFormatedDouble() throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		ModelFactory factory = new ModelFactory();
		URI file = new URIImpl("urn:file1");
		DataContainer databox = factory.getNFOFactory().createDataContainer();
		databox.addPart(file);
		databox.setPrefLabel("My Databox");
		databox.addPrivacyLevel(0.5d);
		
		String json = JSONLDUtils.serializeAsString(databox);
		DataContainer otherDatabox = JSONLDUtils.deserialize(json, DataContainer.class);
		
		assertEquals(databox.asURI(), otherDatabox.asURI());
		assertEquals("My Databox", ModelUtils.findObject(otherDatabox.getModel(), otherDatabox, NAO.prefLabel).asLiteral().getValue());
		assertEquals(file, ModelUtils.findObject(otherDatabox.getModel(), otherDatabox, NIE.hasPart).asURI());
		String privacy = ModelUtils.findObject(otherDatabox.getModel(), otherDatabox, NAO.privacyLevel).asDatatypeLiteral().getValue();
		assertEquals("5,0E-1",privacy);
		Databox resource = new Databox(otherDatabox,new URIImpl("urn:borja"));
		assertEquals("0.5",resource.get("nao:privacyLevel").toString());
	}
	
	
}
