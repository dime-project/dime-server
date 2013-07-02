package eu.dime.ps.communications.web.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.storage.entities.User;
import javax.ws.rs.PathParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(value = "/questionaire", method = RequestMethod.GET)
    public ModelAndView forwardQuestionaire() {
        ModelAndView modelAndView = new ModelAndView("ajax_result");
        modelAndView.addObject("result", "Forwarding to questionaire...");
        return modelAndView;
    }

    @RequestMapping(value = "/notauthenticated", method = RequestMethod.GET)
    public ModelAndView checklogin() {
        logger.info("notauthenticated.");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("jspLoginMessage", "Sorry, wrong password. Please try again, or contact Administrator.");
        return modelAndView;
    }

    @RequestMapping(value = "/auth/@me", method = RequestMethod.GET)
    public ModelAndView getCurrentTenant() {
        ModelAndView modelAndView = new ModelAndView("ajax_result");
        String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
        String pw = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        User user = userManager.getByUsernameAndPassword(username, pw);
        if (user != null) {
            modelAndView.addObject("result", user.getTenant().getName());
        }
        return modelAndView;
    }
}