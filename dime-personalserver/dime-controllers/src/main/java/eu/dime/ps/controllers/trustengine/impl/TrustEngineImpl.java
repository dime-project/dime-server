package eu.dime.ps.controllers.trustengine.impl;

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.DPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NDO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.h2.server.web.DbTableOrView;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.RDFDataException;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.repository.RepositoryException;
import eu.dime.commons.dto.TrustWarning;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.LivePostManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileAttributeManager;
import eu.dime.ps.controllers.trustengine.PrivacyLevel;
import eu.dime.ps.controllers.trustengine.TrustEngine;
import eu.dime.ps.controllers.trustengine.TrustRecommendation;
import eu.dime.ps.controllers.trustengine.exception.PrivacyValueNotValidException;
import eu.dime.ps.controllers.trustengine.exception.TrustValueNotValidException;
import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.RDFReactorThing;
import eu.dime.ps.semantic.model.dcon.Context;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.rdf.ResourceStore;


public class TrustEngineImpl implements TrustEngine {
		
	Logger logger = Logger.getLogger(TrustEngineImpl.class);
	private ResourceStore resourceStore;
	private PrivacyPreferenceService privacyPrefService;
	private PersonManager personManager;
	private ProfileAttributeManager profileAttributeManager;
	private LivePostManager livePostManager;
	private ConnectionProvider connectionProvider;
	
	/**
	 * Constructor
	 */
	public TrustEngineImpl(){
	}
	
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setPersonManager(PersonManager personManager){
		this.personManager = personManager;
	}
	
	public void setLivePostManager(LivePostManager livePostManager){
		this.livePostManager = livePostManager;
	}
	
	public void setProfileAttributeManager(ProfileAttributeManager profileAttributeManager){
		this.profileAttributeManager = profileAttributeManager;
	}
	
	/**
	 * Method for checking if trust value is high enough for privacy level
	 * same like above but other parameters
	 * @param trustValue
	 * @param privacy_level
	 * @return true if trust level is high enough for privacy level
	 */
	private boolean isTrusted(Agent agent, RDFReactorThing thing) {
		double privacyValue = thing.getAllPrivacyLevel().next().doubleValue();
		double trustValue = agent.getAllTrustLevel().next().doubleValue();
		PrivacyLevel pl = PrivacyLevel.getLevelForValue(privacyValue);		
		return (trustValue >= pl.getNextLowerLevel().getValue());
	}
	/**
	 * Calculates the situational trust for the current situation and historic values for the situation
	 * @param person The person 
	 * @param personalTrust	personal trust value for the person
	 * @param situation the current situation of the person
	 * @return trust for given situation
	 */
	private double calculateSituationalTrust(URI contactUri, double personalTrust, Context context){
		// maybe all information is available from Person..
		//double trustLevel = TripleStoreAccessor.getTrustValueForContact(contactUri);
		//TODO: implement
		return 0.0;
	}
	
	
	/**
	 * Method to get a recommendation for sharing a thing with a group of persons
	 * First checks if all persons have enough trust to share
	 * if not, further evaluation is performed and also a recommendation, whether to adapt the tl or the pl
	 * @param contacts
	 * @param sharedThing
	 * @return something to tell the UI what message to display (warning: privacy level too high, trust too low, or do nothing)
	 * @throws ClassCastException 
	 * @throws TrustValueNotValidException 
	 * @throws PrivacyValueNotValidException 
	 * @throws NotFoundException 
	 */
	@Override
	public TrustWarning getTrustRecommendation (List <String> contacts_URIs, URI sharedThing_URI) throws PrivacyValueNotValidException, TrustValueNotValidException, ClassCastException, NotFoundException {
		RDFReactorThing sharedThing = null;
		logger.info("GET TrustRecommendation: sharedThing:"+sharedThing_URI);
		
		try {
			sharedThing = this.getResourceStore().get(sharedThing_URI, RDFReactorThing.class);
		} catch (NotFoundException e) {
			logger.error("Could not find shared resource in ResourceStore.", e);
			return null;
		}
		
//		if (this.getResourceStore().isTypedAs(sharedThing_URI, DLPO.LivePost)){
//			//getTrustRecommendationForShareLivepost(sharedThing_URI, agents);
//		} else if(this.getResourceStore().isTypedAs(sharedThing_URI, NFO.FileDataObject)){
//			
//		} else if(this.getResourceStore().isTypedAs(sharedThing_URI, NCO.PersonContact)){
//			
//		}
		
		Collection<Agent> agents = this.getPrivacyPreferenceService().getAgentsWithAccessTo(sharedThing_URI);
		// step 0: check if enough trust
	    contacts_URIs.removeAll(agents); //remove contacts that already have access
		//int i = 0;
		for (Agent agent : agents) {
			contacts_URIs.remove(agent.asURI().toString());
		}
		TrustWarning tw = new TrustWarning();
		double level = 0;
		double trustValue = 1.0;
		for (String contactId : contacts_URIs) {
			Agent agent = this.getResourceStore().get(new URIImpl(contactId), Agent.class);
			
			if (agent.hasTrustLevel() && sharedThing.hasPrivacyLevel()){
				double t, p;
				try {
					t = agent.getAllTrustLevel().next();
					if (t <= 0.0){
						t = AdvisoryConstants.MIN_TRUST_VALUE;
					} else if (t >= 1.0){
						t = AdvisoryConstants.MAX_TRUST_VALUE;
					}
					p = sharedThing.getAllPrivacyLevel().next().doubleValue();
					if (p <= 0.0){
						p = AdvisoryConstants.PV_PUBLIC;
					} else if (p >= 1.0){
						p = AdvisoryConstants.PV_SECRET;
					}
				}catch(RDFDataException e){
					logger.warn("Trust and/or privacy values are not retrievable. Wrong data format?", e);
					continue;
				}
				if (TrustProcessor.isTrusted(t,p)){
					// s
					logger.info("GET TrustRecommendation: no warning");

					//return donothing;
				}else {
					logger.info("GET TrustRecommendation: conflict detected: ");
					tw.addAgent(agent.asURI().toString());	
					t = agent.getAllTrustLevel().next();
					p = sharedThing.getAllPrivacyLevel().next();
					double tmp_lvl = (p/t) * 0.1;
					if (tmp_lvl > level){
						level = tmp_lvl;
					}
					if (t<trustValue){
						trustValue = t;
					}
					//recommendation.addConflict(i, agent.asURI(), sharedThing_URI);
					//i++;
				}
				 //send detailed information who has not enough tl etc.. 
			}
		}
		if (tw.getUntrustedAgents() == null || tw.getUntrustedAgents().isEmpty()){
			return null;
		}
		tw.addResource(sharedThing_URI.toString());
		tw.setPrivacyValue(sharedThing.getAllPrivacyLevel().next().doubleValue());
		tw.setTrustValue(trustValue);
		level = Math.round(level*100);
		level = level/100;
		tw.setWarningLevel(level);
		int audience = agents.size() + contacts_URIs.size();
		// step 1: calculate threshold
		boolean threshold = TrustProcessor.getThreshold(audience, sharedThing.getAllPrivacyLevel().next());
		// step 2: check
		if (threshold) {
			// TODO: return adaptprivacylevel;
		} else {
			// TODO: return adapttrust;
		}
		return tw;
		
	}
	
