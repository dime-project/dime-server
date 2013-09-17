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
import ie.deri.smile.vocabulary.DDO;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.DPO;
import ie.deri.smile.vocabulary.DUHO;
import ie.deri.smile.vocabulary.FOAF;
import ie.deri.smile.vocabulary.GEO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCAL;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NDO;
import ie.deri.smile.vocabulary.NEXIF;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NID3;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NMM;
import ie.deri.smile.vocabulary.NMO;
import ie.deri.smile.vocabulary.NRL;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;
import ie.deri.smile.vocabulary.TMO;
import info.aduna.net.UriUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.validator.GenericValidator;
import org.joda.time.DateTime;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFReactorRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.rdf.URIGenerator;

public class Resource extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(Resource.class);

	private static final String[][] MAPPINGS = new String[][] {
		// THESE FIRSTS MAPPINGS ARE A HACK FOR THE POC,
		// SINCE THE UI EXPECTS SOME SPECIFIC ATTRIBUTE NAMES
		{ NAO.prefLabel.toString(), "name" },
		{ DAO.accountType.toString(), "serviceadapterguid" },
		{ NAO.prefSymbol.toString(), "imageUrl" },
		{ NAO.created.toString(), "created" },
		{ NAO.lastModified.toString(), "lastModified" },
		{ DLPO.textualContent.toString(), "text" },	    

		// { NFO.fileName.toString(), "name" },

		// 'items' also applies to other properties, so this should be
		// taking into special account when deserializing, because the
		// 'expand' method won't work in this case.
		{ PIMO.hasMember.toString(), "items" },	
		// -------

		{ RDF.RDF_NS, "rdf:" }, { RDFS.RDFS_NS, "rdfs:" },
		{ "http://purl.org/dc/elements/1.1/", "dc:" },
		{ "http://purl.org/dc/terms/", "dcterms:" },
		{ "http://purl.org/dc/dcmitype/", "dctype:" },
		{ "http://www.w3.org/2004/02/skos/core#", "skos:" }, { DAO.NS_DAO.toString(), "dao:" },
		{ DCON.NS_DCON.toString(), "dcon:" }, { DDO.NS_DDO.toString(), "ddo:" },
		{ DLPO.NS_DLPO.toString(), "dlpo:" }, { DPO.NS_DPO.toString(), "dpo:" },
		{ DUHO.NS_DUHO.toString(), "duho:" }, { FOAF.NS_FOAF.toString(), "foaf:" },
		{ GEO.NS_GEO.toString(), "geo:" }, { NAO.NS_NAO.toString(), "nao:" },
		{ NCAL.NS_NCAL.toString(), "ncal:" }, { NCO.NS_NCO.toString(), "nco:" },
		{ NDO.NS_NDO.toString(), "ndo:" }, { NEXIF.NS_NEXIF.toString(), "nexif:" },
		{ NFO.NS_NFO.toString(), "nfo:" }, { NID3.NS_NID3.toString(), "nid3:" },
		{ NIE.NS_NIE.toString(), "nie:" }, { NMM.NS_NMM.toString(), "nmm:" },
		{ NMO.NS_NMO.toString(), "nmo:" }, { NRL.NS_NRL.toString(), "nrl:" },
		{ NSO.NS_NSO.toString(), "nso:" }, { PIMO.NS_PIMO.toString(), "pimo:" },
		{ PPO.NS_PPO.toString(), "ppo:" }, { TMO.NS_TMO.toString(), "tmo:" } };

	// if needed, we can filter out some properties which don't need to go
	// to the UI for example
	private static final URI[] PROPERTIES_TO_FILTER = new URI[] {
		PIMO.createdPimo, PIMO.isDefinedBy, PIMO.referencingOccurrence, PIMO.groundingForDeletedThing,
		PIMO.hasOtherRepresentation, PIMO.hasOtherConceptualization, PIMO.hasDeprecatedRepresentation,
		RDFS.label,
		NAO.annotation, new URIImpl("http://purl.org/dc/elements/1.1/date"),
		new URIImpl("http://purl.org/dc/terms/created"),
		new URIImpl("http://purl.org/dc/terms/modified") };

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

	private String said = null;

	public Resource() {
		super();
	}

	public Resource(org.ontoware.rdfreactor.schema.rdfs.Resource resource, URI me) {
		this(resource, null,me);
	}

	public Resource(org.ontoware.rdfreactor.schema.rdfs.Resource resource, String said,URI me) {
		super();
		this.said = said;
		addToMap(resource, new HashMap<URI, String>(),me);
	}

	public Resource(org.ontoware.rdfreactor.schema.rdfs.Resource resource, String said,Map<URI, String> renamingRules,URI me) {
		super();
		this.said = said;
		addToMap(resource, renamingRules,me);
	}

	protected void addToMap(org.ontoware.rdfreactor.schema.rdfs.Resource resource,
			Map<URI, String> renamingRules,URI me) {
		this.put("guid", resource.asURI().toString());


		ClosableIterator<Statement> it = resource.getModel().findStatements(resource.asResource(),
				Variable.ANY, Variable.ANY);
		while (it.hasNext()) {
			Statement statement = it.next();
			URI predicate = statement.getPredicate();
			Node object = statement.getObject();

			if (ArrayUtils.contains(PROPERTIES_TO_FILTER, predicate)) {
				continue;
			}

			// HACK FOR PoC: the UI expects an attribute 'type' in the JSON
			// mapped to person, group, etc.
			// this need to be mapped to rdf:type and the proper URI for the
			// instance - now hard coded
			// person|group|livestream|livestreamitem|databox|resource|profile|notification|serviceaccount|situation
			if (predicate.equals(RDF.type)) {
				URI type = object.asURI();
				if (type.equals(PIMO.Person)) {
					this.put("type", "person");
				} else if (type.equals(PIMO.PersonGroup)) {
					this.put("type", "group");
				} else if (type.equals(NCO.Contact)) {
					this.put("type", "profile");		    
				} else if (type.equals(DLPO.LivePost)) {
					this.put("type", "livepost");
				} else if (type.equals(DAO.Account)) {
					this.put("type", "account");
				} else if (type.equals(NFO.FileDataObject)) {
					this.put("type", "resource");
				} else if (type.equals(DCON.Situation)) {
					this.put("type", "situation");
				}
				else if (type.equals(PPO.PrivacyPreference)) {					
					if (resource.getModel().contains(resource.asURI(), RDFS.label,
							new PlainLiteralImpl("DATABOX"))) {
						this.put("type", "databox");
					} else if (resource.getModel().contains(resource.asURI(), RDFS.label,
							new PlainLiteralImpl("PROFILECARD"))) {
						this.put("type", "profile");			
					}

				} else if (type.equals(PIMO.SocialEvent)) {
					this.put("type", "event");
				}else if (type.equals(NFO.DataContainer)) {	
					this.put("type", "databox");
				}

				// the rest of the loop is how it should be after PoC
				continue;
			}
			// -------



			Object value = null;
			if (object instanceof DatatypeLiteral) {
				DatatypeLiteral literal = object.asDatatypeLiteral();
				URI type = literal.getDatatype();
				if (type.equals(XSD._double) || type.equals(XSD._float)
						|| type.equals(XSD._decimal)) {
					value = Double.parseDouble(literal.getValue().replace(",","."));
				} else if (type.equals(XSD._long) || type.equals(XSD._integer)
						|| type.equals(XSD._int)) {
					value = Long.parseLong(literal.getValue());
				} else {
					value = literal.getValue();
				}
			} else if (object instanceof Literal) {
				value = object.asLiteral().getValue();
			} else if (object instanceof URI) {
				URI oUri = object.asURI();
				if (renamingRules.containsKey(oUri)) {
					value = renamingRules.get(oUri);
				} else {
					value = collapse(object.asURI().toString());
				}
			} else {
				// discards blank nodes...
				continue;
			}

			if (ArrayUtils.contains(DATETIME_PROPERTIES, predicate)) {

				value = new DateTime((String) value).getMillis();
			}

			String key = null;
			if (renamingRules.containsKey(predicate)) {
				key = renamingRules.get(predicate);
				if(key.equals("defProfile") && 
						!value.toString().startsWith("p_"))
					value="p_"+value;

			} else {
				key = collapse(predicate.toString());
			}
			if (ArrayUtils.contains(DATETIME_PROPERTIES, predicate)) {
				// datetime properties seem to be unique, so all
				// are discarded but one.
				this.put(key, value);
			} else if (this.containsKey(key)) {
				Object v = this.get(key);
				List<Object> values;
				if (v instanceof List) {
					values = (List<Object>) v;
				} else {
					values = new LinkedList<Object>();
					values.add(v);
				}
				values.add(value);
				this.put(key, values);
			} else {
				this.put(key, value);
			}

		}
		it.close();


		//set userId
		setUserId(resource,me.toString());

		// ensure always 'name' and 'imageUrl' is returned
		if (!this.containsKey("name")) {
			this.put("name", this.get("type"));
		}
		if (!this.containsKey("imageUrl")) {
			this.put("imageUrl", "");
		}

		// ensure always 'items' is an array
		if (this.containsKey("items")) {
			Object o = this.get("items");
			if (!(o instanceof List)) {
				List<Object> items = new LinkedList<Object>();
				items.add(o);
				this.put("items", items);
			}
		} else {
			List<Object> items = new LinkedList<Object>();
			this.put("items", items);
		}

		// inject 'downloadUrl' for files
		if (resource instanceof FileDataObject) {
			if (said == null) {
				logger.warn("'downloadUrl' could not be generated, a 'said' must be passed.");				
			} 

			else 
			{
				// a shared file 
				Node shared= ModelUtils.findObject(resource.getModel(), resource, NSO.sharedBy); 
				if(shared != null) {

					//get the account fom the NSO.sharedWith 					
					Node account = ModelUtils.findObject(resource.getModel(), resource, NSO.sharedWith);
					if (account != null){
						this.put("downloadUrl", "/dime-communications/api/dime/rest/" + said
								+ "/resource/@me/shared/" + shared.asURI().toString()+"/"+account.asURI().toString()+"/"+resource.asURI().toString());
					}
					else{					
						logger.warn("'downloadUrl' could not be generated, there was no nso.sharedWith associated to the file");
					}				
				}
				//a file from the CMS
				else{	
					String guid = UriUtil.decodeUri(resource.asURI().toString());
					String encodedGuid;

					try {
						encodedGuid = URLEncoder.encode(guid, "UTF-8");
						this.put("downloadUrl", "/dime-communications/api/dime/rest/" + said
								+ "/resource/filemanager/" + encodedGuid);
					} catch (UnsupportedEncodingException e) {
						logger.warn("'downloadUrl' could not be generated well 'said' is wrong.");
					}

				}
			}
			//TODO add the url from files not shared that are not in the CMS
		}
	}

	private void setUserId(org.ontoware.rdfreactor.schema.rdfs.Resource resource,String me) {

		Node sharedBy = ModelUtils.findObject(resource.getModel(), resource,  NSO.sharedBy);
		Node creator = ModelUtils.findObject(resource.getModel(), resource, NAO.creator);
		
		if (sharedBy != null)
			writeUserId(sharedBy, me);						
		else if (creator != null)
			writeUserId(creator, me);	
		else{ 
			this.put("userId","@me" );
		}

	}

	private void writeUserId(Node node, String me) {
		if (node instanceof  Literal){
			this.put("userId", node.asLiteral().toString().equals(me) ? "@me" : node.asLiteral().toString());
		}
		else{
			this.put("userId", node.asResource().toString().equals(me) ? "@me" : node.asResource().toString());

		}

	}

	protected String collapse(String property) {
		for (String[] mapping : MAPPINGS) {
			if (property.startsWith(mapping[0])) {
				return property.replace(mapping[0], mapping[1]);
			}
		}
		return property;
	}

	protected String expand(String property) {
		for (String[] mapping : MAPPINGS) {
			if (property.startsWith(mapping[1])) {
				return property.replace(mapping[1], mapping[0]);
			}
		}
		return property;
	}

	/**
	 * Returns an instance of the passed 'returnType' class, which underlying
	 * RDF model is filled with the data contained
	 * 
	 * @param returnType
	 * @return
	 */
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(Class<T> returnType,URI me) {
		// if no URI is given, a random one is generated
		return this.asResource(URIGenerator.createNewRandomUniqueURI(), returnType, me);
	}

	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(URI resourceUri,
			Class<T> returnType,URI me) {
		org.ontoware.rdfreactor.schema.rdfs.Resource resource;

		Model rModel = RDF2Go.getModelFactory().createModel().open();
		resource = new org.ontoware.rdfreactor.schema.rdfs.Resource(rModel, resourceUri, false);

		// HACK: Remove attributes not valid	
		this.remove("originId");
		this.remove("originalPath");
		this.remove("editable");
		this.remove("said");	
		// not needed in the RDF store
		this.remove("downloadUrl");
		this.remove("supportsSharing");

		String type = null;
		String category=null;
		if (!containsKey("type")) {
			// WARNING: now we just log the error, and return an empty object
			logger.error("The object is incorrect: the 'type' is missing " + this.toString());
			return (T) resource.castTo(returnType);
		} else {
			type = (String) get("type");
			if (type.equals("person")) {
				addToModel(rModel, resourceUri, RDF.type, PIMO.Person);
				if(this.containsKey("defProfile")){
					String defProfile = this.get("defProfile").toString();
					if(!defProfile.equals("")){
						if(defProfile.startsWith("p_"))
							defProfile=defProfile.replaceFirst("p_","");
						addToModel(rModel, resourceUri, PIMO.groundingOccurrence, defProfile);}
				}
			} else if (type.equals("group")) {
				addToModel(rModel, resourceUri, RDF.type, PIMO.PersonGroup);
			} else if (type.equals("profile")) {	    	
				if(!this.containsKey("nao:includes")){
					addToModel(rModel, resourceUri, RDF.type, NCO.PersonContact);}
				else {
					addToModel(rModel, resourceUri, RDF.type, PPO.PrivacyPreference);
					addToModel(rModel, resourceUri, RDFS.label, "PROFILECARD");
					category="profilecard";}
			} else if (type.equals("livepost")) {
				addToModel(rModel, resourceUri, RDF.type, DLPO.LivePost);
			} else if (type.equals("account")) {
				addToModel(rModel, resourceUri, RDF.type, DAO.Account);
			} else if (type.equals("resource")) {
				addToModel(rModel, resourceUri, RDF.type, NFO.FileDataObject);
			} else if (type.equals("databox")) {
				addToModel(rModel, resourceUri, RDF.type, NFO.DataContainer);
				addToModel(rModel, resourceUri, RDF.type, PPO.PrivacyPreference);
				addToModel(rModel, resourceUri, RDFS.label, "DATABOX");	  
			} else if (type.equals("event")) {
				addToModel(rModel, resourceUri, RDF.type, PIMO.SocialEvent);
			}
			else if (type.equals("situation")) {
				addToModel(rModel, resourceUri, RDF.type, DCON.Situation);
			}	

		}

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

			if (key.equals("items") && type.equals("databox")) {
				if (o instanceof Collection) {
					Collection<Object> c = (Collection<Object>) o;
					for (Object ob : c) {
						addToModel(rModel, resourceUri, NIE.hasPart, ob);
					}
				} else {
					addToModel(rModel, resourceUri, NIE.hasPart, o);
				}
				continue;
			}
			else if (key.equals("items") && category != null) {
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
			else if (key.equals("items") && type.equals("group")) {
				if (o instanceof Collection) {
					Collection<Object> c = (Collection<Object>) o;
					for (Object ob : c) {
						addToModel(rModel, resourceUri, PIMO.hasMember, ob);
					}
				} else {
					addToModel(rModel, resourceUri, PIMO.hasMember, o);
				}

				// the rest of the loop is how it should be after PoC
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