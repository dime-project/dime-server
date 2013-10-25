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
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.context.raw.ifc.RawContextManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.controllers.util.TenantHelper;
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
	private NotifierManager notifierManager;

	public void setContextManager(RawContextManager contextManager) {
		this.contextManager = contextManager;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
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
			
			if (context.scope.equalsIgnoreCase(Constants.SCOPE_CURRENT_PLACE)) {
				SystemNotification notification = 
						new SystemNotification(TenantContextHolder.getTenant(), DimeInternalNotification.OP_UPDATE, 
								context.scope, DimeInternalNotification.ITEM_TYPE_CONTEXT, null);
				notifierManager.pushInternalNotification(notification);
			}
			
			return Response.ok(data);
		} catch (ContextException e) {
			logger.error(e.getMessage(),e);
			return Response.badRequest(e.getMessage(),e);
		} catch (NotifierException e) {
			logger.warn(e.getMessage(),e);
			return Response.ok(data);
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
			
			if (scopeId.equalsIgnoreCase(Constants.SCOPE_CURRENT_PLACE)) {
				SystemNotification notification = 
						new SystemNotification(TenantContextHolder.getTenant(), DimeInternalNotification.OP_REMOVE, 
								scopeId, DimeInternalNotification.ITEM_TYPE_CONTEXT, null);
				notifierManager.pushInternalNotification(notification);
			}
			
			return Response.ok();
		} catch (ContextException e) {
			logger.error(e.getMessage(),e);
			return Response.badRequest(e.getMessage(),e);
		} catch (NotifierException e) {
			logger.warn(e.getMessage(),e);
			return Response.ok();
		}

    }

	@GET
	@Path("/dump")
    @Produces("text/turtle;charset=UTF-8")
	public String dump(@PathParam("said") String said) {
		Connection connection = null;
		String response = null;
		
		try {
			connection = connectionProvider.getConnection(TenantHelper.getCurrentTenantId().toString());
			response = connection.getLiveContextService().getLiveContext().serialize(Syntax.Turtle);
		} catch (RepositoryException e) {
			Response.serverError("Couldn't get connection to RDF services: " + e.getMessage(), e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException e) {}
			}
		}
		
		return response;
	}

}
