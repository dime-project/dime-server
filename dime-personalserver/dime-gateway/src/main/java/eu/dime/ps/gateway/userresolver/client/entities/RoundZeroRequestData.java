package eu.dime.ps.gateway.userresolver.client.entities;

import java.util.Map;

public class RoundZeroRequestData {
	
	private String credentialName;
	private Map<String, String> issuanceValues;
	
	public String getCredentialName() {
		return credentialName;
	}
	public void setCredentialName(String credentialName) {
		this.credentialName = credentialName;
	}
	public Map<String, String> getIssuanceValues() {
		return issuanceValues;
	}
	public void setIssuanceValues(Map<String, String> issuanceValues) {
		this.issuanceValues = issuanceValues;
	}

	public RoundZeroRequestData() {}
	
	public RoundZeroRequestData(String credentialName,
			Map<String, String> issuanceValues) {
		this.credentialName = credentialName;
		this.issuanceValues = issuanceValues;
	}

	@Override
	public String toString() {
		return "RoundZeroRequestData [credentialName=" + credentialName
				+ ", issuanceValues=" + issuanceValues + "]";
	}

}
