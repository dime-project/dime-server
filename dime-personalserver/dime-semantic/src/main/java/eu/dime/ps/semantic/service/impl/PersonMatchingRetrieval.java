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

package eu.dime.ps.semantic.service.impl;

import ie.deri.smile.matching.PersonAddress;
import ie.deri.smile.matching.PersonAffiliation;
import ie.deri.smile.matching.PersonIm;
import ie.deri.smile.matching.PersonProfile;
import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import org.ontoware.rdf2go.model.node.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.util.StringUtils;

public class PersonMatchingRetrieval {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonMatchingRetrieval.class);
	
	private static final String namespaces = "PREFIX nao: "+NAO.NS_NAO.toSPARQL()+
	 										 "PREFIX nie: "+NIE.NS_NIE.toSPARQL()+
	 										 "PREFIX nco: "+NCO.NS_NCO.toSPARQL()+
	 										 "PREFIX pimo: "+PIMO.NS_PIMO.toSPARQL()+
	 										 "PREFIX dao:"+DAO.NS_DAO.toSPARQL();	
	
	private PersonMatchingRetrieval() {
	}
	
	public static Map<Resource, PersonProfile> getAllPersonsSelectList(Resource person, ResourceStore resourceStore, String condition, Resource accountSource) {
		Map<Resource, PersonProfile> profileLookupMap = new HashMap<Resource, PersonProfile>();
		profileLookupMap = getAllPersonsDetailsSelect(person, resourceStore, condition, profileLookupMap, accountSource); //call to retrieve user's basic info
		profileLookupMap = getAllPersonsPhoneSelect(person, resourceStore, condition, profileLookupMap, accountSource); //call to retrieve user's phone numbers
		profileLookupMap = getAllPersonsImSelect(person, resourceStore, condition, profileLookupMap, accountSource); //call to retrieve user's im accounts
		profileLookupMap = getAllPersonsPostalAddressSelect(person, resourceStore, condition, profileLookupMap, accountSource); //call to retrieve user's addresses
		profileLookupMap = getAllPersonsAffiliationSelect(person, resourceStore, condition, profileLookupMap, accountSource); //call to retrieve user's affiliations
		profileLookupMap = getAllPersonsEmailSelect(person, resourceStore, condition, profileLookupMap, accountSource); //call to retrieve user's emails
		profileLookupMap = getAllPersonsUrlSelect(person, resourceStore, condition, profileLookupMap, accountSource); //call to retrieve user's urls
		return profileLookupMap;		
	}
	
	/**
	 * Retrieves the details of the pimo:Person or all the pimo:Persons except the one specified by URI given within the 'person' parameter,
	 * depending on the condition
	 * 
	 * @param person URI of person to retrieve or to not include
	 * @param resourceStore resource store to use for storing retrieved triples
	 * @param condition conditional operator i.e. equals or not equals
	 * @param profileLookupMap Map used for storing all PersonProfile objects of each retrieved contact
	 * @return
	 */
	public static Map<Resource, PersonProfile> getAllPersonsDetailsSelect(Resource person, ResourceStore resourceStore, String condition, Map<Resource, PersonProfile> profileLookupMap, Resource accountSource) {
		
		
		String queryString = null;
		if ((condition.equals("!=")) && (accountSource != null)) {
			queryString = StringUtils.strjoinNL(
					namespaces,				
					"SELECT DISTINCT ?person ?contact ?dataSrc ?source ?fullname ?nickname ?name ?surname ?nameAdd ?namePrefix ?nameSuffix ?gender ?profileUrl ?dob " +			
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            OPTIONAL { {?contact nie:dataSource ?dataSrc} .  {?dataSrc dao:accountType ?source} }   " ,		
					"            OPTIONAL { {?contact nco:hasPersonName ?pn} .  {?pn nco:fullname ?fullname} OPTIONAL {?pn nco:nickname ?nickname} OPTIONAL {?pn nco:nameGiven ?name} OPTIONAL {?pn nco:nameFamily ?surname} OPTIONAL {?pn nco:nameAdditional ?nameAdd} OPTIONAL {?pn nco:nameHonorificPrefix ?namePrefix} OPTIONAL {?pn nco:nameHonorificSuffix ?nameSuffix} }  " ,	
					"            OPTIONAL {?contact nco:gender ?gender} " ,
					"            OPTIONAL {?contact nie:url ?profileUrl} " +	
					"            OPTIONAL { {?contact nco:hasBirthDate ?bdate} .  {?bdate nco:birthDate ?dob} } .  " ,	 
					"FILTER (?person "+condition+person.toSPARQL()+")",
					"FILTER (?dataSrc "+condition+accountSource.toSPARQL()+")", 
			        "}");		
		} else {
			queryString = StringUtils.strjoinNL(
					namespaces,				
					"SELECT DISTINCT ?person ?contact ?dataSrc ?source ?fullname ?nickname ?name ?surname ?nameAdd ?namePrefix ?nameSuffix ?gender ?profileUrl ?dob " +			
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            OPTIONAL { {?contact nie:dataSource ?dataSrc} .  {?dataSrc dao:accountType ?source} }   " ,		
					"            OPTIONAL { {?contact nco:hasPersonName ?pn} .  {?pn nco:fullname ?fullname} OPTIONAL {?pn nco:nickname ?nickname} OPTIONAL {?pn nco:nameGiven ?name} OPTIONAL {?pn nco:nameFamily ?surname} OPTIONAL {?pn nco:nameAdditional ?nameAdd} OPTIONAL {?pn nco:nameHonorificPrefix ?namePrefix} OPTIONAL {?pn nco:nameHonorificSuffix ?nameSuffix} }  " ,	
					"            OPTIONAL {?contact nco:gender ?gender} " ,
					"            OPTIONAL {?contact nie:url ?profileUrl} " +	
					"            OPTIONAL { {?contact nco:hasBirthDate ?bdate} .  {?bdate nco:birthDate ?dob} } .  " ,	 
					"FILTER (?person "+condition+person.toSPARQL()+")",
			        "}");			
		}
		
				QueryResultTable results = resourceStore.sparqlSelect(queryString);					
				for(QueryRow row : results) {	
					 
					Resource contactURI = getResourceValue(row,"contact");					
					Resource personURI = getResourceValue(row,"person");
					Resource sourceURI = getResourceValue(row,"dataSrc");
					String source = getLiteralValue(row,"source");	
					String fullname = getLiteralValue(row,"fullname");	
					String nickname = getLiteralValue(row,"nickname");		
					String name = getLiteralValue(row,"name");				
					String surname = getLiteralValue(row,"surname");				
					String nameAdd = getLiteralValue(row,"nameAdd");			
					String namePre = getLiteralValue(row,"namePrefix");				
					String nameSuf = getLiteralValue(row,"nameSuffix");				
					String gender = getURIValue(row,"gender");	
					String profileUrl = getURIValue(row,"profileUrl");	
					String dob = getLiteralValue(row,"dob");
					
					//perform NLP at this point after retrieving full name values for specific profile
										
					if (!profileLookupMap.containsKey(contactURI))
					{						
						PersonProfile personProfile = new PersonProfile(source,name,surname,fullname,nickname,nameAdd,namePre,nameSuf,gender,dob,profileUrl,personURI,contactURI,sourceURI);										
						profileLookupMap.put(contactURI, personProfile);	
					} else { //contactURI will only be in hash map if a check is performed in the same session
						PersonProfile personProfile = profileLookupMap.get(contactURI);
						personProfile = new PersonProfile(name,surname,fullname,nickname,nameAdd,namePre,nameSuf,gender,dob,profileUrl);	
						profileLookupMap.put(contactURI, personProfile);
					}								
				}	
		return profileLookupMap;
	}
	
	public static Map<Resource, PersonProfile> getAllPersonsPhoneSelect(Resource person, ResourceStore resourceStore, String condition, Map<Resource, PersonProfile> profileLookupMap, Resource accountSource) {
				
		String queryString = null;
		if ((condition.equals("!=")) && (accountSource != null)) {
			queryString = StringUtils.strjoinNL(
					namespaces,				
					"SELECT DISTINCT ?contact ?pNo " +			
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            {?contact nie:dataSource ?dataSrc}  " ,	
					"            { {?contact nco:hasPhoneNumber ?pNum} .  {?pNum nco:phoneNumber ?pNo} } . " ,	 
					"FILTER (?person "+condition+person.toSPARQL()+")",
					"FILTER (?dataSrc "+condition+accountSource.toSPARQL()+")",
			        "}");
		} else {
			queryString = StringUtils.strjoinNL(
					namespaces,				
					"SELECT DISTINCT ?contact ?pNo " +			
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            { {?contact nco:hasPhoneNumber ?pNum} .  {?pNum nco:phoneNumber ?pNo} } . " ,	 
					"FILTER (?person "+condition+person.toSPARQL()+")",			
			        "}");	
		}
		
				QueryResultTable results = resourceStore.sparqlSelect(queryString);		
				Resource contactURI = null;
				
				for(QueryRow row : results) {					
					contactURI = getResourceValue(row,"contact");	
					String phoneNumber = getLiteralValue(row,"pNo");
															
					if (!profileLookupMap.containsKey(contactURI)) 
					{	
						PersonProfile personProfile = new PersonProfile();
						List<String> phoneNumbers = new ArrayList<String>();
						phoneNumbers.add(phoneNumber);
						personProfile.setPhoneNumbers(phoneNumbers);						
						profileLookupMap.put(contactURI, personProfile);
					} else {
						PersonProfile personProfile = profileLookupMap.get(contactURI);
						if (personProfile.getPhoneNumbers() == null) {
							List<String> phoneNumbers = new ArrayList<String>();
							phoneNumbers.add(phoneNumber);
							personProfile.setPhoneNumbers(phoneNumbers);							
						} else {
							List<String> phoneNumbers = personProfile.getPhoneNumbers();
							phoneNumbers.add(phoneNumber);
							personProfile.setPhoneNumbers(phoneNumbers);						
						}					
						profileLookupMap.put(contactURI, personProfile);
					}	
				}	
		return profileLookupMap;
	}
	
	public static Map<Resource, PersonProfile> getAllPersonsImSelect(Resource person, ResourceStore resourceStore, String condition, Map<Resource, PersonProfile> profileLookupMap, Resource accountSource) {
				
		String queryString = null;
		if ((condition.equals("!=")) && (accountSource != null)) {
			queryString = StringUtils.strjoinNL(
					namespaces,				
					"SELECT DISTINCT ?contact ?imType ?imID " +						
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            {?contact nie:dataSource ?dataSrc}  " ,	
					"            { {?contact nco:hasIMAccount ?im} .  {?im nco:imAccountType ?imType} OPTIONAL {?im nco:imID ?imID} } .  " ,						 
					"FILTER (?person "+condition+person.toSPARQL()+")",
					"FILTER (?dataSrc "+condition+accountSource.toSPARQL()+")",
			        "}");	
		} else {
			queryString = StringUtils.strjoinNL(
					namespaces,				
					"SELECT DISTINCT ?contact ?imType ?imID " +						
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            { {?contact nco:hasIMAccount ?im} .  {?im nco:imAccountType ?imType} OPTIONAL {?im nco:imID ?imID} } .  " ,						 
					"FILTER (?person "+condition+person.toSPARQL()+")",				
			        "}");	
		}
				
				QueryResultTable results = resourceStore.sparqlSelect(queryString);		
				Resource contactURI = null;
				
				for(QueryRow row : results) {				
					contactURI = getResourceValue(row,"contact");	
					String imAccountType = getLiteralValue(row,"imType");
					String imID = getLiteralValue(row,"imID");					
										
					if (!profileLookupMap.containsKey(contactURI)) 
					{	
						PersonProfile personProfile = new PersonProfile();
						List<PersonIm> ims = new ArrayList<PersonIm>();
						ims.add(new PersonIm(imAccountType,imID));
						personProfile.setIms(ims);						
						profileLookupMap.put(contactURI, personProfile);						
					} else {
						PersonProfile personProfile = profileLookupMap.get(contactURI);
						if (personProfile.getIms() == null) {
							List<PersonIm> ims = new ArrayList<PersonIm>();
							ims.add(new PersonIm(imAccountType,imID));
							personProfile.setIms(ims);						
						} else {
							List<PersonIm> ims = personProfile.getIms();
							ims.add(new PersonIm(imAccountType,imID));
							personProfile.setIms(ims);							
						}					
						profileLookupMap.put(contactURI, personProfile);
					}	
				}	
		return profileLookupMap;
	}
	
	public static Map<Resource, PersonProfile> getAllPersonsPostalAddressSelect(Resource person, ResourceStore resourceStore, String condition, Map<Resource, PersonProfile> profileLookupMap, Resource accountSource) {
		
		String queryString = null;
		if ((condition.equals("!=")) && (accountSource != null)) {
			queryString = StringUtils.strjoinNL(
					namespaces,					
					"SELECT DISTINCT ?contact ?addrFull ?addrStr ?addrPCode ?addrPOBox ?addrExt ?addrLoc ?addrCnt ?addrReg " +		
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            {?contact nie:dataSource ?dataSrc}  " ,	
					"            { {?contact nco:hasPostalAddress ?addr} . OPTIONAL {?addr nao:prefLabel ?addrFull} OPTIONAL {?addr nco:streetAddress ?addrStr} OPTIONAL {?addr nco:postalcode ?addrPCode} OPTIONAL {?addr nco:pobox ?addrPOBox} OPTIONAL {?addr nco:extendedAddress ?addrExt} OPTIONAL {?addr nco:locality ?addrLoc} OPTIONAL {?addr nco:country ?addrCnt} OPTIONAL {?addr nco:region ?addrReg} } . " ,	 
					"FILTER (?person "+condition+person.toSPARQL()+")",
					"FILTER (?dataSrc "+condition+accountSource.toSPARQL()+")",
			        "}");
		} else {
			queryString = StringUtils.strjoinNL(
					namespaces,					
					"SELECT DISTINCT ?contact ?addrFull ?addrStr ?addrPCode ?addrPOBox ?addrExt ?addrLoc ?addrCnt ?addrReg " +		
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            { {?contact nco:hasPostalAddress ?addr} . OPTIONAL {?addr nao:prefLabel ?addrFull} OPTIONAL {?addr nco:streetAddress ?addrStr} OPTIONAL {?addr nco:postalcode ?addrPCode} OPTIONAL {?addr nco:pobox ?addrPOBox} OPTIONAL {?addr nco:extendedAddress ?addrExt} OPTIONAL {?addr nco:locality ?addrLoc} OPTIONAL {?addr nco:country ?addrCnt} OPTIONAL {?addr nco:region ?addrReg} } . " ,	 
					"FILTER (?person "+condition+person.toSPARQL()+")",				
			        "}");
		}
				
				QueryResultTable results = resourceStore.sparqlSelect(queryString);		
				Resource contactURI = null;
				
				for(QueryRow row : results) {				
					contactURI = getResourceValue(row,"contact");	
					String addressFull = getLiteralValue(row,"addrFull");					
					String addressCountry = getLiteralValue(row,"addrCnt");				
					String addressLocality = getLiteralValue(row,"addrLoc");
					String addressStreet = getLiteralValue(row,"addrStr");
					String addressPostalCode = getLiteralValue(row,"PCode");
					String addressPobox = getLiteralValue(row,"addrPOBox");
					String addressExtended = getLiteralValue(row,"addrExt");
					String addressRegion = getLiteralValue(row,"addrReg");
					
					//perform NLP at this point after retrieving address values for specific profile
														
					if (!profileLookupMap.containsKey(contactURI)) 
					{	
						PersonProfile personProfile = new PersonProfile();
						List<PersonAddress> addresses = new ArrayList<PersonAddress>();
						addresses.add(new PersonAddress(addressFull,addressCountry,addressLocality,addressStreet,addressPostalCode,addressPobox,addressExtended,addressRegion));
						personProfile.setAddresses(addresses);					
						profileLookupMap.put(contactURI, personProfile);
					} else {
						PersonProfile personProfile = profileLookupMap.get(contactURI);
						if (personProfile.getAddresses() == null) {
							List<PersonAddress> addresses = new ArrayList<PersonAddress>();
							addresses.add(new PersonAddress(addressFull,addressCountry,addressLocality,addressStreet,addressPostalCode,addressPobox,addressExtended,addressRegion));
							personProfile.setAddresses(addresses);			
						} else {
							List<PersonAddress> addresses = personProfile.getAddresses();
							addresses.add(new PersonAddress(addressFull,addressCountry,addressLocality,addressStreet,addressPostalCode,addressPobox,addressExtended,addressRegion));
							personProfile.setAddresses(addresses);						
						}
						profileLookupMap.put(contactURI, personProfile);
					}	
				}					
		return profileLookupMap;
	}
	
	public static Map<Resource, PersonProfile> getAllPersonsAffiliationSelect(Resource person, ResourceStore resourceStore, String condition, Map<Resource, PersonProfile> profileLookupMap, Resource accountSource) {
		
		String queryString = null;
		
		if ((condition.equals("!=")) && (accountSource != null)) {
			queryString = StringUtils.strjoinNL(						
					namespaces,					
					"SELECT DISTINCT ?contact ?role ?roleStart ?roleEnd ?roleOrg " +		
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            {?contact nie:dataSource ?dataSrc}   " ,	
					"            { {?contact nco:hasAffiliation ?affl} .  {?affl nco:role ?role} OPTIONAL {?affl nco:start ?roleStart} OPTIONAL {?affl nco:end ?roleEnd} OPTIONAL { {?affl nco:org ?org} . {?org nie:title ?roleOrg} } } . " ,		 
					"FILTER (?person "+condition+person.toSPARQL()+")",
					"FILTER (?dataSrc "+condition+accountSource.toSPARQL()+")",
			        "}");	
		} else {
			queryString = StringUtils.strjoinNL(
					namespaces,					
					"SELECT DISTINCT ?contact ?role ?roleStart ?roleEnd ?roleOrg " +		
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            { {?contact nco:hasAffiliation ?affl} .  {?affl nco:role ?role} OPTIONAL {?affl nco:start ?roleStart} OPTIONAL {?affl nco:end ?roleEnd} OPTIONAL { {?affl nco:org ?org} . {?org nie:title ?roleOrg} } } . " ,		 
					"FILTER (?person "+condition+person.toSPARQL()+")",				
			        "}");
		}
				
    			QueryResultTable results = resourceStore.sparqlSelect(queryString);		
				Resource contactURI = null;
				
				for(QueryRow row : results) {				
					contactURI = getResourceValue(row,"contact");	
					String role = getLiteralValue(row,"role");
					String start = getLiteralValue(row,"roleStart");
					String end = getLiteralValue(row,"roleEnd");
					String organisation = getLiteralValue(row,"roleOrg");
														
					if (!profileLookupMap.containsKey(contactURI)) 
					{	
						PersonProfile personProfile = new PersonProfile();
						List<PersonAffiliation> affiliations = new ArrayList<PersonAffiliation>();
						affiliations.add(new PersonAffiliation(role,start,end,organisation));
						personProfile.setAffiliations(affiliations);					
						profileLookupMap.put(contactURI, personProfile);						
					} else {
						PersonProfile personProfile = profileLookupMap.get(contactURI);
						if (personProfile.getAffiliations() == null) {
							List<PersonAffiliation> affiliations = new ArrayList<PersonAffiliation>();							
							affiliations.add(new PersonAffiliation(role,start,end,organisation));
							personProfile.setAffiliations(affiliations);							
						} else {
							List<PersonAffiliation> affiliations = personProfile.getAffiliations();
							affiliations.add(new PersonAffiliation(role,start,end,organisation));
							personProfile.setAffiliations(affiliations);							
						}					
						profileLookupMap.put(contactURI, personProfile);						
					}	
				}	
		return profileLookupMap;
	}
	
	public static Map<Resource, PersonProfile> getAllPersonsEmailSelect(Resource person, ResourceStore resourceStore, String condition, Map<Resource, PersonProfile> profileLookupMap, Resource accountSource) {
		
		String queryString = null;
		if ((condition.equals("!=")) && (accountSource != null)) {
			queryString = StringUtils.strjoinNL(
					namespaces,					
					"SELECT DISTINCT ?contact ?email " +			
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            {?contact nie:dataSource ?dataSrc}  " ,	
					"            { {?contact nco:hasEmailAddress ?emaddr} .  {?emaddr nco:emailAddress ?email} } .  " , 
					"FILTER (?person "+condition+person.toSPARQL()+")",
					"FILTER (?dataSrc "+condition+accountSource.toSPARQL()+")",
			        "}");
		} else {
			queryString = StringUtils.strjoinNL(
					namespaces,					
					"SELECT DISTINCT ?contact ?email " +			
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"          { {?contact nco:hasEmailAddress ?emaddr} .  {?emaddr nco:emailAddress ?email} } .  " , 
					"FILTER (?person "+condition+person.toSPARQL()+")",				
			        "}");		  
		}		
		
				QueryResultTable results = resourceStore.sparqlSelect(queryString);		
				Resource contactURI = null;
				
				for(QueryRow row : results) {					
					contactURI = getResourceValue(row,"contact");	
					String email = getLiteralValue(row,"email");
																								
					if (!profileLookupMap.containsKey(contactURI)) 
					{	
						PersonProfile personProfile = new PersonProfile();
						List<String> emails = new ArrayList<String>();
						emails.add(email);
						personProfile.setEmails(emails); 
						profileLookupMap.put(contactURI, personProfile);
					} else {
						PersonProfile personProfile = profileLookupMap.get(contactURI);
						if (personProfile.getEmails() == null) {
							List<String> emails = new ArrayList<String>();
							emails.add(email);
							personProfile.setEmails(emails);			
						} else {
							List<String> emails = personProfile.getEmails();
							emails.add(email);
							personProfile.setEmails(emails); 
						}
						profileLookupMap.put(contactURI, personProfile);
					}	
				}
		return profileLookupMap;						  
	}
	
	public static Map<Resource, PersonProfile> getAllPersonsUrlSelect(Resource person, ResourceStore resourceStore, String condition, Map<Resource, PersonProfile> profileLookupMap, Resource accountSource) {
		
		String queryString = null;
		if ((condition.equals("!=")) && (accountSource != null)) {
		    queryString = StringUtils.strjoinNL(						
					namespaces,					
					"SELECT DISTINCT ?contact ?url " +				
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            {?contact nie:dataSource ?dataSrc}   " ,	
					"            {?contact nco:url ?url} .  " , 			 
					"FILTER (?person "+condition+person.toSPARQL()+")",
					"FILTER (?dataSrc "+condition+accountSource.toSPARQL()+")",
			        "}");
		} else {
			queryString = StringUtils.strjoinNL(
					namespaces,					
					"SELECT DISTINCT ?contact ?url " +				
					"WHERE {     {?person pimo:occurrence ?contact} " ,
					"            {?contact nco:url ?url} .  " , 			 
					"FILTER (?person "+condition+person.toSPARQL()+")",				
			        "}");	
		}
	
				QueryResultTable results = resourceStore.sparqlSelect(queryString);		
				Resource contactURI = null;
				
				for(QueryRow row : results) {					
					contactURI = getResourceValue(row,"contact");	
					String url = getURIValue(row,"url");
																													
					if (!profileLookupMap.containsKey(contactURI)) 
					{	
						PersonProfile personProfile = new PersonProfile();
						List<String> urls = new ArrayList<String>();
						urls.add(url);
						personProfile.setUrls(urls);
						profileLookupMap.put(contactURI, personProfile);						
					} else {
						PersonProfile personProfile = profileLookupMap.get(contactURI);
						if (personProfile.getUrls() == null) {
							List<String> urls = new ArrayList<String>();
							urls.add(url);
							personProfile.setUrls(urls);			
						} else {
							List<String> urls = personProfile.getUrls();
							urls.add(url);
							personProfile.setUrls(urls);
						}				
						profileLookupMap.put(contactURI, personProfile);					
					}	
				}
		return profileLookupMap;						  
	}
	
	private static String getLiteralValue(QueryRow row, String attributeName) {
		String attribute = null;
		try {		
			attribute = row.getValue(attributeName).asLiteral().toString();
		} catch (Exception e) {
			logger.debug(e.toString()+" - No "+attributeName+" Variable");
		}
		return attribute;
	}
	
	private static Resource getResourceValue(QueryRow row, String attributeName) {
		Resource attribute = null; //check if this will always be returned due to null exception
		try {
			attribute = row.getValue(attributeName).asResource();
		} catch (NullPointerException e) {
			logger.debug(e.toString()+" - No "+attributeName+" Variable");
		}
		return attribute;
	}
	
	private static String getURIValue(QueryRow row, String attributeName) {
		String attribute = null;
		try {
			attribute = row.getValue(attributeName).asURI().toString();
		} catch (Exception e) {
			logger.debug(e.toString()+" - No "+attributeName+" Variable");
		}
		return attribute;
	}

}
