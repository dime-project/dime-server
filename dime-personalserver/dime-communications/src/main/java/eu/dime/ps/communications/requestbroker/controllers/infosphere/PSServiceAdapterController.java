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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SAdapter;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.ServiceMetadata;

/**
 * Responsible for handling initialization of service adapter capabilities
 * 
 * @author Rafa Gimenez BDCT
 */
@Controller
@Path("/dime/rest/{said}/serviceadapter")
public class PSServiceAdapterController implements APIController {
	
	private static final Logger logger = LoggerFactory.getLogger(PSServiceAdapterController.class);

	@Autowired
	private ServiceGateway serviceGateway;

	@Autowired
	private AccountManager accountManager;
	
	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}	
	
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response<SAdapter> listAllServiceAdapters(@PathParam("said") String said) {
		logger.debug("called API method: GET /dime/rest/{said}/serviceadapter/@me/@all");
		
		Data<SAdapter> data = new Data<SAdapter>();

		try {

			Map<String, ServiceMetadata> supportedAdapters = serviceGateway.listSupportedAdapters(said);

                        //commented this out, since it is not used but throwing a NPE! Please remove it - simon t.
			//Collection<Account> activeAdapters = accountManager.getAllByCreator(accountManager.getMe());
			

			// Only list supported adapters
			for (String key : supportedAdapters.keySet()) {
				ServiceMetadata sm = supportedAdapters.get(key);
				SAdapter jsonServiceAdapter = new SAdapter();
				
				// Default to use the adapter name as the GUID. Only set the adapter GUID if it is a real adapter, otherwise it leads to inconsistencies
				jsonServiceAdapter.setGuid(sm.getAdapterName());

				jsonServiceAdapter.setName(sm.getAdapterName());
				jsonServiceAdapter.setImageUrl(sm.getIcon());
				String authUrl = sm.getAuthURL();
				if (authUrl==null){
					authUrl="";
				}
				jsonServiceAdapter.setAuthUrl(authUrl);
				jsonServiceAdapter.setDescription(sm.getDescription());
				jsonServiceAdapter.setLastModified(""); // FIXME
				if (sm.getSettings() != null && sm.getSettings().length() > 0) {
					jsonServiceAdapter.setIsConfigurable(true);
					jsonServiceAdapter.importSettings(sm.getSettings());
				} else {
					jsonServiceAdapter.setIsConfigurable(false);
					jsonServiceAdapter.importSettings("");
				}
				
				data.getEntries().add(jsonServiceAdapter);
			}

		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

}
