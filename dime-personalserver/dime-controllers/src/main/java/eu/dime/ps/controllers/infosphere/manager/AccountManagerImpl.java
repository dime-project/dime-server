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

package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.NAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.datamining.account.AccountUpdaterService;
import eu.dime.ps.datamining.account.AccountUpdaterServiceImpl;
import eu.dime.ps.datamining.account.ProfileAccountUpdater;
import eu.dime.ps.datamining.account.StreamAccountUpdater;
import eu.dime.ps.datamining.crawler.handler.AccountUpdaterHandler;
import eu.dime.ps.datamining.crawler.handler.ContextUpdaterHandler;
import eu.dime.ps.datamining.crawler.handler.ProfileUpdaterHandler;
import eu.dime.ps.datamining.crawler.handler.StreamUpdaterHandler;
import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.ServiceCrawlerRegistry;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.gateway.service.external.BasicAuthServiceAdapter;
import eu.dime.ps.gateway.service.external.oauth.OAuthServiceAdapter;
import eu.dime.ps.gateway.service.internal.AccountRegistrar;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.DAOFactory;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dao.Credentials;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.service.impl.ResourceMatchingServiceImpl;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * Implements {@link AccountManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 *
 * @author Ismael Rivera
 */
public class AccountManagerImpl extends InfoSphereManagerBase<Account> implements AccountManager {

    private final Logger logger = LoggerFactory.getLogger(AccountManagerImpl.class);
    
    private final DAOFactory daoFactory = (new ModelFactory()).getDAOFactory();
    private EntityFactory entityFactory;
    private ServiceGateway serviceGateway;
    private ServiceCrawlerRegistry serviceCrawlerRegistry;
    private AccountRegistrar accountRegistrar;

    @Autowired
    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    public void setServiceGateway(ServiceGateway serviceGateway) {
        this.serviceGateway = serviceGateway;
    }

    public void setServiceCrawlerRegistry(ServiceCrawlerRegistry serviceCrawlerRegistry) {
        this.serviceCrawlerRegistry = serviceCrawlerRegistry;
    }
    
    public void setAccountRegistrar(AccountRegistrar accountRegistrar) {
    	this.accountRegistrar = accountRegistrar;
    }

    @Override
    public boolean isAccount(String resourceId) throws InfosphereException {
        PimoService pimoService = getPimoService();
        try {
            return pimoService.isTypedAs(new URIImpl(resourceId), DAO.Account);
        } catch (NotFoundException e) {
            throw new InfosphereException("Couldn't check if " + resourceId + " was a dao:Account.", e);
        }
    }

    @Override
    public Collection<Account> getAll() throws InfosphereException {
        return getAll(new ArrayList<URI>(0));
    }

    @Override
    public Collection<Account> getAll(List<URI> properties) throws InfosphereException {
        return getResourceStore().find(Account.class).distinct().select(properties.toArray(new URI[properties.size()])).results();
    }

    @Override
    public Collection<Account> getAllByCreator(Person creator)
            throws InfosphereException {
        return getAllByCreator(creator, new URI[0]);
    }

    @Override
    public Collection<Account> getAllByCreator(Person creator, URI... properties)
            throws InfosphereException {
        return getResourceStore().find(Account.class).distinct().select(properties).where(NAO.creator).is(creator.asResource()).results();
    }

    public Collection<Account> getAllByType(String accountType)
            throws InfosphereException {
        return getAllByType(accountType, new URI[0]);
    }

    public Collection<Account> getAllByType(String accountType, URI... properties)
            throws InfosphereException {
        return getResourceStore().find(Account.class).distinct().select(properties).where(DAO.accountType).is(accountType).results();
    }

    @Override
    public Collection<Account> getAllByCreatorAndByType(String creatorId, String accountType)
            throws InfosphereException {
        return getAllByCreatorAndByType(creatorId, accountType, new URI[0]);
    }

    @Override
    public Collection<Account> getAllByCreatorAndByType(String creatorId, String accountType,
            URI... properties) throws InfosphereException {
        return getResourceStore().find(Account.class).distinct().select(properties).where(NAO.creator).is(new URIImpl(creatorId)).where(DAO.accountType).is(accountType).results();
    }

    @Override
    public Account get(String accountId) throws InfosphereException {
        return get(accountId, new ArrayList<URI>(0));
    }

    @Override
    public Account get(String accountId, List<URI> properties)
            throws InfosphereException {
        try {
            return getResourceStore().get(new URIImpl(accountId), Account.class,
                    properties.toArray(new URI[properties.size()]));
        } catch (NotFoundException e) {
            throw new InfosphereException("cannot find account " + accountId, e);
        }
    }

    @Override
    public Credentials getCredentials(String accountId) throws InfosphereException {
        return getResourceStore().find(Credentials.class).distinct().where(DAO.hasCredentials).is(new URIImpl(accountId)).first();
    }

    @Override
    public void add(Account account) throws InfosphereException {
        String accountType = account.getAccountType();

        if (accountType == null) {
            throw new InfosphereException("Account " + account.asURI() + " cannot be added: accountType must be specified.");
        }

        // retrieve provider for given account type
        ServiceProvider serviceProvider = ServiceProvider.findByName(accountType);

        // if creator not specified, it's set to the owner of the PIM
        if (!account.hasCreator()) {
            account.setCreator(getPimoService().getUserUri());
        } else if (!account.getCreator().asURI().toString().equals(getPimoService().getUserUri())) {
            super.add(account);
            return;
        }
        
        Tenant tenant = TenantHelper.getCurrentTenant();
        ServiceAccount serviceAccount = entityFactory.buildServiceAccount();

        serviceAccount.setServiceProvider(serviceProvider);
        serviceAccount.setTenant(tenant);
        serviceAccount.setEnabled(true);
        serviceAccount.setAccountURI(account.asURI().toString());

        // registers account, and assigns a new name to the new record
        serviceAccount.setName(accountRegistrar.register(serviceAccount));
        
        // persisting account in DB and RDF store
        serviceAccount.persist();
        super.add(account);
    }

    @Override
    public void add(ServiceAdapter serviceAdapter) throws InfosphereException, ServiceAdapterNotSupportedException {

        Tenant tenant = TenantHelper.getCurrentTenant();

        String adapterName = serviceAdapter.getAdapterName();
        ServiceProvider provider = ServiceProvider.findByName(adapterName);
        if (provider == null) {
            throw new ServiceAdapterNotSupportedException("Service provider " + adapterName + " not found.");
        }

        
        // creating dao:Account object
        Account account = daoFactory.createAccount(serviceAdapter.getIdentifier());
        account.setAccountType(adapterName);
        account.setCreator(getPimoService().getUserUri());
        account.setPrefLabel(adapterName);

        // saving service account in DB + credentials
        ServiceAccount dbAccount = entityFactory.buildServiceAccount();
        dbAccount.setTenant(tenant);
        dbAccount.setServiceProvider(provider);
        dbAccount.setAccountURI(account.asURI().toString());
        dbAccount.setName(null); // external accounts must have no name

        if (serviceAdapter instanceof OAuthServiceAdapter) {
            OAuthServiceAdapter adapter = (OAuthServiceAdapter) serviceAdapter;
            dbAccount.setAccessToken(adapter.getAccessToken().getToken());
            dbAccount.setAccessSecret(adapter.getAccessToken().getSecret());
        } else if (serviceAdapter instanceof BasicAuthServiceAdapter) {
            BasicAuthServiceAdapter adapter = (BasicAuthServiceAdapter) serviceAdapter;
            dbAccount.setAccessToken(adapter.getUsername());
            dbAccount.setAccessSecret(adapter.getPassword());
        }

        // persisting account information in DB and RDF repository
        PimoService pimoService = getPimoService();
        try {
            dbAccount.persist();
            pimoService.create(account);
        } catch (ResourceExistsException e) {
            throw new InfosphereException("cannot create account", e);
        }

        try {
            // construct the default handlers
            TripleStore tripleStore = getTripleStore();
            ResourceStore resourceStore = getResourceStore();
            LiveContextService liveContextService = getLiveContextService();
            AccountUpdaterService genericUpdater = new AccountUpdaterServiceImpl(pimoService, new ResourceMatchingServiceImpl(tripleStore));
            ProfileAccountUpdater profileUpdater = new ProfileAccountUpdater(resourceStore, pimoService);
            StreamAccountUpdater streamUpdater = new StreamAccountUpdater(resourceStore);

            CrawlerHandler[] handlers = new CrawlerHandler[4];
            handlers[0] = new AccountUpdaterHandler(account.asURI(), genericUpdater);
            handlers[1] = new ProfileUpdaterHandler(account.asURI(), profileUpdater);
            handlers[2] = new StreamUpdaterHandler(account.asURI(), streamUpdater);
            handlers[3] = new ContextUpdaterHandler(account.asURI(), liveContextService);

            // setup and start service crawling if account was successfully saved...
            serviceCrawlerRegistry.add(tenant.getId(), serviceAdapter, handlers);
        } catch (DataMiningException e) {
            throw new InfosphereException("Error setting up datamining services for tenant '" + tenant.getId() + "'.", e);
        }
    }

    @Override
    public void remove(String accountId) throws InfosphereException {
        // stops and removes crawler
        serviceCrawlerRegistry.remove(accountId);

        // removes adapter for this account from service gateway
        try {
            serviceGateway.unsetServiceAdapter(accountId);
        } catch (InvalidLoginException e) {
            throw new InfosphereException("Invalid login information.", e);
        } catch (ServiceNotAvailableException e) {
            throw new InfosphereException("Service not available.", e);
        }

        // removes entry from the DB
        Tenant tenant = TenantHelper.getCurrentTenant();
        ServiceAccount account = ServiceAccount.findAllByTenantAndAccountUri(tenant, accountId);
        if (account != null) {
            // FIXME throws a 'deleted entity passed to persist' exception
//			account.remove();
        }

        // removes from RDF repository
        super.remove(accountId);
    }

    @Override
    public void crawl(String accountId) {
        serviceCrawlerRegistry.fireCrawler(accountId);
    }
    
}