	/** 
	 * simplified approach (low,med,high)
	 * @return
	 * @throws ClassCastException 
	 * @throws TrustValueNotValidException 
	 * @throws PrivacyValueNotValidException 
	 */
	public TrustWarning getSimpleRecommendation(URI agentUri, URI thingUri) throws PrivacyValueNotValidException, TrustValueNotValidException, ClassCastException{
		RDFReactorThing sharedThing = null;
		Agent recipient_agent = null;
		logger.info("GET TrustRecommendation: sharedThing:"+thingUri);
		
		try {
			sharedThing = getSharedResource(thingUri);
			recipient_agent = this.getResourceStore().get(agentUri, Agent.class);
		} catch (NotFoundException e) {
			logger.error("Could not find shared resource in ResourceStore.", e);
			return null;
		}
		
		Collection<Agent> agents = this.getPrivacyPreferenceService().getAgentsWithAccessTo(thingUri);
	    if (agents.contains(recipient_agent)){
	    	return null; // if already has access return
	    }

		TrustWarning tw = new TrustWarning();
		double level = 0;
		double trustValue = 1.0;
		
		if (recipient_agent.hasTrustLevel() && sharedThing.hasPrivacyLevel()){
			double t, p;
			try {
				t = recipient_agent.getAllTrustLevel().next();
				if (t <= 0.0){
					t = AdvisoryConstants.TV_LOW;
				} else if (t >= 1.0){
					t = AdvisoryConstants.TV_HIGH;
				} else {
					t = AdvisoryConstants.TV_MED;
				}
				p = sharedThing.getAllPrivacyLevel().next().doubleValue();
				if (p <= 0.0){
					p = AdvisoryConstants.PV_LOW;
				} else if (p >= 1.0){
					p = AdvisoryConstants.PV_HIGH;
				} else {
					p = AdvisoryConstants.PV_MED;
				}
			}catch(RDFDataException e){
				logger.warn("Trust and/or privacy values are not retrievable. Wrong data format?", e);
				return null;
			}
			if(p > t){
				logger.info("GET TrustRecommendation: conflict detected: ");
				tw.addAgent(recipient_agent.asURI().toString());	
				tw.addResource(thingUri.toString());
				tw.setPrivacyValue(p);
				tw.setTrustValue(t);
				if (p == AdvisoryConstants.PV_MED){
					level = 0.25;
				} else if (p == AdvisoryConstants.PV_HIGH && t == AdvisoryConstants.TV_MED){
					level = 0.5;
				} else if (p == AdvisoryConstants.PV_HIGH && t == AdvisoryConstants.TV_LOW){
					level = 0.75;
				}
				tw.setWarningLevel(level);
			}
		}
		if (tw.getUntrustedAgents() == null || tw.getUntrustedAgents().isEmpty()){
			return null;
		}
		return tw;
	}

