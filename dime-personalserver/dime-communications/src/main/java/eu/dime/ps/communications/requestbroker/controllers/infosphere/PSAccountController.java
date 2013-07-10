package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SAdapter;
import eu.dime.commons.dto.SAdapterSetting;
import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * Dime REST API Controller for a InfoSphere features
 * 
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 *		 (mplanaguma)</a>
 * 
 */
@Controller
@Path("/dime/rest/{said}/account")
public class PSAccountController implements APIController {

	private AccountManager accountManager;
	private PersonManager personManager;
	@Autowired
	private ServiceGateway serviceGateway;

	private static final Logger logger = LoggerFactory.getLogger(PSAccountController.class);

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}

	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}	

	/** 
	 * Creates service adapter metadata
	 * @param accountName
	 * @return
	 * @throws ServiceNotAvailableException
	 * @throws ServiceAdapterNotSupportedException
	 */
	private SAdapter getSAdapter(Account account) throws ServiceNotAvailableException, ServiceAdapterNotSupportedException {
		String said = account.asURI().toString();
		ServiceAdapter sa = this.serviceGateway.getServiceAdapter(said);

		// Create response for client
		ServiceMetadata sm = this.serviceGateway.getServiceMetadata(sa.getAdapterName(), said);
		SAdapter jsonServiceAdapter = new SAdapter();
		jsonServiceAdapter.setGuid(sm.getGuid());
		jsonServiceAdapter.setStatus(sm.getStatus());
		jsonServiceAdapter.setStatus(ServiceMetadata.STATUS_ACTIVE);
		jsonServiceAdapter.setName(sm.getAdapterName());
		jsonServiceAdapter.setImageUrl(sm.getIcon());
		jsonServiceAdapter.setAuthUrl(sm.getAuthURL());
		jsonServiceAdapter.setDescription(sm.getDescription());
		if (sm.getSettings() != null && sm.getSettings().length() > 0) {
			jsonServiceAdapter.setIsConfigurable(true);
			jsonServiceAdapter.importSettings(sm.getSettings());
		} else {
			jsonServiceAdapter.setIsConfigurable(false);
		}
		return jsonServiceAdapter;
	}

	/**
	 * Return Collection of SAs
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response<SAdapterWrapper> getMyServiceAccounts() {

		Data<SAdapterWrapper> data = null;

		try {
			Collection<Account> accounts = accountManager.getAllByCreator(accountManager.getMe());
			data = new Data<SAdapterWrapper>(0, accounts.size(), accounts.size());

			for (Account account : accounts) {
				try {
					SAdapterWrapper sAdapterWrapper = new SAdapterWrapper(account,accountManager.getMe().asURI());
					sAdapterWrapper.setSettings(getSAdapter(account).getSettings());
					data.getEntries().add(sAdapterWrapper);
				} catch (ServiceNotAvailableException e) {
					logger.warn("Service is unavailable: " + e.getMessage());
				} catch (ServiceAdapterNotSupportedException e) {
					logger.warn("Tried to add a non-supported service to the list of active services: " + e.getMessage());
				}
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Return Collection of SAs
	 * 
	 * @param personID
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/{personID}/@all")
	public Response<Resource> getServices(@PathParam("personID") String personID) {

		Data<Resource> data = null;

		try {
			Person person = personManager.get(personID);
			Collection<Account> accounts = accountManager.getAllByCreator(person);
			data = new Data<Resource>(0, accounts.size(), accounts.size());
			for (Account account : accounts) {
				data.getEntries().add(new Resource(account,accountManager.getMe().asURI()));
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me")
	public Response<SAdapterWrapper> createServiceAccount(
			@PathParam("said") String said,
			Request<SAdapterWrapper> request) {

		Data<SAdapterWrapper> data;

		try {
			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();
			SAdapterWrapper dto = data.getEntries().iterator().next();

			// Handle account creation
			// Remove guid because is a new object
			dto.remove("guid");

			// Use Service Adapter Name to create the adapter
			String serviceAdapterName = (String) dto.get("serviceadapterguid");
			ServiceAdapter sa = this.serviceGateway.makeServiceAdapter(serviceAdapterName);

			// Set configuration
			if (dto.getSettings() != null) {
				Iterator<SAdapterSetting> iter = dto.getSettings().iterator();
				while (iter.hasNext()) {
					SAdapterSetting setting = iter.next();
					sa.setSetting(setting.getName(), setting.getValue());
				}
			}

			
			// Add account
			accountManager.add(sa);
                        

			// Fix the GUID on the returned object
			dto.put("guid", sa.getIdentifier());
			dto.setSettings(sa.getSettings());
			Collection<SAdapterWrapper> entries = new ArrayList<SAdapterWrapper>();
			entries.add(dto);
			data.setEntry(entries);

		} catch (InfosphereException e) {
			return Response.serverError(e.getMessage(), e);
		} catch (ServiceNotAvailableException e) {
			return Response.serverError(e.getMessage(), e);
		} catch (ServiceAdapterNotSupportedException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (ClassCastException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Updates a service account configuration
	 * @param said
	 * @param request
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{accountId}")
	public Response<SAdapterWrapper> updateServiceAccount(
			@PathParam("said") String said,
			Request<SAdapterWrapper> request) {

		Data<SAdapterWrapper> data, returnData;

		try {
			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();
			SAdapterWrapper dto = data.getEntries().iterator().next();

			// Set configuration
			Account account = dto.asResource(Account.class,accountManager.getMe().asURI());
			ServiceAdapter sa = this.serviceGateway.getServiceAdapter(account.asURI().toString());
			if (dto.getSettings() != null) {
				Iterator<SAdapterSetting> iter = dto.getSettings().iterator();
				while (iter.hasNext()) {
					SAdapterSetting setting = iter.next();
					sa.setSetting(setting.getName(), setting.getValue());
				}
			}

		} catch (ServiceNotAvailableException e) {
			return Response.serverError(e.getMessage(), e);
		} catch (ServiceAdapterNotSupportedException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (ClassCastException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();
	}

	/**
	 * DELETE deleted an account
	 * 
	 * @param accountId
	 * @return
	 */		
	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{accountId}")
	public Response deleteAccount(@PathParam("said") String said,
			@PathParam("accountId") String accountId) {

		logger.info("called API method: DELETE /dime/rest" + said + "account/@me/"+accountId);				

		try {
			accountManager.remove(accountId);
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.okEmpty();
	}

	// TODO this has been added to quickly trigger the crawl of accounts
	// if required, this method should be finished, and add it to the API spec
	@POST
	@Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
	@Path("/{accountId}/@crawl")
	public String triggerCrawl(@PathParam("accountId") String accountId) {
		accountManager.crawl(accountId);
		return "OK";
	}

}