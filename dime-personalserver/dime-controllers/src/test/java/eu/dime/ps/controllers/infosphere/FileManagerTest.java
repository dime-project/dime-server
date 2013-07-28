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

package eu.dime.ps.controllers.infosphere;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NEXIF;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.util.FileUtils;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.storage.util.CMSInitHelper;

public class FileManagerTest extends InfoSphereManagerTest {

	@Autowired
	private FileManager fileManager;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		pimoService.clear();
	}
	
	@Test
	public void testGetFiles() throws Exception {
		loadData("files-data.nt");
		assertEquals(12, fileManager.getAll().size());
	}
	
	@Test
	public void testGetFile() throws Exception {
		loadData("files-data.nt");
		FileDataObject file = fileManager.get("file:/home/ismriv/example/dir1/FAST-presentation.pptx");
		assertEquals("FAST-presentation.pptx", file.getFileName());
	}

	@Test
	public void testAddFile() throws Exception {
		FileDataObject file = modelFactory.getNFOFactory().createFileDataObject();
		file.setFileName("file-example");
		file.setFileSize(9876L);
		
		fileManager.add(file);
		assertEquals(1, fileManager.getAll().size());
		assertEquals("file-example", fileManager.getAll().iterator().next().getFileName());
	}
	
	@Test
	public void testUpdateFile() throws Exception {
		FileDataObject file = modelFactory.getNFOFactory().createFileDataObject();
		file.setFileName("file-example");
		fileManager.add(file);
		assertEquals("file-example", fileManager.get(file.asURI().toString()).getFileName());
		
		file.setFileName("file-updated");
		fileManager.update(file);
		assertEquals("file-updated", fileManager.get(file.asURI().toString()).getFileName());
	}

	@Test
	public void testGetByCreatorFile() throws Exception {
		FileDataObject file = modelFactory.getNFOFactory().createFileDataObject();
		Person creator = modelFactory.getPIMOFactory().createPerson();
		creator.setPrefLabel("creator1");
		file.setFileName("file-example");
		file.setFileSize(9876L);
		file.setCreator(creator);
		fileManager.add(file);
		
		assertEquals(1, fileManager.getAllByCreator(creator).size());
		assertEquals("file-example", fileManager.getAll().iterator().next().getFileName());
	}
	
	
	@Ignore
	@Test
	public void testRemoveFile() throws Exception {
		loadData("files-data.nt");
		//FIXME: loadData is not adding file (binary) to DataStore, causing FileNotFoundException
		fileManager.remove("file:/home/ismriv/example/dir1/FAST-presentation.pptx");
		assertFalse(fileManager.exists("file:/home/ismriv/example/dir1/FAST-presentation.pptx"));
	}
	
	@Test
	public void testRemoveFileWithBinary() throws Exception {
		URI file = new URIImpl("urn:test:1");
		fileManager.add(file.toString(), "flower.jpg", this.getClass().getClassLoader().getResourceAsStream("infosphere/flower.jpg"));
		fileManager.remove("urn:test:1");
		assertFalse(fileManager.exists("urn:test:1"));
	}
	
	@Test
	public void testAddFileWithBinary() throws Exception {
		URI file = new URIImpl("urn:test:1");
		fileManager.add(file.toString(), "flower.jpg", this.getClass().getClassLoader().getResourceAsStream("infosphere/flower.jpg"));
		InputStream binary = fileManager.getBinaryStream(file.toString());
		assertTrue(FileUtils.equals(binary, this.getClass().getClassLoader().getResourceAsStream("infosphere/flower.jpg")));
	}
	
	@Test
	public void testMetadataExtractionFromJpegFile() throws Exception {
		URI file = new URIImpl("urn:test:1");
		fileManager.add(file.toString(), "flower.jpg", this.getClass().getClassLoader().getResourceAsStream("infosphere/flower.jpg"));
		Model fileModel = fileManager.get("urn:test:1").getModel();

		// basic metadata & hash
		FileDataObject saved = fileManager.get(file.toString());
		assertEquals("flower.jpg", saved.getPrefLabel());
		assertEquals("flower.jpg", saved.getFileName());
		assertTrue(fileModel.contains(file, NAO.lastModified, Variable.ANY));
		assertTrue(fileModel.contains(file, NIE.mimeType, "image/jpeg"));
		assertTrue(fileModel.contains(file, NFO.hasHash, Variable.ANY));
		assertEquals(211258L, saved.getFileSize().longValue());
		
		// advanced metadata extraction
		assertTrue(fileModel.contains(file, NEXIF.make, "OLYMPUS OPTICAL CO.,LTD"));
		assertTrue(fileModel.contains(file, NEXIF.height, "480"));
		assertTrue(fileModel.contains(file, NEXIF.width, "640"));
	}

	@Test
	public void testMetadataExtractionFromPdfFile() throws Exception {
		URI file = new URIImpl("urn:test:1");
		fileManager.add(file.toString(), "DIGITAL_ME_AnnualPublicReport_2010.pdf", this.getClass().getClassLoader().getResourceAsStream("infosphere/DIGITAL_ME_AnnualPublicReport_2010.pdf"));
		Model fileModel = fileManager.get("urn:test:1").getModel();

		// basic metadata & hash
		FileDataObject saved = fileManager.get(file.toString());
		assertEquals("DIGITAL_ME_AnnualPublicReport_2010.pdf", saved.getPrefLabel());
		assertEquals("DIGITAL_ME_AnnualPublicReport_2010.pdf", saved.getFileName());
		assertTrue(fileModel.contains(file, NAO.lastModified, Variable.ANY));
		assertTrue(fileModel.contains(file, NIE.mimeType, "application/pdf"));
		assertEquals(78958L, saved.getFileSize().longValue());
	}

	@Test
	public void testOverrideWithExtractedMetadata() throws Exception {
		URI fileUri = new URIImpl("urn:test:1");
		Model fileModel = RDF2Go.getModelFactory().createModel().open();
		fileModel.addStatement(fileUri, NFO.fileLastModified, new DatatypeLiteralImpl("2000-01-01T00:00:00.000Z", XSD._dateTime));
		fileModel.addStatement(fileUri, NFO.fileSize, new DatatypeLiteralImpl("0", XSD._long));

		FileDataObject file = new FileDataObject(fileModel, fileUri, false);
		fileManager.add(file, this.getClass().getClassLoader().getResourceAsStream("infosphere/flower.jpg"));

		// basic metadata & hash
		FileDataObject saved = fileManager.get(fileUri.toString());
		assertEquals(211258L, saved.getFileSize().longValue());
		Node fileLastModified = ModelUtils.findObject(saved.getModel(), fileUri, NFO.fileLastModified);
		assertNotSame(new DatatypeLiteralImpl("2000-01-01T00:00:00.000Z", XSD._dateTime), fileLastModified);
	}

	@Test
	public void testCreateThumbnail() throws Exception {
		URI file = new URIImpl("urn:test:1");
		fileManager.add(file.toString(), "flower.jpg", this.getClass().getClassLoader().getResourceAsStream("infosphere/flower.jpg"));
		Model fileModel = fileManager.get("urn:test:1").getModel();
		
		// thumbnail exists as the prefSymbol of the image
		Node thumbnail = ModelUtils.findObject(fileModel, file, NAO.prefSymbol);
		assertNotNull(thumbnail);
		
		// the binaries for the thumbnail were generated
		assertNotNull(fileManager.getBinaryStream(thumbnail.toString()));
	}
	
	@AfterClass
	public static void cleanUp() throws IOException{
		// TODO:destroying of teststore should be optimized
		String os = System.getProperty("os.name").toLowerCase();
		if (!(os.indexOf("win") >= 0)){ //HACK: skip deletion of files on windows
			String path = CMSInitHelper.getCMSFolder() + File.separator + "12345";
			org.apache.commons.io.FileUtils.deleteDirectory(new File(path));	
		}
	}

}