	private RDFReactorThing getSharedResource(URI resUri) throws NotFoundException {
		RDFReactorThing resource = null;
		if (getResourceStore().isTypedAs(resUri, NIE.DataObject)){
			resource = this.getResourceStore().get(resUri, DataObject.class);
		} else if (getResourceStore().isTypedAs(resUri, DLPO.LivePost)){
			resource = this.getResourceStore().get(resUri, LivePost.class);
		} else if (getResourceStore().isTypedAs(resUri, PPO.PrivacyPreference)){
			//Error. Databox should already be resolved here...
		} else {
			resource = this.getResourceStore().get(resUri, RDFReactorThing.class);
		}		
		return resource;
	}

	/**
	 * calculates the resulting trust level when sharing a thing to a person and
	 * sets the trust level of the person to the new value
	 * @param personUri Uri of person
	 * @param thingUri Uri of thing
	 * @return the calculated new trust value
	 */
	private double adoptDirectTrustforSharing(URI personUri, URI thingUri) {
		logger.info("ADOPT Trust: p"+personUri+" t"+thingUri);
		Collection <Agent> contacts = this.getPrivacyPreferenceService().getAgentsWithAccessTo(thingUri);
		Agent recipient = null;
		RDFReactorThing thing = null;
		try {
			recipient = this.getResourceStore().get(personUri, Agent.class);
			thing = getSharedResource(thingUri);
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		}
		double weight = thing.getAllPrivacyLevel().next().doubleValue();
		double trustValue = TrustProcessor.calculateAdopted3AbasedDirectTrust(weight, contacts.size());
		if (trustValue > recipient.getAllTrustLevel().next().doubleValue()){
			logger.info("INCR Trust: trustV > current:"+recipient.getAllTrustLevel().next().doubleValue());
			recipient.setTrustLevel(trustValue);
			try {
				this.getResourceStore().update(recipient);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}
		return trustValue;
	}
	
	private double adoptDirectTrustforSharingToGroup(Person recipient, URI thingUri, int groupSize) 
			throws NotFoundException, PrivacyValueNotValidException, 
			TrustValueNotValidException, InfosphereException {
		logger.info("Adopt Trust Group: recip:"+recipient.asURI().toString()+ " thing: "+thingUri+ " groupSize: "+groupSize);

		Collection <Agent> contacts = this.getPrivacyPreferenceService().getAgentsWithAccessTo(thingUri);
		//TODO: if agent is personGroup get 
		logger.info("Sharing to "+groupSize + " people. "+contacts.size()+" people already have access.");

//		for (Iterator<Agent> iterator = contacts.iterator(); iterator.hasNext();) {
//			Agent agent = iterator.next();
//			//TODO: refactor
//		}
	
		RDFReactorThing thing = null;

		thing = getSharedResource(thingUri);

		if (!thing.hasPrivacyLevel()){
			throw new PrivacyValueNotValidException(-1.0);
		}else if (!recipient.hasTrustLevel()){
			throw new TrustValueNotValidException(-1.0);
		}
		double weight = thing.getAllPrivacyLevel().next().doubleValue();
		double trustValue = TrustProcessor.calculateAdopted3AbasedDirectTrust(weight, contacts.size()+groupSize);
		// TODO: find overlapping people

		if (trustValue > recipient.getAllTrustLevel().next().doubleValue()){
			logger.info("Adapting trust for: "+recipient.asURI().toString() +" old: "+recipient.getAllTrustLevel().next().doubleValue()+ " new: "+trustValue );
			recipient.setTrustLevel(trustValue);
			personManager.update(recipient);
		}
		return trustValue;
	}

	
	private double adoptPrivacyLevelforSharing(URI personUri, URI thingUri) throws NotFoundException {
		Collection <Agent> agentsWithAccess = this.getPrivacyPreferenceService().getAgentsWithAccessTo(thingUri);
		RDFReactorThing thing = null;
		thing = getSharedResource(thingUri);

		double privacyLevel = thing.getAllPrivacyLevel().next().doubleValue();
		double tempTrustValue = privacyLevel / ((1-privacyLevel) * ((agentsWithAccess.size()+2) +1) ); 
		logger.info("ADAPT privacy level: p"+personUri.toString()+" t:"+thingUri.toString());
		if (tempTrustValue<privacyLevel){
			logger.info("SET privacy level: new: "+tempTrustValue+" old: "+privacyLevel);

			//PrivacyLevel plnew = PrivacyLevel.getLevelForValue(tempTrustValue);			
			setPrivacyLevelForThing(tempTrustValue, thingUri);
		}
		
		return tempTrustValue;
	}
	
	private double adoptPrivacyLevelforSharing(Person person, URI thingUri) throws PrivacyValueNotValidException, TrustValueNotValidException, NotFoundException {
		Collection <Agent> agentsWithAccess = this.getPrivacyPreferenceService().getAgentsWithAccessTo(thingUri);
		RDFReactorThing thing = null;
		try {
			thing = getSharedResource(thingUri);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		if (!thing.hasPrivacyLevel()){
			throw new PrivacyValueNotValidException(-1.0);
		} if (!person.hasTrustLevel()){
			throw new TrustValueNotValidException(-1.0);
		}
		double privacyLevel = thing.getAllPrivacyLevel().next().doubleValue();
		double tempTrustValue = privacyLevel / ((1-privacyLevel) * ((agentsWithAccess.size()+2) +1) ); 
		logger.info("ADAPT privacy level: p"+person.asURI().toString()+" t:"+thingUri.toString());

		if (tempTrustValue<privacyLevel){
			logger.info("SET privacy level: new: "+tempTrustValue+" old: "+privacyLevel);

			setPrivacyLevelForThing(tempTrustValue, thingUri);
		}
		
		return tempTrustValue;
	}


// ---------------------------------------------------------------------------------
// --------------------- PUBLIC INTERFACE ------------------------------------------
	
	/**
	 * Checks for conflicts between Agent that have already access to databox and pl of thing to be added
	 * @throws ClassCastException 
	 * @throws TrustValueNotValidException 
	 * @throws PrivacyValueNotValidException 
	 */
	public TrustRecommendation getTrustRecommendationForUpdateDatabox(URI dbUri, URI thingUri) throws PrivacyValueNotValidException, TrustValueNotValidException, ClassCastException {
		// TODO: get all agents with access, get pl of thing to add, check for conflicts
		TrustRecommendation recommendation = new TrustRecommendation("Everything ok, no conflict");
		RDFReactorThing sharedThing = null;
		try {
			sharedThing = getSharedResource(thingUri);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		Collection <Agent> agentsWithAccess = this.getPrivacyPreferenceService().getAgentsWithAccessTo(thingUri);
		for (Iterator<Agent> agentIterator = agentsWithAccess.iterator(); agentIterator.hasNext();) {
			Agent agent = agentIterator.next();
			if (!TrustProcessor.isTrusted(agent.getAllTrustLevel().next().doubleValue(), 
					sharedThing.getAllPrivacyLevel().next().doubleValue())){
				recommendation.addConflict(agentIterator.hashCode(), agent.asURI(), thingUri);
				recommendation.setMessage("One or more privacy issues detected.");
			}			
		}	
		return recommendation;
	}


	@Override
	public TrustRecommendation getTrustRecommendationForShareDatabox(URI dbUri, URI agentUri) 
			throws TrustValueNotValidException, NotFoundException, InfosphereException, 
					PrivacyValueNotValidException, ClassCastException {
		logger.info("getTrustRecommendationForShareDatabox "+dbUri.toString()+" "+agentUri.toString());
		TrustRecommendation recommendation = new TrustRecommendation("init message.");
		double agentTrust = 0, itemPrivLevel = 0;
		
		Person[] persons = getPersonArray(agentUri);
		
		for (int i = 0; i < persons.length; i++) {
			Person person = (Person) persons[i];
			if (person.hasTrustLevel()){
				agentTrust = person.getAllTrustLevel().next().doubleValue();
				logger.info("Person trust is: "+agentTrust);
			}else{
				logger.warn("Could not read agents trustlevel because has none.");
			}
			Collection<DataObject> dbItems = null;
			try {
				dbItems = getAllItemsInDatabox(dbUri);
			} catch (NotFoundException e) {
				logger.fatal("Could not read items in databox. DBUri: "+dbUri.toString());
				throw e;
			}

			for (Iterator <DataObject> dataObjectIterator = dbItems.iterator(); dataObjectIterator.hasNext();) {
				DataObject dataObject = dataObjectIterator.next();
				
				if (dataObject.hasPrivacyLevel()){
					try {
						itemPrivLevel = dataObject.getAllPrivacyLevel().next().doubleValue();
					} catch (Exception e) {
						logger.fatal("Exception when reading privacy value from dataObject: "+dataObject.asURI().toString());
						logger.fatal(e);
					}
				} else{
					logger.error("DataObject has no privacy level. Setting to public ");
					itemPrivLevel = PrivacyLevel.PUBLIC.getValue();
				}
				if (!TrustProcessor.isTrusted(agentTrust, itemPrivLevel)){
					recommendation.addConflict(dataObjectIterator.hashCode(), person.asURI(), dataObject.asURI());
				}
			}
		}
		return recommendation;
	}



	public TrustRecommendation getTrustRecommendationForShareProfile(URI profileUri, URI agentUri) 
		throws PrivacyValueNotValidException, TrustValueNotValidException, InfosphereException, NotFoundException {
		
		logger.info("getTrustRecommendationForShareProfile "+profileUri.toString()+" "+agentUri.toString());

		TrustRecommendation recommendation = new TrustRecommendation("init");

		Person [] persons = getPersonArray(agentUri);
		Collection <Resource> profileAttributes = profileAttributeManager.getAllByContainer(profileUri.toString());

		for (Iterator<Resource> attributes = profileAttributes.iterator(); attributes.hasNext();) {
			Resource attribute = attributes.next();
			double attributePrivacy = getPrivacyLevelforRessource(attribute);

			for (int i = 0; i < persons.length; i++) {
				double personTrust = persons[i].getAllTrustLevel().next().doubleValue();			
				if (!TrustProcessor.isTrusted(personTrust, attributePrivacy)){
					recommendation.addConflict(attributes.hashCode(), persons[i].asURI(), attribute.asURI());
					recommendation.setMessage("Conflict detected. An agent is not trusted enough to receive attribute: "+attribute.asURI());
				}
			}

		}
		return recommendation;
	}

	public TrustRecommendation getTrustRecommendationForUpdateProfile(URI profileUri) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		//Collection <Agent> agentsWithAccess = this.getResourceStore().getAgentsWithAccessTo(profileUri);
		// TODO: implement
		return recommendation;
	}

	
	public TrustRecommendation getTrustRecommendationForUpdateFile(URI fileUri) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		//TODO: implement
		return recommendation;
	}

	
	public TrustRecommendation getTrustRecommendationForShareLivestream(URI liveStreamUri, URI agentUri) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		//TODO: is there a livestream object at all???
		return recommendation;
	}

