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

package eu.dime.ps.controllers.eventlogger;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.impl.Assert;

import eu.dime.ps.controllers.eventlogger.data.LogType;
import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;
import eu.dime.ps.controllers.eventlogger.manager.LogEventManager;
import eu.dime.ps.storage.entities.EvaluationData;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-eventlogger-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
public class LogEventManagerTest extends Assert {

	@Autowired
	private LogEventManager logEventManager;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Transactional
	public void test() throws EventLoggerException {
		
		String randomTenant = "tenant_" + UUID.randomUUID();
		
		logEventManager.setLog(LogType.RESISTER, randomTenant);
		
		List<EvaluationData> list = EvaluationData.findAllLogRegisterEvaluationDatas();
		
		Boolean result = false;
		
		for (EvaluationData evaluationData : list) {
			
			if(randomTenant.equals(evaluationData.getTenantId())){
				result = true;
				break;
			}
		}
		
		assertTrue("no added the registed log correctly!", result);

	}

}
