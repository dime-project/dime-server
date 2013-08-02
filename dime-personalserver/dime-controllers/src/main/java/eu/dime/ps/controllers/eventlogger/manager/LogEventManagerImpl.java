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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.commons.dto.UserRegister;
import eu.dime.ps.controllers.eventlogger.data.LogType;
import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;
import eu.dime.ps.storage.entities.EvaluationData;
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
    public void setLog(LogType type, String evaluationId)
            throws EventLoggerException {

        switch (type) {
            case RESISTER:

                if (!StringUtils.isEmpty(evaluationId)) {
                    EvaluationData evaluationData = entityFactory.buildEvaluationData();

                    evaluationData.setClientid(UserRegister.CLIENT_WEB);
                    evaluationData.setEvaluationaction(EvaluationData.EVALUATIONDATA_ACTION_REGISTER);
                    evaluationData.setEvaluationdate(new Date(System.currentTimeMillis()));
                    evaluationData.setTenantId(evaluationId);
                    evaluationData.persist();
                    evaluationData.flush();
                } else {
                    logger.error("Cannot save evaluation. Parameters evaluationId, view and action cannot be null/empty");
                }
                break;

            default:
                break;
        }

    }
}