	@Override
	public TrustRecommendation getTrustRecommendationForShareLivepost(URI livePostUri, URI agentUri) throws PrivacyValueNotValidException, TrustValueNotValidException, NotFoundException, InfosphereException {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		logger.info("getTrustRecommendationForLivepost "+livePostUri.toString()+" "+agentUri.toString());

//		double agentTrust = 0, itemPrivacyValue = 0;
//		Person [] persons = getPersonArray(agentUri);
//		for (int i = 0; i < persons.length; i++) {
//			Person person = persons[i];
//			if (person.hasTrustLevel()){
//				agentTrust = person.getAllTrustLevel().next().doubleValue();
//			}else{
//				logger.warn("Could not read agents trustlevel because has none.");
//			}
//			LivePost livePost = semanticApi.get(livePostUri, LivePost.class);
//			if (livePost.hasPrivacyLevel()){
//				try {
//					itemPrivacyValue = livePost.getAllPrivacyLevel().next().doubleValue();
//				} catch (Exception e) {
//					logger.fatal(e);
//				}
//			} else{
//				logger.error("Livepost has no privacy level. Setting to public");
//				itemPrivacyValue = PrivacyLevel.PUBLIC.getValue();
//				//throw new PrivacyValueNotValidException(0);
//			}
//			if (!isTrusted(agentTrust, itemPrivacyValue)){
//				recommendation.addConflict(1, agentUri, livePostUri);
//			}
//			
//		}
		LivePost livePost = null;
		try {
			livePost = livePostManager.get(livePostUri.toString());
		} catch (InfosphereException e) {
			logger.warn("Could not retrieve Livepost for URI: "+livePostUri.toString(), e);
		}
		
		/* NLP Test commented */
//		String testString = "Today is a funny day. Sitting with Anna and Bob in the park of Miami and enjoying the sun.";
//		NLPController nlpController = new NLPController();
//		recommendation = nlpController.analyze(testString); //livePost.getPrefLabel()
		return recommendation;
	}

	
	public TrustRecommendation getTrustRecommendationForShareResource(URI resourceUri, URI agentUri) 
			throws NotFoundException, InfosphereException, PrivacyValueNotValidException, TrustValueNotValidException {
		
		TrustRecommendation recommendation = new TrustRecommendation("init");
		logger.info("getTrustRecommendationForShareResource "+resourceUri.toString()+" "+agentUri.toString());

		double agentTrust = 0, itemPrivacyValue = 0;
		Person [] persons = getPersonArray(agentUri);
		for (int i = 0; i < persons.length; i++) {
			Person person = persons[i];
			if (person.hasTrustLevel()){
				agentTrust = person.getAllTrustLevel().next().doubleValue();
			}else{
				logger.warn("Could not read agents trustlevel because has none.");
			}
			Resource resource = this.getResourceStore().get(resourceUri);			
			try {
				itemPrivacyValue = getPrivacyLevelforRessource(resource);
			} catch (Exception e) {
				logger.fatal(e);
			}
			
			if (!TrustProcessor.isTrusted(agentTrust, itemPrivacyValue)){
				recommendation.addConflict(1, agentUri, resourceUri);
			}
		}
		return recommendation;
	}

	
	public TrustRecommendation getTrustRecommendationForUpdateResource(URI resourceUri) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		return recommendation;
	}


	public TrustRecommendation processTrustForShareDatabox(URI agentUri, URI dbUri, String adapt) 
			throws NotFoundException, InfosphereException, PrivacyValueNotValidException, TrustValueNotValidException {
		
		TrustRecommendation recommendation = new TrustRecommendation("Nothing ok");

		logger.info("processTrustForShareDatabox "+dbUri.toString()+" "+agentUri.toString());

		Person[] persons = getPersonArray(agentUri);

		Collection<DataObject> dbItems = null;
		try {
			dbItems = getAllItemsInDatabox(dbUri);
		} catch (NotFoundException e) {
			throw e;
		}

		for (int i = 0; i < persons.length; i++) {
			Person person = (Person) persons[i];

			if (adapt.contains("trust")){
				for (Iterator <DataObject> iterator = dbItems.iterator(); iterator.hasNext();) {
					DataObject dataObject = iterator.next();
					URI doUri = dataObject.asURI();
					logger.info("-- Trust adaption: "+doUri+" "+person.asURI());
					adoptDirectTrustforSharingToGroup(person, doUri, persons.length);
					recommendation.setMessage("Trust values adapted.");

				}
			} else if (adapt.equalsIgnoreCase("privacy")){
				for (Iterator <DataObject> iterator = dbItems.iterator(); iterator.hasNext();) {
					DataObject dataObject = (DataObject) iterator.next();
					URI doUri = dataObject.asURI();
					adoptPrivacyLevelforSharing(person, doUri);
					recommendation.setMessage("Privacy values adapted.");

				}
			} else if (adapt.equalsIgnoreCase("none")){
				// do nothing
				recommendation.setMessage("No values adapted this time.");
				
			}
			else{
				logger.info("nothing adapted.");
			}
		}
		return recommendation;
	}
	
	
	public TrustRecommendation processTrustForUpdateDatabox(URI dbUri, URI thingUri, String adapt) 
			throws ClassCastException, NotFoundException {
		
		TrustRecommendation recommendation = new TrustRecommendation("init");
		logger.info("processTrustForUpdateDatabox "+dbUri.toString()+" "+thingUri.toString()+ " "+ adapt);

		//TODO: how does the update work?
		// update could be add file, remove file, also update file?
		// atm here we build add file to databox
		RDFReactorThing sharedThing = null;
		sharedThing = getSharedResource(thingUri);
		Collection <Agent> agentsWithAccess = this.getPrivacyPreferenceService().getAgentsWithAccessTo(dbUri);
		for (Iterator <Agent> agentIterator = agentsWithAccess.iterator(); agentIterator.hasNext();) {
			Agent agent = agentIterator.next();
			if (!isTrusted(agent, sharedThing)){
				recommendation.addConflict(agentIterator.hashCode(), agent.asURI(), thingUri);
				if (adapt.equalsIgnoreCase("trust")){
					adoptDirectTrustforSharing(agent.asURI(), thingUri);
				}else if (adapt.equalsIgnoreCase("privacy")){
					adoptPrivacyLevelforSharing(agent.asURI(), thingUri);
				}else {
					//do nothing
				}
			}			
		}	
		return recommendation;
	}

	
	public TrustRecommendation processTrustForShareProfile(URI profileUri, URI agentUri, String adapt) throws NotFoundException {
		TrustRecommendation recommendation = new TrustRecommendation("init");

		logger.info("processTrustForShareProfile "+profileUri.toString()+" "+agentUri.toString()+ " "+ adapt);

		if (adapt.equalsIgnoreCase("trust")){
			adoptDirectTrustforSharing(agentUri, profileUri);
			recommendation.setMessage("Trust values adapted.");
		} else if (adapt.equalsIgnoreCase("privacy")){
			adoptPrivacyLevelforSharing(agentUri, profileUri);
			recommendation.setMessage("Privacy values adapted.");

		} else if (adapt.equalsIgnoreCase("none")){
			// do nothing
			recommendation.setMessage("No values adapted this time.");
		}
		return recommendation;
	}

	
	public TrustRecommendation processTrustForUpdateProfile(URI profileUri, String adapt) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		//TODO: check here the pl of the attribute to add, and adapt pl of profile
		return recommendation;
	}

	
	public TrustRecommendation processTrustForUpdateFile(URI fileUri, String adapt) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		return recommendation;
	}

	
	public TrustRecommendation processTrustForShareLivestream(URI liveStreamUri, URI agentUri, String adapt) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		
		return recommendation;
	}

	
	public TrustRecommendation processTrustForShareLivepost(URI livePostUri, URI agentUri, String adapt) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		logger.info("processTrustForShareLivepost "+livePostUri.toString()+" "+agentUri.toString()+ " "+ adapt);

		return recommendation;
	}

	
	public TrustRecommendation processTrustForShareResource(URI resourceUri, URI agentUri, String adapt) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		logger.info("processTrustForShareResource "+resourceUri.toString()+" "+agentUri.toString()+ " "+ adapt);

		return recommendation;
	}

	
	public TrustRecommendation processTrustForUpdateResource(URI resourceUri, String adapt) {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		return recommendation;
	}

	
	public TrustRecommendation getRecommendation() {
		TrustRecommendation recommendation = new TrustRecommendation("init");
		logger.info("getRecommendation ");

		return recommendation;
	}
	/**
	 * NEW API:
	 * universal endpoint for recommendations of all kinds
	 * 
	 * @param agents
	 * @param sharedThings
	 * @return advisory object
	 */ 
	public List<TrustWarning> getRecommendation(List<String> agents, List<String> sharedThings) {
		//TrustRecommendation recommendation = new TrustRecommendation("init");
		List <TrustWarning> warnings = new ArrayList<TrustWarning>();
		for (String thing : sharedThings) {
			URI thing_uri = new URIImpl(thing);
			TrustWarning tmpRec = null;
			for (String agent : agents){
				try {
					//tmpRec = this.getTrustRecommendation(list, thing_uri);
					tmpRec = getSimpleRecommendation(new URIImpl(agent), thing_uri);
					if (tmpRec!=null){
						warnings.add(tmpRec);
					}
				} catch (PrivacyValueNotValidException e) {
					logger.error("Could not calculate recommendations.",e);
					return null;
				} catch (TrustValueNotValidException e) {
					logger.error("Could not calculate recommendations.",e);
					return null;
				} catch (ClassCastException e) {
					logger.error("Could not calculate recommendations.",e);
					return null;
				}
			}
		}
		return sortWarnings(warnings);
		//return warnings;
	}

	
	// ------------ some helper methods ------------
	
	
	/*
	 * Sorts the warnigns according to the simplified format (low,med high)
	 */
	private List<TrustWarning> sortWarnings(List<TrustWarning> warnings) {
		Set<String> agent_set_low_med = new HashSet<String>();
		Set<String> agent_set_med_high = new HashSet<String>();
		Set<String> agent_set_low_high = new HashSet<String>();

		Set<String> res_set_low_med = new HashSet<String>();
		Set<String> res_set_med_high = new HashSet<String>();
		Set<String> res_set_low_high = new HashSet<String>();


		for (TrustWarning warning: warnings){
			double value = warning.getWarningLevel();
				
			if (value == 0.25){
				agent_set_low_med.addAll(warning.getUntrustedAgents());
				res_set_low_med.addAll(warning.getPrivateResources());

			} else if (value == 0.5){
				agent_set_med_high.addAll(warning.getUntrustedAgents());
				res_set_med_high.addAll(warning.getPrivateResources());

				
			} else if (value == 0.75){
				agent_set_low_high.addAll(warning.getUntrustedAgents());
				res_set_low_high.addAll(warning.getPrivateResources());				
			}
		}
		List <TrustWarning> list = new ArrayList<TrustWarning>();

		if (!agent_set_low_high.isEmpty()){
			TrustWarning low_high = new TrustWarning();
			low_high.addAllAgents(agent_set_low_high);
			low_high.addAllResources(res_set_low_high);
			low_high.setTrustValue(0.0);
			low_high.setPrivacyValue(1.0);
			low_high.setWarningLevel(0.75);
			list.add(low_high);
		}
		if (!agent_set_med_high.isEmpty()){
			TrustWarning med_high = new TrustWarning();
			med_high.addAllAgents(agent_set_med_high);
			med_high.addAllResources(res_set_med_high);
			med_high.setTrustValue(0.5);
			med_high.setPrivacyValue(1.0);
			med_high.setWarningLevel(0.5);
			list.add(med_high);
		}
		if (!agent_set_low_med.isEmpty()){
			TrustWarning low_med = new TrustWarning();
			low_med.addAllAgents(agent_set_low_med);
			low_med.addAllResources(res_set_low_med);
			low_med.setTrustValue(0.0);
			low_med.setPrivacyValue(0.5);
			low_med.setWarningLevel(0.25);
			list.add(low_med);
		}
		return list;
	}

	public boolean setPrivacyLevelForThing(double privacyValue, RDFReactorThing thing) throws NotFoundException{
		thing.setPrivacyLevel(privacyValue);
		this.getResourceStore().update(thing, true);
		return false;
	}
	
	private boolean setPrivacyLevelForThing(double privacyValue, URI thingUri) throws NotFoundException {
		RDFReactorThing thing = getSharedResource(thingUri);
		return setPrivacyLevelForThing(privacyValue, thing);
	}
	
	private double getPrivacyLevelforRessource(Resource resource){
		ClosableIterator<Statement> it = resource.getModel().findStatements(resource.asResource(), NAO.privacyLevel, Variable.ANY);
		double privacyLevel = -1;
		if (it.hasNext()) {
			Statement statement = it.next();
			privacyLevel = Double.parseDouble(statement.getObject().asDatatypeLiteral().getValue());
		} else {
			logger.error("No nao:privacyLevel associated with"+resource.asResource()+" was found.");
		}
		it.close();
		return privacyLevel;
	}
	
	/**
	 * Returns array of persons for given agentUri. 
	 * @param agentUri
	 * @return
	 * @throws NotFoundException 
	 * @throws InfosphereException 
	 */
	private Person[] getPersonArray(URI agentUri) throws NotFoundException, InfosphereException{
		boolean	isAgent = false;

		isAgent = this.getResourceStore().isTypedAs(agentUri, PIMO.Person);

		Person[] persons = null;
		if (isAgent){
			persons = new Person[1];
			persons[0] = this.getResourceStore().get(agentUri, Person.class);

		}else {
			PersonGroup group;
			Collection <Person> agents;
			group = this.getResourceStore().get(agentUri, PersonGroup.class);
			agents = personManager.getAllByGroup(group);
			persons = agents.toArray(new Person [agents.size()]);
		}
		return persons;
	}
	
	/**
	 * Retrieves all the items contained in a databox.
	 * 
	 * @param databoxUri
	 * @return
	 */
	private Collection<DataObject> getAllItemsInDatabox(URI databoxUri)
			throws NotFoundException {
		PrivacyPreference databox = this.getResourceStore().get(databoxUri, PrivacyPreference.class);
		
		// only the URIs of the things are shared in the databox
		Collection<Resource> itemUris = databox.getAllAppliesToResource_as().asList();
		
		// loading all the metadata from the triple store
		Collection<DataObject> items = new ArrayList<DataObject>();
		for (Resource item : itemUris) {
			try {
				items.add(this.getResourceStore().get(item.asURI(), DataObject.class));
			} catch (NotFoundException e) {
				logger.warn("Item " + item.asURI() + " is in databox " + databox.asURI() + " but it is does not exist.");
			}
		}
		
		return items;
	}
	
	public ResourceStore getResourceStore() {
		if (this.resourceStore== null){
			try {
				this.resourceStore = connectionProvider.getConnection(TenantContextHolder.getTenant().toString()).getResourceStore();
			} catch (RepositoryException e) {
				logger.error("Could not get ResourceStore", e);
			}
		}
		return this.resourceStore;
	}
	
	public PrivacyPreferenceService getPrivacyPreferenceService(){
		if (this.privacyPrefService == null){
			try {
				this.privacyPrefService = connectionProvider.getConnection(TenantContextHolder.getTenant().toString()).getPrivacyPreferenceService();
			} catch (RepositoryException e) {
				logger.error("Could not get PrivacyPreferenceService", e);
			}
		}
		return this.privacyPrefService;
	}

	
}

