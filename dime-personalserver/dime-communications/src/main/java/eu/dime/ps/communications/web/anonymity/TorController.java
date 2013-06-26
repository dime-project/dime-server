package eu.dime.ps.communications.web.anonymity;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.dime.ps.controllers.security.anonymity.StandaloneTorService;

/**
 * Test Controller for manual hidden service creation
 * @author USIEGEN (philipp, marcel) 
 *
 */
@Controller
@RequestMapping("/anonymity/")
public class TorController {
	private final static Logger logger = Logger.getLogger(TorController.class);
	
	@Autowired
	private StandaloneTorService standaloneTorService;
		
	@RequestMapping("/")
	public String main(Model model) {
		
		model.addAttribute("addresses", standaloneTorService.getOnionAdresses());
		return "oniontest";
	}
	
	@RequestMapping(value = "refresh", method = RequestMethod.GET)
	public String refresh() throws Exception{
		standaloneTorService.refresh();
		return "redirect:";
	}
	
	@RequestMapping(value = "start", method = RequestMethod.POST)
	public String startTor() {
		standaloneTorService.startTor();
		return "redirect:";
	}
	
	@RequestMapping(value = "stop", method = RequestMethod.POST)
	public String stopTor() {
		standaloneTorService.stopTor();
		return "redirect:";
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String addService() throws Exception{
		standaloneTorService.addHiddenService(
				String.valueOf(System.currentTimeMillis()));		
		return "redirect:";
	}
	
}
