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
	
	//@Autowired
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
