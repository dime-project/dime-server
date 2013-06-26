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


public class PSEvaluationControllerTest extends Assert {

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
