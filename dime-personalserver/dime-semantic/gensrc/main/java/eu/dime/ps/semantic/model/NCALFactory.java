package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.ncal.*;

/**
 * A factory for the Java classes generated automatically for the NCAL vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NCALFactory extends ResourceFactory {

	public AccessClassification createAccessClassification() {
		return new AccessClassification(createModel(), generateUniqueURI(), true);
	}

	public AccessClassification createAccessClassification(URI resourceUri) {
		return new AccessClassification(createModel(), resourceUri, true);
	}

	public AccessClassification createAccessClassification(String resourceUriString) {
		return new AccessClassification(createModel(), new URIImpl(resourceUriString), true);
	}

	public Alarm createAlarm() {
		return new Alarm(createModel(), generateUniqueURI(), true);
	}

	public Alarm createAlarm(URI resourceUri) {
		return new Alarm(createModel(), resourceUri, true);
	}

	public Alarm createAlarm(String resourceUriString) {
		return new Alarm(createModel(), new URIImpl(resourceUriString), true);
	}

	public AlarmAction createAlarmAction() {
		return new AlarmAction(createModel(), generateUniqueURI(), true);
	}

	public AlarmAction createAlarmAction(URI resourceUri) {
		return new AlarmAction(createModel(), resourceUri, true);
	}

	public AlarmAction createAlarmAction(String resourceUriString) {
		return new AlarmAction(createModel(), new URIImpl(resourceUriString), true);
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

	public AttachmentEncoding createAttachmentEncoding() {
		return new AttachmentEncoding(createModel(), generateUniqueURI(), true);
	}

	public AttachmentEncoding createAttachmentEncoding(URI resourceUri) {
		return new AttachmentEncoding(createModel(), resourceUri, true);
	}

	public AttachmentEncoding createAttachmentEncoding(String resourceUriString) {
		return new AttachmentEncoding(createModel(), new URIImpl(resourceUriString), true);
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

	public AttendeeOrOrganizer createAttendeeOrOrganizer() {
		return new AttendeeOrOrganizer(createModel(), generateUniqueURI(), true);
	}

	public AttendeeOrOrganizer createAttendeeOrOrganizer(URI resourceUri) {
		return new AttendeeOrOrganizer(createModel(), resourceUri, true);
	}

	public AttendeeOrOrganizer createAttendeeOrOrganizer(String resourceUriString) {
		return new AttendeeOrOrganizer(createModel(), new URIImpl(resourceUriString), true);
	}

	public AttendeeRole createAttendeeRole() {
		return new AttendeeRole(createModel(), generateUniqueURI(), true);
	}

	public AttendeeRole createAttendeeRole(URI resourceUri) {
		return new AttendeeRole(createModel(), resourceUri, true);
	}

	public AttendeeRole createAttendeeRole(String resourceUriString) {
		return new AttendeeRole(createModel(), new URIImpl(resourceUriString), true);
	}

	public BydayRulePart createBydayRulePart() {
		return new BydayRulePart(createModel(), generateUniqueURI(), true);
	}

	public BydayRulePart createBydayRulePart(URI resourceUri) {
		return new BydayRulePart(createModel(), resourceUri, true);
	}

	public BydayRulePart createBydayRulePart(String resourceUriString) {
		return new BydayRulePart(createModel(), new URIImpl(resourceUriString), true);
	}

	public Calendar createCalendar() {
		return new Calendar(createModel(), generateUniqueURI(), true);
	}

	public Calendar createCalendar(URI resourceUri) {
		return new Calendar(createModel(), resourceUri, true);
	}

	public Calendar createCalendar(String resourceUriString) {
		return new Calendar(createModel(), new URIImpl(resourceUriString), true);
	}

	public CalendarDataObject createCalendarDataObject() {
		return new CalendarDataObject(createModel(), generateUniqueURI(), true);
	}

	public CalendarDataObject createCalendarDataObject(URI resourceUri) {
		return new CalendarDataObject(createModel(), resourceUri, true);
	}

	public CalendarDataObject createCalendarDataObject(String resourceUriString) {
		return new CalendarDataObject(createModel(), new URIImpl(resourceUriString), true);
	}

	public CalendarScale createCalendarScale() {
		return new CalendarScale(createModel(), generateUniqueURI(), true);
	}

	public CalendarScale createCalendarScale(URI resourceUri) {
		return new CalendarScale(createModel(), resourceUri, true);
	}

	public CalendarScale createCalendarScale(String resourceUriString) {
		return new CalendarScale(createModel(), new URIImpl(resourceUriString), true);
	}

	public CalendarUserType createCalendarUserType() {
		return new CalendarUserType(createModel(), generateUniqueURI(), true);
	}

	public CalendarUserType createCalendarUserType(URI resourceUri) {
		return new CalendarUserType(createModel(), resourceUri, true);
	}

	public CalendarUserType createCalendarUserType(String resourceUriString) {
		return new CalendarUserType(createModel(), new URIImpl(resourceUriString), true);
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

	public EventStatus createEventStatus() {
		return new EventStatus(createModel(), generateUniqueURI(), true);
	}

	public EventStatus createEventStatus(URI resourceUri) {
		return new EventStatus(createModel(), resourceUri, true);
	}

	public EventStatus createEventStatus(String resourceUriString) {
		return new EventStatus(createModel(), new URIImpl(resourceUriString), true);
	}

	public Freebusy createFreebusy() {
		return new Freebusy(createModel(), generateUniqueURI(), true);
	}

	public Freebusy createFreebusy(URI resourceUri) {
		return new Freebusy(createModel(), resourceUri, true);
	}

	public Freebusy createFreebusy(String resourceUriString) {
		return new Freebusy(createModel(), new URIImpl(resourceUriString), true);
	}

	public FreebusyPeriod createFreebusyPeriod() {
		return new FreebusyPeriod(createModel(), generateUniqueURI(), true);
	}

	public FreebusyPeriod createFreebusyPeriod(URI resourceUri) {
		return new FreebusyPeriod(createModel(), resourceUri, true);
	}

	public FreebusyPeriod createFreebusyPeriod(String resourceUriString) {
		return new FreebusyPeriod(createModel(), new URIImpl(resourceUriString), true);
	}

	public FreebusyType createFreebusyType() {
		return new FreebusyType(createModel(), generateUniqueURI(), true);
	}

	public FreebusyType createFreebusyType(URI resourceUri) {
		return new FreebusyType(createModel(), resourceUri, true);
	}

	public FreebusyType createFreebusyType(String resourceUriString) {
		return new FreebusyType(createModel(), new URIImpl(resourceUriString), true);
	}

	public Journal createJournal() {
		return new Journal(createModel(), generateUniqueURI(), true);
	}

	public Journal createJournal(URI resourceUri) {
		return new Journal(createModel(), resourceUri, true);
	}

	public Journal createJournal(String resourceUriString) {
		return new Journal(createModel(), new URIImpl(resourceUriString), true);
	}

	public JournalStatus createJournalStatus() {
		return new JournalStatus(createModel(), generateUniqueURI(), true);
	}

	public JournalStatus createJournalStatus(URI resourceUri) {
		return new JournalStatus(createModel(), resourceUri, true);
	}

	public JournalStatus createJournalStatus(String resourceUriString) {
		return new JournalStatus(createModel(), new URIImpl(resourceUriString), true);
	}

	public NcalDateTime createNcalDateTime() {
		return new NcalDateTime(createModel(), generateUniqueURI(), true);
	}

	public NcalDateTime createNcalDateTime(URI resourceUri) {
		return new NcalDateTime(createModel(), resourceUri, true);
	}

	public NcalDateTime createNcalDateTime(String resourceUriString) {
		return new NcalDateTime(createModel(), new URIImpl(resourceUriString), true);
	}

	public NcalPeriod createNcalPeriod() {
		return new NcalPeriod(createModel(), generateUniqueURI(), true);
	}

	public NcalPeriod createNcalPeriod(URI resourceUri) {
		return new NcalPeriod(createModel(), resourceUri, true);
	}

	public NcalPeriod createNcalPeriod(String resourceUriString) {
		return new NcalPeriod(createModel(), new URIImpl(resourceUriString), true);
	}

	public NcalTimeEntity createNcalTimeEntity() {
		return new NcalTimeEntity(createModel(), generateUniqueURI(), true);
	}

	public NcalTimeEntity createNcalTimeEntity(URI resourceUri) {
		return new NcalTimeEntity(createModel(), resourceUri, true);
	}

	public NcalTimeEntity createNcalTimeEntity(String resourceUriString) {
		return new NcalTimeEntity(createModel(), new URIImpl(resourceUriString), true);
	}

	public Organizer createOrganizer() {
		return new Organizer(createModel(), generateUniqueURI(), true);
	}

	public Organizer createOrganizer(URI resourceUri) {
		return new Organizer(createModel(), resourceUri, true);
	}

	public Organizer createOrganizer(String resourceUriString) {
		return new Organizer(createModel(), new URIImpl(resourceUriString), true);
	}

	public ParticipationStatus createParticipationStatus() {
		return new ParticipationStatus(createModel(), generateUniqueURI(), true);
	}

	public ParticipationStatus createParticipationStatus(URI resourceUri) {
		return new ParticipationStatus(createModel(), resourceUri, true);
	}

	public ParticipationStatus createParticipationStatus(String resourceUriString) {
		return new ParticipationStatus(createModel(), new URIImpl(resourceUriString), true);
	}

	public RecurrenceFrequency createRecurrenceFrequency() {
		return new RecurrenceFrequency(createModel(), generateUniqueURI(), true);
	}

	public RecurrenceFrequency createRecurrenceFrequency(URI resourceUri) {
		return new RecurrenceFrequency(createModel(), resourceUri, true);
	}

	public RecurrenceFrequency createRecurrenceFrequency(String resourceUriString) {
		return new RecurrenceFrequency(createModel(), new URIImpl(resourceUriString), true);
	}

	public RecurrenceIdentifier createRecurrenceIdentifier() {
		return new RecurrenceIdentifier(createModel(), generateUniqueURI(), true);
	}

	public RecurrenceIdentifier createRecurrenceIdentifier(URI resourceUri) {
		return new RecurrenceIdentifier(createModel(), resourceUri, true);
	}

	public RecurrenceIdentifier createRecurrenceIdentifier(String resourceUriString) {
		return new RecurrenceIdentifier(createModel(), new URIImpl(resourceUriString), true);
	}

	public RecurrenceIdentifierRange createRecurrenceIdentifierRange() {
		return new RecurrenceIdentifierRange(createModel(), generateUniqueURI(), true);
	}

	public RecurrenceIdentifierRange createRecurrenceIdentifierRange(URI resourceUri) {
		return new RecurrenceIdentifierRange(createModel(), resourceUri, true);
	}

	public RecurrenceIdentifierRange createRecurrenceIdentifierRange(String resourceUriString) {
		return new RecurrenceIdentifierRange(createModel(), new URIImpl(resourceUriString), true);
	}

	public RecurrenceRule createRecurrenceRule() {
		return new RecurrenceRule(createModel(), generateUniqueURI(), true);
	}

	public RecurrenceRule createRecurrenceRule(URI resourceUri) {
		return new RecurrenceRule(createModel(), resourceUri, true);
	}

	public RecurrenceRule createRecurrenceRule(String resourceUriString) {
		return new RecurrenceRule(createModel(), new URIImpl(resourceUriString), true);
	}

	public RequestStatus createRequestStatus() {
		return new RequestStatus(createModel(), generateUniqueURI(), true);
	}

	public RequestStatus createRequestStatus(URI resourceUri) {
		return new RequestStatus(createModel(), resourceUri, true);
	}

	public RequestStatus createRequestStatus(String resourceUriString) {
		return new RequestStatus(createModel(), new URIImpl(resourceUriString), true);
	}

	public TimeTransparency createTimeTransparency() {
		return new TimeTransparency(createModel(), generateUniqueURI(), true);
	}

	public TimeTransparency createTimeTransparency(URI resourceUri) {
		return new TimeTransparency(createModel(), resourceUri, true);
	}

	public TimeTransparency createTimeTransparency(String resourceUriString) {
		return new TimeTransparency(createModel(), new URIImpl(resourceUriString), true);
	}

	public Timezone createTimezone() {
		return new Timezone(createModel(), generateUniqueURI(), true);
	}

	public Timezone createTimezone(URI resourceUri) {
		return new Timezone(createModel(), resourceUri, true);
	}

	public Timezone createTimezone(String resourceUriString) {
		return new Timezone(createModel(), new URIImpl(resourceUriString), true);
	}

	public TimezoneObservance createTimezoneObservance() {
		return new TimezoneObservance(createModel(), generateUniqueURI(), true);
	}

	public TimezoneObservance createTimezoneObservance(URI resourceUri) {
		return new TimezoneObservance(createModel(), resourceUri, true);
	}

	public TimezoneObservance createTimezoneObservance(String resourceUriString) {
		return new TimezoneObservance(createModel(), new URIImpl(resourceUriString), true);
	}

	public Todo createTodo() {
		return new Todo(createModel(), generateUniqueURI(), true);
	}

	public Todo createTodo(URI resourceUri) {
		return new Todo(createModel(), resourceUri, true);
	}

	public Todo createTodo(String resourceUriString) {
		return new Todo(createModel(), new URIImpl(resourceUriString), true);
	}

	public TodoStatus createTodoStatus() {
		return new TodoStatus(createModel(), generateUniqueURI(), true);
	}

	public TodoStatus createTodoStatus(URI resourceUri) {
		return new TodoStatus(createModel(), resourceUri, true);
	}

	public TodoStatus createTodoStatus(String resourceUriString) {
		return new TodoStatus(createModel(), new URIImpl(resourceUriString), true);
	}

	public Trigger createTrigger() {
		return new Trigger(createModel(), generateUniqueURI(), true);
	}

	public Trigger createTrigger(URI resourceUri) {
		return new Trigger(createModel(), resourceUri, true);
	}

	public Trigger createTrigger(String resourceUriString) {
		return new Trigger(createModel(), new URIImpl(resourceUriString), true);
	}

	public TriggerRelation createTriggerRelation() {
		return new TriggerRelation(createModel(), generateUniqueURI(), true);
	}

	public TriggerRelation createTriggerRelation(URI resourceUri) {
		return new TriggerRelation(createModel(), resourceUri, true);
	}

	public TriggerRelation createTriggerRelation(String resourceUriString) {
		return new TriggerRelation(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfAlarmEventFreebusyJournalTodo createUnionOfAlarmEventFreebusyJournalTodo() {
		return new UnionOfAlarmEventFreebusyJournalTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfAlarmEventFreebusyJournalTodo createUnionOfAlarmEventFreebusyJournalTodo(URI resourceUri) {
		return new UnionOfAlarmEventFreebusyJournalTodo(createModel(), resourceUri, true);
	}

	public UnionOfAlarmEventFreebusyJournalTodo createUnionOfAlarmEventFreebusyJournalTodo(String resourceUriString) {
		return new UnionOfAlarmEventFreebusyJournalTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfAlarmEventFreebusyTodo createUnionOfAlarmEventFreebusyTodo() {
		return new UnionOfAlarmEventFreebusyTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfAlarmEventFreebusyTodo createUnionOfAlarmEventFreebusyTodo(URI resourceUri) {
		return new UnionOfAlarmEventFreebusyTodo(createModel(), resourceUri, true);
	}

	public UnionOfAlarmEventFreebusyTodo createUnionOfAlarmEventFreebusyTodo(String resourceUriString) {
		return new UnionOfAlarmEventFreebusyTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfAlarmEventJournalTodo createUnionOfAlarmEventJournalTodo() {
		return new UnionOfAlarmEventJournalTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfAlarmEventJournalTodo createUnionOfAlarmEventJournalTodo(URI resourceUri) {
		return new UnionOfAlarmEventJournalTodo(createModel(), resourceUri, true);
	}

	public UnionOfAlarmEventJournalTodo createUnionOfAlarmEventJournalTodo(String resourceUriString) {
		return new UnionOfAlarmEventJournalTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfAlarmEventTodo createUnionOfAlarmEventTodo() {
		return new UnionOfAlarmEventTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfAlarmEventTodo createUnionOfAlarmEventTodo(URI resourceUri) {
		return new UnionOfAlarmEventTodo(createModel(), resourceUri, true);
	}

	public UnionOfAlarmEventTodo createUnionOfAlarmEventTodo(String resourceUriString) {
		return new UnionOfAlarmEventTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfEventFreebusy createUnionOfEventFreebusy() {
		return new UnionOfEventFreebusy(createModel(), generateUniqueURI(), true);
	}

	public UnionOfEventFreebusy createUnionOfEventFreebusy(URI resourceUri) {
		return new UnionOfEventFreebusy(createModel(), resourceUri, true);
	}

	public UnionOfEventFreebusy createUnionOfEventFreebusy(String resourceUriString) {
		return new UnionOfEventFreebusy(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfEventFreebusyJournalTodo createUnionOfEventFreebusyJournalTodo() {
		return new UnionOfEventFreebusyJournalTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfEventFreebusyJournalTodo createUnionOfEventFreebusyJournalTodo(URI resourceUri) {
		return new UnionOfEventFreebusyJournalTodo(createModel(), resourceUri, true);
	}

	public UnionOfEventFreebusyJournalTodo createUnionOfEventFreebusyJournalTodo(String resourceUriString) {
		return new UnionOfEventFreebusyJournalTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfEventJournalTimezoneTodo createUnionOfEventJournalTimezoneTodo() {
		return new UnionOfEventJournalTimezoneTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfEventJournalTimezoneTodo createUnionOfEventJournalTimezoneTodo(URI resourceUri) {
		return new UnionOfEventJournalTimezoneTodo(createModel(), resourceUri, true);
	}

	public UnionOfEventJournalTimezoneTodo createUnionOfEventJournalTimezoneTodo(String resourceUriString) {
		return new UnionOfEventJournalTimezoneTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfEventJournalTodo createUnionOfEventJournalTodo() {
		return new UnionOfEventJournalTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfEventJournalTodo createUnionOfEventJournalTodo(URI resourceUri) {
		return new UnionOfEventJournalTodo(createModel(), resourceUri, true);
	}

	public UnionOfEventJournalTodo createUnionOfEventJournalTodo(String resourceUriString) {
		return new UnionOfEventJournalTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfEventTodo createUnionOfEventTodo() {
		return new UnionOfEventTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfEventTodo createUnionOfEventTodo(URI resourceUri) {
		return new UnionOfEventTodo(createModel(), resourceUri, true);
	}

	public UnionOfEventTodo createUnionOfEventTodo(String resourceUriString) {
		return new UnionOfEventTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo createUnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo() {
		return new UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo createUnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo(URI resourceUri) {
		return new UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo(createModel(), resourceUri, true);
	}

	public UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo createUnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo(String resourceUriString) {
		return new UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfTimezoneObservanceEventFreebusyTimezoneTodo createUnionOfTimezoneObservanceEventFreebusyTimezoneTodo() {
		return new UnionOfTimezoneObservanceEventFreebusyTimezoneTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfTimezoneObservanceEventFreebusyTimezoneTodo createUnionOfTimezoneObservanceEventFreebusyTimezoneTodo(URI resourceUri) {
		return new UnionOfTimezoneObservanceEventFreebusyTimezoneTodo(createModel(), resourceUri, true);
	}

	public UnionOfTimezoneObservanceEventFreebusyTimezoneTodo createUnionOfTimezoneObservanceEventFreebusyTimezoneTodo(String resourceUriString) {
		return new UnionOfTimezoneObservanceEventFreebusyTimezoneTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionOfTimezoneObservanceEventJournalTimezoneTodo createUnionOfTimezoneObservanceEventJournalTimezoneTodo() {
		return new UnionOfTimezoneObservanceEventJournalTimezoneTodo(createModel(), generateUniqueURI(), true);
	}

	public UnionOfTimezoneObservanceEventJournalTimezoneTodo createUnionOfTimezoneObservanceEventJournalTimezoneTodo(URI resourceUri) {
		return new UnionOfTimezoneObservanceEventJournalTimezoneTodo(createModel(), resourceUri, true);
	}

	public UnionOfTimezoneObservanceEventJournalTimezoneTodo createUnionOfTimezoneObservanceEventJournalTimezoneTodo(String resourceUriString) {
		return new UnionOfTimezoneObservanceEventJournalTimezoneTodo(createModel(), new URIImpl(resourceUriString), true);
	}

	public UnionParentClass createUnionParentClass() {
		return new UnionParentClass(createModel(), generateUniqueURI(), true);
	}

	public UnionParentClass createUnionParentClass(URI resourceUri) {
		return new UnionParentClass(createModel(), resourceUri, true);
	}

	public UnionParentClass createUnionParentClass(String resourceUriString) {
		return new UnionParentClass(createModel(), new URIImpl(resourceUriString), true);
	}

	public Weekday createWeekday() {
		return new Weekday(createModel(), generateUniqueURI(), true);
	}

	public Weekday createWeekday(URI resourceUri) {
		return new Weekday(createModel(), resourceUri, true);
	}

	public Weekday createWeekday(String resourceUriString) {
		return new Weekday(createModel(), new URIImpl(resourceUriString), true);
	}

}