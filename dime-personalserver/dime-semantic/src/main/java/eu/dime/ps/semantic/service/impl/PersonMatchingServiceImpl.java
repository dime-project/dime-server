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

import ie.deri.smile.matching.MatchAttributes;
import ie.deri.smile.matching.MatchResult;
import ie.deri.smile.matching.PersonMatcher;
import ie.deri.smile.matching.PersonProfile;
import ie.deri.smile.matching.matcher.MongeElkanPersonMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.PersonMatch;
import eu.dime.ps.semantic.service.PersonMatchingService;

public class PersonMatchingServiceImpl implements PersonMatchingService {

	private static final Logger logger = LoggerFactory.getLogger(PersonMatchingServiceImpl.class);
	
	private ResourceStore resourceStore;
	
	private final int matchingTechnique;
	private final Map<String, Integer> techniqueValues;
	private final int weightingScheme;
	private final int weightingApproach;
	private final boolean semanticExtension;
	
	private static final String conditionEquals = "=";
	private static final String conditionNotEquals = "!=";
	
	public PersonMatchingServiceImpl(int matchingTechnique, int weightingScheme, int weightingApproach, boolean semanticExtension) {
		this.matchingTechnique = matchingTechnique;
		this.techniqueValues = MatchAttributes.getTechniqueValues(matchingTechnique);
		this.weightingScheme = weightingScheme;
		this.weightingApproach = weightingApproach;
		this.semanticExtension = semanticExtension;
	}
	
	public PersonMatchingServiceImpl(ResourceStore resourceStore, int matchingTechnique, int weightingScheme, int weightingApproach, boolean semanticExtension) {
		this(matchingTechnique, weightingScheme, weightingApproach, semanticExtension);
		this.resourceStore = resourceStore;
	}
	
	public PersonMatchingServiceImpl(int matchingTechnique, int weightingScheme, int weightingApproach) {
		this(matchingTechnique, weightingScheme, weightingApproach, false);
	}
	
	public PersonMatchingServiceImpl(ResourceStore resourceStore, int matchingTechnique, int weightingScheme, int weightingApproach) {
		this(resourceStore, matchingTechnique, weightingScheme, weightingApproach, false);
	}
	
	public PersonMatchingServiceImpl(ResourceStore resourceStore) {
		this(resourceStore, PersonMatchingConfiguration.MATCHING_TECHNIQUE, PersonMatchingConfiguration.WEIGHTING_SCHEME,
				PersonMatchingConfiguration.WEIGHTING_APPROACH, PersonMatchingConfiguration.SEMANTIC_EXTENSION);
	}

	public PersonMatchingServiceImpl() {
		this(PersonMatchingConfiguration.MATCHING_TECHNIQUE, PersonMatchingConfiguration.WEIGHTING_SCHEME,
				PersonMatchingConfiguration.WEIGHTING_APPROACH, PersonMatchingConfiguration.SEMANTIC_EXTENSION);
	}

	public void setResourceStore(ResourceStore resourceStore) {
		this.resourceStore = resourceStore;
	}
	
	@Override
	public List<PersonMatch> match(Resource person) {
		return match(person, 0.0);
	}

	@Override
	public List<PersonMatch> match(Resource person, double threshold) {
		logger.debug("Looking for matches for person "+person+" [threshold = "+threshold+"]");			
		
		long start = System.currentTimeMillis();
		List<PersonMatch> matches = new ArrayList<PersonMatch>();
		Map<Resource, PersonProfile> profiles = PersonMatchingRetrieval.getAllPersonsSelectList(person, resourceStore, conditionEquals, null);
		if (!profiles.isEmpty()) {
			PersonProfile matchingProfile = profiles.entrySet().iterator().next().getValue();
			ModelSet knowledgeBase = this.resourceStore.getTripleStore().getUnderlyingModelSet();
			matches = performPersonMatching(person, matchingProfile, threshold, this.techniqueValues, this.weightingApproach, this.semanticExtension, knowledgeBase);
		}		
		
		logger.info("Performed person matching for person "+person+" [repository="+resourceStore.getName()+", " +
				"technique="+this.matchingTechnique+", time="+(System.currentTimeMillis()-start)+"ms]");

		return matches;
	}

	@Override
	public List<List<PersonMatch>> match(List<Resource> people) {
		return match(people, 0.0);
	}
	
	@Override
	public List<List<PersonMatch>> match(List<Resource> people, double threshold) {
		List<List<PersonMatch>> results = new ArrayList<List<PersonMatch>>();
		for (Resource person : people) {
			results.add(match(person, threshold));
		}
		return results;
	}
	
