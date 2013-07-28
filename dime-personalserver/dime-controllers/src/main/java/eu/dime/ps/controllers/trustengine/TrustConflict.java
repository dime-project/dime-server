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

package eu.dime.ps.controllers.trustengine;

public class TrustConflict {
	
	public TrustConflict(){
	}
	public TrustConflict(String message){
		this.message = message;
	}
	public TrustConflict(String message, String agentId, String thingId){
		this.message = message;
		this.agentId = agentId;
		this.thingId = thingId;
	}

	public String getAgentId() {
		return agentId;
	}
	public String getThingId() {
		return thingId;
	}
	public String getMessage() {
		return message;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public void setThingId(String thingId) {
		this.thingId = thingId;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	private String agentId;
	private String thingId;
	private String message;
	
	
	
}
