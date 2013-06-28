/**
 * 
 */
package eu.dime.ps.controllers.eventlogger.manager;

import eu.dime.ps.controllers.eventlogger.data.LogType;
import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;

/**
 * @author  mplanaguma (BDigital)
 *
 */
public interface LogEventManager {
	
	/**
	 * @param type
	 * @param message
	 */
	public void setLog (LogType type, String message) throws EventLoggerException;


}
