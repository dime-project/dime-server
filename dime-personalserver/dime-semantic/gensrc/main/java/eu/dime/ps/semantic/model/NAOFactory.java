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

import eu.dime.ps.semantic.model.nao.*;

/**
 * A factory for the Java classes generated automatically for the NAO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NAOFactory extends ResourceFactory {

	public Agent createAgent() {
		return new Agent(createModel(), generateUniqueURI(), true);
	}

	public Agent createAgent(URI resourceUri) {
		return new Agent(createModel(), resourceUri, true);
	}

	public Agent createAgent(String resourceUriString) {
		return new Agent(createModel(), new URIImpl(resourceUriString), true);
	}

	public FreeDesktopIcon createFreeDesktopIcon() {
		return new FreeDesktopIcon(createModel(), generateUniqueURI(), true);
	}

	public FreeDesktopIcon createFreeDesktopIcon(URI resourceUri) {
		return new FreeDesktopIcon(createModel(), resourceUri, true);
	}

	public FreeDesktopIcon createFreeDesktopIcon(String resourceUriString) {
		return new FreeDesktopIcon(createModel(), new URIImpl(resourceUriString), true);
	}

	public Party createParty() {
		return new Party(createModel(), generateUniqueURI(), true);
	}

	public Party createParty(URI resourceUri) {
		return new Party(createModel(), resourceUri, true);
	}

	public Party createParty(String resourceUriString) {
		return new Party(createModel(), new URIImpl(resourceUriString), true);
	}

	public Symbol createSymbol() {
		return new Symbol(createModel(), generateUniqueURI(), true);
	}

	public Symbol createSymbol(URI resourceUri) {
		return new Symbol(createModel(), resourceUri, true);
	}

	public Symbol createSymbol(String resourceUriString) {
		return new Symbol(createModel(), new URIImpl(resourceUriString), true);
	}

	public Tag createTag() {
		return new Tag(createModel(), generateUniqueURI(), true);
	}

	public Tag createTag(URI resourceUri) {
		return new Tag(createModel(), resourceUri, true);
	}

	public Tag createTag(String resourceUriString) {
		return new Tag(createModel(), new URIImpl(resourceUriString), true);
	}

}