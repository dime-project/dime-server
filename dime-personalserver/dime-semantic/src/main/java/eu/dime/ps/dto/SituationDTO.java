package eu.dime.ps.dto;

import ie.deri.smile.vocabulary.DCON;

import java.util.HashMap;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.model.pimo.Person;

public class SituationDTO extends Resource {
	
	private static final long serialVersionUID = 1L;

	public SituationDTO() {
		super();
	}
	
	public SituationDTO(Situation situation, Person me) {
		super();
		addToMap(situation, new HashMap<URI, String>(),me.asURI());
		
		// <me> dcon:hasSituation <situationX> indicates a situation is active
		this.put("active", situation.getModel().contains(me, DCON.hasSituation, situation.asURI()));
	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(URI resourceUri,
			Class<T> returnType, URI me) {
		
		boolean isActive = false;
		if (this.get("active").equals(true)) {
			isActive = true;
		}
		
		this.remove("active");
		T situation = super.asResource(resourceUri, returnType, me);
		if (isActive) {
			situation.getModel().addStatement(me, DCON.hasSituation, situation);
		}
			
		return situation;
	}

}