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

package eu.dime.ps.communications.web.authentication;

import eu.dime.ps.communications.requestbroker.controllers.authentication.AuthenticationController;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;
import eu.dime.ps.controllers.eventlogger.manager.LogEventManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.dns.DimeDNSException;
import eu.dime.ps.gateway.service.external.DimeUserResolverServiceAdapter;
import eu.dime.ps.gateway.service.internal.DimeIPResolver;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.storage.entities.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Level;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Request mapping for login and logout calls
 *
 * @author <a href="mailto:heupel@wiwi.uni-siegen.de"> Marcel Heupel
 * (mheupel)</a>
 */
@Controller
@RequestMapping("/access")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserManager userManager;

    @Autowired
    private ServiceGateway serviceGateway;
    
    private LogEventManager logEventManager;

	@Autowired
	public void setLogEventManager(LogEventManager logEventManager) {
		this.logEventManager = logEventManager;
	}

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        logger.info("Login page accessed");
        return modelAndView;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout() {
        logger.info("Logout.");
        try {
			logEventManager.setLog("logout", "user");
		} catch (EventLoggerException e) {
			logger.error("Login operation could not be logged",e);
		}
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("jspLoginMessage", "You have been logged out.");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("jspContainerId", "registerContainer");
        logger.info("Register page accessed");
        return modelAndView;
    }

    @RequestMapping(value = "/howto", method = RequestMethod.GET)
    public ModelAndView howto() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("jspContainerId", "howtoContainer");
        logger.info("howto page accessed");
        return modelAndView;
    }

    @RequestMapping(value = "/conditions", method = RequestMethod.GET)
    public ModelAndView conditions(@RequestParam(value = "lang", required = false) String language) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        if (language != null && language.equals("de")) {
            modelAndView.addObject("jspContainerId", "usageTermsContainer_DE");

        } else {
            modelAndView.addObject("jspContainerId", "usageTermsContainer");
        }
        logger.info("conditions page accessed");
        return modelAndView;
    }

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public ModelAndView about(@RequestParam(value = "lang", required = false) String language) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        if (language != null && language.equals("de")) {
            modelAndView.addObject("jspContainerId", "aboutContainer_DE");
        } else {
            modelAndView.addObject("jspContainerId", "aboutContainer");
        }
        logger.info("about page accessed");
        return modelAndView;
    }

    @RequestMapping(value = "/privacypolicy", method = RequestMethod.GET)
    public ModelAndView privacypolicy(@RequestParam(value = "lang", required = false) String language) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        if (language != null && language.equals("de")) {
            modelAndView.addObject("jspContainerId", "privacyPolicyContainer_DE");

        } else {
            modelAndView.addObject("jspContainerId", "privacyPolicyContainer");
        }
        logger.info("privacypolicy page accessed");
        return modelAndView;
    }


    @RequestMapping(value = "/notauthenticated", method = RequestMethod.GET)
    public ModelAndView checklogin() {
        logger.info("notauthenticated.");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("jspLoginMessage", "Sorry, wrong password or username. Please try again.");
        return modelAndView;
    }



    @RequestMapping(value = "/auth/@me", method = RequestMethod.GET)
    public ModelAndView getCurrentTenant() {
        ModelAndView modelAndView = new ModelAndView("ajax_result");
        String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
        String pw = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        User user = userManager.getByUsernameAndPassword(username, pw);
        try {
			logEventManager.setLog("login", "user");
		} catch (EventLoggerException e) {
			logger.error("Login operation could not be logged",e);
		}
        if (user != null) {
            modelAndView.addObject("result", user.getTenant().getName());
        }
        return modelAndView;
    }


    @RequestMapping(value = "/questionaire", method = RequestMethod.GET)
    public String forwardQuestionaire(@RequestParam(value = "lang", required = false) String language) {
        User user = getCurrentUser();

        String myLanguage=language;
        if (language==null){
            myLanguage="en";
        }

        String forwardUrl;
        String encodedId="";
        if (user!=null){
            try {
                encodedId = URLEncoder.encode(user.getEvaluationId(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                java.util.logging.Logger.getLogger(AuthenticationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        forwardUrl= "http://www.survey-hci.iao.fraunhofer.de/index.php?sid=18346&18346X319X1680="+encodedId+"&lang="+myLanguage;

       
        return "redirect:"+forwardUrl;
    }

	@RequestMapping(value = "/resolveSaid", method = RequestMethod.GET)
	public ModelAndView resolveSaid(@RequestParam(value = "said", required = true) String said) {
		ModelAndView modelAndView = new ModelAndView("ajax_result");
		try {
			DimeIPResolver resolver = new DimeIPResolver();
			modelAndView.addObject("result", "received from "+resolver.getDimeDns()+": ip:"+resolver.resolve(said));
		} catch (DimeDNSException ex) {
			modelAndView.addObject("result", "DNS resolve failed! Unable to resolve said: "+said+" at DNS."
					+"<br/>"+ex.getClass().getName()
					+"<br/>"+ex.getMessage());
		}
		return modelAndView;
	}


    @RequestMapping(value = "/ursRegisterTest", method = RequestMethod.GET)
    public ModelAndView ursRegisterTest() {

         ModelAndView modelAndView = new ModelAndView("ajax_result");
        try {
            DimeUserResolverServiceAdapter userResolver =serviceGateway.getDimeUserResolverServiceAdapter();
            String id = UUID.randomUUID().toString();
            String result = userResolver.registerAtURS(id,"dummyUserTest"+id, "dummyUserTestfirstName", "dummyUserTestSurName" );
            modelAndView.addObject("result",result);

        } catch (Exception ex) {
            modelAndView.addObject("result", "Exception occured:"
                    +"<br/>"+ex.getClass().getName()
                    +"<br/>"+ex.getMessage());
        }

        return modelAndView;
    }


    private User getCurrentUser() {


        String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
        String pw = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        User user = userManager.getByUsernameAndPassword(username, pw);

        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

}