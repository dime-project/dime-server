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

package eu.dime.ps.datamining.impl;

import ie.deri.smile.rdf.util.ModelUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.util.FileUtils;
import eu.dime.ps.datamining.FileDataMining;
import eu.dime.ps.datamining.util.OSDetector;
import eu.dime.ps.semantic.rdf.URIGenerator;

/**
 * It extracts metadata from files using Aperture.
 * For a given file, a filesystem crawler is executed for the file path.
 * 
 * @author Ismael Rivera (ismael.rivera@deri.org)
 */
public class FileDataMiningImpl implements FileDataMining {

	private static final Logger logger = LoggerFactory.getLogger(FileDataMiningImpl.class);

	private TemporaryFileCrawlerHandler crawlerHandler;
	private FileSystemCrawler fileSystemCrawler;
	private FileSystemDataSource source;
	
	public FileDataMiningImpl() {
		try {
			crawlerHandler = new TemporaryFileCrawlerHandler();
		} catch (ModelException e) {
			throw new RuntimeException("FileDataMining cannot be initialized: the creation of the crawler handler has failed.", e);
		}
		fileSystemCrawler = new FileSystemCrawler();
		fileSystemCrawler.setCrawlerHandler(crawlerHandler);
		fileSystemCrawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
		RDFContainerFactoryImpl containerFactory = new RDFContainerFactoryImpl();
		RDFContainer configuration = containerFactory.newInstance("source:datamining");
		this.source = new FileSystemDataSource();
		this.source.setConfiguration(configuration);
	}
	
	@Override
	public Model extractFromContent(URI fileUri, File file) throws IOException {
		this.source.setRootFolder(file.getCanonicalPath());
		fileSystemCrawler.setDataSource(source);

		// does the crawling/extraction
		fileSystemCrawler.crawl();

		// done with the crawling, getting the model and fixing a few things...
		URI tmpUri = toURI(file);
		Model model = crawlerHandler.getRDFContainer(tmpUri).getModel();

		// removes the fileName retrieve from the temporary file
		model.removeStatements(tmpUri, NFO.fileName, Variable.ANY);
		
		// removes the temporary data source metadata
		model.removeStatements(new URIImpl("source:datamining"), Variable.ANY, Variable.ANY);
		model.removeStatements(Variable.ANY, Variable.ANY, new URIImpl("source:datamining"));

		// removes the data about the temp folder
		ClosableIterator<Statement> folders = model.findStatements(tmpUri, NFO.belongsToContainer, Variable.ANY);
		while (folders.hasNext()) {
			URI folder = folders.next().getObject().asURI();
			model.removeStatements(folder, Variable.ANY, Variable.ANY);
			model.removeStatements(Variable.ANY, Variable.ANY, folder);
		}

		// replaces the file path in the temp directory for the given one
		ModelUtils.replaceIdentifier(model, tmpUri, fileUri);
		
		// adds hash to metadata
		String hash = FileUtils.doSHA1Hash(file.toURI());
		URI hashUri = URIGenerator.createNewRandomUniqueURI();
		model.addStatement(fileUri, NFO.hasHash, hashUri);
		model.addStatement(hashUri, NFO.hashValue, hash);
		model.addStatement(hashUri, NFO.hashAlgorithm, "SHA-1");

		return model;
	}
	
	private URI toURI(File file) throws IOException {
		// file.toURI() doesn't not return the canonical path which Aperture resolves internally
		// so the URI is constructed with file.getCanonicalPath() instead
		String filePath = null;
		if (OSDetector.isWindows()) {
			filePath = "file:/"+file.getCanonicalPath().replace("\\", "/");
		} else {
			filePath = "file:"+file.getCanonicalPath();
		}
		return new URIImpl(UriUtil.encodeUri(filePath));
	}
	
	/**
	 * A CrawlerHandler which stores in memory the metadata about the files.
	 * 
	 * @author Ismael Rivera (ismael.rivera@deri.org)
	 */
	private class TemporaryFileCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

		// the ModelSet to store all metadata in
		private ModelSet modelSet;

		// the object responsible for determining a file's MIME type
		private MimeTypeIdentifier mimeTypeIdentifier;

		// the registry holding the ExtractorFactories
		private ExtractorRegistry extractorRegistry;

		public TemporaryFileCrawlerHandler() throws ModelException {
			ModelFactory modelFactory = RDF2Go.getModelFactory();
			modelSet = modelFactory.createModelSet();
			modelSet.open();
			
			// create components for processing file contents
			mimeTypeIdentifier = new MagicMimeTypeIdentifier();
			extractorRegistry = new DefaultExtractorRegistry();
		}

		@Override
		public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
			return this;
		}

		public RDFContainer getRDFContainer(URI uri) {
			Model model = modelSet.getModel(uri);
			model.open();
			return new RDFContainerImpl(model, uri);
		}

		@Override
		public void crawlStarted(Crawler crawler) {
			logger.debug("Crawling started...");
		}

		@Override
		public void crawlStopped(Crawler crawler, ExitCode exitCode) {
			logger.debug("Crawling completed (exit code: "+exitCode.toString()+")");
		}

		@Override
		public void accessingObject(Crawler crawler, String url) {
			logger.debug("Processing file "+url+"...");
		}

		@Override
		public void objectNew(Crawler crawler, DataObject object) {
			if (object instanceof FileDataObject) {
				process((FileDataObject) object);
			}
			object.dispose();
		}

		@Override
		public void objectChanged(Crawler crawler, DataObject object) {
			// TODO Auto-generated method stub
			logger.debug("not yet implemented!");
		}

		@Override
		public void objectNotModified(Crawler crawler, String url) {
			// TODO Auto-generated method stub
			logger.debug("not yet implemented!");
		}

		@Override
		public void objectRemoved(Crawler crawler, String url) {
			// TODO Auto-generated method stub
			logger.debug("not yet implemented!");
		}

		@Override
		public void clearStarted(Crawler crawler) {
            // no-op
		}

		@Override
		public void clearingObject(Crawler crawler, String url) {
            // no-op
		}

		@Override
		public void clearFinished(Crawler crawler, ExitCode exitCode) {
            // no-op
		}
		
		private void process(FileDataObject object) {
			URI id = object.getID();
			try {
				// Create a buffer around the object's stream large enough to be able to reset the stream
				// after MIME type identification has taken place. Add some extra to the minimum array
				// length required by the MimeTypeIdentifier for safety.
				int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
				int bufferSize = Math.max(minimumArrayLength, 8192);
				BufferedInputStream buffer = new BufferedInputStream(object.getContent(), bufferSize);
				buffer.mark(minimumArrayLength + 10); // add some for safety

				// apply the MimeTypeIdentifier
				byte[] bytes = IOUtil.readBytes(buffer, minimumArrayLength);
				String mimeType = mimeTypeIdentifier.identify(bytes, null, id);

				if (mimeType != null) {
					// add the mime type to the metadata
					RDFContainer metadata = object.getMetadata();
					metadata.add(NIE.mimeType, mimeType);

					// apply an Extractor if available
					buffer.reset();

					Set extractors = extractorRegistry.get(mimeType);
					if (!extractors.isEmpty()) {
						ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
						Extractor extractor = factory.get();
						extractor.extract(id, buffer, null, mimeType, metadata);
					}
				}
			}
			catch (Exception e) {
				logger.error("ExtractorException while processing "+id, e);
			}
		}

	}
	
}
