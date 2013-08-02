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

package eu.dime.ps.controllers.security.anonymity;

import java.util.List;
import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.URI;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.model.dao.Account;

/**
 * Listens for Account creation and creates corresponding Tor Hidden Service
 * @author marcel
 *
 */
public class TorHSCreationService implements BroadcastReceiver{

	static Logger logger = Logger.getLogger(TorHSCreationService.class);
	
//	private Properties mapping;
	
	@Autowired
	private StandaloneTorService torService;
	
	@Autowired
	CredentialStore credentialStore;
	
	String dns;
	
	public TorHSCreationService(){
		BroadcastManager.getInstance().registerReceiver(this);
	}
	
	@Override
	public void onReceive(Event event) {
		
		List<URI> typeList = event.getTypes();
		boolean accountCreated = false;
		for (URI uri : typeList) {
			if (uri.equals(Account.RDFS_CLASS)){
				accountCreated = true;
				break;
			}
		}
		if (accountCreated){
			Account account = (Account) event.getData();
			try {
				torService.addHiddenService(account.getPrefLabel());
			} catch (Exception e) {
				logger.error("Could not create Tor Hidden Service.", e);
			}
		}
	}
}
