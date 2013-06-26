package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.tmo.*;

/**
 * A factory for the Java classes generated automatically for the TMO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class TMOFactory extends ResourceFactory {

	public AbilityCarrier createAbilityCarrier() {
		return new AbilityCarrier(createModel(), generateUniqueURI(), true);
	}

	public AbilityCarrier createAbilityCarrier(URI resourceUri) {
		return new AbilityCarrier(createModel(), resourceUri, true);
	}

	public AbilityCarrier createAbilityCarrier(String resourceUriString) {
		return new AbilityCarrier(createModel(), new URIImpl(resourceUriString), true);
	}

	public AbilityCarrierInvolvement createAbilityCarrierInvolvement() {
		return new AbilityCarrierInvolvement(createModel(), generateUniqueURI(), true);
	}

	public AbilityCarrierInvolvement createAbilityCarrierInvolvement(URI resourceUri) {
		return new AbilityCarrierInvolvement(createModel(), resourceUri, true);
	}

	public AbilityCarrierInvolvement createAbilityCarrierInvolvement(String resourceUriString) {
		return new AbilityCarrierInvolvement(createModel(), new URIImpl(resourceUriString), true);
	}

	public AbilityCarrierRole createAbilityCarrierRole() {
		return new AbilityCarrierRole(createModel(), generateUniqueURI(), true);
	}

	public AbilityCarrierRole createAbilityCarrierRole(URI resourceUri) {
		return new AbilityCarrierRole(createModel(), resourceUri, true);
	}

	public AbilityCarrierRole createAbilityCarrierRole(String resourceUriString) {
		return new AbilityCarrierRole(createModel(), new URIImpl(resourceUriString), true);
	}

	public AgentAbilityCarrier createAgentAbilityCarrier() {
		return new AgentAbilityCarrier(createModel(), generateUniqueURI(), true);
	}

	public AgentAbilityCarrier createAgentAbilityCarrier(URI resourceUri) {
		return new AgentAbilityCarrier(createModel(), resourceUri, true);
	}

	public AgentAbilityCarrier createAgentAbilityCarrier(String resourceUriString) {
		return new AgentAbilityCarrier(createModel(), new URIImpl(resourceUriString), true);
	}

	public AssociationDependency createAssociationDependency() {
		return new AssociationDependency(createModel(), generateUniqueURI(), true);
	}

	public AssociationDependency createAssociationDependency(URI resourceUri) {
		return new AssociationDependency(createModel(), resourceUri, true);
	}

	public AssociationDependency createAssociationDependency(String resourceUriString) {
		return new AssociationDependency(createModel(), new URIImpl(resourceUriString), true);
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

	public AttachmentRole createAttachmentRole() {
		return new AttachmentRole(createModel(), generateUniqueURI(), true);
	}

	public AttachmentRole createAttachmentRole(URI resourceUri) {
		return new AttachmentRole(createModel(), resourceUri, true);
	}

	public AttachmentRole createAttachmentRole(String resourceUriString) {
		return new AttachmentRole(createModel(), new URIImpl(resourceUriString), true);
	}

	public Delegability createDelegability() {
		return new Delegability(createModel(), generateUniqueURI(), true);
	}

	public Delegability createDelegability(URI resourceUri) {
		return new Delegability(createModel(), resourceUri, true);
	}

	public Delegability createDelegability(String resourceUriString) {
		return new Delegability(createModel(), new URIImpl(resourceUriString), true);
	}

	public Importance createImportance() {
		return new Importance(createModel(), generateUniqueURI(), true);
	}

	public Importance createImportance(URI resourceUri) {
		return new Importance(createModel(), resourceUri, true);
	}

	public Importance createImportance(String resourceUriString) {
		return new Importance(createModel(), new URIImpl(resourceUriString), true);
	}

	public Interdependence createInterdependence() {
		return new Interdependence(createModel(), generateUniqueURI(), true);
	}

	public Interdependence createInterdependence(URI resourceUri) {
		return new Interdependence(createModel(), resourceUri, true);
	}

	public Interdependence createInterdependence(String resourceUriString) {
		return new Interdependence(createModel(), new URIImpl(resourceUriString), true);
	}

	public PersonInvolvement createPersonInvolvement() {
		return new PersonInvolvement(createModel(), generateUniqueURI(), true);
	}

	public PersonInvolvement createPersonInvolvement(URI resourceUri) {
		return new PersonInvolvement(createModel(), resourceUri, true);
	}

	public PersonInvolvement createPersonInvolvement(String resourceUriString) {
		return new PersonInvolvement(createModel(), new URIImpl(resourceUriString), true);
	}

	public PersonInvolvementRole createPersonInvolvementRole() {
		return new PersonInvolvementRole(createModel(), generateUniqueURI(), true);
	}

	public PersonInvolvementRole createPersonInvolvementRole(URI resourceUri) {
		return new PersonInvolvementRole(createModel(), resourceUri, true);
	}

	public PersonInvolvementRole createPersonInvolvementRole(String resourceUriString) {
		return new PersonInvolvementRole(createModel(), new URIImpl(resourceUriString), true);
	}

	public PredecessorDependency createPredecessorDependency() {
		return new PredecessorDependency(createModel(), generateUniqueURI(), true);
	}

	public PredecessorDependency createPredecessorDependency(URI resourceUri) {
		return new PredecessorDependency(createModel(), resourceUri, true);
	}

	public PredecessorDependency createPredecessorDependency(String resourceUriString) {
		return new PredecessorDependency(createModel(), new URIImpl(resourceUriString), true);
	}

	public PredecessorSuccessorDependency createPredecessorSuccessorDependency() {
		return new PredecessorSuccessorDependency(createModel(), generateUniqueURI(), true);
	}

	public PredecessorSuccessorDependency createPredecessorSuccessorDependency(URI resourceUri) {
		return new PredecessorSuccessorDependency(createModel(), resourceUri, true);
	}

	public PredecessorSuccessorDependency createPredecessorSuccessorDependency(String resourceUriString) {
		return new PredecessorSuccessorDependency(createModel(), new URIImpl(resourceUriString), true);
	}

	public Priority createPriority() {
		return new Priority(createModel(), generateUniqueURI(), true);
	}

	public Priority createPriority(URI resourceUri) {
		return new Priority(createModel(), resourceUri, true);
	}

	public Priority createPriority(String resourceUriString) {
		return new Priority(createModel(), new URIImpl(resourceUriString), true);
	}

	public Role createRole() {
		return new Role(createModel(), generateUniqueURI(), true);
	}

	public Role createRole(URI resourceUri) {
		return new Role(createModel(), resourceUri, true);
	}

	public Role createRole(String resourceUriString) {
		return new Role(createModel(), new URIImpl(resourceUriString), true);
	}

	public SimilarityDependence createSimilarityDependence() {
		return new SimilarityDependence(createModel(), generateUniqueURI(), true);
	}

	public SimilarityDependence createSimilarityDependence(URI resourceUri) {
		return new SimilarityDependence(createModel(), resourceUri, true);
	}

	public SimilarityDependence createSimilarityDependence(String resourceUriString) {
		return new SimilarityDependence(createModel(), new URIImpl(resourceUriString), true);
	}

	public Skill createSkill() {
		return new Skill(createModel(), generateUniqueURI(), true);
	}

	public Skill createSkill(URI resourceUri) {
		return new Skill(createModel(), resourceUri, true);
	}

	public Skill createSkill(String resourceUriString) {
		return new Skill(createModel(), new URIImpl(resourceUriString), true);
	}

	public StateTypeRole createStateTypeRole() {
		return new StateTypeRole(createModel(), generateUniqueURI(), true);
	}

	public StateTypeRole createStateTypeRole(URI resourceUri) {
		return new StateTypeRole(createModel(), resourceUri, true);
	}

	public StateTypeRole createStateTypeRole(String resourceUriString) {
		return new StateTypeRole(createModel(), new URIImpl(resourceUriString), true);
	}

	public SuccessorDependency createSuccessorDependency() {
		return new SuccessorDependency(createModel(), generateUniqueURI(), true);
	}

	public SuccessorDependency createSuccessorDependency(URI resourceUri) {
		return new SuccessorDependency(createModel(), resourceUri, true);
	}

	public SuccessorDependency createSuccessorDependency(String resourceUriString) {
		return new SuccessorDependency(createModel(), new URIImpl(resourceUriString), true);
	}

	public SuperSubTaskDependency createSuperSubTaskDependency() {
		return new SuperSubTaskDependency(createModel(), generateUniqueURI(), true);
	}

	public SuperSubTaskDependency createSuperSubTaskDependency(URI resourceUri) {
		return new SuperSubTaskDependency(createModel(), resourceUri, true);
	}

	public SuperSubTaskDependency createSuperSubTaskDependency(String resourceUriString) {
		return new SuperSubTaskDependency(createModel(), new URIImpl(resourceUriString), true);
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

	public TaskContainer createTaskContainer() {
		return new TaskContainer(createModel(), generateUniqueURI(), true);
	}

	public TaskContainer createTaskContainer(URI resourceUri) {
		return new TaskContainer(createModel(), resourceUri, true);
	}

	public TaskContainer createTaskContainer(String resourceUriString) {
		return new TaskContainer(createModel(), new URIImpl(resourceUriString), true);
	}

	public TaskDependency createTaskDependency() {
		return new TaskDependency(createModel(), generateUniqueURI(), true);
	}

	public TaskDependency createTaskDependency(URI resourceUri) {
		return new TaskDependency(createModel(), resourceUri, true);
	}

	public TaskDependency createTaskDependency(String resourceUriString) {
		return new TaskDependency(createModel(), new URIImpl(resourceUriString), true);
	}

	public TaskPrivacyState createTaskPrivacyState() {
		return new TaskPrivacyState(createModel(), generateUniqueURI(), true);
	}

	public TaskPrivacyState createTaskPrivacyState(URI resourceUri) {
		return new TaskPrivacyState(createModel(), resourceUri, true);
	}

	public TaskPrivacyState createTaskPrivacyState(String resourceUriString) {
		return new TaskPrivacyState(createModel(), new URIImpl(resourceUriString), true);
	}

	public TaskState createTaskState() {
		return new TaskState(createModel(), generateUniqueURI(), true);
	}

	public TaskState createTaskState(URI resourceUri) {
		return new TaskState(createModel(), resourceUri, true);
	}

	public TaskState createTaskState(String resourceUriString) {
		return new TaskState(createModel(), new URIImpl(resourceUriString), true);
	}

	public TaskTransmission createTaskTransmission() {
		return new TaskTransmission(createModel(), generateUniqueURI(), true);
	}

	public TaskTransmission createTaskTransmission(URI resourceUri) {
		return new TaskTransmission(createModel(), resourceUri, true);
	}

	public TaskTransmission createTaskTransmission(String resourceUriString) {
		return new TaskTransmission(createModel(), new URIImpl(resourceUriString), true);
	}

	public TransmissionState createTransmissionState() {
		return new TransmissionState(createModel(), generateUniqueURI(), true);
	}

	public TransmissionState createTransmissionState(URI resourceUri) {
		return new TransmissionState(createModel(), resourceUri, true);
	}

	public TransmissionState createTransmissionState(String resourceUriString) {
		return new TransmissionState(createModel(), new URIImpl(resourceUriString), true);
	}

	public TransmissionType createTransmissionType() {
		return new TransmissionType(createModel(), generateUniqueURI(), true);
	}

	public TransmissionType createTransmissionType(URI resourceUri) {
		return new TransmissionType(createModel(), resourceUri, true);
	}

	public TransmissionType createTransmissionType(String resourceUriString) {
		return new TransmissionType(createModel(), new URIImpl(resourceUriString), true);
	}

	public UndirectedDependency createUndirectedDependency() {
		return new UndirectedDependency(createModel(), generateUniqueURI(), true);
	}

	public UndirectedDependency createUndirectedDependency(URI resourceUri) {
		return new UndirectedDependency(createModel(), resourceUri, true);
	}

	public UndirectedDependency createUndirectedDependency(String resourceUriString) {
		return new UndirectedDependency(createModel(), new URIImpl(resourceUriString), true);
	}

	public Urgency createUrgency() {
		return new Urgency(createModel(), generateUniqueURI(), true);
	}

	public Urgency createUrgency(URI resourceUri) {
		return new Urgency(createModel(), resourceUri, true);
	}

	public Urgency createUrgency(String resourceUriString) {
		return new Urgency(createModel(), new URIImpl(resourceUriString), true);
	}

}