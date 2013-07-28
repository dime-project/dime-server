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
