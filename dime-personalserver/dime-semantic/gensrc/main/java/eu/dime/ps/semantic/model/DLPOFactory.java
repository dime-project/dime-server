package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.dlpo.*;

/**
 * A factory for the Java classes generated automatically for the DLPO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class DLPOFactory extends ResourceFactory {

	public ActivityPost createActivityPost() {
		return new ActivityPost(createModel(), generateUniqueURI(), true);
	}

	public ActivityPost createActivityPost(URI resourceUri) {
		return new ActivityPost(createModel(), resourceUri, true);
	}

	public ActivityPost createActivityPost(String resourceUriString) {
		return new ActivityPost(createModel(), new URIImpl(resourceUriString), true);
	}

	public AudioPost createAudioPost() {
		return new AudioPost(createModel(), generateUniqueURI(), true);
	}

	public AudioPost createAudioPost(URI resourceUri) {
		return new AudioPost(createModel(), resourceUri, true);
	}

	public AudioPost createAudioPost(String resourceUriString) {
		return new AudioPost(createModel(), new URIImpl(resourceUriString), true);
	}

	public AvailabilityPost createAvailabilityPost() {
		return new AvailabilityPost(createModel(), generateUniqueURI(), true);
	}

	public AvailabilityPost createAvailabilityPost(URI resourceUri) {
		return new AvailabilityPost(createModel(), resourceUri, true);
	}

	public AvailabilityPost createAvailabilityPost(String resourceUriString) {
		return new AvailabilityPost(createModel(), new URIImpl(resourceUriString), true);
	}

	public BlogPost createBlogPost() {
		return new BlogPost(createModel(), generateUniqueURI(), true);
	}

	public BlogPost createBlogPost(URI resourceUri) {
		return new BlogPost(createModel(), resourceUri, true);
	}

	public BlogPost createBlogPost(String resourceUriString) {
		return new BlogPost(createModel(), new URIImpl(resourceUriString), true);
	}

	public Checkin createCheckin() {
		return new Checkin(createModel(), generateUniqueURI(), true);
	}

	public Checkin createCheckin(URI resourceUri) {
		return new Checkin(createModel(), resourceUri, true);
	}

	public Checkin createCheckin(String resourceUriString) {
		return new Checkin(createModel(), new URIImpl(resourceUriString), true);
	}

	public Comment createComment() {
		return new Comment(createModel(), generateUniqueURI(), true);
	}

	public Comment createComment(URI resourceUri) {
		return new Comment(createModel(), resourceUri, true);
	}

	public Comment createComment(String resourceUriString) {
		return new Comment(createModel(), new URIImpl(resourceUriString), true);
	}

	public EventPost createEventPost() {
		return new EventPost(createModel(), generateUniqueURI(), true);
	}

	public EventPost createEventPost(URI resourceUri) {
		return new EventPost(createModel(), resourceUri, true);
	}

	public EventPost createEventPost(String resourceUriString) {
		return new EventPost(createModel(), new URIImpl(resourceUriString), true);
	}

	public ImagePost createImagePost() {
		return new ImagePost(createModel(), generateUniqueURI(), true);
	}

	public ImagePost createImagePost(URI resourceUri) {
		return new ImagePost(createModel(), resourceUri, true);
	}

	public ImagePost createImagePost(String resourceUriString) {
		return new ImagePost(createModel(), new URIImpl(resourceUriString), true);
	}

	public LivePost createLivePost() {
		return new LivePost(createModel(), generateUniqueURI(), true);
	}

	public LivePost createLivePost(URI resourceUri) {
		return new LivePost(createModel(), resourceUri, true);
	}

	public LivePost createLivePost(String resourceUriString) {
		return new LivePost(createModel(), new URIImpl(resourceUriString), true);
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

	public MultimediaPost createMultimediaPost() {
		return new MultimediaPost(createModel(), generateUniqueURI(), true);
	}

	public MultimediaPost createMultimediaPost(URI resourceUri) {
		return new MultimediaPost(createModel(), resourceUri, true);
	}

	public MultimediaPost createMultimediaPost(String resourceUriString) {
		return new MultimediaPost(createModel(), new URIImpl(resourceUriString), true);
	}

	public NotePost createNotePost() {
		return new NotePost(createModel(), generateUniqueURI(), true);
	}

	public NotePost createNotePost(URI resourceUri) {
		return new NotePost(createModel(), resourceUri, true);
	}

	public NotePost createNotePost(String resourceUriString) {
		return new NotePost(createModel(), new URIImpl(resourceUriString), true);
	}

	public PresencePost createPresencePost() {
		return new PresencePost(createModel(), generateUniqueURI(), true);
	}

	public PresencePost createPresencePost(URI resourceUri) {
		return new PresencePost(createModel(), resourceUri, true);
	}

	public PresencePost createPresencePost(String resourceUriString) {
		return new PresencePost(createModel(), new URIImpl(resourceUriString), true);
	}

	public Status createStatus() {
		return new Status(createModel(), generateUniqueURI(), true);
	}

	public Status createStatus(URI resourceUri) {
		return new Status(createModel(), resourceUri, true);
	}

	public Status createStatus(String resourceUriString) {
		return new Status(createModel(), new URIImpl(resourceUriString), true);
	}

	public VideoPost createVideoPost() {
		return new VideoPost(createModel(), generateUniqueURI(), true);
	}

	public VideoPost createVideoPost(URI resourceUri) {
		return new VideoPost(createModel(), resourceUri, true);
	}

	public VideoPost createVideoPost(String resourceUriString) {
		return new VideoPost(createModel(), new URIImpl(resourceUriString), true);
	}

	public WebDocumentPost createWebDocumentPost() {
		return new WebDocumentPost(createModel(), generateUniqueURI(), true);
	}

	public WebDocumentPost createWebDocumentPost(URI resourceUri) {
		return new WebDocumentPost(createModel(), resourceUri, true);
	}

	public WebDocumentPost createWebDocumentPost(String resourceUriString) {
		return new WebDocumentPost(createModel(), new URIImpl(resourceUriString), true);
	}

}