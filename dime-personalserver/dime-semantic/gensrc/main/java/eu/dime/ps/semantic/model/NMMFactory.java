/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

import eu.dime.ps.semantic.model.nmm.*;

/**
 * A factory for the Java classes generated automatically for the NMM vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NMMFactory extends ResourceFactory {

	public Movie createMovie() {
		return new Movie(createModel(), generateUniqueURI(), true);
	}

	public Movie createMovie(URI resourceUri) {
		return new Movie(createModel(), resourceUri, true);
	}

	public Movie createMovie(String resourceUriString) {
		return new Movie(createModel(), new URIImpl(resourceUriString), true);
	}

	public MusicAlbum createMusicAlbum() {
		return new MusicAlbum(createModel(), generateUniqueURI(), true);
	}

	public MusicAlbum createMusicAlbum(URI resourceUri) {
		return new MusicAlbum(createModel(), resourceUri, true);
	}

	public MusicAlbum createMusicAlbum(String resourceUriString) {
		return new MusicAlbum(createModel(), new URIImpl(resourceUriString), true);
	}

	public MusicPiece createMusicPiece() {
		return new MusicPiece(createModel(), generateUniqueURI(), true);
	}

	public MusicPiece createMusicPiece(URI resourceUri) {
		return new MusicPiece(createModel(), resourceUri, true);
	}

	public MusicPiece createMusicPiece(String resourceUriString) {
		return new MusicPiece(createModel(), new URIImpl(resourceUriString), true);
	}

	public TVSeries createTVSeries() {
		return new TVSeries(createModel(), generateUniqueURI(), true);
	}

	public TVSeries createTVSeries(URI resourceUri) {
		return new TVSeries(createModel(), resourceUri, true);
	}

	public TVSeries createTVSeries(String resourceUriString) {
		return new TVSeries(createModel(), new URIImpl(resourceUriString), true);
	}

	public TVShow createTVShow() {
		return new TVShow(createModel(), generateUniqueURI(), true);
	}

	public TVShow createTVShow(URI resourceUri) {
		return new TVShow(createModel(), resourceUri, true);
	}

	public TVShow createTVShow(String resourceUriString) {
		return new TVShow(createModel(), new URIImpl(resourceUriString), true);
	}

}