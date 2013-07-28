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

package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.nfo.*;

/**
 * A factory for the Java classes generated automatically for the NFO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NFOFactory extends ResourceFactory {

	public Application createApplication() {
		return new Application(createModel(), generateUniqueURI(), true);
	}

	public Application createApplication(URI resourceUri) {
		return new Application(createModel(), resourceUri, true);
	}

	public Application createApplication(String resourceUriString) {
		return new Application(createModel(), new URIImpl(resourceUriString), true);
	}

	public Archive createArchive() {
		return new Archive(createModel(), generateUniqueURI(), true);
	}

	public Archive createArchive(URI resourceUri) {
		return new Archive(createModel(), resourceUri, true);
	}

	public Archive createArchive(String resourceUriString) {
		return new Archive(createModel(), new URIImpl(resourceUriString), true);
	}

	public ArchiveItem createArchiveItem() {
		return new ArchiveItem(createModel(), generateUniqueURI(), true);
	}

	public ArchiveItem createArchiveItem(URI resourceUri) {
		return new ArchiveItem(createModel(), resourceUri, true);
	}

	public ArchiveItem createArchiveItem(String resourceUriString) {
		return new ArchiveItem(createModel(), new URIImpl(resourceUriString), true);
	}

	public Attachment createAttachment() {
		return new Attachment(createModel(), generateUniqueURI(), true);
	}

	public Attachment createAttachment(URI resourceUri) {
		return new Attachment(createModel(), resourceUri, true);
	}

	public Attachment createAttachment(String resourceUriString) {
		return new Attachment(createModel(), new URIImpl(resourceUriString), true);
	}

	public Audio createAudio() {
		return new Audio(createModel(), generateUniqueURI(), true);
	}

	public Audio createAudio(URI resourceUri) {
		return new Audio(createModel(), resourceUri, true);
	}

	public Audio createAudio(String resourceUriString) {
		return new Audio(createModel(), new URIImpl(resourceUriString), true);
	}

	public Bookmark createBookmark() {
		return new Bookmark(createModel(), generateUniqueURI(), true);
	}

	public Bookmark createBookmark(URI resourceUri) {
		return new Bookmark(createModel(), resourceUri, true);
	}

	public Bookmark createBookmark(String resourceUriString) {
		return new Bookmark(createModel(), new URIImpl(resourceUriString), true);
	}

	public BookmarkFolder createBookmarkFolder() {
		return new BookmarkFolder(createModel(), generateUniqueURI(), true);
	}

	public BookmarkFolder createBookmarkFolder(URI resourceUri) {
		return new BookmarkFolder(createModel(), resourceUri, true);
	}

	public BookmarkFolder createBookmarkFolder(String resourceUriString) {
		return new BookmarkFolder(createModel(), new URIImpl(resourceUriString), true);
	}

	public CompressionType createCompressionType() {
		return new CompressionType(createModel(), generateUniqueURI(), true);
	}

	public CompressionType createCompressionType(URI resourceUri) {
		return new CompressionType(createModel(), resourceUri, true);
	}

	public CompressionType createCompressionType(String resourceUriString) {
		return new CompressionType(createModel(), new URIImpl(resourceUriString), true);
	}

	public Cursor createCursor() {
		return new Cursor(createModel(), generateUniqueURI(), true);
	}

	public Cursor createCursor(URI resourceUri) {
		return new Cursor(createModel(), resourceUri, true);
	}

	public Cursor createCursor(String resourceUriString) {
		return new Cursor(createModel(), new URIImpl(resourceUriString), true);
	}

	public DataContainer createDataContainer() {
		return new DataContainer(createModel(), generateUniqueURI(), true);
	}

	public DataContainer createDataContainer(URI resourceUri) {
		return new DataContainer(createModel(), resourceUri, true);
	}

	public DataContainer createDataContainer(String resourceUriString) {
		return new DataContainer(createModel(), new URIImpl(resourceUriString), true);
	}

	public DeletedResource createDeletedResource() {
		return new DeletedResource(createModel(), generateUniqueURI(), true);
	}

	public DeletedResource createDeletedResource(URI resourceUri) {
		return new DeletedResource(createModel(), resourceUri, true);
	}

	public DeletedResource createDeletedResource(String resourceUriString) {
		return new DeletedResource(createModel(), new URIImpl(resourceUriString), true);
	}

	public Document createDocument() {
		return new Document(createModel(), generateUniqueURI(), true);
	}

	public Document createDocument(URI resourceUri) {
		return new Document(createModel(), resourceUri, true);
	}

	public Document createDocument(String resourceUriString) {
		return new Document(createModel(), new URIImpl(resourceUriString), true);
	}

	public EmbeddedFileDataObject createEmbeddedFileDataObject() {
		return new EmbeddedFileDataObject(createModel(), generateUniqueURI(), true);
	}

	public EmbeddedFileDataObject createEmbeddedFileDataObject(URI resourceUri) {
		return new EmbeddedFileDataObject(createModel(), resourceUri, true);
	}

	public EmbeddedFileDataObject createEmbeddedFileDataObject(String resourceUriString) {
		return new EmbeddedFileDataObject(createModel(), new URIImpl(resourceUriString), true);
	}

	public EncryptionStatus createEncryptionStatus() {
		return new EncryptionStatus(createModel(), generateUniqueURI(), true);
	}

	public EncryptionStatus createEncryptionStatus(URI resourceUri) {
		return new EncryptionStatus(createModel(), resourceUri, true);
	}

	public EncryptionStatus createEncryptionStatus(String resourceUriString) {
		return new EncryptionStatus(createModel(), new URIImpl(resourceUriString), true);
	}

	public Executable createExecutable() {
		return new Executable(createModel(), generateUniqueURI(), true);
	}

	public Executable createExecutable(URI resourceUri) {
		return new Executable(createModel(), resourceUri, true);
	}

	public Executable createExecutable(String resourceUriString) {
		return new Executable(createModel(), new URIImpl(resourceUriString), true);
	}

	public FileDataObject createFileDataObject() {
		return new FileDataObject(createModel(), generateUniqueURI(), true);
	}

	public FileDataObject createFileDataObject(URI resourceUri) {
		return new FileDataObject(createModel(), resourceUri, true);
	}

	public FileDataObject createFileDataObject(String resourceUriString) {
		return new FileDataObject(createModel(), new URIImpl(resourceUriString), true);
	}

	public FileHash createFileHash() {
		return new FileHash(createModel(), generateUniqueURI(), true);
	}

	public FileHash createFileHash(URI resourceUri) {
		return new FileHash(createModel(), resourceUri, true);
	}

	public FileHash createFileHash(String resourceUriString) {
		return new FileHash(createModel(), new URIImpl(resourceUriString), true);
	}

	public Filesystem createFilesystem() {
		return new Filesystem(createModel(), generateUniqueURI(), true);
	}

	public Filesystem createFilesystem(URI resourceUri) {
		return new Filesystem(createModel(), resourceUri, true);
	}

	public Filesystem createFilesystem(String resourceUriString) {
		return new Filesystem(createModel(), new URIImpl(resourceUriString), true);
	}

	public FilesystemImage createFilesystemImage() {
		return new FilesystemImage(createModel(), generateUniqueURI(), true);
	}

	public FilesystemImage createFilesystemImage(URI resourceUri) {
		return new FilesystemImage(createModel(), resourceUri, true);
	}

	public FilesystemImage createFilesystemImage(String resourceUriString) {
		return new FilesystemImage(createModel(), new URIImpl(resourceUriString), true);
	}

	public Folder createFolder() {
		return new Folder(createModel(), generateUniqueURI(), true);
	}

	public Folder createFolder(URI resourceUri) {
		return new Folder(createModel(), resourceUri, true);
	}

	public Folder createFolder(String resourceUriString) {
		return new Folder(createModel(), new URIImpl(resourceUriString), true);
	}

	public Font createFont() {
		return new Font(createModel(), generateUniqueURI(), true);
	}

	public Font createFont(URI resourceUri) {
		return new Font(createModel(), resourceUri, true);
	}

	public Font createFont(String resourceUriString) {
		return new Font(createModel(), new URIImpl(resourceUriString), true);
	}

	public HardDiskPartition createHardDiskPartition() {
		return new HardDiskPartition(createModel(), generateUniqueURI(), true);
	}

	public HardDiskPartition createHardDiskPartition(URI resourceUri) {
		return new HardDiskPartition(createModel(), resourceUri, true);
	}

	public HardDiskPartition createHardDiskPartition(String resourceUriString) {
		return new HardDiskPartition(createModel(), new URIImpl(resourceUriString), true);
	}

	public HtmlDocument createHtmlDocument() {
		return new HtmlDocument(createModel(), generateUniqueURI(), true);
	}

	public HtmlDocument createHtmlDocument(URI resourceUri) {
		return new HtmlDocument(createModel(), resourceUri, true);
	}

	public HtmlDocument createHtmlDocument(String resourceUriString) {
		return new HtmlDocument(createModel(), new URIImpl(resourceUriString), true);
	}

	public Icon createIcon() {
		return new Icon(createModel(), generateUniqueURI(), true);
	}

	public Icon createIcon(URI resourceUri) {
		return new Icon(createModel(), resourceUri, true);
	}

	public Icon createIcon(String resourceUriString) {
		return new Icon(createModel(), new URIImpl(resourceUriString), true);
	}

	public Image createImage() {
		return new Image(createModel(), generateUniqueURI(), true);
	}

	public Image createImage(URI resourceUri) {
		return new Image(createModel(), resourceUri, true);
	}

	public Image createImage(String resourceUriString) {
		return new Image(createModel(), new URIImpl(resourceUriString), true);
	}

	public Media createMedia() {
		return new Media(createModel(), generateUniqueURI(), true);
	}

	public Media createMedia(URI resourceUri) {
		return new Media(createModel(), resourceUri, true);
	}

	public Media createMedia(String resourceUriString) {
		return new Media(createModel(), new URIImpl(resourceUriString), true);
	}

	public MediaFileListEntry createMediaFileListEntry() {
		return new MediaFileListEntry(createModel(), generateUniqueURI(), true);
	}

	public MediaFileListEntry createMediaFileListEntry(URI resourceUri) {
		return new MediaFileListEntry(createModel(), resourceUri, true);
	}

	public MediaFileListEntry createMediaFileListEntry(String resourceUriString) {
		return new MediaFileListEntry(createModel(), new URIImpl(resourceUriString), true);
	}

	public MediaList createMediaList() {
		return new MediaList(createModel(), generateUniqueURI(), true);
	}

	public MediaList createMediaList(URI resourceUri) {
		return new MediaList(createModel(), resourceUri, true);
	}

	public MediaList createMediaList(String resourceUriString) {
		return new MediaList(createModel(), new URIImpl(resourceUriString), true);
	}

	public MediaStream createMediaStream() {
		return new MediaStream(createModel(), generateUniqueURI(), true);
	}

	public MediaStream createMediaStream(URI resourceUri) {
		return new MediaStream(createModel(), resourceUri, true);
	}

	public MediaStream createMediaStream(String resourceUriString) {
		return new MediaStream(createModel(), new URIImpl(resourceUriString), true);
	}

	public MindMap createMindMap() {
		return new MindMap(createModel(), generateUniqueURI(), true);
	}

	public MindMap createMindMap(URI resourceUri) {
		return new MindMap(createModel(), resourceUri, true);
	}

	public MindMap createMindMap(String resourceUriString) {
		return new MindMap(createModel(), new URIImpl(resourceUriString), true);
	}

	public OperatingSystem createOperatingSystem() {
		return new OperatingSystem(createModel(), generateUniqueURI(), true);
	}

	public OperatingSystem createOperatingSystem(URI resourceUri) {
		return new OperatingSystem(createModel(), resourceUri, true);
	}

	public OperatingSystem createOperatingSystem(String resourceUriString) {
		return new OperatingSystem(createModel(), new URIImpl(resourceUriString), true);
	}

	public PaginatedTextDocument createPaginatedTextDocument() {
		return new PaginatedTextDocument(createModel(), generateUniqueURI(), true);
	}

	public PaginatedTextDocument createPaginatedTextDocument(URI resourceUri) {
		return new PaginatedTextDocument(createModel(), resourceUri, true);
	}

	public PaginatedTextDocument createPaginatedTextDocument(String resourceUriString) {
		return new PaginatedTextDocument(createModel(), new URIImpl(resourceUriString), true);
	}

	public Placemark createPlacemark() {
		return new Placemark(createModel(), generateUniqueURI(), true);
	}

	public Placemark createPlacemark(URI resourceUri) {
		return new Placemark(createModel(), resourceUri, true);
	}

	public Placemark createPlacemark(String resourceUriString) {
		return new Placemark(createModel(), new URIImpl(resourceUriString), true);
	}

	public PlacemarkContainer createPlacemarkContainer() {
		return new PlacemarkContainer(createModel(), generateUniqueURI(), true);
	}

	public PlacemarkContainer createPlacemarkContainer(URI resourceUri) {
		return new PlacemarkContainer(createModel(), resourceUri, true);
	}

	public PlacemarkContainer createPlacemarkContainer(String resourceUriString) {
		return new PlacemarkContainer(createModel(), new URIImpl(resourceUriString), true);
	}

	public PlainTextDocument createPlainTextDocument() {
		return new PlainTextDocument(createModel(), generateUniqueURI(), true);
	}

	public PlainTextDocument createPlainTextDocument(URI resourceUri) {
		return new PlainTextDocument(createModel(), resourceUri, true);
	}

	public PlainTextDocument createPlainTextDocument(String resourceUriString) {
		return new PlainTextDocument(createModel(), new URIImpl(resourceUriString), true);
	}

	public Presentation createPresentation() {
		return new Presentation(createModel(), generateUniqueURI(), true);
	}

	public Presentation createPresentation(URI resourceUri) {
		return new Presentation(createModel(), resourceUri, true);
	}

	public Presentation createPresentation(String resourceUriString) {
		return new Presentation(createModel(), new URIImpl(resourceUriString), true);
	}

	public RasterImage createRasterImage() {
		return new RasterImage(createModel(), generateUniqueURI(), true);
	}

	public RasterImage createRasterImage(URI resourceUri) {
		return new RasterImage(createModel(), resourceUri, true);
	}

	public RasterImage createRasterImage(String resourceUriString) {
		return new RasterImage(createModel(), new URIImpl(resourceUriString), true);
	}

	public RemoteDataObject createRemoteDataObject() {
		return new RemoteDataObject(createModel(), generateUniqueURI(), true);
	}

	public RemoteDataObject createRemoteDataObject(URI resourceUri) {
		return new RemoteDataObject(createModel(), resourceUri, true);
	}

	public RemoteDataObject createRemoteDataObject(String resourceUriString) {
		return new RemoteDataObject(createModel(), new URIImpl(resourceUriString), true);
	}

	public RemotePortAddress createRemotePortAddress() {
		return new RemotePortAddress(createModel(), generateUniqueURI(), true);
	}

	public RemotePortAddress createRemotePortAddress(URI resourceUri) {
		return new RemotePortAddress(createModel(), resourceUri, true);
	}

	public RemotePortAddress createRemotePortAddress(String resourceUriString) {
		return new RemotePortAddress(createModel(), new URIImpl(resourceUriString), true);
	}

	public Software createSoftware() {
		return new Software(createModel(), generateUniqueURI(), true);
	}

	public Software createSoftware(URI resourceUri) {
		return new Software(createModel(), resourceUri, true);
	}

	public Software createSoftware(String resourceUriString) {
		return new Software(createModel(), new URIImpl(resourceUriString), true);
	}

	public SoftwareItem createSoftwareItem() {
		return new SoftwareItem(createModel(), generateUniqueURI(), true);
	}

	public SoftwareItem createSoftwareItem(URI resourceUri) {
		return new SoftwareItem(createModel(), resourceUri, true);
	}

	public SoftwareItem createSoftwareItem(String resourceUriString) {
		return new SoftwareItem(createModel(), new URIImpl(resourceUriString), true);
	}

	public SoftwareService createSoftwareService() {
		return new SoftwareService(createModel(), generateUniqueURI(), true);
	}

	public SoftwareService createSoftwareService(URI resourceUri) {
		return new SoftwareService(createModel(), resourceUri, true);
	}

	public SoftwareService createSoftwareService(String resourceUriString) {
		return new SoftwareService(createModel(), new URIImpl(resourceUriString), true);
	}

	public SourceCode createSourceCode() {
		return new SourceCode(createModel(), generateUniqueURI(), true);
	}

	public SourceCode createSourceCode(URI resourceUri) {
		return new SourceCode(createModel(), resourceUri, true);
	}

	public SourceCode createSourceCode(String resourceUriString) {
		return new SourceCode(createModel(), new URIImpl(resourceUriString), true);
	}

	public Spreadsheet createSpreadsheet() {
		return new Spreadsheet(createModel(), generateUniqueURI(), true);
	}

	public Spreadsheet createSpreadsheet(URI resourceUri) {
		return new Spreadsheet(createModel(), resourceUri, true);
	}

	public Spreadsheet createSpreadsheet(String resourceUriString) {
		return new Spreadsheet(createModel(), new URIImpl(resourceUriString), true);
	}

	public TextDocument createTextDocument() {
		return new TextDocument(createModel(), generateUniqueURI(), true);
	}

	public TextDocument createTextDocument(URI resourceUri) {
		return new TextDocument(createModel(), resourceUri, true);
	}

	public TextDocument createTextDocument(String resourceUriString) {
		return new TextDocument(createModel(), new URIImpl(resourceUriString), true);
	}

	public Trash createTrash() {
		return new Trash(createModel(), generateUniqueURI(), true);
	}

	public Trash createTrash(URI resourceUri) {
		return new Trash(createModel(), resourceUri, true);
	}

	public Trash createTrash(String resourceUriString) {
		return new Trash(createModel(), new URIImpl(resourceUriString), true);
	}

	public VectorImage createVectorImage() {
		return new VectorImage(createModel(), generateUniqueURI(), true);
	}

	public VectorImage createVectorImage(URI resourceUri) {
		return new VectorImage(createModel(), resourceUri, true);
	}

	public VectorImage createVectorImage(String resourceUriString) {
		return new VectorImage(createModel(), new URIImpl(resourceUriString), true);
	}

	public Video createVideo() {
		return new Video(createModel(), generateUniqueURI(), true);
	}

	public Video createVideo(URI resourceUri) {
		return new Video(createModel(), resourceUri, true);
	}

	public Video createVideo(String resourceUriString) {
		return new Video(createModel(), new URIImpl(resourceUriString), true);
	}

	public Visual createVisual() {
		return new Visual(createModel(), generateUniqueURI(), true);
	}

	public Visual createVisual(URI resourceUri) {
		return new Visual(createModel(), resourceUri, true);
	}

	public Visual createVisual(String resourceUriString) {
		return new Visual(createModel(), new URIImpl(resourceUriString), true);
	}

	public WebDataObject createWebDataObject() {
		return new WebDataObject(createModel(), generateUniqueURI(), true);
	}

	public WebDataObject createWebDataObject(URI resourceUri) {
		return new WebDataObject(createModel(), resourceUri, true);
	}

	public WebDataObject createWebDataObject(String resourceUriString) {
		return new WebDataObject(createModel(), new URIImpl(resourceUriString), true);
	}

	public Website createWebsite() {
		return new Website(createModel(), generateUniqueURI(), true);
	}

	public Website createWebsite(URI resourceUri) {
		return new Website(createModel(), resourceUri, true);
	}

	public Website createWebsite(String resourceUriString) {
		return new Website(createModel(), new URIImpl(resourceUriString), true);
	}

}