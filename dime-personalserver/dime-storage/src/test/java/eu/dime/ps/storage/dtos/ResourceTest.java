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

package eu.dime.ps.storage.dtos;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import info.aduna.net.UriUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;

import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;

/**
 * Tests {@link Resource}
 * 
 * @author Ismael Rivera
 */
public class ResourceTest extends TestCase {

	private static final Map<URI, String> RENAMING_RULES;
	static {
		RENAMING_RULES = new HashMap<URI, String>();
		RENAMING_RULES.put(NIE.hasPart, "items");
	}		


	@Test
	public void testSerialization() throws JSONException,
	JsonParseException, IOException {
		Person person = (new ModelFactory()).getPIMOFactory().createPerson("urn:ismael");
		person.setPrefLabel("Ismael Rivera");
		person.setCreator(new URIImpl("urn:ismael"));
		person.setPrefSymbol(new URIImpl("http://myphoto.jpg"));
		person.addAltSymbol(new URIImpl("alt:symbol:1"));
		person.addAltSymbol(new URIImpl("alt:symbol:2"));
		person.setGroundingOccurrence(new URIImpl("urn:ismael:super-profile"));
		person.addOccurrence(new URIImpl("urn:ismael:linkedin-profile"));

		Resource resource = new Resource(person,new URIImpl("urn:ismael"));

		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapper.writeValue(baos, resource);
		JSONObject json = new JSONObject(baos.toString());

		assertEquals("urn:ismael", json.get("guid"));
		assertEquals("Ismael Rivera", json.get("name"));
		assertEquals("http://myphoto.jpg", json.get("imageUrl"));
		assertEquals("@me", json.get("userId"));
		assertEquals(2, json.getJSONArray("nao:altSymbol").length());
	}


	@Test
	public void testPoCPersonDeserialization() throws JSONException,
	JsonParseException, IOException {
		String jsonStr = "{\"guid\":\"urn:ismael\",\"userId\":\"@me\",\"nao:altSymbol\":[\"alt:symbol:1\",\"alt:symbol:2\"],\"imageUrl\":\"http://myphoto.jpg\",\"type\":\"person\",\"name\":\"Ismael Rivera\"}";

		ObjectMapper mapper = new ObjectMapper();
		Resource resource = mapper.readValue(jsonStr, Resource.class);
		Person person = resource.asResource(new URIImpl("urn:ismael"), Person.class,new URIImpl("urn:ismael"));

		assertEquals("urn:ismael", person.asURI().toString());
		assertEquals("Ismael Rivera", person.getPrefLabel());
		assertEquals("urn:ismael", person.getCreator_asNode().toString());
		assertEquals(new URIImpl("http://myphoto.jpg"), person.getPrefSymbol().asURI());
		assertEquals(2, person.getAllAltSymbol_as().count());
	}

	@Test
	public void testPoCFileDataObjectDeserialization() throws JSONException,
	JsonParseException, IOException {
		String jsonStr = "{\"guid\":\"urn:file1\",\"type\":\"resource\",\"name\":\"Name File 1\"}";

		ObjectMapper mapper = new ObjectMapper();
		Resource resource = mapper.readValue(jsonStr, Resource.class);
		FileDataObject file = resource.asResource(new URIImpl("urn:file1"), FileDataObject.class,new URIImpl("urn:ismael"));

		assertEquals("urn:file1", file.asURI().toString());


	}

	@Test
	public void testDeserializeDateToISO8601() throws JsonParseException, IOException {
		String jsonStr = "{\"guid\":\"urn:uuid:5cec76f4-a620-4f82-a21b-b59f7fb42b69\",\"type\":\"resource\",\"nfo:fileName\":\"file1.txt\",\"nfo:belongsToContainer\":\"file:/Users\",\"nfo:fileLastModified\":1319892317123}";
		ObjectMapper mapper = new ObjectMapper();
		Resource resource = mapper.readValue(jsonStr, Resource.class);

		FileDataObject file = resource.asResource(FileDataObject.class,new URIImpl("urn:ismael"));
		assertEquals("2011-10-29T12:45:17.123Z",
				file.getAllFileLastModified_asNode().next().asDatatypeLiteral().getValue());
	}

	@Test
	public void testSerializeDateToMilliseconds() throws Exception {
		FileDataObject file = (new ModelFactory()).getNFOFactory().createFileDataObject();
		file.setFileName("file1.txt");
		file.setCreator(new URIImpl("urn:ismael"));
		file.setFileLastModified(new DatatypeLiteralImpl("2011-10-29T14:45:17.123+02:00", XSD._dateTime));

		Resource resource = new Resource(file,new URIImpl("urn:jose"));
		assertEquals(1319892317123L, resource.get("nfo:fileLastModified"));
		assertEquals("urn:ismael",resource.get("userId"));
	}

