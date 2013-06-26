package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.nuao.*;

/**
 * A factory for the Java classes generated automatically for the NUAO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NUAOFactory extends ResourceFactory {

	public DesktopEvent createDesktopEvent() {
		return new DesktopEvent(createModel(), generateUniqueURI(), true);
	}

	public DesktopEvent createDesktopEvent(URI resourceUri) {
		return new DesktopEvent(createModel(), resourceUri, true);
	}

	public DesktopEvent createDesktopEvent(String resourceUriString) {
		return new DesktopEvent(createModel(), new URIImpl(resourceUriString), true);
	}

	public Event createEvent() {
		return new Event(createModel(), generateUniqueURI(), true);
	}

	public Event createEvent(URI resourceUri) {
		return new Event(createModel(), resourceUri, true);
	}

	public Event createEvent(String resourceUriString) {
		return new Event(createModel(), new URIImpl(resourceUriString), true);
	}

	public FocusEvent createFocusEvent() {
		return new FocusEvent(createModel(), generateUniqueURI(), true);
	}

	public FocusEvent createFocusEvent(URI resourceUri) {
		return new FocusEvent(createModel(), resourceUri, true);
	}

	public FocusEvent createFocusEvent(String resourceUriString) {
		return new FocusEvent(createModel(), new URIImpl(resourceUriString), true);
	}

	public ModificationEvent createModificationEvent() {
		return new ModificationEvent(createModel(), generateUniqueURI(), true);
	}

	public ModificationEvent createModificationEvent(URI resourceUri) {
		return new ModificationEvent(createModel(), resourceUri, true);
	}

	public ModificationEvent createModificationEvent(String resourceUriString) {
		return new ModificationEvent(createModel(), new URIImpl(resourceUriString), true);
	}

	public UsageEvent createUsageEvent() {
		return new UsageEvent(createModel(), generateUniqueURI(), true);
	}

	public UsageEvent createUsageEvent(URI resourceUri) {
		return new UsageEvent(createModel(), resourceUri, true);
	}

	public UsageEvent createUsageEvent(String resourceUriString) {
		return new UsageEvent(createModel(), new URIImpl(resourceUriString), true);
	}

}