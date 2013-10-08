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

package eu.dime.ps.communications.requestbroker.controllers.system;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.rdf2go.model.Syntax;
import org.openrdf.repository.RepositoryException;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;

@Controller
@Path("/dime/rest/{said}/dump")
public class PSDumpController {

	private ConnectionProvider connectionProvider;

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<String> ping(@PathParam("said") String said) {
		Connection connection = null;
		Data<String> data  = new Data<String>();
		try {
			connection = connectionProvider.getConnection(TenantHelper.getCurrentTenantId().toString());
			String rdf = connection.getTripleStore().serialize(Syntax.Trig);
			data.addEntry(rdf);
		} catch (RepositoryException e) {
			Response.serverError("Couldn't get connection to RDF services: " + e.getMessage(), e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException e) {}
			}
		}

		return Response.ok(data);
	}

}
