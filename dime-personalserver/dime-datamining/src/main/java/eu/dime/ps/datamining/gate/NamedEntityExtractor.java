package eu.dime.ps.datamining.gate;

import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import eu.dime.ps.datamining.exceptions.DataMiningException;

public class NamedEntityExtractor {

	// TODO Gate stuff for finding named entities (people, places, etc.) in arbitrary text
	
	public ResourceModel extract(ResourceModel resource) throws DataMiningException {
		String content = null;

		if (resource.is(DLPO.LivePost)) {
			content = resource.getString(DLPO.textualContent);
		} else {
			content = resource.getString(NAO.prefLabel);
		}
		
		if (content == null) {
			throw new DataMiningException("No textual content was found in the resource for NE extraction (e.g. nao:prefLabel, etc.)");
		}
		
		return null;
	}
	
}
