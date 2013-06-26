package eu.dime.ps.datamining.crawler.impl;

import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import info.aduna.io.IOUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.mail.DataObjectFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.mbox.MboxDataSource;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.FileExtractor;
import org.semanticdesktop.aperture.extractor.FileExtractorFactory;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.extractor.util.ThreadedExtractorWrapper;
import org.semanticdesktop.aperture.extractor.xmp.XMPExtractorFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;


/**
 *  
 * @author Jeremy Debattista
 */
public class MailExtractor implements RDFContainerFactory{

	//-- Properties needed to set URIs and mail content --//
	protected String folderURI;
	protected String mboxURI;
	protected String emailURI;
	protected String content;
	//protected String hashValue;

	//-- Properties needed for extracting data from attachments --//
	protected MimeTypeIdentifier mimeTypeIdentifier;
	protected ExtractorRegistry extractorRegistry;
	protected ExtractorFactory xmpExtractorFactory;

	//-- RDFContainter and DataSource properties to be used for crawling and storing emails --//
	protected RDFContainerFactoryImpl factory = null;
	protected RDFContainer configuration = null;
	protected MboxDataSource source = null;

	public MailExtractor(String folderURI, String mboxURI, String emailURI, String content){

		//-- Requested Parameters  --//
		this.folderURI = folderURI;
		this.mboxURI = mboxURI;
		this.emailURI = emailURI;
		this.content = content;
		//this.hashValue = hashValue;

		//-- Initialisation of other properties --//
		extractorRegistry = new DefaultExtractorRegistry();
		xmpExtractorFactory = new XMPExtractorFactory();
		mimeTypeIdentifier = new MagicMimeTypeIdentifier();

		//RDF source and factory initalization
		this.factory = new RDFContainerFactoryImpl();
		this.configuration = factory.newInstance(mboxURI);

		this.source = new MboxDataSource();
		source.setConfiguration(configuration);
	}

	public Model extract() throws IOException, MessagingException{
		MimeMessage msg = new MimeMessage(null, new ByteArrayInputStream(content.getBytes()));

		Model emailModel = this.crawlSingleMessage(msg, emailURI, new URIImpl(folderURI), source);

		source.dispose();
		return emailModel;
	}


	private Model crawlSingleMessage(MimeMessage message, String uri, URI folderUri, DataSource source) throws MessagingException, IOException{
		DataObjectFactory dataObjectFactory = null;
		Model model = null;
		try
		{
			//TODO: uri for email hash value
			URI _uri = new URIImpl(uri);


			dataObjectFactory = new DataObjectFactory(message, this, null, source, _uri , folderUri);

			boolean first = true;
			DataObject object = null;

			while ((object = dataObjectFactory.getObject()) != null)
			{
				if (first) {
					object.getMetadata().add(NIE.isPartOf, folderUri);
					first = false;
				}

				parseForAttachements(object);
				if (model == null){
					model = object.getMetadata().getModel();
				} else {
					model.addModel(object.getMetadata().getModel());
				}
			}

			dataObjectFactory.disposeRemainingObjects();
			dataObjectFactory = null;

			return model;

		} catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}

	private void parseForAttachements(DataObject object) {
		// TODO Auto-generated method stub
		if (object instanceof FileDataObject) {
			try {
				process((FileDataObject) object);
			} catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
	}

	private void process(FileDataObject object) throws IOException, ExtractorException, ModelException, SubCrawlerException {
		URI id = object.getID();

		int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
		InputStream contentStream = object.getContent();
		contentStream.mark(minimumArrayLength + 10); // add some for safety

		// apply the MimeTypeIdentifier
		byte[] bytes = IOUtil.readBytes(contentStream, minimumArrayLength);
		String mimeType = mimeTypeIdentifier.identify(bytes, object.getMetadata().getString(NFO.fileName), id);
		if (mimeType != null) {
			// add the MIME type to the metadata
			RDFContainer metadata = object.getMetadata();
			metadata.add(NIE.mimeType, mimeType);
			contentStream.reset();

			// apply an Extractor if available
			boolean done = applyExtractor(id, contentStream, mimeType, metadata);
			if (done) {return;}

			// else try to apply a FileExtractor
			done = applyFileExtractor(object, id, mimeType, metadata);
			if (done) {return;}
		}
	}

	private boolean applyExtractor(URI id, InputStream contentStream, String mimeType, RDFContainer metadata) throws ExtractorException, IOException {
		Set extractors = extractorRegistry.getExtractorFactories(mimeType);
		boolean supportedByXmp = xmpExtractorFactory.getSupportedMimeTypes().contains(mimeType);
		boolean result = false;
		byte [] buffer = null;

		if (!extractors.isEmpty() && supportedByXmp) {
			buffer = IOUtil.readBytes(contentStream);
		}

		if (!extractors.isEmpty()) {
			ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
			Extractor extractor = factory.get();
			ThreadedExtractorWrapper wrapper = new ThreadedExtractorWrapper(extractor);
			if (buffer != null) {
				contentStream = new BufferedInputStream(new ByteArrayInputStream(buffer));
			}
			try {
				wrapper.extract(id, contentStream, null, mimeType, metadata);
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (supportedByXmp) {
			Extractor extractor = xmpExtractorFactory.get();
			ThreadedExtractorWrapper wrapper = new ThreadedExtractorWrapper(extractor);
			if (buffer != null) {
				contentStream = new BufferedInputStream(new ByteArrayInputStream(buffer));
			}
			try {
				wrapper.extract(id, contentStream, null, mimeType, metadata);
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private boolean applyFileExtractor(FileDataObject object, URI id, String mimeType, RDFContainer metadata) throws ExtractorException, IOException {
		Set fileextractors = extractorRegistry.getFileExtractorFactories(mimeType);
		if (!fileextractors.isEmpty()) {
			FileExtractorFactory factory = (FileExtractorFactory) fileextractors.iterator().next();
			FileExtractor extractor = factory.get();
			File originalFile = object.getFile();
			if (originalFile != null) {
				extractor.extract(id, originalFile, null, mimeType, metadata);
				return true;
			}
			else {
				File tempFile = object.downloadContent();
				try {
					extractor.extract(id, tempFile, null, mimeType, metadata);
					return true;
				}
				finally {
					if (tempFile != null) {
						tempFile.delete();
					}
				}
			}
		}
		else {
			return false;
		}
	}

	@Override
	public RDFContainer getRDFContainer(URI paramURI) {
		Model rdfContainerModel = RDF2Go.getModelFactory().createModel().open(); 
		return new RDFContainerImpl(rdfContainerModel, paramURI);
	}
}
