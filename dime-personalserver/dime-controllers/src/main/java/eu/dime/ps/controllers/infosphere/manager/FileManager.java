package eu.dime.ps.controllers.infosphere.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.nfo.FileDataObject;

/**
 * Manager for managing files in the user's infoSphere.
 * 
 * @author Ismael Rivera
 */
public interface FileManager extends InfoSphereManager<FileDataObject> {
	
	boolean exists(String fileUri) throws InfosphereException;
	
	boolean existsByHash(String hash) throws InfosphereException;
	
	Collection<FileDataObject> getAllSharedBy(String personId)
			throws InfosphereException;
	
	InputStream getBinaryStream(String fileUri) throws InfosphereException;

	InputStream getBinaryStreamByHash(String hash) throws InfosphereException;

	FileDataObject add(InputStream inputStream) throws IOException, InfosphereException;

	FileDataObject add(String fileUri, String fileName, InputStream inputStream)
			throws IOException, InfosphereException;

	FileDataObject add(FileDataObject fdo, InputStream inputStream)
			throws IOException, InfosphereException;
	
	FileDataObject update(String fileUri, InputStream inputStream)
			throws IOException, InfosphereException;
	
	FileDataObject update(FileDataObject file, InputStream inputStream)
			throws IOException, InfosphereException;

}