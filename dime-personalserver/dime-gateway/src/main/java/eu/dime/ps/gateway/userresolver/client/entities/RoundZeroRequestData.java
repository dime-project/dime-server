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
