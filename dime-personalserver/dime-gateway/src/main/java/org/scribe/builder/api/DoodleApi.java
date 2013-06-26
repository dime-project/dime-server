package org.scribe.builder.api;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;


/**
 * TODO Describe the purpose of this class!
 *
 * @author Ingo.Siebert (<a href="mailto:Ingo.Siebert@cas.de">Ingo.Siebert@cas.de</a>)
 * @since 30.01.2013
 */
public class DoodleApi extends DefaultApi10a {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.scribe.builder.api.DefaultApi10a#getAccessTokenEndpoint()
	 */
	@Override
	public String getAccessTokenEndpoint() {
		return "http://www.doodle.com/api1/oauth/accesstoken";
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.scribe.builder.api.DefaultApi10a#getAuthorizationUrl(org.scribe.model.Token)
	 */
	@Override
	public String getAuthorizationUrl(final Token requestToken) {
		return String.format(
			"http://www.doodle.com/api1/oauth/authorizeConsumer?oauth_token=%s",
			requestToken.getToken());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.scribe.builder.api.DefaultApi10a#getRequestTokenEndpoint()
	 */
	@Override
	public String getRequestTokenEndpoint() {
		return "http://www.doodle.com/api1/oauth/requesttoken";
	}

}
