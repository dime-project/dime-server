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
