package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.util.FileUtils;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.util.ImageUtils;
import eu.dime.ps.datamining.FileDataMining;
import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.NAOFactory;
import eu.dime.ps.semantic.model.NFOFactory;
import eu.dime.ps.semantic.model.nao.Symbol;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.query.impl.BasicQuery;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.storage.datastore.DataStore;
import eu.dime.ps.storage.datastore.impl.DataStoreProvider;

/**
 * Implementation of file manager which brings together three
 * related components dealing with files management:
 * <ul>
 * <li>ResourceStore: it manages all resources including files, and acts
 * as the main repository of the files in the user's infosphere.
 * <li>DataStore: it stores the file contents.</li>
 * <li>FileDataMining: it extracts metadata of the contents of the files.</li>
 * </ul>
 * 
 * @author Ismael Rivera
 * @author Marcel Heupel
 */
public class FileManagerImpl extends InfoSphereManagerBase<FileDataObject> implements FileManager {
	
	private static final Logger logger = LoggerFactory.getLogger(FileManagerImpl.class);
	
	public static final int DEFAULT_WIDTH = 600; // in pixels
	
	private FileDataMining fileDataMining;
	
	private DataStore dataStore;
		
	@Autowired
	private DataStoreProvider dataStoreProvider;
	
	private final NFOFactory nfoFactory;
	private final NAOFactory naoFactory;

	public FileManagerImpl() {
		ModelFactory modelFactory = new ModelFactory();
		this.nfoFactory = modelFactory.getNFOFactory();
		this.naoFactory = modelFactory.getNAOFactory();
	}
	
	public void setFileDataMining(FileDataMining fileDataMining) {
		this.fileDataMining = fileDataMining;
	}
	
	public DataStore getDataStore() {
		if (dataStore == null){
			dataStore = dataStoreProvider.getTenantStore(TenantContextHolder.getTenant().longValue());
		}
		return dataStore;
	}

