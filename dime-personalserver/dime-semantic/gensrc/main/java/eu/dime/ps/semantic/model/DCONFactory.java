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

import eu.dime.ps.semantic.model.dcon.*;

/**
 * A factory for the Java classes generated automatically for the DCON vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */
public class DCONFactory extends ResourceFactory {

	public Aspect createAspect() {
		return new Aspect(createModel(), generateUniqueURI(), true);
	}

	public Aspect createAspect(URI resourceUri) {
		return new Aspect(createModel(), resourceUri, true);
	}

	public Aspect createAspect(String resourceUriString) {
		return new Aspect(createModel(), new URIImpl(resourceUriString), true);
	}

	public Attention createAttention() {
		return new Attention(createModel(), generateUniqueURI(), true);
	}

	public Attention createAttention(URI resourceUri) {
		return new Attention(createModel(), resourceUri, true);
	}

	public Attention createAttention(String resourceUriString) {
		return new Attention(createModel(), new URIImpl(resourceUriString), true);
	}

	public Connectivity createConnectivity() {
		return new Connectivity(createModel(), generateUniqueURI(), true);
	}

	public Connectivity createConnectivity(URI resourceUri) {
		return new Connectivity(createModel(), resourceUri, true);
	}

	public Connectivity createConnectivity(String resourceUriString) {
		return new Connectivity(createModel(), new URIImpl(resourceUriString), true);
	}

	public Context createContext() {
		return new Context(createModel(), generateUniqueURI(), true);
	}

	public Context createContext(URI resourceUri) {
		return new Context(createModel(), resourceUri, true);
	}

	public Context createContext(String resourceUriString) {
		return new Context(createModel(), new URIImpl(resourceUriString), true);
	}

	public Element createElement() {
		return new Element(createModel(), generateUniqueURI(), true);
	}

	public Element createElement(URI resourceUri) {
		return new Element(createModel(), resourceUri, true);
	}

	public Element createElement(String resourceUriString) {
		return new Element(createModel(), new URIImpl(resourceUriString), true);
	}

	public Environment createEnvironment() {
		return new Environment(createModel(), generateUniqueURI(), true);
	}

	public Environment createEnvironment(URI resourceUri) {
		return new Environment(createModel(), resourceUri, true);
	}

	public Environment createEnvironment(String resourceUriString) {
		return new Environment(createModel(), new URIImpl(resourceUriString), true);
	}

	public LiveContext createLiveContext() {
		return new LiveContext(createModel(), generateUniqueURI(), true);
	}

	public LiveContext createLiveContext(URI resourceUri) {
		return new LiveContext(createModel(), resourceUri, true);
	}

	public LiveContext createLiveContext(String resourceUriString) {
		return new LiveContext(createModel(), new URIImpl(resourceUriString), true);
	}

	public Peers createPeers() {
		return new Peers(createModel(), generateUniqueURI(), true);
	}

	public Peers createPeers(URI resourceUri) {
		return new Peers(createModel(), resourceUri, true);
	}

	public Peers createPeers(String resourceUriString) {
		return new Peers(createModel(), new URIImpl(resourceUriString), true);
	}

	public Schedule createSchedule() {
		return new Schedule(createModel(), generateUniqueURI(), true);
	}

	public Schedule createSchedule(URI resourceUri) {
		return new Schedule(createModel(), resourceUri, true);
	}

	public Schedule createSchedule(String resourceUriString) {
		return new Schedule(createModel(), new URIImpl(resourceUriString), true);
	}

	public Situation createSituation() {
		return new Situation(createModel(), generateUniqueURI(), true);
	}

	public Situation createSituation(URI resourceUri) {
		return new Situation(createModel(), resourceUri, true);
	}

	public Situation createSituation(String resourceUriString) {
		return new Situation(createModel(), new URIImpl(resourceUriString), true);
	}

	public SpaTem createSpaTem() {
		return new SpaTem(createModel(), generateUniqueURI(), true);
	}

	public SpaTem createSpaTem(URI resourceUri) {
		return new SpaTem(createModel(), resourceUri, true);
	}

	public SpaTem createSpaTem(String resourceUriString) {
		return new SpaTem(createModel(), new URIImpl(resourceUriString), true);
	}

	public State createState() {
		return new State(createModel(), generateUniqueURI(), true);
	}

	public State createState(URI resourceUri) {
		return new State(createModel(), resourceUri, true);
	}

	public State createState(String resourceUriString) {
		return new State(createModel(), new URIImpl(resourceUriString), true);
	}

}