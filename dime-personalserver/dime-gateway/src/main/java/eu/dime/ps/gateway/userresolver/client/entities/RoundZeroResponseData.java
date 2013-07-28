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
