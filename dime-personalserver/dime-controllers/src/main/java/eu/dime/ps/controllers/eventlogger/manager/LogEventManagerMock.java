package eu.dime.ps.controllers.eventlogger.manager;

import eu.dime.ps.controllers.eventlogger.data.LogType;
import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;

public class LogEventManagerMock implements LogEventManager {

	@Override
	public void setLog(LogType type, String message)
			throws EventLoggerException {

	}

}
