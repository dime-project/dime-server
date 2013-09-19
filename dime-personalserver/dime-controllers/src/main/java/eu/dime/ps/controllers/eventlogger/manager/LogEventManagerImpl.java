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

package eu.dime.ps.controllers.eventlogger.manager;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;
import eu.dime.ps.storage.entities.SphereLog;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

public class LogEventManagerImpl implements LogEventManager {

	private static final Logger logger = LoggerFactory.getLogger(LogEventManagerImpl.class);
	private EntityFactory entityFactory;

	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	@Override
	@Transactional
	public void setLog(String action, String type,Tenant tenant) throws EventLoggerException {
		logger.debug("logging server operation");
		if(tenant == null || action == null || type == null) logger.warn("operation could not be logged, there are parameters missing");
		else{
			try{
				SphereLog sphereLog = entityFactory.buildSphereLog();    				
				sphereLog.setAction(action);
				sphereLog.setType(type);
				sphereLog.setEvaluationdate(new Date(System.currentTimeMillis()));
				sphereLog.setTenantId(User.findLocalUserByTenant(tenant).getEvaluationId());
				sphereLog.persist();
				sphereLog.flush();  
			}
			catch(Exception e){ 
				throw  new EventLoggerException("operation: "+ action+" of type: "+type+" could not be logged",e);
			}
		}


	}   
} 
