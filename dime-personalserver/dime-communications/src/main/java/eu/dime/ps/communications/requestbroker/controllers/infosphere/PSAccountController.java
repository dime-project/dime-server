package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SAccount;
import eu.dime.commons.dto.SAdapterSetting;
import eu.dime.commons.object.ServiceMetadata;
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
import java.util.Collection;
import java.util.Iterator;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Dime REST API Controller for a InfoSphere features
 *
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 * (mplanaguma)</a>
 * 
 * Refactored by Simon Thiel
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
     *
     * @param accountName
     * @return
     * @throws ServiceNotAvailableException
     * @throws ServiceAdapterNotSupportedException
     */
    private SAccount getSAccount(Account account) throws ServiceNotAvailableException, ServiceAdapterNotSupportedException {
        String said = account.asURI().toString();
        ServiceAdapter sa = this.serviceGateway.getServiceAdapter(said);

        // Create response for client
        ServiceMetadata sm = this.serviceGateway.getServiceMetadata(sa.getAdapterName(), said);
        SAccount jsonServiceAdapter = new SAccount();
        jsonServiceAdapter.setGuid(sm.getGuid());
        jsonServiceAdapter.setStatus(sm.getStatus());
        jsonServiceAdapter.setStatus(ServiceMetadata.STATUS_ACTIVE);
        jsonServiceAdapter.setName(sm.getAdapterName());
        jsonServiceAdapter.setImageUrl(sm.getIcon());
        jsonServiceAdapter.setAuthUrl(sm.getAuthURL());
        jsonServiceAdapter.setDescription(sm.getDescription());
        jsonServiceAdapter.setServiceadapterguid(sa.getAdapterName());
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
    public Response<SAccount> getMyServiceAccounts() {

        Data<SAccount> data = null;

        try {
            Collection<Account> accounts = accountManager.getAllByCreator(accountManager.getMe());
            data = new Data<SAccount>(0, accounts.size(), accounts.size());

            for (Account account : accounts) {
                try {
//					SAccountWrapper sAdapterWrapper = new SAccountWrapper(account,accountManager.getMe().asURI());
//					sAdapterWrapper.setSettings(getSAccount(account).getSettings());
                    data.getEntries().add(getSAccount(account));
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
                data.getEntries().add(new Resource(account, accountManager.getMe().asURI()));
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
    public Response<SAccount> createServiceAccount(
            @PathParam("said") String said,
            Request<SAccount> request) {

        Data<SAccount> data;

        try {
            RequestValidator.validateRequest(request);

            data = request.getMessage().getData();
            SAccount newAccount = data.getEntries().iterator().next();


            // Use Service Adapter Name to create the adapter
            String serviceAdapterName = (String) newAccount.getServiceadapterguid();
            ServiceAdapter sa = this.serviceGateway.makeServiceAdapter(serviceAdapterName);

            // Set configuration
            if (newAccount.getSettings() != null) {
                Iterator<SAdapterSetting> iter = newAccount.getSettings().iterator();
                while (iter.hasNext()) {
                    SAdapterSetting setting = iter.next();
                    sa.setSetting(setting.getName(), setting.getValue());
                }
            }


            // Add account
            accountManager.add(sa);


            // Fix the GUID on the returned object
            newAccount.setGuid(sa.getIdentifier());
            newAccount.setSettings(sa.getSettings());

            data = new Data<SAccount>(0, 1, 1);
            data.getEntries().add(newAccount);

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
     *
     * @param said
     * @param request
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/@me/{accountId}")
    public Response<SAccount> updateServiceAccount(
            @PathParam("said") String said,
            Request<SAccount> request) {

        Data<SAccount> data, returnData;

        try {
            RequestValidator.validateRequest(request);

            data = request.getMessage().getData();
            SAccount updatedAccount = data.getEntries().iterator().next();

            // Set configuration
            ServiceAdapter sa = this.serviceGateway.getServiceAdapter(updatedAccount.getGuid());
            if (updatedAccount.getSettings() != null) {
                Iterator<SAdapterSetting> iter = updatedAccount.getSettings().iterator();
                while (iter.hasNext()) {
                    SAdapterSetting setting = iter.next();
                    sa.setSetting(setting.getName(), setting.getValue());
                }
            }

            data = new Data<SAccount>(0, 1, 1);
            data.getEntries().add(updatedAccount);

        } catch (ServiceNotAvailableException e) {
            return Response.serverError(e.getMessage(), e);
        } catch (ServiceAdapterNotSupportedException e) {
            return Response.badRequest(e.getMessage(), e);
        } catch (ClassCastException e) {
            return Response.badRequest(e.getMessage(), e);
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

        logger.info("called API method: DELETE /dime/rest/" + said + "/account/@me/" + accountId);

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