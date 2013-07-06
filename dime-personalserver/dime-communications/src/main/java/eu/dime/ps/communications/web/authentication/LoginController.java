package eu.dime.ps.communications.web.authentication;

import eu.dime.ps.communications.requestbroker.controllers.authentication.AuthenticationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.service.internal.DimeIPResolver;
import eu.dime.ps.storage.entities.User;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
         DimeIPResolver dir = new DimeIPResolver();
        try {

            modelAndView.addObject("result", "received from "+dir.getDimeDns()+": ip:"+dir.resolveSaid(said));
        } catch (NamingException ex) {
            modelAndView.addObject("result", "DNS resolve failed! Unable to resolve said: "+said+" at "+dir.getDimeDns());
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