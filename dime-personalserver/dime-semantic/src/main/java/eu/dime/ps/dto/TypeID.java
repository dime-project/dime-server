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

package eu.dime.ps.dto;

import org.ontoware.rdf2go.model.node.URI;

public class TypeID {
	
	URI uri;
	String subtype;
	
	public TypeID(URI uri) {
		this.uri = uri;
	}
	
	public TypeID(URI uri, String subtype){
		this.uri = uri;
		this.subtype = subtype;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypeID) {
			TypeID other = (TypeID) obj;
			if (other.uri != null && !other.uri.equals(this.uri))
				return false;
			if (other.subtype != null && !other.subtype.equals(this.subtype))
				return false;
		} else {
			return false;
		}
		return true;
	}
	
}
