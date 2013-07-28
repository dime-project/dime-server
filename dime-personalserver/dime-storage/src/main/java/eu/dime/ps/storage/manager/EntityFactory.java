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

package eu.dime.ps.storage.manager;

import eu.dime.ps.storage.entities.AccountCredentials;
import eu.dime.ps.storage.entities.AttributeMatch;
import eu.dime.ps.storage.entities.CrawlerHandler;
import eu.dime.ps.storage.entities.CrawlerJob;
import eu.dime.ps.storage.entities.EvaluationData;
import eu.dime.ps.storage.entities.HistoryCache;
import eu.dime.ps.storage.entities.Notification;
import eu.dime.ps.storage.entities.PersonMatch;
import eu.dime.ps.storage.entities.ProfileMatch;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.entities.UserDefaults;

/**
 * This is the entry point to use the dime-storage module. import
 * <i>ps-storage-applicationContext.xml</i> on your applicationContext to load
 * the DB connection adn DAOs. Use the bean entityFactory to build the Entities.
 * 
 * EntityFactory is a Singleton
 * 
 * @author Marc Planaguma
 * 
 */
public class EntityFactory {

	private static EntityFactory INSTANCE = null;

	public static EntityFactory getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new EntityFactory();
		}
		return INSTANCE;
	}

	private EntityFactory() {
	}

	public CrawlerHandler buildCrawlerHandler() {
		return new CrawlerHandler();
	}

	public CrawlerJob buildCrawlerJob() {
		return new CrawlerJob();
	}

	public Notification buildNotification() {
		return new Notification();
	}

	public User buildUser() {
		return new User();
	}

	public ServiceProvider buildServiceProvider() {
		return new ServiceProvider();
	}

	public ServiceAccount buildServiceAccount() {
		return new ServiceAccount();
	}

	public AccountCredentials buildAccountCredentials() {
		return new AccountCredentials();
	}

	public HistoryCache buildHistoryCache() {
		return new HistoryCache();
	}

	public EvaluationData buildEvaluationData() {
		return new EvaluationData();
	}

	public Tenant buildTenant() {
		return new Tenant();
	}

	public PersonMatch buildPersonMatch() {
		return new PersonMatch();
	}

	public ProfileMatch buildProfileMatch() {
		return new ProfileMatch();
	}

	public AttributeMatch buildAttributeMatch() {
		return new AttributeMatch();
	}
	
	public UserDefaults buildUserDefaults(){
		return new UserDefaults();
	}

}
