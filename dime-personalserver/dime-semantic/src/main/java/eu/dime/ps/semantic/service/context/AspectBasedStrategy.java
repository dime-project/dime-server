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

import ie.deri.smile.vocabulary.DCON;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;

/**
 * <p>Provides an implementation of {@link UpdateStrategy} based on Aspect instances updates.</p>
 * 
 * <p>Every time a change in the live context occurs, the Aspect instance within which
 * the change occurred is copied to the previous context graph, and the update is performed.</p> 
 * 
 * <p>Thus each aspect branch within the previous context graph identifies only the last change 
 * within the whole aspect branch.</p>
 * 
 * @author Judie Attard
 * @author Ismael Rivera
 */
public class AspectBasedStrategy implements UpdateStrategy {

    private Model previousContext;
    private Model liveContext;
    
    private static final URI[] ASPECTS = new URI[] { DCON.Attention, DCON.Connectivity, DCON.Environment, DCON.Peers, DCON.Schedule, DCON.SpaTem, DCON.State };
    
	public AspectBasedStrategy(Model previousContext, Model liveContext) {
		this.previousContext = previousContext;
		this.liveContext = liveContext;
	}

//	@Override
//	public <T extends Resource> void update(Aspect aspect, T... resources) {
//		//updating previous context with values from live context
//		
//		Iterator <Statement> liveContextIterator = liveContext.iterator();
//		List<Statement> aspects = new ArrayList<Statement> ();
//		while (liveContextIterator.hasNext()) {
//			Statement statement = liveContextIterator.next();
//			if (!(statement.getObject().toString().contains(XMLSchema.NAMESPACE))){ //DCON.NS_DCON
//				aspects.add(statement);
//			}
//		}
//		
//		previousContext.addAll(aspects.iterator());
//		for (T resource : resources) {
//			previousContext.removeStatements(resource.asResource(), Variable.ANY, Variable.ANY);
//		}
//		for (T resource : resources) {
//			previousContext.addAll(liveContext.findStatements(resource.asResource(), Variable.ANY, Variable.ANY));		
//		}
//		
//		// updating live context
//		for (T resource : resources) {
//			liveContext.removeStatements(resource.asResource(), Variable.ANY, Variable.ANY);
//		}
//		liveContext.removeStatements(aspect.asResource(), Variable.ANY, Variable.ANY);
//		for (T resource : resources) {
//			liveContext.addAll(resource.getModel().iterator());
//		}	
//		liveContext.addAll(aspect.getModel().iterator());
//	}
	
	@Override
	public void update(List<Statement> toAdd, List<Statement> toRemove) {
		
		// --------
		// updating previous context with values from live context
		// --------
		
		ClosableIterator<Statement> lcStmts = liveContext.iterator();
		List<URI> aspects = new ArrayList<URI>();
		List<Statement> toKeep = new ArrayList<Statement>();
		
		// find all aspects in live context and keep these statements statements (to copy to previous context)
		while (lcStmts.hasNext()) {
			Statement statement = lcStmts.next();
			Node object = statement.getObject();
			if (object instanceof URI && ArrayUtils.contains(ASPECTS, object.asURI())) {
				aspects.add(statement.getSubject().asURI());
				toKeep.add(statement);
			}
		}
		lcStmts.close();
		
		// find all statements linking aspects to elements
		lcStmts = liveContext.iterator();
		while (lcStmts.hasNext()) {
			Statement statement = lcStmts.next();
			Resource subject = statement.getSubject();
			if (subject instanceof URI && aspects.contains(subject.asURI())) {
				toKeep.add(statement);
			}
		}
		lcStmts.close();
		
		// removes data in previous context which will be updated (will be removed from live context, thus moved to previous context)
		for (Statement stmt : toRemove) {
			URI resource = stmt.getSubject().asURI();
			if (!aspects.contains(resource)) {
				previousContext.removeStatements(resource.asResource(), Variable.ANY, Variable.ANY);
			}
		}
		
		// move data from live context to previous context
		List<Statement> all = new ArrayList<Statement>();
		all.addAll(toAdd);
		all.addAll(toRemove);
		for (Statement stmt : all) {
			URI resource = stmt.getSubject().asURI();
			if (!aspects.contains(resource)) {
				previousContext.addAll(liveContext.findStatements(resource, Variable.ANY, Variable.ANY));
			}
		}
		previousContext.addAll(toKeep.iterator());

		
		// --------
		// updating live context with method parameters
		// --------

		liveContext.removeAll(toRemove.iterator());
		liveContext.addAll(toAdd.iterator());
	}

}
