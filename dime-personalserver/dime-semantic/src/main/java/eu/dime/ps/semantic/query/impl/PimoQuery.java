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

package eu.dime.ps.semantic.query.impl;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Extends {@link BasicQuery}, and only return resources which are in
 * the user's PIM graph.
 * 
 * @author Ismael Rivera
 */
public class PimoQuery<T extends org.ontoware.rdfreactor.schema.rdfs.Resource> extends BasicQuery<T> {

	public PimoQuery(PimoService pimoService, Class<T> returnType) {
		super(pimoService, returnType);
		this.from(pimoService.getPimoUri());
	}

	public PimoQuery(PimoService pimoService, Class<T> returnType, URI... types) {
		super(pimoService, returnType, types);
		this.from(pimoService.getPimoUri());
	}

}
