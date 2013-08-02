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

package com.ontotext.kim.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.openrdf.model.Value;

/**
 * Gathers statistics on the classes of the entities loaded from the vocabulary.
 * 
 * @author mnozchev
 */
public class ClassStatisticListener extends EntitiesQueryListener {
	private static final Logger log = Logger.getLogger(StatisticListener.class);
	
    private final EntitiesQueryListener innerListener;
    private final Map<String, Integer> countByClass = new HashMap<String, Integer>();
    
    public void endTableQueryResult() throws IOException {    
        // We don't call super here on purpose. Calling the inner listener should take care of that.
        this.innerListener.endTableQueryResult();
        for (Entry<String, Integer> ent : countByClass.entrySet()) {
            log.debug(ent.getKey() + " : " + ent.getValue());
        }
    }

    public void startTableQueryResult(String[] arg0) throws IOException {
        // We don't call super here on purpose. Calling the inner listener should take care of that.
        this.innerListener.startTableQueryResult(arg0);
        countByClass.clear();
    }

    public void startTableQueryResult() throws IOException {
        // We don't call super here on purpose. Calling the inner listener should take care of that.
        this.innerListener.startTableQueryResult();
        countByClass.clear();
    }
    
    @Override
    protected void addEntity(String instUri, String classUri, String aliasLabel) {
        // We don't call super here on purpose. Calling the inner listener should take care of that.
        this.innerListener.addEntity(instUri, classUri, aliasLabel);
        Integer oldValue = countByClass.get(classUri); 
        countByClass.put(classUri, oldValue == null ? 0 : oldValue + 1);
    }
    
    public ClassStatisticListener(EntitiesQueryListener innerListener) {
        this.innerListener = innerListener;
    }

    @Override
    public void endTuple() throws IOException {    	
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
    	this.innerListener.endTuple();
    }
    
    @Override
    public void tupleValue(Value value) throws IOException {
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
    	this.innerListener.tupleValue(value);
    }
    
    @Override
    public void startTuple() throws IOException {
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
    	this.innerListener.startTuple();
    }
}
