package eu.dime.ps.controllers.evaluation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.dto.Evaluation;
import eu.dime.ps.controllers.evaluationtool.EvaluationManager;
@Ignore
@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class EvaluationManagerTest extends Assert {

	@Autowired
	private EvaluationManager evaluationManager;

	
	@Test
	public void testSaveEvaluation(){
		String guid = null;
		String callId = null;
		long date = -1L;
		String clientId = null;
		String[] view = null;
		String action = null;
		String currentPlace = null;
		String currentSituationId = null; 
		String SAID = "juan";
		Evaluation eval = new Evaluation();
		
		eval.setTenantId(callId);
		eval.setCreated(date);
		eval.setClientId(clientId);
		eval.setViewStack(view);
		eval.setAction(action);
		eval.setCurrPlace(currentPlace);
		eval.setCurrSituationId(currentSituationId);
		
		// TODO: will fail because guid, callid, view and action cannot be null
		boolean nullValues = evaluationManager.saveEvaluation(eval,SAID);
		
		assertEquals(false, nullValues);
		
		guid = "123456";
		callId = "123456";
		view = new String[]{"view"};
		action = "action";	
		
		
		eval.setTenantId(callId);
		eval.setViewStack(view);
		eval.setAction(action);
		// will fail because date is negative
		boolean impossibleDate = evaluationManager.saveEvaluation(eval,SAID);
										
		assertEquals(false, impossibleDate);
		
		date = 500000;
		
		eval.setCreated(date);
		
		// will fail because date's year must be greater than or equal to 2012
		boolean incongruentDate = evaluationManager.saveEvaluation(eval,SAID);
		
		assertEquals(false, incongruentDate);
		
		date = new Date().getTime();
		
		
		eval.setCreated(date);
		Map<String,Object> invItems = new HashMap<String,Object>();
		invItems.put("profile", 3);
		eval.setInvolvedItems(invItems);
		// will NOT fail :)
		boolean success = evaluationManager.saveEvaluation(eval,SAID);
		
		assertEquals(true, success);
	}
	
}
