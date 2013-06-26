package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.ndo.*;

/**
 * A factory for the Java classes generated automatically for the NDO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NDOFactory extends ResourceFactory {

	public DownloadEvent createDownloadEvent() {
		return new DownloadEvent(createModel(), generateUniqueURI(), true);
	}

	public DownloadEvent createDownloadEvent(URI resourceUri) {
		return new DownloadEvent(createModel(), resourceUri, true);
	}

	public DownloadEvent createDownloadEvent(String resourceUriString) {
		return new DownloadEvent(createModel(), new URIImpl(resourceUriString), true);
	}

	public P2PFile createP2PFile() {
		return new P2PFile(createModel(), generateUniqueURI(), true);
	}

	public P2PFile createP2PFile(URI resourceUri) {
		return new P2PFile(createModel(), resourceUri, true);
	}

	public P2PFile createP2PFile(String resourceUriString) {
		return new P2PFile(createModel(), new URIImpl(resourceUriString), true);
	}

	public Torrent createTorrent() {
		return new Torrent(createModel(), generateUniqueURI(), true);
	}

	public Torrent createTorrent(URI resourceUri) {
		return new Torrent(createModel(), resourceUri, true);
	}

	public Torrent createTorrent(String resourceUriString) {
		return new Torrent(createModel(), new URIImpl(resourceUriString), true);
	}

	public TorrentedFile createTorrentedFile() {
		return new TorrentedFile(createModel(), generateUniqueURI(), true);
	}

	public TorrentedFile createTorrentedFile(URI resourceUri) {
		return new TorrentedFile(createModel(), resourceUri, true);
	}

	public TorrentedFile createTorrentedFile(String resourceUriString) {
		return new TorrentedFile(createModel(), new URIImpl(resourceUriString), true);
	}

}