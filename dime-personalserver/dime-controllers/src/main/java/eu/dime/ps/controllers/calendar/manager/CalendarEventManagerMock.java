package eu.dime.ps.controllers.calendar.manager;

import eu.dime.ps.controllers.calendar.data.CalendarEvent;
import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;

public class CalendarEventManagerMock implements CalendarEventManager {

	@Override
	public void addCalendarEvent(CalendarEvent event)
			throws EventLoggerException {

	}

	@Override
	public void modifyCalendarEvent(Integer id, CalendarEvent event)
			throws EventLoggerException {

	}

	@Override
	public void removeCalendarEvent(Integer id) throws EventLoggerException {

	}

}
