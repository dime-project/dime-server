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

package eu.dime.ps.controllers.situation;

import ie.deri.smile.context.ContextMatcher;
import ie.deri.smile.context.MatchingException;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.NAO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.rdf.ResourceStore;

public class SimpleContextMatcher implements ContextMatcher {

	private static final Logger logger = LoggerFactory.getLogger(SimpleContextMatcher.class);

	private final ResourceStore resourceStore;
	private final URI queryContextUri;
	private final CopyOnWriteArraySet<URI> candidateSet;

	Situation workMeeting;
	Situation exercising;
	Situation officeBreak;
	
	public SimpleContextMatcher(URI queryContext, ResourceStore resourceStore) {
		this.queryContextUri = queryContext;
		this.resourceStore = resourceStore;
		this.candidateSet = new CopyOnWriteArraySet<URI>();
	}

	@Override
	public Map<URI, Double> match() throws MatchingException {
		Map<URI, Double> results = new HashMap<URI, Double>();
		
		if (workMeeting == null)
			workMeeting = resourceStore.find(Situation.class).where(NAO.prefLabel).is("work meeting").first();
		if (exercising == null)
			exercising = resourceStore.find(Situation.class).where(NAO.prefLabel).is("exercising").first();
		if (officeBreak == null)
			officeBreak = resourceStore.find(Situation.class).where(NAO.prefLabel).is("office break").first();
		
		Model livecontext = resourceStore.getTripleStore().getModel(queryContextUri);
		
		Node running = ModelUtils.findSubject(livecontext, DCON.currentActivity, new URIImpl("http://www.semanticdesktop.org/ontologies/2011/10/05/dpo#Running"));
		if (running != null && exercising != null) {
			results.put(exercising.asURI(), 0.98);
			return results;
		}
		
		Node conraduser = ModelUtils.findObject(livecontext, new URIImpl("urn:ssid:conraduser"), DCON.signal);
		Node epiknet = ModelUtils.findObject(livecontext, new URIImpl("urn:ssid:epik-net"), DCON.signal);
		if (conraduser != null && epiknet != null) {
			double conraduserSignal = Double.parseDouble(conraduser.asLiteral().getValue());
			double epiknetSignal = Double.parseDouble(epiknet.asLiteral().getValue());
			if (epiknetSignal > conraduserSignal) {
				if (officeBreak != null)
					results.put(officeBreak.asURI(), 0.91);
			} else {
				if (workMeeting != null)
					results.put(workMeeting.asURI(), 0.96);
			}
			return results;
		}
		
		return results;
	}

	@Override
	public void addCandidate(URI candidateContext) {
		this.candidateSet.add(candidateContext);
	}
	
	@Override
	public void removeCandidate(URI candidateContext) {
		this.candidateSet.remove(candidateContext);
	}

}