	@Test
	public void testWriteCorrectDownloadURL() throws Exception {
		FileDataObject file = (new ModelFactory()).getNFOFactory().createFileDataObject();
		file.setFileName("file1.txt");
		file.setCreator(new URIImpl("urn:ismael"));
		file.setDataSource(new URIImpl("urn:ismaelaccount"));
		file.setSharedWith(new URIImpl("urn:annaccount"));		
		file.setSharedBy(new URIImpl("urn:ismaelaccount"));
		file.setFileLastModified(new DatatypeLiteralImpl("2011-10-29T14:45:17.123+02:00", XSD._dateTime));

		Resource resource = new Resource(file,"fakeSAID",new URIImpl("urn:jose"));
		assertEquals(1319892317123L, resource.get("nfo:fileLastModified"));
		assertEquals("urn:ismael",resource.get("userId"));
		assertEquals("/dime-communications/api/dime/rest/fakeSAID/resource/@me/shared/urn:ismaelaccount/urn:annaccount/"+file.asURI().toString(),resource.get("downloadUrl"));

		FileDataObject localFile = (new ModelFactory()).getNFOFactory().createFileDataObject();
		localFile.setFileName("file2.txt");
		localFile.setCreator(new URIImpl("urn:jose"));
		localFile.setFileLastModified(new DatatypeLiteralImpl("2011-10-29T14:45:17.123+02:00", XSD._dateTime));

		Resource localResource = new Resource(localFile,"fakeSAID",new URIImpl("urn:jose"));
		assertEquals(1319892317123L, localResource.get("nfo:fileLastModified"));
		assertEquals("@me",localResource.get("userId"));


		String guid = UriUtil.decodeUri(localFile.asURI().toString());
		String encodedGuid;

		encodedGuid = URLEncoder.encode(guid, "UTF-8");
		assertEquals("/dime-communications/api/dime/rest/fakeSAID/resource/filemanager/"+encodedGuid,localResource.get("downloadUrl"));
	}


	@Test
	public void testDeserializeDatabox() throws Exception {
		Model rModel = RDF2Go.getModelFactory().createModel().open();
		org.ontoware.rdfreactor.schema.rdfs.Resource resource = new org.ontoware.rdfreactor.schema.rdfs.Resource(rModel, new URIImpl("urn:uuid:4fb20703-49bd-4032-9883-1b839cdd8a5f"), false);
		rModel.open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("databox.ttl"),
				Syntax.Turtle, resource.getModel());

		Resource deserializedDatabox = new Resource(resource,"fakeSAID",RENAMING_RULES,new URIImpl("urn:jose"));
		assertNotNull(deserializedDatabox);
		assertEquals(deserializedDatabox.get("guid").toString(),resource.asURI().toString());
		assertEquals(deserializedDatabox.get("userId").toString(),"urn:uuid:b2d0d495-e821-4e47-8479-c6b5ee83dbe3");
		assertEquals(deserializedDatabox.get("name").toString(),"foticos");
		assertEquals(deserializedDatabox.get("type").toString(),"databox");
		assertEquals(deserializedDatabox.get("created").toString(),"1369838052525");
		assertEquals(deserializedDatabox.get("lastModified").toString(),"1369838045410");
		assertEquals(deserializedDatabox.get("imageUrl").toString(),"https://localhost:8443/dime-communications/static/ui/dime/icons/data_box.png");
		ArrayList<String> items = new ArrayList<String>();
		items.add("urn:uuid:87urm361-8tum-1b99-1716-jj8ir9001p4l");
		assertEquals(deserializedDatabox.get("items").toString(),items.toString());
		
	}




	@Test
	public void testDeserializeDataboxHasPart() throws Exception {
		//create the databox

		PrivacyPreference databox = (new ModelFactory()).getPPOFactory().createPrivacyPreference();
		databox.addType(NFO.DataContainer);
		databox.setLabel("DATABOX");
		databox.setPrefLabel("example");


		Resource deserializedDatabox = new Resource(databox,"fakeSAID",RENAMING_RULES,new URIImpl("urn:jose"));

		assertEquals("example",deserializedDatabox.get("name"));
		ArrayList<String> items = new ArrayList<String>();

		assertEquals(items,deserializedDatabox.get("items"));
		assertNull(deserializedDatabox.get("nie:hasPart"));

		//add one file to the databox
		FileDataObject file1 = (new ModelFactory()).getNFOFactory().createFileDataObject();
		file1.setFileName("file1.txt");
		file1.setCreator(new URIImpl("urn:ismael"));
		file1.setDataSource(new URIImpl("urn:ismaelaccount"));
		file1.setSharedBy(new URIImpl("urn:jose"));
		file1.setFileLastModified(new DatatypeLiteralImpl("2011-10-29T14:45:17.123+02:00", XSD._dateTime));

		FileDataObject file2 = (new ModelFactory()).getNFOFactory().createFileDataObject();
		file2.setFileName("file2.txt");
		file2.setCreator(new URIImpl("urn:ismael"));
		file2.setDataSource(new URIImpl("urn:ismaelaccount"));
		file2.setSharedBy(new URIImpl("urn:jose"));
		file2.setFileLastModified(new DatatypeLiteralImpl("2011-10-29T14:45:17.123+02:00", XSD._dateTime));

		databox.getModel().addStatement(databox, NIE.hasPart, file1);		
		databox.addAppliesToResource(file1);


		Resource deserializedDatabox2 = new Resource(databox,"fakeSAID",RENAMING_RULES,new URIImpl("urn:jose"));

		assertEquals("example",deserializedDatabox2.get("name"));
		ArrayList<String> items2 = new ArrayList<String>();
		items2.add(file1.toString());

		assertEquals(items2,deserializedDatabox2.get("items"));
		assertNull(deserializedDatabox2.get("nie:hasPart"));

		//add another file
		databox.getModel().addStatement(databox, NIE.hasPart, file2);		
		databox.addAppliesToResource(file2);

		Resource deserializedDatabox3 = new Resource(databox,"fakeSAID",RENAMING_RULES,new URIImpl("urn:jose"));

		assertEquals("example",deserializedDatabox3.get("name"));
		ArrayList<String> items3 = new ArrayList<String>();
		items3.add(file1.toString());
		items3.add(file2.toString());
		assertEquals(items3,deserializedDatabox3.get("items"));
		assertNull(deserializedDatabox3.get("nie:hasPart"));
	}

}
