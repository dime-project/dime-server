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
