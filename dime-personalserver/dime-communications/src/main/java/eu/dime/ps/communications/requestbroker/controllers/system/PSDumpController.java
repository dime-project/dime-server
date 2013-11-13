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
import javax.ws.rs.core.Response;

import org.ontoware.rdf2go.model.Syntax;
import org.openrdf.repository.RepositoryException;
import org.springframework.stereotype.Controller;

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
	@Produces("text/turtle;charset=UTF-8")
	public Response dump(@PathParam("said") String said) {
		Connection connection = null;
		Response.ResponseBuilder rb = null;

		try {
			connection = connectionProvider.getConnection(TenantHelper.getCurrentTenantId().toString());
			rb = Response.ok(connection.getTripleStore().serialize(Syntax.Trig));
		} catch (RepositoryException e) {
			rb = Response.serverError();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException e) {}
			}
		}

		return rb.build();
	}

}
