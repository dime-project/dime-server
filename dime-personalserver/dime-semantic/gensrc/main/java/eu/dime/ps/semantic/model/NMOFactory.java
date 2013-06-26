package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.nmo.*;

/**
 * A factory for the Java classes generated automatically for the NMO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NMOFactory extends ResourceFactory {

	public Email createEmail() {
		return new Email(createModel(), generateUniqueURI(), true);
	}

	public Email createEmail(URI resourceUri) {
		return new Email(createModel(), resourceUri, true);
	}

	public Email createEmail(String resourceUriString) {
		return new Email(createModel(), new URIImpl(resourceUriString), true);
	}

	public IMMessage createIMMessage() {
		return new IMMessage(createModel(), generateUniqueURI(), true);
	}

	public IMMessage createIMMessage(URI resourceUri) {
		return new IMMessage(createModel(), resourceUri, true);
	}

	public IMMessage createIMMessage(String resourceUriString) {
		return new IMMessage(createModel(), new URIImpl(resourceUriString), true);
	}

	public Mailbox createMailbox() {
		return new Mailbox(createModel(), generateUniqueURI(), true);
	}

	public Mailbox createMailbox(URI resourceUri) {
		return new Mailbox(createModel(), resourceUri, true);
	}

	public Mailbox createMailbox(String resourceUriString) {
		return new Mailbox(createModel(), new URIImpl(resourceUriString), true);
	}

	public MailboxDataObject createMailboxDataObject() {
		return new MailboxDataObject(createModel(), generateUniqueURI(), true);
	}

	public MailboxDataObject createMailboxDataObject(URI resourceUri) {
		return new MailboxDataObject(createModel(), resourceUri, true);
	}

	public MailboxDataObject createMailboxDataObject(String resourceUriString) {
		return new MailboxDataObject(createModel(), new URIImpl(resourceUriString), true);
	}

	public Message createMessage() {
		return new Message(createModel(), generateUniqueURI(), true);
	}

	public Message createMessage(URI resourceUri) {
		return new Message(createModel(), resourceUri, true);
	}

	public Message createMessage(String resourceUriString) {
		return new Message(createModel(), new URIImpl(resourceUriString), true);
	}

	public MessageHeader createMessageHeader() {
		return new MessageHeader(createModel(), generateUniqueURI(), true);
	}

	public MessageHeader createMessageHeader(URI resourceUri) {
		return new MessageHeader(createModel(), resourceUri, true);
	}

	public MessageHeader createMessageHeader(String resourceUriString) {
		return new MessageHeader(createModel(), new URIImpl(resourceUriString), true);
	}

	public MimeEntity createMimeEntity() {
		return new MimeEntity(createModel(), generateUniqueURI(), true);
	}

	public MimeEntity createMimeEntity(URI resourceUri) {
		return new MimeEntity(createModel(), resourceUri, true);
	}

	public MimeEntity createMimeEntity(String resourceUriString) {
		return new MimeEntity(createModel(), new URIImpl(resourceUriString), true);
	}

}