/**
 * 
 */
package eu.dime.ps.controllers.calendar.manager;

import eu.dime.ps.controllers.calendar.data.CalendarEvent;
import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;

/**
 * @author  mplanaguma (BDigital)
 *
 */
public interface CalendarEventManager {
	
	/**
	 * @param event
	 */
	public void addCalendarEvent(CalendarEvent event) throws EventLoggerException;
	
	/**
	 * @param id
	 * @param event
	 */
	public void modifyCalendarEvent(Integer id, CalendarEvent event) throws EventLoggerException;
	
	/**
	 * @param id
	 */
	public void removeCalendarEvent (Integer id) throws EventLoggerException;
	
	

}
