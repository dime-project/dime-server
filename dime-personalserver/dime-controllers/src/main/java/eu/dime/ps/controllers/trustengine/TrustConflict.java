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
