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
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.RDFReactorRuntime;
import org.ontoware.rdfreactor.runtime.converter.CalendarConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.rdf.URIGenerator;

public class Profile extends Resource {

	//TODO add postalAdress and other attributes after revision
	private static final URI[] ITEMS_PROPERTIES = new URI[] { NCO.hasPersonName, NCO.hasAffiliation,
		NCO.hasEmailAddress, NCO.hasPhoneNumber, NCO.hasBirthDate,NCO.hasPostalAddress,NCO.hobby,NCO.hasLocation};

	
	private static final Logger logger = LoggerFactory.getLogger(Profile.class);
	public Profile() {
		super();
                this.put("supportsSharing", false);
	}

	public Profile(org.ontoware.rdfreactor.schema.rdfs.Resource resource,URI me) {
		super();
                put("type", "profile");
                this.put("supportsSharing", false);
		addToMap(resource,"",me);
	}

	public Profile(org.ontoware.rdfreactor.schema.rdfs.Resource resource, String serviceAccountId,URI me) {
		super();
		put("type", "profile");
                this.put("supportsSharing", false);
		addToMap(resource,serviceAccountId,me);
	}

	protected void addToMap(org.ontoware.rdfreactor.schema.rdfs.Resource resource, String serviceAccountId,URI me) {
		put("guid", "p_"+resource.asURI().toString());

		
		List<String> items = new LinkedList<String>();
		put("items", items);
		ClosableIterator<Statement> it = resource.getModel().findStatements(resource.asResource(),
				Variable.ANY, Variable.ANY);
		while (it.hasNext()) {
			Statement statement = it.next();
			URI predicate = statement.getPredicate();
			Node object = statement.getObject();

			if (ArrayUtils.contains(ITEMS_PROPERTIES, predicate)) {

				if (object instanceof URI)	    	    		
					items.add(object.asURI().toString());		
			} else if (predicate.equals(NAO.lastModified)) {
				put("lastUpdate",
						CalendarConverter.parseXSDDateTime_toCalendar(
								(String) object.asDatatypeLiteral().getValue()).getTimeInMillis());
			} else if (predicate.equals(NAO.prefLabel)) {
				put("name", object.asLiteral().getValue());
			} else if (predicate.equals(NAO.prefSymbol)) {
				if (object instanceof Literal )
					put("imageUrl", object.asLiteral().toString());
				else if (object instanceof URI)
					put("imageUrl", object.asURI().toString());
			} 
		
			
			

		}
		it.close();
		// adding said 
		this.put("said",serviceAccountId);		
		// Adding said 
				if (!containsKey("said")) {
					put("said", "");
				}
		
		if (!containsKey("name")) {
			put("name", "Profile " + resource.asURI().toString());
		}
		
		//adding editable attribute	
		//editable when it is di.me profile(name ends with @di.me),
		//not editable when it is external profile
		if (this.get("name").toString().endsWith("@di.me")){
			put("editable",true);		
		}
		else put("editable",false);

	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(Class<T> returnType, URI me) {
		if (!returnType.equals(PersonContact.class)) {
			throw new IllegalArgumentException("Only PersonContact class is allowed!");
		}
		return (T) asResource(URIGenerator.createNewRandomUniqueURI(), me);
	}

	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(URI resourceUri,
			Class<T> returnType,URI me) {
		if (!returnType.equals(PersonContact.class)) {
			throw new IllegalArgumentException("Only PersonContact class is allowed!");
		}
		return (T) asResource(resourceUri, me);
	}

	public PersonContact asResource(URI resourceUri, URI me) {
		Model rModel = RDF2Go.getModelFactory().createModel().open();
		PersonContact result = new PersonContact(rModel, resourceUri, true);

		// TODO set userId
		this.remove("userId");
		this.remove("said");
		this.remove("editable");

		for (String key : keySet()) {
			if (key.equals("guid") || key.equals("items")) {		
				continue; 
			}

			if (!(get(key) == null || "".equals(get(key)))) {

				if (key.equals("created")) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis((Long) get(key));
					rModel.addStatement(resourceUri, NAO.created,
							RDFReactorRuntime.java2node(rModel, calendar));
				} else if (key.equals("lastModified")) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis((Long) get(key));
					rModel.addStatement(resourceUri, NAO.lastModified,
							RDFReactorRuntime.java2node(rModel, calendar));
				} else if (key.equals("name")) {
					rModel.addStatement(resourceUri, NAO.prefLabel, new PlainLiteralImpl(
							(String) get(key)));
				} else if (key.equals("imageUrl")) {
					rModel.addStatement(resourceUri, NAO.prefSymbol, new URIImpl((String) get(key)));
				}		
			}
		}

		return result;
	}
	
	
	public void setUserId(org.ontoware.rdfreactor.schema.rdfs.Resource resource,String personId) {	
		
		if(personId.equals("@me")) this.put("userId", "@me");
		else this.put("userId", personId);
	}

}
