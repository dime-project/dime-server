package eu.dime.ps.gateway.userresolver.client.entities;

import java.util.Map;

public class StructureInformation {
	
	private String issuerID;
	private String issuerURL;
	
	private String groupParameterURI;
	private String systemParameterURI;
	private String publicKeyURI;
	
	private Map<String, String> credentialStrucutures;

	public String getIssuerID() {
		return issuerID;
	}

	public void setIssuerID(String issuerID) {
		this.issuerID = issuerID;
	}

	public String getIssuerURL() {
		return issuerURL;
	}

	public void setIssuerURL(String issuerURL) {
		this.issuerURL = issuerURL;
	}

	public String getGroupParameterURI() {
		return groupParameterURI;
	}

	public void setGroupParameterURI(String groupParameterURI) {
		this.groupParameterURI = groupParameterURI;
	}

	public String getSystemParameterURI() {
		return systemParameterURI;
	}

	public void setSystemParameterURI(String systemParameterURI) {
		this.systemParameterURI = systemParameterURI;
	}

	public String getPublicKeyURI() {
		return publicKeyURI;
	}

	public void setPublicKeyURI(String publicKeyURI) {
		this.publicKeyURI = publicKeyURI;
	}

	public Map<String, String> getCredentialStrucutures() {
		return credentialStrucutures;
	}

	public void setCredentialStrucutures(
			Map<String, String> credentialStrucutures) {
		this.credentialStrucutures = credentialStrucutures;
	}

	public StructureInformation(String issuerID, String issuerURL,
			String groupParameterURI, String systemParameterURI,
			String publicKeyURI, Map<String, String> credentialStrucutures) {
		this.issuerID = issuerID;
		this.issuerURL = issuerURL;
		this.groupParameterURI = groupParameterURI;
		this.systemParameterURI = systemParameterURI;
		this.publicKeyURI = publicKeyURI;
		this.credentialStrucutures = credentialStrucutures;
	}

	@Override
	public String toString() {
		return "StructureInformation [issuerID=" + issuerID + ", issuerURL="
				+ issuerURL + ", groupParameterURI=" + groupParameterURI
				+ ", systemParameterURI=" + systemParameterURI
				+ ", publicKeyURI=" + publicKeyURI + ", credentialStrucutures="
				+ credentialStrucutures + "]";
	}
	
}
