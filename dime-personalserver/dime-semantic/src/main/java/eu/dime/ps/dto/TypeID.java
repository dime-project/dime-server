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
