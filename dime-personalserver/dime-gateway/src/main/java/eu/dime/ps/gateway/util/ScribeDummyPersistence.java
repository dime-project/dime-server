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

package eu.dime.ps.gateway.util;

import java.util.Properties;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

@Service
public class ScribeDummyPersistence {
	
	Properties properties;
	
	public ScribeDummyPersistence() throws Exception{
		properties = 
			PropertiesLoaderUtils.loadAllProperties("services.properties");
	}
		
	public void saveAccessToken(String serviceName, Token token) {
		properties.setProperty(serviceName + ".accessToken", token.getToken());
		properties.setProperty(serviceName + ".accessSecret", token.getSecret());
	}
	
	public Token getAccessToken(String serviceName) {
		String accessToken = properties.getProperty(serviceName + ".accessToken");
		String accessSecret = properties.getProperty(serviceName + ".accessSecret");
		
		if(accessToken == null || accessSecret == null) {
			return null;
		}
		
		return new Token(accessToken, accessSecret);
	}
	
	//TODO: Make generic later -- Use semantic model or properties file for many External Services
	public OAuthService getLinkedInService() {
		String consumerKey = properties.getProperty("linkedin.consumerKey");
		String consumerSecret = properties.getProperty("linkedin.consumerSecret");
		
		if(consumerKey == null || consumerSecret == null) {
			return null;
		}
		
		return new ServiceBuilder()
    		.provider(LinkedInApi.class)
    		.apiKey(consumerKey)
    		.apiSecret(consumerSecret)
    	   	.build();
	}
}
