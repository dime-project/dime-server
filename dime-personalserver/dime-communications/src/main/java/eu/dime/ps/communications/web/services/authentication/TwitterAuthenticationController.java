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
