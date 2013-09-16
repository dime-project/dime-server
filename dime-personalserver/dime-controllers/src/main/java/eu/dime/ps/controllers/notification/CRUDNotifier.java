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

package eu.dime.ps.controllers.notification;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.ps.controllers.evaluationtool.EvaluationManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.dto.ProfileAttributeType;
import eu.dime.ps.dto.Type;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;

/**
 * Notifies the UI/clients of any created/updated/deleted resource from the user's data store.
 * 
 * @author Ismael Rivera
 */
public class CRUDNotifier implements BroadcastReceiver {

	private static final Logger logger = LoggerFactory.getLogger(CRUDNotifier.class);

	private NotifierManager notifierManager;
	
	private EvaluationManager evaluationManager;
	
	private static final String[] ACTIONS = new String[] {
		Event.ACTION_RESOURCE_ADD,
		Event.ACTION_RESOURCE_MODIFY,
		Event.ACTION_RESOURCE_DELETE
	};

	private static final Map<String, String> NOTIFY_ACTIONS;
	static {
		NOTIFY_ACTIONS = new HashMap<String, String>();
		NOTIFY_ACTIONS.put(Event.ACTION_RESOURCE_ADD, DimeInternalNotification.OP_CREATE);
		NOTIFY_ACTIONS.put(Event.ACTION_RESOURCE_MODIFY, DimeInternalNotification.OP_UPDATE);
		NOTIFY_ACTIONS.put(Event.ACTION_RESOURCE_DELETE, DimeInternalNotification.OP_REMOVE);
	}
	
	public CRUDNotifier() {
		BroadcastManager.getInstance().registerReceiver(this);
	}
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}
	
	@Override
	public void onReceive(Event event) {
		String action = event.getAction();

		// only interested in create/update/delete actions
		if (!ArrayUtils.contains(ACTIONS, action)) {
			return;
		}
		
		String itemId = event.getIdentifier().toString();
		Resource resource = event.getData();
		
		if (NOTIFY_ACTIONS.containsKey(action)) {
			if (resource == null) {
				logger.debug("Impossible to find type of resource (no metadata provided in the event), no notification will be sent [item="+itemId+", action="+action+"]");
			} else {				
				Type itemType = Type.get(resource); //if Type is not found maybe it is a profile attribute TODO find a better way of handling this
				ProfileAttributeType itemTypeProfileAttribute = ProfileAttributeType.get(resource);
				if (itemType == null && itemTypeProfileAttribute ==null) {						
					logger.debug("Type is undefined, no notification will be sent [item="+resource+", action="+action+"]");
					return;
					}				
				
				String type = (itemTypeProfileAttribute == null)? itemType.toString():"profileattribute";
				String creatorId = null;
				Node creator = ModelUtils.findObject(resource.getModel(), resource, NAO.creator);
				if (creator != null) {
					creatorId = creator.toString();
				}
				
				Long tenant = Long.parseLong(event.getTenant());
				String operation = NOTIFY_ACTIONS.get(action);
				
				//selfevaulation tool
				
				
				// sends internal notifications (to UI)
				SystemNotification notification = new SystemNotification(tenant, operation, itemId, type, creatorId);
				try {
					logger.debug("Pushing internal notification: "+notification.toString());
					notifierManager.pushInternalNotification(notification);
				} catch (NotifierException e) {
					logger.error("Error while pushing notification ["+notification+"].", e);
				}
			}
		}
	}
	
}
