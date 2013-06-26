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
