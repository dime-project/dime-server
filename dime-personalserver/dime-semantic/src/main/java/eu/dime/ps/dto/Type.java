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

package eu.dime.ps.dto;

import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DDO;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.PIMO;
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
 * Mapping of RDF resources to di.me entities.
 * 
 * @author Ismael Rivera
 */
public enum Type {
	
	ACCOUNT("account", DAO.Account),
	DATABOX("databox", NFO.DataContainer),
	DEVICE("device", DDO.Device),
	EVENT("event", PIMO.SocialEvent),
	GROUP("group", PIMO.PersonGroup),
	FILE_DATA_OBJECT("resource", NFO.FileDataObject),
	LIVEPOST("livepost", DLPO.LivePost),
	LOCATION("location", PIMO.Location),
	PERSON("person", PIMO.Person),
	PLACEMARK("place", NFO.Placemark),
	PROFILE("profile", NCO.PersonContact),
	PRIVACY_PREFERENCE_FILE("resource", PPO.PrivacyPreference, "FILE"),
	PRIVACY_PREFERENCE_LIVEPOST("livepost", PPO.PrivacyPreference, "LIVEPOST"),
	PRIVACY_PREFERENCE_LIVESTREAM("livestream", PPO.PrivacyPreference, "LIVESTREAM"),
	PRIVACY_PREFERENCE_PROFILECARD("profilecard", PPO.PrivacyPreference, "PROFILECARD"),
	SITUATION("situation", DCON.Situation);
	
	private static final Map<String, Type> lookup;

	static {
		lookup = new HashMap<String,Type>();
		for (Type type : EnumSet.allOf(Type.class)) {
			if (type.getTypeID().uri.equals(PPO.PrivacyPreference)) {
				lookup.put(type.getTypeID().subtype, type);
			} else {
				lookup.put(type.getTypeID().uri.toString(), type);
			}
		}
	}

	private String label;
	private TypeID id;
	
	private Type(String label, URI uri) {
		this.label = label;
		this.id = new TypeID(uri);
	}

	private Type(String label, URI uri, String subtype) {
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
	
	public static Type get(org.ontoware.rdfreactor.schema.rdfs.Resource resource) {
		Type resultType = null;
		
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