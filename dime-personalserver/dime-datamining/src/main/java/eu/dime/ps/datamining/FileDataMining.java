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
