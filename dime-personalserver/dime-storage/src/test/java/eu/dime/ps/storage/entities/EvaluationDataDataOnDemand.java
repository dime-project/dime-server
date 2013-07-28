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

package eu.dime.ps.storage.entities;

import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Component
@Configurable
@RooDataOnDemand(entity = EvaluationData.class)
public class EvaluationDataDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<EvaluationData> data;

	

	public EvaluationData getNewTransientEvaluationData(int index) {
        eu.dime.ps.storage.entities.EvaluationData obj = new eu.dime.ps.storage.entities.EvaluationData();        
        setTenantId(obj, index);
        setEvaluationdate(obj, index);
        setClientid(obj, index);
        setViewStack(obj, index);
        setEvaluationaction(obj, index);
        setCurrentplace(obj, index);
        setCurrsituationid(obj, index);
        setInvolvelItemsid(obj, index);     
        return obj;
    }

	public void setTenantId(EvaluationData obj, int index) {
        java.lang.String serviceaccountid = "tenantid_" + index;
        obj.setTenantId(serviceaccountid);
    }	

	public void setEvaluationdate(EvaluationData obj, int index) {
        java.util.Date evaluationdate = new java.util.GregorianCalendar(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), java.util.Calendar.getInstance().get(java.util.Calendar.MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY), java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE), java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setEvaluationdate(evaluationdate);
    }

	public void setClientid(EvaluationData obj, int index) {
        java.lang.String clientid = "clientid_" + index;
        obj.setClientid(clientid);
    }

	public void setViewStack(EvaluationData obj, int index) {
        java.lang.String evaluationview = "evaluationview_" + index;
        obj.setViewstack(evaluationview);
    }

	public void setEvaluationaction(EvaluationData obj, int index) {
        java.lang.String evaluationaction = "evaluationaction_" + index;
        obj.setEvaluationaction(evaluationaction);
    }

	public void setCurrentplace(EvaluationData obj, int index) {
        java.lang.String currentplace = "currentplace_" + index;
        obj.setCurrentplace(currentplace);
    }

	public void setInvolvelItemsid(EvaluationData obj, int index) {
        java.lang.String currsituationid = "involvedItems_" + index;
        obj.setInvolvedItems(currsituationid);
    }	
	
	public void setCurrsituationid(EvaluationData obj, int index) {
        java.lang.String currsituationid = "currsituationid_" + index;
        obj.setCurrsituationid(currsituationid);
    }

	public EvaluationData getSpecificEvaluationData(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        EvaluationData obj = data.get(index);
        return EvaluationData.findEvaluationData(obj.getId());
    }

	public EvaluationData getRandomEvaluationData() {
        init();
        EvaluationData obj = data.get(rnd.nextInt(data.size()));
        return EvaluationData.findEvaluationData(obj.getId());
    }

	public boolean modifyEvaluationData(EvaluationData obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.EvaluationData.findEvaluationDataEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'EvaluationData' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.EvaluationData>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.EvaluationData obj = getNewTransientEvaluationData(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
