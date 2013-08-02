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

package eu.dime.ps.communications.web.services.authentication;

import javax.servlet.http.HttpSession;
import javax.ws.rs.PathParam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import eu.dime.ps.gateway.service.external.oauth.TwitterServiceAdapter;

/**
 * 
 * @author Sophie Wrobel
 * @author Marc Planaguma
 * @author Ismael Rivera
 */
@Controller
@RequestMapping("/services/{said}/twitter")
public class TwitterAuthenticationController extends OAuthAuthenticationController<TwitterServiceAdapter> {

	@RequestMapping(value = "/connect", method = RequestMethod.GET)
	public ModelAndView requestConnection(HttpSession session, @PathVariable("said") String said) {
		return super.requestConnection(session, said, TwitterServiceAdapter.class);
	}

	@Override
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView authorizeCallback(HttpSession session,
			@PathVariable("said") String said,
			@RequestParam("id") String adapterId,
			@RequestParam("oauth_token") String oauthToken,
			@RequestParam("oauth_verifier") String verifierString) {
		return super.authorizeCallback(session, said, adapterId, oauthToken, verifierString);
	}
	
}
