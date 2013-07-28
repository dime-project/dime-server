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

import ie.deri.smile.vocabulary.GEO;
import ie.deri.smile.vocabulary.NCO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;


/**
 * Mapping of profile attributes as RDF resources to DTO profile attributes.
 * 
 * @author Ismael Rivera
 */
public enum ProfileAttributeType {
	
	// order is important, subclasses should be above the class that is a subclass of
	
	EMAIL_ADDRESS("EmailAddress", NCO.EmailAddress),

	BBS_NUMBER("BbsNumber", NCO.BbsNumber),
	CAR_PHONE_NUMBER("CarPhoneNumber", NCO.CarPhoneNumber),
	CELL_PHONE_NUMBER("CellPhoneNumber", NCO.CellPhoneNumber),
	FAX_NUMBER("FaxNumber", NCO.FaxNumber),
	ISDN_NUMBER("IsdnNumber", NCO.IsdnNumber),
	MESSAGING_NUMBER("MessagingNumber", NCO.MessagingNumber),
	MODEM_NUMBER("ModemNumber", NCO.ModemNumber),
	PAGER_NUMBER("PagerNumber", NCO.PagerNumber),
	PCS_NUMBER("PcsNumber", NCO.PcsNumber),
	VIDEO_TELEPHONE_NUMBER("VideoTelephoneNumber", NCO.VideoTelephoneNumber),
	VOICE_PHONE_NUMBER("VoicePhoneNumber", NCO.VoicePhoneNumber),
	PHONE_NUMBER("PhoneNumber", NCO.PhoneNumber),
	
	AUDIO_IM_ACCOUNT("AudioIMAccount", NCO.AudioIMAccount),
	VIDEO_IM_ACCOUNT("VideoIMAccount", NCO.VideoIMAccount),
	IM_ACCOUNT("IMAccount", NCO.IMAccount),
	IM_CAPABILITY("IMCapability", NCO.IMCapability),
	
	DOMESTIC_DELIVERY_ADDRESS("DomesticDeliveryAddress", NCO.DomesticDeliveryAddress),
	INTERNATIONAL_DELIVERY_ADDRESS("InternationalDeliveryAddress", NCO.InternationalDeliveryAddress),
	PARCEL_DELIVERY_ADDRESS("ParcelDeliveryAddress", NCO.ParcelDeliveryAddress),
	POSTAL_ADDRESS("PostalAddress", NCO.PostalAddress),
	
	PERSON_NAME("PersonName", NCO.PersonName),
	NAME("Name", NCO.Name),
	
	AFFILIATION("Affiliation", NCO.Affiliation),
	BIRTH_DATE("BirthDate", NCO.BirthDate),
	GENDER("Gender", NCO.Gender),
	HOBBY("Hobby", NCO.Hobby),
	POINT("Point", GEO.Point);
	
	static final Map<String, ProfileAttributeType> lookup;

	static {
		// using a LinkedHashMap to guarantee order (as inserted)
		lookup = new LinkedHashMap<String,ProfileAttributeType>();
		for (ProfileAttributeType type : EnumSet.allOf(ProfileAttributeType.class)) {
			lookup.put(type.getTypeID().uri.toString(), type);
		}
	}

	private String label;
	private TypeID id;
	
	private ProfileAttributeType(String label, URI uri) {
		this.label = label;
		this.id = new TypeID(uri);
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
	
	public static ProfileAttributeType get(org.ontoware.rdfreactor.schema.rdfs.Resource resource) {
		// builds a list with all RDF types of the resource
		List<URI> rdfTypes = new ArrayList<URI>();
		ClosableIterator<Statement> typeIt = resource.getModel().findStatements(resource, RDF.type, Variable.ANY);
		while (typeIt.hasNext()) {
			Node rdfType = typeIt.next().getObject();
			if (rdfType instanceof URI && !rdfType.equals(RDFS.Resource)) {
				rdfTypes.add(rdfType.asURI());
			}
		}
		typeIt.close();
		
		// looks up the types in order of specificity			
		for (ProfileAttributeType type : lookup.values()) {
			// first attribute type matches any RDF type, then it's returned
			if (rdfTypes.contains(type.getTypeID().uri)) {
				return type;
			}
		}
		
		return null; // no type found
	}
	
}