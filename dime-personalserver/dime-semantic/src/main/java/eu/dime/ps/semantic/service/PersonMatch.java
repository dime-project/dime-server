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

