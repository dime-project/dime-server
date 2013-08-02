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

package eu.dime.ps.semantic.service;

import ie.deri.smile.matching.MatchResult;

import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.model.node.Resource;

public class PersonMatch {

	private Resource source;

	private Resource target;
	
	private Double similarityScore;

    private Set<MatchResult> profileMatches = new HashSet<MatchResult>();
    
    public PersonMatch() {
    }

	public PersonMatch(Resource source, Resource target, Double similarityScore) {
		this.source = source;
		this.target = target;
		this.similarityScore = similarityScore;
	}
	
	public PersonMatch(Resource source, Resource target, Double similarityScore, Set<MatchResult> profileMatches) {
		this(source, target, similarityScore);
		this.profileMatches = profileMatches;
	}

	public Resource getSource() {
		return this.source;
	}
	
	public Resource getTarget() {
		return target;
	}

	public Double getSimilarityScore() {
		return similarityScore;
	}

	public Set<MatchResult> getProfileMatches() {
		return profileMatches;
	}

	@Override
	public String toString() {
		return "eu.dime.ps.semantic.service.PersonMatch [source="+source+", target="+target+", similarityScore="+similarityScore+"]";
	}
	
}

