package eu.dime.ps.controllers.evaluationtool;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.util.Calendar;

import eu.dime.commons.dto.Evaluation;
import eu.dime.ps.storage.entities.EvaluationData;
import eu.dime.ps.storage.manager.EntityFactory;

public class EvaluationManagerImpl implements EvaluationManager {

	private static final Logger logger = LoggerFactory.getLogger(EvaluationManagerImpl.class);

	
	private EntityFactory entityFactory;

	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

//	@Resource(name = "validViewIds")
//	private Properties validViewIds;
//	
//	@Resource(name = "validActionIds")
//	private Properties validActionIds;
	

	@Override
	@Transactional
	public boolean saveEvaluation(Evaluation ev,String said) {
		
		
    	boolean saved = false;
		// Validate parameters
		
		//  tenantId and action must be non-null, non-empty
		if (!StringUtils.isEmpty(ev.getTenantId()) && !StringUtils.isEmpty(ev.getAction())){
			// Date must be from 2012 onwards
			if (ev.getCreated() > 0L){
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(ev.getCreated());
				if (calendar.get(Calendar.YEAR) > 2011){
					// Date is correct. 
				
					EvaluationData ed = entityFactory.buildEvaluationData();					
					ed.setTenantId(ev.getTenantId());
			    	ed.setEvaluationdate(new Date(ev.getCreated()));
			    	ed.setClientid(ev.getClientId());
			    	ed.setViewstack(arrayToString(ev.getViewStack()));
			    	ed.setEvaluationaction(ev.getAction());
			    	ed.setCurrentplace(ev.getCurrPlace());
			    	ed.setCurrsituationid(ev.getCurrSituationId());   	
			    	ed.setInvolvedItems(ev.getInvolvedItems().toString());
			    	ed.persist();
			    	ed.flush();
			    	saved = true;
			    	
				}else{
					// Incongruent date
					logger.error("Cannot save evaluation. Incongruent date (year must be >= 2012): " + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
				}
			}else{
				// Invalid date
				logger.error("Cannot save evaluation. Impossible date (in miliseconds): " + ev.getCreated());
			}
		}else{
			logger.error("Cannot save evaluation. Parameters tenantId, view and action cannot be null/empty");
		}
		return saved;
	}




private String arrayToString(String[] viewStack) {
	StringBuilder builder = new StringBuilder();
	for(String s : viewStack) {
		builder.append('\'').append(s).append('\'').append(',');
	}
	 if (viewStack.length != 0) builder.deleteCharAt(builder.length()-1);
	return builder.toString();
}
	

}
