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

package eu.dime.ps.semantic.service.context;

import java.util.List;

import org.ontoware.rdf2go.model.Statement;

/**
 * Provides a strategy for updates on the live context, and how these 
 * changes are propagated to the previous context.
 * 
 * @author Ismael Rivera
 */
public interface UpdateStrategy {

	/**
	 * Update live context.
	 * 
	 * @param toAdd set of triples to be added to the live context
	 * @param toRemove sets of triples to be removed from the live context
	 */
	public void update(List<Statement> toAdd, List<Statement> toRemove);
	
}
