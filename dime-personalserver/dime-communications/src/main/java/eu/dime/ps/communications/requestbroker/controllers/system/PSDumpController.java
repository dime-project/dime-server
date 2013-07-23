package eu.dime.ps.communications.requestbroker.controllers.system;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.rdf2go.model.Syntax;
import org.openrdf.repository.RepositoryException;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.TenantContextHolder;
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
	public Response ping(@PathParam("said") String said) {
		Connection connection = null;
		
		try {
			connection = connectionProvider.getConnection(TenantContextHolder.getTenant().toString());
			System.out.println(connection.getTripleStore().serialize(Syntax.Trig));
		} catch (RepositoryException e) {
			Response.serverError("Couldn't get connection to RDF services: " + e.getMessage(), e);
		} finally {
			if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException e) {}
            }
		}
		
		return Response.ok();
	}
	
}
