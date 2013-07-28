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

package org.scribe.builder.api;

import org.scribe.model.Token;

public class FitbitApi extends DefaultApi10a {
	
	private static final String AUTHORIZE_URL = "http://www.fitbit.com/oauth/authenticate?oauth_token=%s";

	@Override
	public String getRequestTokenEndpoint() {
		return "http://api.fitbit.com/oauth/request_token";
	}

	@Override
	public String getAccessTokenEndpoint() {
		return "http://api.fitbit.com/oauth/access_token";
	}

	@Override
	public String getAuthorizationUrl(Token requestToken) {
		return String.format(AUTHORIZE_URL, requestToken.getToken());
	}

}
