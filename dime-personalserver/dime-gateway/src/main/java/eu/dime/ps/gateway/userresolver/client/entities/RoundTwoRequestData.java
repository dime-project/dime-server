package eu.dime.ps.gateway.userresolver.client.entities;

public class RoundTwoRequestData {
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RoundTwoRequestData() {}
	
	public RoundTwoRequestData(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "RoundTwoRequestData [message=" + message + "]";
	}
	
}
