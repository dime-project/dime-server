package eu.dime.ps.semantic.service;

import org.ontoware.rdf2go.model.node.Resource;

public class ResourceStructure {
	
	private Resource subject;	
	private String predicate;
	private String object;
	
	public ResourceStructure() {
		
	}
	
	public ResourceStructure(Resource subject, String predicate, String object) {
		this.subject=subject;
		this.predicate=predicate;
		this.object=object;		
	}
	
	public Resource getSubject() {
		return subject;
	}
	public String getPredicate() {
		return predicate;
	}
	public String getObject() {
		return object;
	}
	
	@Override
	public String toString() {
		return subject + " - " + predicate + " - " + object;
	}

}
