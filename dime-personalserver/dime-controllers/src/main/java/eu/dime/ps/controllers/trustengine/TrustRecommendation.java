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

package eu.dime.ps.controllers.trustengine;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.URI;

public class TrustRecommendation {

	String message;
	
	boolean updateTrust;
	boolean updatePrivacy;
	boolean updateModel; 
	
	Map<String, TrustConflict> conflictMap = new HashMap<String, TrustConflict>();
	
	Logger logger = Logger.getLogger(TrustRecommendation.class);
	
	public boolean updateModel() {
		return conflictMap.isEmpty() && updateModel;
	}
	public TrustRecommendation setUpdateModel(boolean updateModel) {
		this.updateModel = updateModel;
		return this;
	}

	public TrustRecommendation(){
		this.message = "default";
		updateModel = true;
	}
	
	public TrustRecommendation(String message) {
		this.message = message;
		updateModel = true;
	}
	public TrustRecommendation(String message, boolean updateModel) {
		this.message = message;
		this.updateModel = updateModel;
	}

	public String getMessage() {
		return message;
	}

	public TrustRecommendation setMessage(String message) {
		this.message = message;
		return this;
	}

	/**
	 * Converts the Recommendation Object to a HashMap, with the attributes as key value pairs. 
	 * Necessary as response for the RequestBroker
	 * @return
	 */
	public Map<String, String> toMap() {
		Map <String,String> trustMap = new HashMap<String, String>();
		trustMap.put("message", message);
		return trustMap;
	}
	
	public Map<String, TrustConflict> getConflictMap() {
		return this.conflictMap;
	}
	
	public TrustRecommendation addConflict(int key, URI agent, URI thing) {
		this.conflictMap.put(String.valueOf(key), new TrustConflict("Trust conflict", agent.toString(), thing.toString()));
		this.setMessage("Detected "+ conflictMap.size()+"TrustConflict(s)");
		logger.info("Detected TrustConflict. Added to conflictMap with key: "+key+"agent:"+agent.toString()+" thing:"+thing.toString());
		return this;
	}
	
	public void addConflictMap(Map<? extends String, ? extends TrustConflict> newMap) {
		this.conflictMap.putAll(newMap);
	}
	

}
