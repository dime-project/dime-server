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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.List;


import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.UserRegister;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.storage.entities.User;

@Controller
@Path("/dime/rest/{said}/admin")
public class PSAdminController implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSAdminController.class);

	private UserManager userManager;
	

	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	
	/**
	 * Retrieves all registered users
	 * 
	 * @return collection containing all registered users
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@all")
	public Response<UserRegister> getAllUsers() {

		Data<UserRegister> data = null;

		try {
			List<User> users = userManager.getAll();
			data = new Data<UserRegister>(0, 0, 0);
			for (User user : users) {
				UserRegister userRegister = new UserRegister();
				userRegister.setUsername(user.getUsername());
				data.addEntry(userRegister);
			}
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);

	}

	/**
	 * check user
	 * 
	 * @param json
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/{userName}")
	public Response<UserRegister> getAllResources(
			@PathParam("userName") String userName) {
		Data<UserRegister> data = new Data<UserRegister>(0, 0, 0);

		// If there is not a user registered returns an empty OK response
		// otherwise returns the username
		if (userManager.existsByUsername(userName)) {
			UserRegister user = new UserRegister();
			user.setUsername(userName);
			data.addEntry(user);
		} else {
			return Response.ok();
		}

		return Response.ok(data);
	}
	
		
	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/{userName}")
    public Response deleteNotification(@PathParam("userName") String userName) {
		
		userManager.removeByUsername(userName);
		return Response.okEmpty();	
	}
}
