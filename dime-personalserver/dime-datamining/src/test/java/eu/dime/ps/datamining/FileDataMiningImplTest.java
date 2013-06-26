package eu.dime.ps.datamining;

import ie.deri.smile.vocabulary.NIE;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.vocabulary.NFO;

import eu.dime.ps.datamining.impl.FileDataMiningImpl;

public class FileDataMiningImplTest extends TestCase {

	private FileDataMining fileDataMining = new FileDataMiningImpl();
	
	@Test
	public void testFileMetadataExtraction() throws Exception {
		URI testUri = new URIImpl("urn:test");
		File file = new File(this.getClass().getClassLoader().getResource("Construction_plan_presentation.ppt").toURI()); 
		Model fileModel = fileDataMining.extractFromContent(testUri, file);

		assertTrue(fileModel.contains(testUri, RDF.type, NFO.FileDataObject));
		assertTrue(fileModel.contains(testUri, RDF.type, NFO.FileDataObject));
		assertFalse(fileModel.contains(testUri, NIE.rootElementOf, Variable.ANY));
		assertTrue(fileModel.contains(testUri, NIE.plainTextContent, Variable.ANY));
		assertTrue(fileModel.contains(testUri, NFO.hasHash, Variable.ANY));
	}
	
}