	@Override
	public boolean exists(String fileUri) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.exists(new URIImpl(fileUri));
	}

	@Override
	public boolean existsByHash(String hash) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(FileDataObject.class)
				.where(NFO.hasHash).is(BasicQuery.X)
				.where(BasicQuery.X, NFO.hashValue, hash)
				.count() > 0;
	}

	@Override
	public Collection<FileDataObject> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}

	@Override
	public Collection<FileDataObject> getAll(List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(FileDataObject.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(RDF.type).isNot(NFO.Folder) // to filter out nfo:Folders
				.results();
	}

	@Override
	public Collection<FileDataObject> getAllSharedBy(String personId)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(FileDataObject.class)
				.distinct()
				.where(RDF.type).isNot(NFO.Folder) // to filter out nfo:Folders
				.where(NSO.sharedBy).is(new URIImpl(personId))
				.results();
	}

	@Override
	public FileDataObject get(String fileId) throws InfosphereException {
		return get(fileId, new ArrayList<URI>(0));
	}
	
	@Override
	public FileDataObject get(String fileId, List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			return resourceStore.get(new URIImpl(fileId), FileDataObject.class,
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("File "+fileId+" not found.", e);
		}
	}

	@Override
	public InputStream getBinaryStream(String fileUri) throws InfosphereException {
//		if (!resourceStore.exists(fileUri))
//			throw new InfosphereException(fileUri+" was not found in the resource store.");
		try {
			return getDataStore().get(fileUri);
		} catch (FileNotFoundException e) {
			throw new InfosphereException("cannot retrieve contents for file "+fileUri, e);
		}
	}

	public InputStream getBinaryStreamByHash(String hash) throws InfosphereException {
		try {
			return getDataStore().getByHash(hash);
		} catch (FileNotFoundException e) {
			throw new InfosphereException("cannot retrieve contents for file with hash "+hash, e);
		}
	}

	@Override
	public void add(FileDataObject fdo) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			resourceStore.create(fdo);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("File "+fdo.asResource()+" not found.", e);
		}
	}

	/**
	 * 
	 * @param fileUri
	 * @param inputStream
	 * @return updated FileDataObject
	 * @throws IOException
	 * @throws InfosphereException 
	 */
	@Override
	public FileDataObject add(String fileUri, String fileName, InputStream inputStream)
		throws IOException, InfosphereException {
		FileDataObject fdo = nfoFactory.createFileDataObject(fileUri);
		fdo.setPrefLabel(fileName);
		fdo.setFileName(fileName);
		return this.add(fdo, inputStream);
	}
	
	/**
	 * 
	 * @param inputStream
	 * @return updated FileDataObject
	 * @throws IOException
	 * @throws InfosphereException 
	 */
	@Override
	public FileDataObject add(InputStream inputStream) throws IOException, InfosphereException {
		return this.add(nfoFactory.createFileDataObject(), inputStream);
	}
	
	/**
	 * 
	 * @param fdo
	 * @param inputStream
	 * @return updated FileDataObject
	 * @throws IOException
	 * @throws InfosphereException 
	 */
	@Override
	public FileDataObject add(FileDataObject fdo, InputStream inputStream)
			throws IOException, InfosphereException {

		ResourceStore resourceStore = getResourceStore();
		
		// this is a reusable input stream, but it was throwing 'marking not supported'
		// in some systems, so instead write stream to a temp file and create a new 
		// input stream each time
//		inputStream = new ResetOnCloseInputStream(inputStream);
		
		// writes stream to temporary file and calculate its checksum/hash
		File file = writeToTempFile(inputStream);
		try {
			String hash = extractMetadata(fdo.getModel(), fdo.asURI(), file);
			createThumbnail(fdo, new BufferedInputStream(new FileInputStream(file)));
			resourceStore.create(fdo);
			getDataStore().addFile(hash, fdo.asURI().toString(), 
					new BufferedInputStream(new FileInputStream(file)));
		} catch (ResourceExistsException e) {
			throw new InfosphereException("The file already exist - cannot add file "+fdo.asURI() +" ERROR: " + e.getMessage(), e);
		} catch (DataMiningException e) {
			throw new InfosphereException("cannot add file "+fdo.asURI()+": an error occurred while extracting its metadata", e);
		} finally {
			file.delete();
		}

		return fdo;
	}

	@Override
	public void update(FileDataObject fdo) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			resourceStore.update(fdo, true);
		} catch (NotFoundException e) {
			throw new InfosphereException("File "+fdo.asResource()+" not found.", e);
		}
	}

	@Override
	public FileDataObject update(String fileUri, InputStream inputStream)
			throws IOException, InfosphereException {
		return this.update(nfoFactory.createFileDataObject(fileUri), inputStream);
	}

	@Override
	public FileDataObject update(FileDataObject fdo, InputStream inputStream)
			throws IOException, InfosphereException {
		
		ResourceStore resourceStore = getResourceStore();

		// this is a reusable input stream, but it was throwing 'marking not supported'
		// in some systems, so instead write stream to a temp file and create a new 
		// input stream each time
//		inputStream = new ResetOnCloseInputStream(inputStream);

		// writes stream to temporary file and calculate its checksum/hash
		File file = writeToTempFile(inputStream);

		try {
			String hash = extractMetadata(fdo.getModel(), fdo.asURI(), file);
			updateThumbnail(fdo, new BufferedInputStream(new FileInputStream(file)));
			resourceStore.update(fdo, true);
			getDataStore().update(hash, fdo.asURI().toString(), 
					new BufferedInputStream(new FileInputStream(file)));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot update file "+fdo.asURI(), e);
		} catch (DataMiningException e) {
			throw new InfosphereException("cannot update file "+fdo.asURI()+": an error occurred while extracting its metadata", e);
		} finally {
			file.delete();
		}

		return fdo;
	}

	@Override
	public void remove(String fileUri) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			// removes thumbnail
			Node thumbnail = ModelUtils.findObject(resourceStore.getTripleStore(), new URIImpl(fileUri), NAO.prefSymbol);
			if (thumbnail != null) {
				getDataStore().delete(thumbnail.toString());
			}
			
			// removes file
			resourceStore.remove(new URIImpl(fileUri));
			getDataStore().delete(fileUri);
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot delete file "+fileUri, e);
		} catch (FileNotFoundException e) {
			throw new InfosphereException("cannot delete file, because not found. "+fileUri, e);

		}
	}

	/**
	 * Writes the content of an input stream into a file in the system's temp directory
	 */
	private synchronized File writeToTempFile(InputStream inputStream) throws IOException {
		String tmpDir = System.getProperty("java.io.tmpdir");
		if (tmpDir == null) {
			throw new IOException("OS current temporary directory not found.");
		}
		tmpDir = tmpDir.endsWith(File.separator) ?
				tmpDir : tmpDir.concat(File.separator);
		String tmpFile = tmpDir + UUID.randomUUID().toString();
		OutputStream outputstream = new FileOutputStream(new File(tmpFile));
		IOUtils.copy(inputStream, outputstream);
		inputStream.close();
		outputstream.close();
		return new File(tmpFile);
	}

	/**
	 * Adds file metadata to file model, and returns the hash (SHA-1) of the file contents
	 */
	private String extractMetadata(Model fileModel, URI fileUri, File file)
			throws DataMiningException, IOException, InfosphereException {
		
		TripleStore tripleStore = getTripleStore();
		
		// extract metadata from the binary contents of the file
		Model extracted = fileDataMining.extractFromContent(fileUri, file);
		
		// if extracted metadata contains a property X, the metadata for that
		// property X is removed from the given RDF in fileModel;
		// in other words, extracted metadata overrides given data in fileModel 
		ClosableIterator<Statement> statements = extracted.findStatements(fileUri, Variable.ANY, Variable.ANY);
		while (statements.hasNext()) {
			URI predicate = statements.next().getPredicate();
			
			// remove the triples for the given predicate
			fileModel.removeStatements(fileUri, predicate, Variable.ANY);
			
			// remove the triples all predicate's subproperties
			ClosableIterator<Statement> it = tripleStore.findStatements(Variable.ANY, Variable.ANY, RDFS.subPropertyOf, predicate);
			while (it.hasNext()) {
				URI property = it.next().getSubject().asURI();
				if (!property.equals(predicate)) {
					fileModel.removeStatements(fileUri, property, Variable.ANY);
				}
			}
			it.close();
		}
		statements.close();
		fileModel.addAll(extracted.iterator());

		// nie:title & nfo:fileName are both subproperties of nao:prefLabel,
		// not both can be provided, so nie:title is removed for all files
		fileModel.removeStatements(fileUri, NIE.title, Variable.ANY);

		// returns hash from metadata
		String hash = null;
		URI hashUri = ModelUtils.findObject(fileModel, fileUri, NFO.hasHash).asURI();
		if (hashUri != null) {
			hash = ModelUtils.findObject(fileModel, hashUri, NFO.hashValue).asLiteral().getValue();
		} else {
			logger.warn("No hash value found for file "+fileUri);
		}
			
		return hash;
	}

	/**
	 * It creates a thumbnail as nao:prefSymbol of a given file.
	 */
	private void createThumbnail(FileDataObject fdo, InputStream inputStream) {
		Symbol thumbnail = null;
		String mimeType = null;
		String format = null;
		try {
			mimeType = ModelUtils.findObject(fdo.getModel(), fdo.asResource(), NIE.mimeType).asLiteral().getValue();
			format = ImageUtils.getFormatForMimetype(mimeType);
			
			if (format == null) {
				logger.debug("Cannot create thumbnail, mimeType "+mimeType+" is not supported.");
			} else {
				thumbnail = naoFactory.createSymbol();

				// create thumbnail image
				BufferedImage image = ImageUtils.createThumbnail(inputStream, DEFAULT_WIDTH);
				
				// dataStore request a input stream, but ImageIO only writes
				// an Image to an output stream, which is passed later on
				// to the required input stream
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				ImageIO.write(image, format, stream);
				InputStream binaryStream = new ResetOnCloseInputStream(
						new ByteArrayInputStream(stream.toByteArray()));
				
				String hash = FileUtils.doSHA1Hash(binaryStream);
				
				// saves thumbnail file
				getDataStore().addFile(hash, thumbnail.toString(), binaryStream);
				
				// sets thumbnail as prefSymbol
				fdo.setPrefSymbol(thumbnail);
			}
			
		} catch (Exception e) {
			logger.error("cannot create thumbnail for file "+fdo.asResource(), e);
		}
	}
	
	/**
	 * It updates the thumbnail as nao:prefSymbol of a given file.
	 */
	private void updateThumbnail(FileDataObject fdo, InputStream inputStream) {
		Symbol thumbnail = null;
		String mimeType = null;
		String format = null;
		try {
			mimeType = ModelUtils.findObject(fdo.getModel(), fdo.asResource(), NIE.mimeType).asLiteral().getValue();
			format = ImageUtils.getFormatForMimetype(mimeType);
			
			if (format == null) {
				logger.debug("Cannot create thumbnail, mimeType "+mimeType+" is not supported.");
			} else {
				// TODO only re-create the thumbnail if the file is different
				// from the known one (which should have a thumbnail)

				thumbnail = fdo.getPrefSymbol();
				if (thumbnail == null) {
					thumbnail = naoFactory.createSymbol();
				}
				
				// create thumbnail image
				BufferedImage image = ImageUtils.createThumbnail(inputStream, DEFAULT_WIDTH);

				// dataStore request a input stream, but ImageIO only writes
				// an Image to an output stream, which is passed later on
				// to the required input stream
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				ImageIO.write(image, format, stream);
				InputStream binaryStream = new ResetOnCloseInputStream(
						new ByteArrayInputStream(stream.toByteArray()));

				String hash = FileUtils.doSHA1Hash(binaryStream);

				// saves thumbnail file
				getDataStore().addFile(hash, thumbnail.toString(), binaryStream);
			}
		} catch (Exception e) {
			logger.error("cannot create thumbnail for file "+fdo.asResource(), e);
		} 
	}
	
	/**
	 * This class allows the reutilization of an InputStream,
	 * by forcing a reset when close() is called.
	 * http://stackoverflow.com/questions/924990/how-to-cache-inputstream-for-multiple-use
	 */
	private class ResetOnCloseInputStream extends InputStream {

		private final InputStream decorated;

		public ResetOnCloseInputStream(InputStream anInputStream) {
			if (!anInputStream.markSupported()) {
				throw new IllegalArgumentException("marking not supported");
			}
			
			anInputStream.mark(1 << 24); // magic constant: BEWARE
			decorated = anInputStream;
		}

		@Override
		public void close() throws IOException {
			decorated.reset();
		}

		@Override
		public int read() throws IOException {
			return decorated.read();
		}
	}
	
}
