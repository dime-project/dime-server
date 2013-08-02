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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.service.MediaType;

@Controller
@Path("/dime/rest/{said}/livestream/")
public class PSLivestreamController extends PSControllerBase implements APIController {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@all")
	public Response<Resource> getMyLivestreams() {
		Data<Resource> data = new Data<Resource>(0, 0, 0);
		return Response.ok(data);
	}

}