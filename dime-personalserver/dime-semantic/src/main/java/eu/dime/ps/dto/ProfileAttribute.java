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

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.GEO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;

import java.util.Calendar;
import java.util.HashMap;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.exception.BadFormedException;
import eu.dime.ps.semantic.rdf.URIGenerator;

public class ProfileAttribute extends Resource {

	private static final Logger logger = LoggerFactory.getLogger(ProfileAttribute.class);

	public ProfileAttribute() {
		super();
	}

	public ProfileAttribute(org.ontoware.rdfreactor.schema.rdfs.Resource resource,URI me)
			throws BadFormedException {
		super();
		this.put("type", "profileattribute");
		addToMap(resource,me);
	}


	private HashMap<String, Object> extractPostalAddress(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> postalAddress = new HashMap<String, Object>();		
			
		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.region);
		if (node != null)
			postalAddress.put("region", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.country);
		if (node != null)
			postalAddress.put("country", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.extendedAddress);
		if (node != null)
			postalAddress.put("extendedAddress", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.addressLocation);
		if (node != null){
			
			try{
				postalAddress.put("addressLocation", node.asURI().toString());
			}
			catch(ClassCastException e){
				logger.debug("addressLocation paramater in PostalAddress attribute "+resource.asURI().toString()+"is not an URI",e);
				if(node instanceof Literal)
					postalAddress.put("addressLocation", node.asLiteral().getValue());
			}
		}
		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.streetAddress);
		if (node != null)
			postalAddress.put("streetAddress", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.postalcode);
		if (node != null)
			postalAddress.put("postalCode", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.locality);
		if (node != null)
			postalAddress.put("locality", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.pobox);
		if (node != null)
			postalAddress.put("pobox", node.asLiteral().getValue());

		return postalAddress;
	}

	private HashMap<String, Object> extractPersonName(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> personName = new HashMap<String, Object>();

		// PersonName is a subclass of Name		
		personName.putAll(extractName(resource));

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.nameHonorificSuffix);
		if (node != null)
			personName.put("nameHonorificSuffix", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.nameFamily);
		if (node != null)
			personName.put("nameFamily", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.nameHonorificPrefix);
		if (node != null)
			personName.put("nameHonorificPrefix", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.nameAdditional);
		if (node != null)
			personName.put("nameAdditional", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.nameGiven);
		if (node != null)
			personName.put("nameGiven", node.asLiteral().getValue());

		return personName;
	}

	private HashMap<String, Object> extractIMAccount(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> imaccount = new HashMap<String, Object>();

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.imStatus);
		if (node != null)
			imaccount.put("imStatus", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.imStatusType);
		if (node != null)				
			imaccount.put("imStatusType", node.asURI().toString());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.imID);
		if (node != null)
			imaccount.put("imID", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.imNickname);
		if (node != null)
			imaccount.put("imNickname", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.imAccountType);
		if (node != null)
			imaccount.put("imAccountType", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.imStatusMessage);
		if (node != null)
			imaccount.put("imStatusMessage", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.hasIMCapability);
		if (node != null)
			imaccount.put("hasIMCapability", node.asURI().toString());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.isAccessedBy);
		if (node != null)
			imaccount.put("isAccessedBy", node.asURI().toString());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.publishesPresenceTo);
		if (node != null)
			imaccount.put("publishesPresenceTo", node.asURI().toString());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.requestedPresenceSubscriptionTo);
		if (node != null)
			imaccount.put("requestedPresenceSubscriptionTo", node.asURI().toString());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.isBlocked);
		if (node != null)
			imaccount.put("isBlocked", node.asLiteral().getValue());

		return imaccount;
	}

	private HashMap<String, Object> extractAffiliation(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> affiliation = new HashMap<String, Object>();

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.department);
		if (node != null)
			affiliation.put("department", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.title);
		if (node != null)
			affiliation.put("title", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.role);
		if (node != null)
			affiliation.put("role", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.org);
		if (node != null){ 
		
		 Node contact = ModelUtils.findObject(resource.getModel(), node.asResource(), NCO.Contact);
			try{
				affiliation.put("org", contact.asURI().toString());
				affiliation.put("org", node.asURI().toString());
			}
		catch(ClassCastException e){
			logger.debug("org paramater in Affiliation attribute "+resource.asURI().toString()+"is not an URI",e);
			if(node instanceof Literal)
				affiliation.put("org", node.asLiteral().getValue());
		}}
		return affiliation;
	}

	private HashMap<String, Object> extractName(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> name = new HashMap<String, Object>();

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.nickname);
		if (node != null)
			name.put("nickname", node.asLiteral().getValue());

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.fullname);
		if (node != null)
			name.put("fullname", node.asLiteral().getValue());


		return name;
	}

	private HashMap<String, Object> extractBirthDate(org.ontoware.rdfreactor.schema.rdfs.Resource resource) 
			throws BadFormedException {
		HashMap<String, Object> birthDate = new HashMap<String, Object>();

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.birthDate);
		if (node != null){
			try {
				// This is a DataTypeLiteral					
				DatatypeLiteral literal = node.asDatatypeLiteral();
				if (literal != null){
					String value = literal.getValue();				
					birthDate.put("birthDate",value);				
				}				
			} catch(Exception e){
				throw new BadFormedException("BirthDate value"+ node+" cannot be added to profile attribute",e);
			}

		}

		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.age);
		if (node != null){
			try{
				// age should be a nonNegativeNumber
				Literal literal = node.asLiteral();
				if (literal != null){
					String value = literal.getValue();
					Integer age = Integer.valueOf(value);
					birthDate.put("age", age);
				}					
			} catch(Exception e){			
			}			
		}

		return birthDate;
	}

	private HashMap<String, Object> extractEmailAddress(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> emailAddress = new HashMap<String, Object>();

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.emailAddress);
		if (node != null)
			emailAddress.put("emailAddress", node.asLiteral().getValue());

		return emailAddress;
	}

	private HashMap<String, Object> extractPhoneNumber(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> phoneNumber = new HashMap<String, Object>();

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.phoneNumber);
		if (node != null)
			phoneNumber.put("phoneNumber", node.asLiteral().getValue());

		return phoneNumber;
	}
	
	private HashMap<String, Object> extractHobby(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> hobby = new HashMap<String, Object>();

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NAO.prefLabel);
		if (node != null)
			hobby.put("hobby", node.asLiteral().getValue());

		return hobby;
	}

	private HashMap<String, Object> extractVoicePhoneNumber(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> voicePhoneNumber = new HashMap<String, Object>();

		// VoicePhoneNumber is a subclass of PhoneNumber
		voicePhoneNumber.putAll(extractPhoneNumber(resource));

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), NCO.voiceMail);
		if (node != null){
			// voiceMail is a boolean
			try {
				voicePhoneNumber.put("voiceMail", Boolean.valueOf(node.asLiteral().getValue()));	
			} catch (Exception e){

			}			
		}
		return voicePhoneNumber;
	}
	
	private HashMap<String, Object> extractLocation(org.ontoware.rdfreactor.schema.rdfs.Resource resource){
		HashMap<String, Object> location = new HashMap<String, Object>();

		Node node = ModelUtils.findObject(resource.getModel(), resource.asResource(), GEO.lat);
		if (node != null)
			location.put("lat",node);
		node = ModelUtils.findObject(resource.getModel(), resource.asResource(), GEO.lon);
		if (node != null)
			location.put("lon", node);

		return location;
	}

	protected void addToMap(org.ontoware.rdfreactor.schema.rdfs.Resource resource,URI me)
			throws BadFormedException {

		
		/*
		URI type=resource.getRDFSClassURI();
		Node rdfType = ModelUtils.findObject(resource.getModel(), resource.asResource(), RDF.type);
		if (type ==null || type.toString().equals("http://www.w3.org/2000/01/rdf-schema#Resource") && rdfType != null) {
			type = rdfType.asURI();
		}*/
		ProfileAttributeType typeDTO = ProfileAttributeType.get(resource);
		URI type =  typeDTO.getTypeID().uri;
		logger.debug(resource.asResource()+" is of type "+typeDTO.getLabel());

		if (type.equals(NCO.PersonName)
				|| type.equals(NCO.BirthDate)
				|| type.equals(NCO.EmailAddress)
				|| type.equals(NCO.PhoneNumber)
				|| type.equals(NCO.IMAccount)
				|| type.equals(NCO.Name)
				|| type.equals(NCO.PhoneNumber) || type.equals(NCO.CellPhoneNumber) 
				|| type.equals(NCO.FaxNumber) || type.equals(NCO.CarPhoneNumber)
				|| type.equals(NCO.ModemNumber) || type.equals(NCO.PagerNumber)
				|| type.equals(NCO.MessagingNumber) || type.equals(NCO.VideoTelephoneNumber)
				|| type.equals(NCO.VoicePhoneNumber)
				|| type.equals(NCO.PostalAddress) || type.equals(NCO.DomesticDeliveryAddress) 
				|| type.equals(NCO.InternationalDeliveryAddress) || type.equals(NCO.ParcelDeliveryAddress)
				|| type.equals(NCO.Affiliation)
				|| type.equals(NCO.Hobby)
				|| type.equals(GEO.Point)
				) {
			this.put("guid", resource.asURI().toString());
			if(type.equals(GEO.Point))
				{
				this.put("category","Location");
				}
			
			else if (type.equals(NCO.VoicePhoneNumber)){
				this.put("category","VoiceMail");
				
			}
			else{
				this.put("category", collapse(type.toString()).replaceFirst("nco:", "")); 
				}

			// UserID is set to "@me" for all profile attributes
			//created by the owner of the PS
			Node creator = ModelUtils.findObject(resource.getModel(), resource.asResource(), NAO.creator);			
			if (creator != null){
				if (creator instanceof  Literal){
					this.put("userId", creator.asLiteral().toString().equals(me.toString()) ? "@me" : creator.asLiteral().toString());
				}
				else{
					this.put("userId", creator.asResource().toString().equals(me.toString()) ? "@me" : creator.asResource().toString());
				}
			}
			else{
				//FIXME HACK - in case the creator is null assume @me
				logger.debug("creator is null for item: "+ this.get("guid")+ " ("+this.get("type")+")  set to \"@me\"");
				this.put("userId", "@me");				
			}

			//set name
			Node nameNode = ModelUtils.findObject(resource.getModel(), resource.asResource(), NAO.prefLabel);
			if (nameNode != null){
				this.put("name", nameNode.asLiteral().toString());
			}
			else{
				//set a blank name if it has none
				this.put("name", "");
			}
			
			// Uff, and this is even worse, the 'name' of the profile
			// attribute can be *any* property to set the different
			// values of these things
			HashMap<String, Object> profileAttributeValue = null;
			String predicate = null;
			if (type.equals(NCO.PersonName)) {
				profileAttributeValue = extractPersonName(resource);				
				predicate = "nco:hasPersonName";			
			} else if (type.equals(NCO.Name)) {
				profileAttributeValue = extractName(resource);				
				predicate = "nco:hasName";				
			} else if (type.equals(NCO.BirthDate)) {
				profileAttributeValue = extractBirthDate(resource);				
				predicate = "nco:hasBirthDate";				
			} else if (type.equals(NCO.EmailAddress)) {
				profileAttributeValue = extractEmailAddress(resource);				
				predicate = "nco:hasEmailAddress";							
			} else if (type.equals(NCO.PhoneNumber) || type.equals(NCO.CellPhoneNumber) 
					|| type.equals(NCO.FaxNumber) || type.equals(NCO.CarPhoneNumber)
					|| type.equals(NCO.ModemNumber) || type.equals(NCO.PagerNumber)
					|| type.equals(NCO.MessagingNumber) || type.equals(NCO.VideoTelephoneNumber)) {
				profileAttributeValue = extractPhoneNumber(resource);				
				predicate = "nco:hasPhoneNumber";				
			} else if (type.equals(NCO.VoicePhoneNumber)) {
				profileAttributeValue = extractVoicePhoneNumber(resource);
				predicate = "nco:hasPhoneNumber";					
			} else if (type.equals(NCO.PostalAddress) || type.equals(NCO.DomesticDeliveryAddress) 
					|| type.equals(NCO.InternationalDeliveryAddress) || type.equals(NCO.ParcelDeliveryAddress)) {
				profileAttributeValue = extractPostalAddress(resource);
				predicate = "nco:hasPostalAddress";					
			} else if (type.equals(NCO.IMAccount)) {
				profileAttributeValue = extractIMAccount(resource);				
				predicate = "nco:hasIMAccount";	
			} else if (type.equals(NCO.Affiliation)) {				
				profileAttributeValue = extractAffiliation(resource);				
				predicate = "nco:hasAffiliation";
			} 
			else if (type.equals(NCO.Hobby)) {				
				profileAttributeValue = extractHobby(resource);				
				predicate = "nco:hobby";
			}
			else if (type.equals(GEO.Point)) {				
				profileAttributeValue = extractLocation(resource);				
				predicate = "nco:hasLocation";
			} 

			if (profileAttributeValue != null && ! profileAttributeValue.isEmpty()){
				this.put("value", profileAttributeValue);
				this.put("predicate", predicate);
			} else {
				throw new BadFormedException(resource.asResource()+" does not have any property to map to 'value'");
			}
		} else {
			throw new BadFormedException(resource.asResource()+" is not a supported profile attribute");
		}
	}

	public String getCategory() {
		return (String) this.get("category");
	}

	public void setCategory(String category) {
		this.put("category", category);
	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(Class<T> returnType,URI me) {
		return asResource(URIGenerator.createNewRandomUniqueURI(), returnType,me);
	}

	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(URI resourceUri, Class<T> returnType,URI me) {
		org.ontoware.rdfreactor.schema.rdfs.Resource resource;

		Model rModel = RDF2Go.getModelFactory().createModel().open();
		resource = new org.ontoware.rdfreactor.schema.rdfs.Resource(rModel, resourceUri, false);

		//set the creator
		if(this.containsKey("userId")){
			Object creator = this.get("userId");
			if( creator.toString().equals("@me"))  rModel.addStatement(resourceUri, NAO.creator, me.toString());
			else rModel.addStatement(resourceUri, NAO.creator, creator.toString());
		}
		else{
			//FIXME hack -in case there is no userId in the payload, asume "@me"
			logger.debug("there is no userId in the payload of: "+ resourceUri.toString()+ "  \"@me\" assumed");
			rModel.addStatement(resourceUri, NAO.creator, me.toString());
		}
		
		//set the prefLabel
				if(this.containsKey("name") && !this.get("category").toString().equals("Hobby")){		
					Object name = this.get("name");
					rModel.addStatement(resourceUri, NAO.prefLabel, new PlainLiteralImpl((String) name));
				}
				else if(!this.get("category").toString().equals("Hobby")){
					//if it has no name set one 
					rModel.addStatement(resourceUri, NAO.prefLabel,	new PlainLiteralImpl(""));
				}

		// Get the value of this ProfileAttribute
		HashMap<String, Object> valueMap = (HashMap<String, Object>) this.get("value");
		if (valueMap== null || valueMap.isEmpty())
			throw new IllegalArgumentException("there are no values to map or the \"values\" field is missing for the profileAttribute");

		if (this.containsKey("category")) {
			URI type = null;
			if(this.get("category").toString().equals("Location")){
			 type = new URIImpl(expand("geo:Point"));	
			}
			else if(this.get("category").toString().equals("VoiceMail")){
				 type = new URIImpl(expand("nco:VoicePhoneNumber"));	
				}
			
			else{
			 type = new URIImpl(expand("nco:"+this.get("category")));
			}
			if (type.equals(NCO.PersonName)) {
				rModel.addStatement(resourceUri, RDF.type, type);
				injectPersonName(rModel, resourceUri, valueMap);
			} else if (type.equals(NCO.BirthDate)) {
				rModel.addStatement(resourceUri, RDF.type, type);
				injectBirthDate(rModel, resourceUri, valueMap);
			} else if (type.equals(NCO.EmailAddress)) {
				rModel.addStatement(resourceUri, RDF.type, type);
				injectEmailAddress(rModel, resourceUri, valueMap);
			} else if (type.equals(NCO.IMAccount)){
				rModel.addStatement(resourceUri, RDF.type, type);
				injectIMAccount(rModel, resourceUri, valueMap);
			} else if (type.equals(NCO.Name)) {
				rModel.addStatement(resourceUri, RDF.type, type);
				injectName(rModel, resourceUri, valueMap);
			} else if (type.equals(NCO.PhoneNumber) || type.equals(NCO.CellPhoneNumber) 
					|| type.equals(NCO.FaxNumber) || type.equals(NCO.CarPhoneNumber)
					|| type.equals(NCO.ModemNumber) || type.equals(NCO.PagerNumber)
					|| type.equals(NCO.MessagingNumber) || type.equals(NCO.VideoTelephoneNumber)){
				rModel.addStatement(resourceUri, RDF.type, type);
				injectPhoneNumber(rModel, resourceUri, valueMap);
			} else if (type.equals(NCO.VoicePhoneNumber)) {
				rModel.addStatement(resourceUri, RDF.type, type);
				injectVoicePhoneNumber(rModel, resourceUri, valueMap);
			} else if (type.equals(NCO.PostalAddress) || type.equals(NCO.DomesticDeliveryAddress) 
					|| type.equals(NCO.InternationalDeliveryAddress) || type.equals(NCO.ParcelDeliveryAddress)){
				rModel.addStatement(resourceUri, RDF.type, type);
				injectPostalAddress(rModel, resourceUri, valueMap);	
			}else if (type.equals(NCO.Affiliation)){
				rModel.addStatement(resourceUri, RDF.type, type);
				injectAffiliation(rModel, resourceUri, valueMap);
			}else if (type.equals(NCO.Hobby)){
				rModel.addStatement(resourceUri, RDF.type, type);
				injectHobby(rModel, resourceUri, valueMap);
			}
			else if (type.equals(GEO.Point)){
				rModel.addStatement(resourceUri, RDF.type, type);
				injectLocation(rModel, resourceUri, valueMap);
			}
			else {
				throw new IllegalArgumentException("category '"+this.get("category")+"' is not valid in "+this.toString());
			}
		} else {
			throw new IllegalArgumentException("'category' cannot be null in "+this.toString());
		}
		
		

		return (T) resource.castTo(returnType);
	}

	private void injectPostalAddress(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		if (valueMap.containsKey("region"))
			model.addStatement(resourceUri, NCO.region, (String) valueMap.get("region"));
		if (valueMap.containsKey("country"))
			model.addStatement(resourceUri, NCO.country, (String) valueMap.get("country"));
		if (valueMap.containsKey("extendedAddress"))
			model.addStatement(resourceUri, NCO.extendedAddress, (String) valueMap.get("extendedAddress"));
		if (valueMap.containsKey("streetAddress"))
			model.addStatement(resourceUri, NCO.streetAddress, (String) valueMap.get("streetAddress"));
		if (valueMap.containsKey("postalCode"))
			model.addStatement(resourceUri, NCO.postalcode, (String) valueMap.get("postalCode"));
		if (valueMap.containsKey("locality"))
			model.addStatement(resourceUri, NCO.locality, (String) valueMap.get("locality"));
		if (valueMap.containsKey("pobox"))
			model.addStatement(resourceUri, NCO.pobox, (String) valueMap.get("pobox"));
		///////TODO: Following attributes are not literal values, but references to other objects. How to manage these?/////
		if (valueMap.containsKey("addressLocation"))
			model.addStatement(resourceUri, NCO.addressLocation, (String) valueMap.get("addressLocation"));
	}

	private void injectBirthDate(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		Calendar calendar = Calendar.getInstance();
		if (valueMap.containsKey("birthDate"))			
			try {
				
				Object value = valueMap.get("birthDate");
				/*if (value instanceof Long) {
					calendar.setTimeInMillis((Long) value);
					value = calendar;
				}
				if (value instanceof Integer) {

					if (GenericValidator.isLong(value.toString())) {
						calendar.setTimeInMillis( ((Integer) value).longValue());
						value = calendar;
					}
				}		*/
				
				if (value instanceof String) {
				model.addStatement(resourceUri, NCO.birthDate, new DatatypeLiteralImpl((String)value, XSD._dateTime) );
				}
				else{
				throw new IllegalArgumentException("birthdate is not a string");
				}
			}catch (Exception e){
				throw new IllegalArgumentException("Cannot add birthDate to the model",e);
			}

		if (valueMap.containsKey("age")){
			try {
				Integer age = (Integer) valueMap.get("age");
				model.addStatement(resourceUri, NCO.age, age.toString());	
			}catch (Exception e){
				throw new IllegalArgumentException("Cannot add age to the model",e);
			}
		}

	}

	private void injectIMAccount(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		if (valueMap.containsKey("imStatus"))
			model.addStatement(resourceUri, NCO.imStatus, (String) valueMap.get("imStatus"));

		if (valueMap.containsKey("imID"))
			model.addStatement(resourceUri, NCO.imID, (String) valueMap.get("imID"));
		if (valueMap.containsKey("imNickname"))
			model.addStatement(resourceUri, NCO.imNickname, (String) valueMap.get("imNickname"));
		if (valueMap.containsKey("imAccountType"))
			model.addStatement(resourceUri, NCO.imAccountType, (String) valueMap.get("imAccountType"));
		if (valueMap.containsKey("imStatusMessage"))
			model.addStatement(resourceUri, NCO.imStatusMessage, (String) valueMap.get("imStatusMessage"));		
		if (valueMap.containsKey("isBlocked"))
			model.addStatement(resourceUri, NCO.isBlocked, (String) valueMap.get("isBlocked"));

		///////TODO: Following attributes are not literal values, but references to other objects (URIs). How to manage these?/////
		if (valueMap.containsKey("imStatusType"))
			model.addStatement(resourceUri, NCO.imStatusType, (String) valueMap.get("imStatusType"));
		if (valueMap.containsKey("hasIMCapability"))
			model.addStatement(resourceUri, NCO.hasIMCapability, (String) valueMap.get("hasIMCapability"));
		if (valueMap.containsKey("isAccessedBy"))
			model.addStatement(resourceUri, NCO.isAccessedBy, (String) valueMap.get("isAccessedBy"));
		if (valueMap.containsKey("publishesPresenceTo"))
			model.addStatement(resourceUri, NCO.publishesPresenceTo, (String) valueMap.get("publishesPresenceTo"));
		if (valueMap.containsKey("requestedPresenceSubscriptionTo"))
			model.addStatement(resourceUri, NCO.requestedPresenceSubscriptionTo, (String) valueMap.get("requestedPresenceSubscriptionTo"));

	}

	private void injectName(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		if (valueMap.containsKey("nickname"))
			model.addStatement(resourceUri, NCO.nickname, (String) valueMap.get("nickname"));
		if (valueMap.containsKey("fullname"))
			model.addStatement(resourceUri, NCO.fullname, (String) valueMap.get("fullname"));
	}


	private void injectPersonName(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		// PersonaName is a subclass of name
		injectName(model, resourceUri, valueMap);

		if (valueMap.containsKey("nameHonorificSuffix"))
			model.addStatement(resourceUri, NCO.nameHonorificSuffix, (String) valueMap.get("nameHonorificSuffix"));
		if (valueMap.containsKey("nameFamily"))
			model.addStatement(resourceUri, NCO.nameFamily, (String) valueMap.get("nameFamily"));
		if (valueMap.containsKey("nameHonorificPrefix"))
			model.addStatement(resourceUri, NCO.nameHonorificPrefix, (String) valueMap.get("nameHonorificPrefix"));
		if (valueMap.containsKey("nameAdditional"))
			model.addStatement(resourceUri, NCO.nameAdditional, (String) valueMap.get("nameAdditional"));
		if (valueMap.containsKey("nameGiven"))
			model.addStatement(resourceUri, NCO.nameGiven, (String) valueMap.get("nameGiven"));
	}

	private void injectPhoneNumber(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		if (valueMap.containsKey("phoneNumber"))
			model.addStatement(resourceUri, NCO.phoneNumber, (String) valueMap.get("phoneNumber"));
	}

	private void injectVoicePhoneNumber(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		// VoicePhoneNumber is a subclass of PhoneNumber
		injectPhoneNumber(model, resourceUri, valueMap);

		if (valueMap.containsKey("voiceMail"))
			model.addStatement(resourceUri, NCO.voiceMail, valueMap.get("voiceMail").toString());
	}

	private void injectAffiliation(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		if (valueMap.containsKey("department"))
			model.addStatement(resourceUri, NCO.department, (String) valueMap.get("department"));
		if (valueMap.containsKey("title"))
			model.addStatement(resourceUri, NCO.title, (String) valueMap.get("title"));
		if (valueMap.containsKey("role"))
			model.addStatement(resourceUri, NCO.role, (String) valueMap.get("role"));
		///////TODO: Following attributes are not literal values, but references to other objects. How to manage these?/////
		if (valueMap.containsKey("org"))			
			model.addStatement(resourceUri, NCO.org, (String) valueMap.get("org"));
	}


	private void injectEmailAddress(Model model, URI resourceUri, HashMap<String, Object> valueMap){	
		if (valueMap.containsKey("emailAddress"))
			model.addStatement(resourceUri, NCO.emailAddress, (String) valueMap.get("emailAddress"));
		
			
	}
	private void injectHobby(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		if (valueMap.containsKey("hobby"))
			model.addStatement(resourceUri, NAO.prefLabel, (String) valueMap.get("hobby"));
	}
	private void injectLocation(Model model, URI resourceUri, HashMap<String, Object> valueMap){
		if (valueMap.containsKey("lat") )			
			model.addStatement(resourceUri, GEO.lat, new DatatypeLiteralImpl(valueMap.get("lat").toString(),XSD._float));
		if (valueMap.containsKey("lon"))
			model.addStatement(resourceUri, GEO.lon,new DatatypeLiteralImpl(valueMap.get("lon").toString(),XSD._float));
	}

}
