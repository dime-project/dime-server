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

package eu.dime.ps.datamining.account;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

/**
 * Provides means for updating the data gathered from different social
 * services such as LinkedIn, Twitter, etc.
 * 
 * @author Ismael Rivera
 */
public interface AccountUpdaterService {

	public static final String ACTION_RESOURCE_NEW = "eu.dime.ps.controllers.account.RESOURCE_NEW";
	public static final String ACTION_RESOURCE_MODIFY = "eu.dime.ps.controllers.account.RESOURCE_MODIFY";
	public static final String ACTION_RESOURCE_DELETE = "eu.dime.ps.controllers.account.RESOURCE_DELETE";

	<T extends Resource> void updateResources(URI accountUri, String path, Collection<T> resources)
			throws AccountIntegrationException;
	
	void removeResources(URI accountUri)
			throws AccountIntegrationException;
	
	void removeResources(URI accountUri, String path)
			throws AccountIntegrationException;

}
