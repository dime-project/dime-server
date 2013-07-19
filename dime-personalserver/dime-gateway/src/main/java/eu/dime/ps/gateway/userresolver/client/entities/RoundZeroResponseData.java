package eu.dime.ps.gateway.userresolver.client.entities;

public class RoundZeroResponseData {
	
	private String issuanceId;
	private String message;
	
	public String getIssuanceId() {
		return issuanceId;
	}

	public void setIssuanceId(String issuanceId) {
		this.issuanceId = issuanceId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RoundZeroResponseData(String issuanceId, String message) {
		this.issuanceId = issuanceId;
		this.message = message;
	}

	@Override
	public String toString() {
		return "RoundZeroResponseData [issuanceId=" + issuanceId + ", message="
				+ message + "]";
	}

}
