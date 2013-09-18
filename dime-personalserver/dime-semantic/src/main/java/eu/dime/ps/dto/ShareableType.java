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

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.PPO;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

/**
 * Mapping of RDF resources to shareable di.me entities.
 * 
 * @author Ismael Rivera
 */
public enum ShareableType {
	
	DATABOX("databox", NFO.DataContainer),
	FILE_DATA_OBJECT("resource", NFO.FileDataObject),
	LIVEPOST("livepost", DLPO.LivePost),
	PRIVACY_PREFERENCE_FILE("resource", PPO.PrivacyPreference, "FILE"),
	PRIVACY_PREFERENCE_LIVEPOST("livepost", PPO.PrivacyPreference, "LIVEPOST"),
	PRIVACY_PREFERENCE_LIVESTREAM("livestream", PPO.PrivacyPreference, "LIVESTREAM"),
	
	// profilecards are sent as 'profiles' (PersonContact) between peers
	PRIVACY_PREFERENCE_PROFILECARD("profile", PPO.PrivacyPreference, "PROFILECARD");
	
	private static final Map<String, ShareableType> lookup;

	static {
		lookup = new HashMap<String, ShareableType>();
		for (ShareableType type : EnumSet.allOf(ShareableType.class)) {
			if (type.getTypeID().uri.equals(PPO.PrivacyPreference)) {
				lookup.put(type.getTypeID().subtype, type);
			} else {
				lookup.put(type.getTypeID().uri.toString(), type);
			}
		}
	}

	private String label;
	private TypeID id;
	
	private ShareableType(String label, URI uri) {
		this.label = label;
		this.id = new TypeID(uri);
	}

	private ShareableType(String label, URI uri, String subtype) {
		this.label = label;
		this.id = new TypeID(uri, subtype);
	}

	public String getLabel() {
		return label;
	}
	
	public TypeID getTypeID() {
		return id;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
	
	public static ShareableType get(org.ontoware.rdfreactor.schema.rdfs.Resource resource) {
		ShareableType resultType = null;
		
		// query its model, and get the first rdf:type found
		ClosableIterator<Statement> typeIt = resource.getModel().findStatements(resource, RDF.type, Variable.ANY);
		while (typeIt.hasNext()) {
			Node rdfType = typeIt.next().getObject();
			if (rdfType instanceof URI && !rdfType.equals(RDFS.Resource)) {
				URI resourceType = rdfType.asURI();
				
				if (PPO.PrivacyPreference.equals(resourceType)) {
					String subtype = getPrivacyPreferenceType(resource);
					if (subtype != null) {
						resultType = lookup.get(subtype);
					}
				} else {
					resultType = lookup.get(resourceType.toString());
				}

				// first type found is returned
				if (resultType != null) {
					return resultType;
				}
			}
		}
		typeIt.close();

		return null; // no type found
	}
	
	private static String getPrivacyPreferenceType(org.ontoware.rdfreactor.schema.rdfs.Resource privacyPreference) {
		for (String label : privacyPreference.getAllLabel_as().asList()) {
			if ("DATABOX".equals(label)
					|| "FILE".equals(label)
					|| "LIVEPOST".equals(label)
					|| "PROFILECARD".equals(label)) {
				return label;
			}
		}
		return null;
	}
	
}