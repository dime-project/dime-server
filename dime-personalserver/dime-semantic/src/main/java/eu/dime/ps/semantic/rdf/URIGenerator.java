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

package eu.dime.ps.semantic.rdf;

import java.util.UUID;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class URIGenerator {
	
	public static URI createNewRandomUniqueURI() {
		return new URIImpl("urn:uuid:" + UUID.randomUUID().toString());
	}
	
	/**
	 * @param uriPrefix must include schema information
	 * @return a new, random unique URI starting with uriPrefix
	 */
	public static URI createNewRandomUniqueURI(String uriPrefix) {
		return new URIImpl(uriPrefix + UUID.randomUUID().toString());
	}
}
