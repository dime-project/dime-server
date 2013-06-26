package eu.dime.ps.communications.requestbroker.controllers.context;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.Syntax;
import org.openrdf.repository.RepositoryException;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Context;
import eu.dime.commons.dto.ContextData;
import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.exception.DimeException;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.context.raw.ifc.RawContextManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;

/**
 * Dime REST API Controller for a context features
 * 
 * @author mplanaguma (BDCT)
 * 
 */
@Controller
@Path("/dime/rest/{said}/context")
public class PSContextController {
	
	private Logger logger = Logger.getLogger(PSContextController.class);

	private RawContextManager contextManager;
	private ConnectionProvider connectionProvider;

	public void setContextManager(RawContextManager contextManager) {
		this.contextManager = contextManager;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/{personId}/{scopeId}")
    public Response<Context> getMyContext(
    	@PathParam("said") String said,
	    @PathParam("personId") String personId,
	    @PathParam("scopeId") String scopeId) throws DimeException {
    	
    	try {
    		Data<Context> data = contextManager.getContext(said, scopeId);
    		return Response.ok(data);
		} catch (ContextException e) {
			logger.error(e.getMessage(),e);
			return Response.badRequest(e.getMessage(),e);
		}

    }


    @POST
    @Path("/{personId}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response postMyContext(
    		@PathParam("said") String said,
    	    @PathParam("personId") String personId, 
    	    Request<Context> request) {
    	
    	Data<Context> data = request.getMessage().getData();
	    Context context = (Context)data.getEntries().iterator().next();
	    context.entity.type = Constants.ENTITY_USER;
	    context.entity.id = said;
	    
	    ContextData ctxdata = new ContextData();
	    ctxdata.addEntry(context);
	    
	    try {
			contextManager.contextUpdate(said, ctxdata);
			return Response.ok(data);
		} catch (ContextException e) {
			logger.error(e.getMessage(),e);
			return Response.badRequest(e.getMessage(),e);
		}

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/{personId}/{scopeId}")
    public Response deleteMyContext(
    		@PathParam("said") String said,
    		@PathParam("personId") String personId,
    		@PathParam("scopeId") String scopeId) {
    	
    	try {
			contextManager.deleteContext(said, scopeId);
			return Response.ok();
		} catch (ContextException e) {
			logger.error(e.getMessage(),e);
			return Response.badRequest(e.getMessage(),e);
		}

    }

	@GET
	@Path("/dump")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response dump(@PathParam("said") String said) {
		Connection connection = null;
		
		try {
			connection = connectionProvider.getConnection(TenantContextHolder.getTenant().toString());
			System.out.println(connection.getLiveContextService().getLiveContext().serialize(Syntax.Trig));
		} catch (RepositoryException e) {
			Response.serverError("Couldn't get connection to RDF services: " + e.getMessage(), e);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (RepositoryException e) {}
		}
		
		return Response.ok();
	}

}
