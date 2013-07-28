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

package eu.dime.ps.controllers.context.raw.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.dime.commons.notifications.user.UNAdhocGroupRecommendation;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.context.model.api.IEntity;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.context.raw.data.ContextGroup;
import eu.dime.ps.controllers.context.raw.ifc.IContextGroupService;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nao.Party;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;

public class ContextGroupService implements IContextGroupService {
	
	Logger logger = Logger.getLogger(ContextGroupService.class);
	
	// Note [TI] limit to 3 adHoc group implemented as workaround in Segovia removed
	// it should be fixed according to general group management under discussion
	
	//private int groupLimit = 3;
	//private Vector<PersonGroup> currentGroups = new Vector<PersonGroup>();
	
	private TenantManager tenantManager;
	private PersonManager personManager;
	private PersonGroupManager personGroupManager;
	private AccountManager accountManager;
	private ModelFactory mf = null;
	
	private NotifierManager notifierManager;
	
	private HashMap<String,ContextGroup> lastCreatedGroups = new HashMap<String,ContextGroup>();
	//private Properties props = null;

	public ContextGroupService(TenantManager tenantManager, AccountManager accountManager, PersonManager personManager, PersonGroupManager personGroupManager) {
		this.tenantManager = tenantManager;
		this.personManager = personManager;
		this.personGroupManager = personGroupManager;
		this.accountManager = accountManager;
		//this.notifierManager = notifierManager;
		this.mf = new ModelFactory();
	}

	@Override
	public void addContextGroup(IEntity entity, ContextGroup group) {
		
		logger.debug("Checking group " + group.toString());
		
		String said = entity.getEntityIDAsString();
		ContextGroup last = lastCreatedGroups.get(said);
		
		if (group.equals(last)) {
			logger.debug("Group " + group.toString() + " has already been created");
			return;
		} 
		
		Tenant t = tenantManager.getByAccountName(said);
		TenantContextHolder.setTenant(t.getId());
		
		Vector<Person> persons = getMembers(group.getMembers());
		if (persons.size() > 0) {
			String groupName = getGroupName(group,persons);
			logger.debug("CREATING GROUP " + groupName);
			try {
				PersonGroup personGroup = this.mf.getPIMOFactory().createPersonGroup();
				personGroup.setPrefLabel(groupName);
				Iterator<Person> it = persons.iterator();
				while (it.hasNext()) {
					personGroup.addMember(it.next());
				}
				this.personGroupManager.addAdhocGroup(personGroup);
				UNAdhocGroupRecommendation un = new UNAdhocGroupRecommendation();
				un.setNao_creator("urn:auto-generated");
				un.setName(personGroup.getPrefLabel());
				UserNotification notif = new UserNotification(t.getId(),un);
				this.notifierManager.pushInternalNotification(notif);
				lastCreatedGroups.put(entity.getEntityIDAsString(), group);
			} catch (InfosphereException e) {
				logger.error(e.toString(),e);
			} catch (Exception e) {
				logger.error(e.toString(),e);
			}
		} else logger.debug("No members found for group " + group.toString() + ". GROUP NOT CREATED");
		
		TenantContextHolder.unset();
		
	}
	
	private Vector<Person> getMembers(Set<String> members) {
		Iterator<String> it = members.iterator();
		Vector<Person> persons = new Vector<Person>();
		while (it.hasNext()) {
			// member = urn:account:anna
			String member = it.next();
			try {
				// version pre-service configuration
				/*ServiceAccount sa = ServiceAccount.findAllByAccountUri(member);
				String username = sa.getName();
				User u = User.findByTenantAndByUsername(sa.getTenant(), username);
				if (u != null) {
					String userUri = u.getAccountUri();
					Account account = accountManager.get(userUri);
					if (account != null) {
						Party pt = account.getCreator();
						if (pt != null) {
							Person p = personManager.get(pt.toString());
							if (p != null) {
								persons.add(p);
								logger.debug("Found member " + p.getPrefLabel() + " for adHoc group");
							}
						}
					}
				}*/
				Collection<Person> ps = personManager.getAllByAccount(member);
				if (ps != null && ps.size() > 0) {
					Iterator<Person> pit = ps.iterator();
					Person p = pit.next();
					persons.add(p);
					logger.debug("Found member " + p.getPrefLabel() + " for adHoc group");
				} else logger.debug("No persons found related to account: " + member);
			} catch (InfosphereException e) {
				logger.debug("Member " + member + " not found: " + e.toString());
			} 
		}
		return persons;
	}

	private String getGroupName(ContextGroup group, Vector<Person> persons) {
		String members = "";
		if (persons.size() > 3) members = persons.size() + "people";
		else {
			Iterator<Person> it = persons.iterator();
			while (it.hasNext()) {
				members += it.next().getPrefLabel() + ", ";
			}
			members = members.substring(0,members.lastIndexOf(","));
		}
		Format format = new SimpleDateFormat("MM-dd'T'HH:mm");
		String date = format.format(new Date());
		String name = "";
		if (!group.getPlace().equalsIgnoreCase("")) 
			name = "@" + group.getPlace() + " with " + members + " (" + date + ")";
		else 
			name = "With " + members + " (" + date + ")";
		return name;
	}
	
	/*private void notifyNewGroup(Tenant t, PersonGroup group) throws NotifierException {
		try {
			DimeNotification notification = new DimeNotification(t.getId());
			notification.setItemID(group.asURI().toString()); 
			notification.setName(group.getPrefLabel()); 
			notification.setItemType(DimeNotification.TYPE_GROUP);
			notification.setOperation(DimeNotification.OP_CREATE);
			notification.setSender(t.getName());
			notification.setTarget("@me");		
			this.notifierManager.pushInternalNotification(notification);
		} catch (ClassCastException e) {
			logger.error(e.getMessage(),e);
		} 
	}*/

}
