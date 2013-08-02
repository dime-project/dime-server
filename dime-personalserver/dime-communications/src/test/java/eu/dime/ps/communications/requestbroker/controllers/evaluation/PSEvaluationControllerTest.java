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

package eu.dime.ps.communications.requestbroker.controllers.evaluation;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Evaluation;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.evaluationtool.EvaluationManager;
import eu.dime.ps.semantic.model.ModelFactory;
import org.junit.Ignore;
import org.mockito.Mock;


public class PSEvaluationControllerTest extends Assert {

	@Mock UserManager userManager;

	private PSEvaluationController controller = new PSEvaluationController();
	private static final String said= "juan";

	protected ModelFactory modelFactory = new ModelFactory();

	public PSEvaluationControllerTest() {
		UserManager mockedUserManager = buildUserManager();
		EvaluationManager mockedEvaluationManager = buildEvaluationManager();
		controller.setUserManager(mockedUserManager);
		controller.setEvaluationManager(mockedEvaluationManager);

	}


	private EvaluationManager buildEvaluationManager() {
		EvaluationManager mockedManager = mock(EvaluationManager.class);		
		when(mockedManager.saveEvaluation(any(Evaluation.class),anyString())).thenReturn(true);
		return mockedManager;

	}


	private UserManager buildUserManager() {
		UserManager mockedManager = mock(UserManager.class);		
		
		return mockedManager;

	}




	private Request<Evaluation> buildSaveEvaluationRequest() {
		Request<Evaluation> request = new Request<Evaluation>();
		Message<Evaluation> message = new Message<Evaluation>();
		Data<Evaluation> data = new Data<Evaluation>();
		Evaluation ev = new Evaluation();
		ev.setGuid("dumbGUID");
		ev.setTenantId("hashedTenant");
		ev.setCreated(12345678);
		ev.setClientId("0.1.1");
		ev.setViewStack(new String[]{"search","all","group","people","main"});
		ev.setAction("add people to group");
		ev.setCurrPlace("hashedPlace");
		ev.setCurrSituationId("hashedSituation");

		data.addEntry(ev);
		message.setData(data);
		request.setMessage(message);
		return request;
	}

	

        /**
         * this test is ignored since the call requires a user mocked up correctly
         * I don't know how this would work ~~~~ Simon T.
         */
        @Ignore
	@Test
	public void testSaveEvaluationOK(){


		Request<Evaluation> request = buildSaveEvaluationRequest();
		Response<Evaluation> response = controller.saveEvaluation(said, request);
		assertNotNull(response);
		Evaluation ev = response.getMessage().getData().getEntries().iterator().next();
		assertNotNull(ev);	
		assertEquals(ev.getTenantId(),"hashedTenant");
		assertEquals(ev.getCreated(),12345678);
		assertEquals(ev.getClientId(),"0.1.1");
		assertEquals(ev.getAction(),"add people to group");
		assertEquals(ev.getCurrPlace(),"hashedPlace");
		assertEquals(ev.getCurrSituationId(),"hashedSituation");
		assertEquals(ev.getType(),"evaluation");
		assertEquals(ev.getGuid(),"dumbGUID");
	}





}
