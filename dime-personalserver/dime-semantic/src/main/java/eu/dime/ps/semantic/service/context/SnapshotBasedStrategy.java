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

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

/**
 * <p>Provides a basic implementation of {@link UpdateStrategy}.</p>
 * 
 * <p>Every time a change in the live context occurs, the WHOLE state of the graph
 * is copied to the previous context graph, and the update is performed.</p>
 * 
 * <p>It does not allow any fine-grained update, or keeping the state of specific
 * aspects/elements.</p>
 * 
 * @author Ismael Rivera
 */
public class SnapshotBasedStrategy implements UpdateStrategy {

    private Model previousContext;
    private Model liveContext;
    
	public SnapshotBasedStrategy(Model previousContext, Model liveContext) {
		this.previousContext = previousContext;
		this.liveContext = liveContext;
	}
	
	@Override
	public void update(List<Statement> toAdd, List<Statement> toRemove) {
		// the data of 'live context' goes to 'previous context'
		previousContext.removeAll();
		previousContext.addAll(liveContext.iterator());
		
		// makes changes to live context (add/remove statements)
		liveContext.removeAll(toRemove.iterator());
		liveContext.addAll(toAdd.iterator());
	}

}