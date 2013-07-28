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

import eu.dime.ps.semantic.model.pimo.*;

/**
 * A factory for the Java classes generated automatically for the PIMO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class PIMOFactory extends ResourceFactory {

	public Agent createAgent() {
		return new Agent(createModel(), generateUniqueURI(), true);
	}

	public Agent createAgent(URI resourceUri) {
		return new Agent(createModel(), resourceUri, true);
	}

	public Agent createAgent(String resourceUriString) {
		return new Agent(createModel(), new URIImpl(resourceUriString), true);
	}

	public Association createAssociation() {
		return new Association(createModel(), generateUniqueURI(), true);
	}

	public Association createAssociation(URI resourceUri) {
		return new Association(createModel(), resourceUri, true);
	}

	public Association createAssociation(String resourceUriString) {
		return new Association(createModel(), new URIImpl(resourceUriString), true);
	}

	public Attendee createAttendee() {
		return new Attendee(createModel(), generateUniqueURI(), true);
	}

	public Attendee createAttendee(URI resourceUri) {
		return new Attendee(createModel(), resourceUri, true);
	}

	public Attendee createAttendee(String resourceUriString) {
		return new Attendee(createModel(), new URIImpl(resourceUriString), true);
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

	public Building createBuilding() {
		return new Building(createModel(), generateUniqueURI(), true);
	}

	public Building createBuilding(URI resourceUri) {
		return new Building(createModel(), resourceUri, true);
	}

	public Building createBuilding(String resourceUriString) {
		return new Building(createModel(), new URIImpl(resourceUriString), true);
	}

	public City createCity() {
		return new City(createModel(), generateUniqueURI(), true);
	}

	public City createCity(URI resourceUri) {
		return new City(createModel(), resourceUri, true);
	}

	public City createCity(String resourceUriString) {
		return new City(createModel(), new URIImpl(resourceUriString), true);
	}

	public ClassOrThing createClassOrThing() {
		return new ClassOrThing(createModel(), generateUniqueURI(), true);
	}

	public ClassOrThing createClassOrThing(URI resourceUri) {
		return new ClassOrThing(createModel(), resourceUri, true);
	}

	public ClassOrThing createClassOrThing(String resourceUriString) {
		return new ClassOrThing(createModel(), new URIImpl(resourceUriString), true);
	}

	public ClassOrThingOrPropertyOrAssociation createClassOrThingOrPropertyOrAssociation() {
		return new ClassOrThingOrPropertyOrAssociation(createModel(), generateUniqueURI(), true);
	}

	public ClassOrThingOrPropertyOrAssociation createClassOrThingOrPropertyOrAssociation(URI resourceUri) {
		return new ClassOrThingOrPropertyOrAssociation(createModel(), resourceUri, true);
	}

	public ClassOrThingOrPropertyOrAssociation createClassOrThingOrPropertyOrAssociation(String resourceUriString) {
		return new ClassOrThingOrPropertyOrAssociation(createModel(), new URIImpl(resourceUriString), true);
	}

	public ClassRole createClassRole() {
		return new ClassRole(createModel(), generateUniqueURI(), true);
	}

	public ClassRole createClassRole(URI resourceUri) {
		return new ClassRole(createModel(), resourceUri, true);
	}

	public ClassRole createClassRole(String resourceUriString) {
		return new ClassRole(createModel(), new URIImpl(resourceUriString), true);
	}

	public Collection createCollection() {
		return new Collection(createModel(), generateUniqueURI(), true);
	}

	public Collection createCollection(URI resourceUri) {
		return new Collection(createModel(), resourceUri, true);
	}

	public Collection createCollection(String resourceUriString) {
		return new Collection(createModel(), new URIImpl(resourceUriString), true);
	}

	public Contract createContract() {
		return new Contract(createModel(), generateUniqueURI(), true);
	}

	public Contract createContract(URI resourceUri) {
		return new Contract(createModel(), resourceUri, true);
	}

	public Contract createContract(String resourceUriString) {
		return new Contract(createModel(), new URIImpl(resourceUriString), true);
	}

	public Country createCountry() {
		return new Country(createModel(), generateUniqueURI(), true);
	}

	public Country createCountry(URI resourceUri) {
		return new Country(createModel(), resourceUri, true);
	}

	public Country createCountry(String resourceUriString) {
		return new Country(createModel(), new URIImpl(resourceUriString), true);
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

	public Event createEvent() {
		return new Event(createModel(), generateUniqueURI(), true);
	}

	public Event createEvent(URI resourceUri) {
		return new Event(createModel(), resourceUri, true);
	}

	public Event createEvent(String resourceUriString) {
		return new Event(createModel(), new URIImpl(resourceUriString), true);
	}

	public Locatable createLocatable() {
		return new Locatable(createModel(), generateUniqueURI(), true);
	}

	public Locatable createLocatable(URI resourceUri) {
		return new Locatable(createModel(), resourceUri, true);
	}

	public Locatable createLocatable(String resourceUriString) {
		return new Locatable(createModel(), new URIImpl(resourceUriString), true);
	}

	public Location createLocation() {
		return new Location(createModel(), generateUniqueURI(), true);
	}

	public Location createLocation(URI resourceUri) {
		return new Location(createModel(), resourceUri, true);
	}

	public Location createLocation(String resourceUriString) {
		return new Location(createModel(), new URIImpl(resourceUriString), true);
	}

	public LogicalMediaType createLogicalMediaType() {
		return new LogicalMediaType(createModel(), generateUniqueURI(), true);
	}

	public LogicalMediaType createLogicalMediaType(URI resourceUri) {
		return new LogicalMediaType(createModel(), resourceUri, true);
	}

	public LogicalMediaType createLogicalMediaType(String resourceUriString) {
		return new LogicalMediaType(createModel(), new URIImpl(resourceUriString), true);
	}

	public Meeting createMeeting() {
		return new Meeting(createModel(), generateUniqueURI(), true);
	}

	public Meeting createMeeting(URI resourceUri) {
		return new Meeting(createModel(), resourceUri, true);
	}

	public Meeting createMeeting(String resourceUriString) {
		return new Meeting(createModel(), new URIImpl(resourceUriString), true);
	}

	public Note createNote() {
		return new Note(createModel(), generateUniqueURI(), true);
	}

	public Note createNote(URI resourceUri) {
		return new Note(createModel(), resourceUri, true);
	}

	public Note createNote(String resourceUriString) {
		return new Note(createModel(), new URIImpl(resourceUriString), true);
	}

	public Organization createOrganization() {
		return new Organization(createModel(), generateUniqueURI(), true);
	}

	public Organization createOrganization(URI resourceUri) {
		return new Organization(createModel(), resourceUri, true);
	}

	public Organization createOrganization(String resourceUriString) {
		return new Organization(createModel(), new URIImpl(resourceUriString), true);
	}

	public OrganizationMember createOrganizationMember() {
		return new OrganizationMember(createModel(), generateUniqueURI(), true);
	}

	public OrganizationMember createOrganizationMember(URI resourceUri) {
		return new OrganizationMember(createModel(), resourceUri, true);
	}

	public OrganizationMember createOrganizationMember(String resourceUriString) {
		return new OrganizationMember(createModel(), new URIImpl(resourceUriString), true);
	}

	public Person createPerson() {
		return new Person(createModel(), generateUniqueURI(), true);
	}

	public Person createPerson(URI resourceUri) {
		return new Person(createModel(), resourceUri, true);
	}

	public Person createPerson(String resourceUriString) {
		return new Person(createModel(), new URIImpl(resourceUriString), true);
	}

	public PersonGroup createPersonGroup() {
		return new PersonGroup(createModel(), generateUniqueURI(), true);
	}

	public PersonGroup createPersonGroup(URI resourceUri) {
		return new PersonGroup(createModel(), resourceUri, true);
	}

	public PersonGroup createPersonGroup(String resourceUriString) {
		return new PersonGroup(createModel(), new URIImpl(resourceUriString), true);
	}

	public PersonRole createPersonRole() {
		return new PersonRole(createModel(), generateUniqueURI(), true);
	}

	public PersonRole createPersonRole(URI resourceUri) {
		return new PersonRole(createModel(), resourceUri, true);
	}

	public PersonRole createPersonRole(String resourceUriString) {
		return new PersonRole(createModel(), new URIImpl(resourceUriString), true);
	}

	public PersonalInformationModel createPersonalInformationModel() {
		return new PersonalInformationModel(createModel(), generateUniqueURI(), true);
	}

	public PersonalInformationModel createPersonalInformationModel(URI resourceUri) {
		return new PersonalInformationModel(createModel(), resourceUri, true);
	}

	public PersonalInformationModel createPersonalInformationModel(String resourceUriString) {
		return new PersonalInformationModel(createModel(), new URIImpl(resourceUriString), true);
	}

	public ProcessConcept createProcessConcept() {
		return new ProcessConcept(createModel(), generateUniqueURI(), true);
	}

	public ProcessConcept createProcessConcept(URI resourceUri) {
		return new ProcessConcept(createModel(), resourceUri, true);
	}

	public ProcessConcept createProcessConcept(String resourceUriString) {
		return new ProcessConcept(createModel(), new URIImpl(resourceUriString), true);
	}

	public Project createProject() {
		return new Project(createModel(), generateUniqueURI(), true);
	}

	public Project createProject(URI resourceUri) {
		return new Project(createModel(), resourceUri, true);
	}

	public Project createProject(String resourceUriString) {
		return new Project(createModel(), new URIImpl(resourceUriString), true);
	}

	public Room createRoom() {
		return new Room(createModel(), generateUniqueURI(), true);
	}

	public Room createRoom(URI resourceUri) {
		return new Room(createModel(), resourceUri, true);
	}

	public Room createRoom(String resourceUriString) {
		return new Room(createModel(), new URIImpl(resourceUriString), true);
	}

	public SocialEvent createSocialEvent() {
		return new SocialEvent(createModel(), generateUniqueURI(), true);
	}

	public SocialEvent createSocialEvent(URI resourceUri) {
		return new SocialEvent(createModel(), resourceUri, true);
	}

	public SocialEvent createSocialEvent(String resourceUriString) {
		return new SocialEvent(createModel(), new URIImpl(resourceUriString), true);
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

	public Tag createTag() {
		return new Tag(createModel(), generateUniqueURI(), true);
	}

	public Tag createTag(URI resourceUri) {
		return new Tag(createModel(), resourceUri, true);
	}

	public Tag createTag(String resourceUriString) {
		return new Tag(createModel(), new URIImpl(resourceUriString), true);
	}

	public Task createTask() {
		return new Task(createModel(), generateUniqueURI(), true);
	}

	public Task createTask(URI resourceUri) {
		return new Task(createModel(), resourceUri, true);
	}

	public Task createTask(String resourceUriString) {
		return new Task(createModel(), new URIImpl(resourceUriString), true);
	}

	public Thing createThing() {
		return new Thing(createModel(), generateUniqueURI(), true);
	}

	public Thing createThing(URI resourceUri) {
		return new Thing(createModel(), resourceUri, true);
	}

	public Thing createThing(String resourceUriString) {
		return new Thing(createModel(), new URIImpl(resourceUriString), true);
	}

	public Topic createTopic() {
		return new Topic(createModel(), generateUniqueURI(), true);
	}

	public Topic createTopic(URI resourceUri) {
		return new Topic(createModel(), resourceUri, true);
	}

	public Topic createTopic(String resourceUriString) {
		return new Topic(createModel(), new URIImpl(resourceUriString), true);
	}

	public User createUser() {
		return new User(createModel(), generateUniqueURI(), true);
	}

	public User createUser(URI resourceUri) {
		return new User(createModel(), resourceUri, true);
	}

	public User createUser(String resourceUriString) {
		return new User(createModel(), new URIImpl(resourceUriString), true);
	}

}