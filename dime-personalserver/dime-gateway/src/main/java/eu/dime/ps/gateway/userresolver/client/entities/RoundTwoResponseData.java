package eu.dime.ps.gateway.userresolver.client.entities;

public class RoundTwoResponseData {
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RoundTwoResponseData(String message) {
		super();
		this.message = message;
	}

	@Override
	public String toString() {
		return "RoundTwoResponseData [message=" + message + "]";
	}
	
}
