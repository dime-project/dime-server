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

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.validator.GenericValidator;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFReactorRuntime;

import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.rdf.URIGenerator;

public class ProfileCard extends Resource {

	private static final Map<URI, String> RENAMING_RULES;
	static {
		RENAMING_RULES = new HashMap<URI, String>();
		RENAMING_RULES.put(PPO.appliesToResource, "items");

	}

	// these properties are datetimes/timestamps
	// they need to be serialized as UNIX time (in milliseconds)
	// but deserialized and formatted following ISO 8601
	private static final URI[] DATETIME_PROPERTIES = new URI[] {
		new URIImpl("http://purl.org/dc/terms/date"),
		new URIImpl("http://purl.org/dc/terms/modified"), NIE.contentCreated,
		NIE.contentLastModified, NIE.contentModified, NIE.created, NIE.lastModified,
		NIE.lastRefreshed, NIE.modified, NAO.created, NAO.lastModified, NAO.modified,
		NFO.deletionDate, NFO.fileCreated, NFO.fileLastAccessed, NFO.fileLastModified,
		DLPO.timestamp };

	// for properties that want to be forced to a specific type, a converter can be provided
	private static final Map<URI, INodeConverter<?>> CONVERTERS = new HashMap<URI, INodeConverter<?>>();
	static {
		CONVERTERS.put(NAO.trustLevel, RDFReactorRuntime.getConverter(Double.class));
		CONVERTERS.put(NAO.directTrust, RDFReactorRuntime.getConverter(Double.class));
		CONVERTERS.put(NAO.networkTrust, RDFReactorRuntime.getConverter(Double.class));
		CONVERTERS.put(NAO.privacyLevel, RDFReactorRuntime.getConverter(Double.class));
	}

	public ProfileCard() {
		super();
		this.put("supportsSharing", true);
	}

	public ProfileCard(PrivacyPreference profileCard, String serviceAccountId,URI me) {
		super();
		addToMap(profileCard, RENAMING_RULES,me);
		this.put("editable", true);
		this.put("supportsSharing", true);
		this.put("guid", "pc_" + profileCard.asURI().toString());
		this.put("said",serviceAccountId);
		// adding said 

		ArrayList<String> includes = new ArrayList<String>();
		ArrayList<String> excludes = new ArrayList<String>();
		ClosableIterator<Statement> iterator = profileCard.getModel()
				.findStatements(profileCard, PPO.hasAccessSpace, Variable.ANY);
		while (iterator.hasNext()) {
			Node object = iterator.next().getObject();
			if (object instanceof URI) {
				Collection<Node> nodes = ModelUtils.findObjects(
						profileCard.getModel(), object.asURI(), NSO.includes);
				nodes.size();
				for (Node node : nodes) {
					if (node != null) {
						includes.add(node.asURI().toString());											
					}
				}
				nodes = ModelUtils.findObjects(profileCard.getModel(), object.asURI(), NSO.excludes);
				for (Node node : nodes){
					if (node != null){
						excludes.add(node.asURI().toString());
					}
				}

			}
		}
		iterator.close();
		this.put("nao:includes", includes);
		this.put("nao:excludes", excludes);


	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(Class<T> returnType,URI me) {
		// if no URI is given, a random one is generated
		return this.asResource(URIGenerator.createNewRandomUniqueURI(), returnType, me);
	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(URI resourceUri,
			Class<T> returnType,URI me) {		
		if (!returnType.equals(PrivacyPreference.class)) {
			throw new IllegalArgumentException("Only PrivacyClass class is allowed to create Profile Card!");
		}
		org.ontoware.rdfreactor.schema.rdfs.Resource resource;
		Model rModel = RDF2Go.getModelFactory().createModel().open();
		resource = new org.ontoware.rdfreactor.schema.rdfs.Resource(rModel, resourceUri, false);

		// HACK: Remove attributes not valid		
		this.remove("editable");
		this.remove("said");				
		this.remove("supportsSharing");

		String type = null;

		addToModel(rModel, resourceUri, RDF.type, PPO.PrivacyPreference);
		addToModel(rModel, resourceUri, RDFS.label, "PROFILECARD");



		for (String key : keySet()) {
			if (key.equals("guid"))
				continue; // discards the attribute guid
			if (key.equals("type") )
				continue; // already read above the loop
			if (key.equals("defProfile") )
				continue;
			if (key.equals("nao:includes") ||key.equals("nao:excludes"))
				continue;
			if (key.equals("serviceadapterguid"))
				continue;

			Object o = get(key);

			// skip keys without value
			if (o == null) {
				continue;
			}

			else if (key.equals("items")) {
				if (o instanceof Collection) {
					Collection<Object> c = (Collection<Object>) o;
					for (Object ob : c) {
						addToModel(rModel, resourceUri, PPO.appliesToResource, ob);
					}
				} else {
					addToModel(rModel, resourceUri,PPO.appliesToResource, o);
				}
				continue;
			}
			else if(key.equals("userId")){	
				addToModel(rModel, resourceUri, NAO.creator, o.toString().equals("@me") ? me.toString() : o);
				continue;	
			}							

			URI predicate = new URIImpl(expand(key));
			if (o instanceof Collection) {
				Collection<Object> c = (Collection<Object>) o;
				for (Object ob : c) {
					addToModel(rModel, resourceUri, predicate, ob);
				}
			} else {
				addToModel(rModel, resourceUri, predicate, o);		
			}
		}

		return (T) resource.castTo(returnType);
	}

	private void addToModel(Model model, URI rUri, URI property, Object value) {
		if (ArrayUtils.contains(DATETIME_PROPERTIES, property)) {
			Calendar calendar = Calendar.getInstance();

			if (value instanceof Long) {
				calendar.setTimeInMillis((Long) value);
				value = calendar;
			}
			if (value instanceof Integer) {

				if (GenericValidator.isLong(value.toString())) {
					calendar.setTimeInMillis( ((Integer) value).longValue());
					value = calendar;
				}
			}

		}

		if (value instanceof String) {
			Node object = null;
			try {
				object = new URIImpl(expand((String) value));
				model.addStatement(rUri, property, object);
			} catch (IllegalArgumentException e) {
				object = model.createPlainLiteral((String) value);
				model.addStatement(rUri, property, object);
			}
		} else if (CONVERTERS.containsKey(property)) {
			model.addStatement(rUri, property, CONVERTERS.get(property).toNode(model, value));
		} else {
			model.addStatement(rUri, property, RDFReactorRuntime.java2node(model, value));
		}
	}


}