	/**
	 * Retrieves the details of all the Persons that will be matched against,
	 * where each PersonContact is stored in a PersonProfile data structure.
	 * The similarity score for each PersonContact of each Person is calculated
	 * and stored in a Hash Table.
	 * 
	 * @param matchingProfile Profile of Person which will be matched
	 * @param profileLookupMap Hash Map to store all the person profiles of a Person
	 * @param weights weighting scheme to be used for calculating the similarity
	 * @param approach number specifying the weighting approach to be used
	 * @param semanticExtension boolean specifying if the semantic extension will be switched on or not
	 * @return
	 */
	private Map<Resource, Set<MatchResult>> calculateSimilarity(PersonProfile matchingProfile, Map<Resource, PersonProfile> profileLookupMap, Map<String, Integer> weights, Integer approach, boolean semanticExtension, ModelSet localRepository) {
		PersonMatcher personMatcher = new MongeElkanPersonMatcher(this.weightingScheme);
	    Map<Resource, Set<MatchResult>> personLookupMap = new HashMap<Resource, Set<MatchResult>>();
	    
		for (Resource key : profileLookupMap.keySet()) {
			PersonProfile personContact = profileLookupMap.get(key);
			MatchResult profileMatch = personMatcher.match(matchingProfile, personContact, weights, approach, semanticExtension, localRepository);
		
			if (!personLookupMap.containsKey(personContact.getPersonURI()))
			{
				Set<MatchResult> matchList = new HashSet<MatchResult>();
				matchList.add(profileMatch);
				personLookupMap.put(personContact.getPersonURI(), matchList); //maps Person URI and match result of it's PersonContact 
			} else {
				Set<MatchResult> matchList = personLookupMap.get(personContact.getPersonURI());
				matchList.add(profileMatch); //adds match result of another PersonContact belonging to an existing Person URI
				personLookupMap.put(personContact.getPersonURI(), matchList);
			}				
		}
		return personLookupMap;
	}
	
	/**
	 * Calculates the person matching similarity score and determines 
	 * the matching score of the matching Person with all the Persons
	 * within the triple store, by taking into consideration the scores
	 * obtained for each PersonContact attributed to each Person. 
	 * The final scores are stored in a Hash Table containing the 
	 * Person URI and the equivalent matching score.
	 * 
	 * @param person URI of Person which will be matched 
	 * @param matchingProfile Profile of Person which will be matched
	 * @param threshold Threshold that will determine if a profile/set of profiles match to a corresponding Person depending to the similarity score
	 * @param weights weighting scheme to be used for calculating the similarity
	 * @param approach number specifying the weighting approach to be used
	 * @param semanticExtension boolean specifying if the semantic extension will be switched on or not
	 * @return
	 */
	private List<PersonMatch> performPersonMatching(Resource person, PersonProfile matchingProfile, double threshold, Map<String, Integer> weights, Integer approach, boolean semanticExtension, ModelSet localRepository) {
		Map<Resource, PersonProfile> profileLookupMap  = PersonMatchingRetrieval.getAllPersonsSelectList(person, resourceStore, conditionNotEquals, matchingProfile.getSourceURI());
		List<PersonMatch> matches = new ArrayList<PersonMatch>();			
		
		if (profileLookupMap.size() > 0) {
			Map <Resource, Set<MatchResult>> personLookupMap = calculateSimilarity(matchingProfile, profileLookupMap, weights, approach, semanticExtension, localRepository); 
			for (Resource key : personLookupMap.keySet()) {
				double personTotalScore = 0.0;				
				for (MatchResult mr : personLookupMap.get(key)) {
					personTotalScore += mr.getSimilarityScore();
				}					
				double score = 0.0;
				if ((personLookupMap.get(key).size() > 0) && (personTotalScore > 0.0)) {
		    		score = personScoreFunction(personTotalScore, personLookupMap.get(key).size());
		    	}				
				if (score >= threshold) {
					matches.add(new PersonMatch(matchingProfile.getPersonURI(),key,score,personLookupMap.get(key)));
				}	
			}
		}		
		return matches;		
	}
	
	/**
	 * Calculates the overall score function of a particular contact when compared 
	 * to all the contact profiles of a particular person. This will determine the 
	 * degree of equivalence between the contact profile and person.
	 * 
	 * @param totalSimilarityScore total similarity score of person contacts
	 * @param totalContacts total number of contacts linked to a person
	 * @return
	 */
	private double personScoreFunction(double totalSimilarityScore, int totalContacts) {
		return totalContacts > 0 ? totalSimilarityScore / totalContacts : 0;
	}
	
}
