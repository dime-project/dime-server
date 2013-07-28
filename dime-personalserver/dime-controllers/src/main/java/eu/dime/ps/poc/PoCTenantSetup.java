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

package eu.dime.ps.poc;

import ie.deri.smile.vocabulary.NAO;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonMatchManagerImpl;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.storage.entities.AccountCredentials;
import eu.dime.ps.storage.entities.PersonMatch;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.jfix.util.Arrays;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * This creates and configures, by default, tenants for a pre-defined list of
 * personas, loading pre-defined data for each of them.
 * 
 * @author Ismael Rivera
 */
public class PoCTenantSetup {

	private static final Logger logger = LoggerFactory.getLogger(PoCTenantSetup.class);

	private static final String[] PERSONAS = new String[] { "juan", "anna", "norbert" };

	private TestDataLoader testDataLoader = new TestDataLoader();

	private ConnectionProvider connectionProvider;
	private TenantManager tenantManager;
	private AccountManager accountManager;

	private EntityFactory factory;
	
	@Autowired
	ShaPasswordEncoder dimePasswordEncoder;

	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.factory = entityFactory;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void init() throws Exception {
		
		logger.info("Setting up tenants for "+Arrays.join(PERSONAS, ","));

		Connection connection = null;
		
		for (String persona : PERSONAS) {
			List<User> users = User.findByUsernameLike(persona).getResultList();
			User user = users.size() == 0 ? null : users.get(0);

			if (user != null) {
				logger.info("Persona "+persona+" already exist, aborting PoC data setup...");
				return;
				
			} else {
				user = factory.buildUser();
				user.setEnabled(true);
				user.setUsername(persona);
				user.setPassword(dimePasswordEncoder.encodePassword(persona + "123", persona));
				user.setRole(Role.OWNER);
				user.persist();

				logger.info("Created user with id="+user.getId()+" and username="+user.getUsername());

				Tenant tenant = tenantManager.create(user.getUsername(), user);
				logger.info("Tenant for "+persona+" is [id="+tenant.getId()+", name="+tenant.getName()+"]");

				// load PIMO service with pre-defined data for the persona 
				connection = connectionProvider.getConnection(tenant.getId().toString());
				PimoService pimoService = connection.getPimoService();
				testDataLoader.preparePimoService(persona, pimoService);

				// populates the db with information of the user's accounts
				Collection<Account> accounts = pimoService.find(Account.class)
						.distinct()
						.where(NAO.creator).is(pimoService.getUserUri())
						.results();
				for (Account account : accounts) {
					if (account.hasAccountType()) {
						ServiceProvider provider = ServiceProvider.findByName(account.getAccountType());
						
						ServiceAccount dbAccount = factory.buildServiceAccount();
						dbAccount.setServiceProvider(provider);
						dbAccount.setTenant(tenant);
						dbAccount.setEnabled(true);
	
						dbAccount.setAccountURI(account.asURI().toString());
	
						if ("di.me".equals(account.getAccountType())) {
							// the name is the said used for routing, sharing, etc.
							dbAccount.setName(UUID.randomUUID().toString());
						} else {
							// credentials are not needed for di.me accounts, and are fake for the
							// rest of the accounts, so we don't need to set them
							dbAccount.setAccessToken("DUMMY");
							dbAccount.setAccessSecret("DUMMY");
							
							// TODO should we allow NULL in this attribute??
							dbAccount.setName("");
						}
	
						dbAccount.persist();
					} else {
						logger.error("Account "+account.asURI()+" must specify an accountType, but could not be found.");
					}
				}
				
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////
		// once setup is completed, adding connections: Juan-Anna and Juan-Norbert //
		/////////////////////////////////////////////////////////////////////////////
		
		User user = null;
		AccountCredentials credentials = null;
		
		// Juan & Anna
		
		// add anna as GUEST in juan's tenant
		user = factory.buildUser();
		user.setTenant(Tenant.findByName("juan"));
		user.setEnabled(true);
		user.setUsername(ServiceAccount.findAllByAccountUri("urn:uuid:a000018").getName());
		user.setPassword(dimePasswordEncoder.encodePassword("janna456", user.getUsername()));
		user.setAccountUri("urn:uuid:j000115");
		user.setRole(Role.GUEST);
		user.persist();
			
		// juan's account credentials to access anna's account (targetUri is the account uri assigned to anna's account in juan's store)
		credentials = factory.buildAccountCredentials();
		credentials.setTenant(Tenant.findByName("juan"));
		credentials.setSource(ServiceAccount.findAllByAccountUri("urn:uuid:j000071"));
		credentials.setTarget(user.getUsername());
		credentials.setTargetUri("urn:uuid:j000115");
		credentials.setSecret("ajuan456");
		credentials.persist();

		// add juan as GUEST in anna's tenant
		user = factory.buildUser();
		user.setTenant(Tenant.findByName("anna"));
		user.setEnabled(true);
		user.setUsername(ServiceAccount.findAllByAccountUri("urn:uuid:j000071").getName());
		user.setPassword(dimePasswordEncoder.encodePassword("ajuan456", user.getUsername()));
		user.setRole(Role.GUEST);
		user.setAccountUri("urn:uuid:a000039");
		user.persist();
		
		// anna's account credentials to access juan's account
		credentials = factory.buildAccountCredentials();
		credentials.setTenant(Tenant.findByName("anna"));
		credentials.setSource(ServiceAccount.findAllByAccountUri("urn:uuid:a000018"));
		credentials.setTarget(user.getUsername());
		credentials.setTargetUri("urn:uuid:a000039");
		credentials.setSecret("janna456");
		credentials.persist();
					
		// Juan & Norbert
		
		// add norbert as GUEST in juan's tenant
		user = factory.buildUser();
		user.setTenant(Tenant.findByName("juan"));
		user.setEnabled(true);
		user.setUsername(ServiceAccount.findAllByAccountUri("urn:uuid:n000250").getName());
		user.setPassword(dimePasswordEncoder.encodePassword("jnorbert456", user.getUsername()));
		user.setAccountUri("urn:uuid:j000124");
		user.setRole(Role.GUEST);
		user.persist();
			
		// juan's account credentials to access norbert's account (targetUri is the account uri assigned to norbert's account in juan's store)
		credentials = factory.buildAccountCredentials();
		credentials.setTenant(Tenant.findByName("juan"));
		credentials.setSource(ServiceAccount.findAllByAccountUri("urn:uuid:j000071"));
		credentials.setTarget(user.getUsername());
		credentials.setTargetUri("urn:uuid:j000124");
		credentials.setSecret("njuan456");
		credentials.persist();

		// add juan as GUEST in norbert's tenant
		user = factory.buildUser();
		user.setTenant(Tenant.findByName("norbert"));
		user.setEnabled(true);
		user.setUsername(ServiceAccount.findAllByAccountUri("urn:uuid:j000071").getName());
		user.setPassword(dimePasswordEncoder.encodePassword("njuan456", user.getUsername()));
		user.setAccountUri("urn:uuid:n000277");
		user.setRole(Role.GUEST);
		user.persist();
		
		// norbert's account credentials to access juan's account
		credentials = factory.buildAccountCredentials();
		credentials.setTenant(Tenant.findByName("norbert"));
		credentials.setSource(ServiceAccount.findAllByAccountUri("urn:uuid:n000250"));
		credentials.setTarget(user.getUsername());
		credentials.setTargetUri("urn:uuid:n000277");
		credentials.setSecret("jnorbert456");
		credentials.persist();
		
		// add person matchings for juan
		PersonMatch personmatch1 = factory.buildPersonMatch();
		personmatch1.setTenant(Tenant.findByName("juan"));
		personmatch1.setStatus(PersonMatch.PENDING);
		personmatch1.setLastPerformed(new Date());
		personmatch1.setSimilarityScore(0.89);
		personmatch1.setSource("urn:uuid:j000101");
		personmatch1.setTarget("urn:uuid:j000082");
		personmatch1.setTechnique(PersonMatchManagerImpl.DEFAULT_TECHNIQUE);
		personmatch1.persist();
		
		PersonMatch personmatch2 = factory.buildPersonMatch();
		personmatch2.setTenant(Tenant.findByName("juan"));
		personmatch2.setStatus(PersonMatch.ACCEPTED);
		personmatch2.setLastPerformed(new Date());
		personmatch2.setSimilarityScore(0.93);
		personmatch2.setSource("urn:uuid:j000101");
		personmatch2.setTarget("urn:uuid:j000104");
		personmatch2.setTechnique(PersonMatchManagerImpl.DEFAULT_TECHNIQUE);
		personmatch2.persist();
		
		PersonMatch personmatch3 = factory.buildPersonMatch();
		personmatch3.setTenant(Tenant.findByName("juan"));
		personmatch3.setStatus(PersonMatch.PENDING);
		personmatch3.setLastPerformed(new Date());
		personmatch3.setSimilarityScore(0.89);
		personmatch3.setSource("urn:uuid:j000082");
		personmatch3.setTarget("urn:uuid:j000070");
		personmatch3.setTechnique(PersonMatchManagerImpl.DEFAULT_TECHNIQUE);
		personmatch3.persist();
	}

}
