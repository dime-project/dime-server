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

import java.io.File;
import java.io.IOException;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.datamining.exceptions.DataMiningException;

/**
 * Extracts metadata from files.
 * 
 * @author Ismael Rivera (ismael.rivera@deri.org)
 */
public interface FileDataMining {
	
	/**
	 * Extracts metadata about a file from its binary contents.
	 * 
	 * @param fileUri file identifier (eg. file:/home/joedoe/example.txt)
	 * @param file file to be analysed
	 * @return RDF model containing the metadata
	 * @throws DataMiningException if the extraction process fails
	 * @throws IOException if an error occurs while reading from the input stream
	 */
	Model extractFromContent(URI fileUri, File file) throws DataMiningException, IOException;
	
}
