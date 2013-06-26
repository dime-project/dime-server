package eu.dime.ps.controllers.trustengine;

import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.commons.dto.TrustWarning;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.trustengine.exception.PrivacyValueNotValidException;
import eu.dime.ps.controllers.trustengine.exception.TrustValueNotValidException;
import eu.dime.ps.semantic.exception.NotFoundException;


public interface TrustEngine {

	public TrustRecommendation getRecommendation();
	
	public List<TrustWarning> getRecommendation(List<String> agents, List<String> things);
	
	/**
	 * Method to get a recommendation for sharing a thing with a group of persons
	 * First checks if all persons have enough trust to share
	 * if not, further evaluation is performed. also returns a recommendation, whether to adapt the tl or the pl
	 * @param contacts
	 * @param sharedThing
	 * @return something to tell the UI what message to display (warning: privacy level too high, trust too low, or do nothing)
	 * @throws ClassCastException 
	 * @throws TrustValueNotValidException 
	 * @throws PrivacyValueNotValidException 
	 * @throws NotFoundException 
	 */
	public TrustWarning getTrustRecommendation (List <String> contacts_URIs, URI sharedThing_URI) throws PrivacyValueNotValidException, TrustValueNotValidException, ClassCastException, NotFoundException;
	
	/**
	 * Method to get a recommendation for sharing a databox with a person or group of persons
	 * First checks if all persons have enough trust to share
	 * @param dbUri
	 * @param agentUri uri of person or group
	 * @return Recommendation Object with informations for the UI etc
	 * @throws TrustValueNotValidException
	 * @throws NotFoundException
	 * @throws InfosphereException
	 * @throws PrivacyValueNotValidException
	 * @throws ClassCastException
	 */
	public TrustRecommendation getTrustRecommendationForShareDatabox(URI dbUri, URI agentUri) throws TrustValueNotValidException, NotFoundException, InfosphereException, PrivacyValueNotValidException, ClassCastException;
	
	/**
	 * Method to get a recommendation for Updating a databox (e.g. adding a file)
	 * @param dbUri
	 * @param thingUri
	 * @return
	 * @throws PrivacyValueNotValidException
	 * @throws TrustValueNotValidException
	 * @throws ClassCastException
	 */
	public TrustRecommendation getTrustRecommendationForUpdateDatabox(URI dbUri, URI thingUri) throws PrivacyValueNotValidException, TrustValueNotValidException, ClassCastException;
	
	/**
	 * Method to get a recommendation for sharing a profile to a person or group
	 * @param profileUri
	 * @param agentUri Uri of person or group
	 * @return
	 * @throws PrivacyValueNotValidException
	 * @throws TrustValueNotValidException
	 * @throws InfosphereException
	 * @throws NotFoundException
	 */
	public TrustRecommendation getTrustRecommendationForShareProfile(URI profileUri, URI agentUri) throws PrivacyValueNotValidException, TrustValueNotValidException, InfosphereException, NotFoundException;
	
	/**
	 * Method to get a recommendation for updating a profile (e.g. adding a profile attribute)
	 * @param profileUri
	 * @return
	 */
	public TrustRecommendation getTrustRecommendationForUpdateProfile(URI profileUri);
	
	/**
	 * Method to get a recommendation for updating a file (e.g. changing content) 
	 * This only has effect if the privacy level also changed
	 * @param fileUri
	 * @return
	 */
	public TrustRecommendation getTrustRecommendationForUpdateFile(URI fileUri);
	
	/**
	 * Method to get a recommendation for sharing a livestream
	 * @param liveStreamUri
	 * @param agentUri
	 * @return
	 */
	public TrustRecommendation getTrustRecommendationForShareLivestream(URI liveStreamUri, URI agentUri);
	
	/**
	 * Method to get a recommendation for sharing a livepost
	 * @param livePostUri
	 * @param agentUri
	 * @return
	 * @throws PrivacyValueNotValidException
	 * @throws TrustValueNotValidException
	 * @throws NotFoundException
	 * @throws InfosphereException
	 */
	public TrustRecommendation getTrustRecommendationForShareLivepost(URI livePostUri, URI agentUri) throws PrivacyValueNotValidException, TrustValueNotValidException, NotFoundException, InfosphereException;
	
	/**
	 * Method to get a recommendation for sharing a ressource
	 * @param resourceUri
	 * @param agentUri
	 * @return
	 * @throws NotFoundException
	 * @throws InfosphereException
	 * @throws PrivacyValueNotValidException
	 * @throws TrustValueNotValidException
	 */
	public TrustRecommendation getTrustRecommendationForShareResource(URI resourceUri, URI agentUri) throws NotFoundException, InfosphereException, PrivacyValueNotValidException, TrustValueNotValidException;
	
	/**
	 * Method to get a recommendation for updating a ressource
	 * @param resourceUri
	 * @return
	 */
	public TrustRecommendation getTrustRecommendationForUpdateResource(URI resourceUri);
	
	/**
	 * Method to adapt the privacy and/or trust values for sharing a databox 
	 * @param agentUri
	 * @param dbUri
	 * @param adapt
	 * @return
	 * @throws NotFoundException
	 * @throws InfosphereException
	 * @throws PrivacyValueNotValidException
	 * @throws TrustValueNotValidException
	 */
	public TrustRecommendation processTrustForShareDatabox(URI agentUri, URI dbUri, String adapt) throws NotFoundException, InfosphereException, PrivacyValueNotValidException, TrustValueNotValidException;
	
	/**
	 * Method to adapt the privacy and/or trust values for updating a databox
	 * @param dbUri
	 * @param thingUri
	 * @param adapt
	 * @return
	 * @throws ClassCastException
	 * @throws NotFoundException
	 */
	public TrustRecommendation processTrustForUpdateDatabox(URI dbUri, URI thingUri, String adapt) throws ClassCastException, NotFoundException;
	
	/**
	 * Method to adapt the privacy and/or trust values for sharing a profile
	 * @param profileUri
	 * @param agentUri
	 * @param adapt
	 * @return
	 * @throws NotFoundException
	 */
	public TrustRecommendation processTrustForShareProfile(URI profileUri, URI agentUri, String adapt) throws NotFoundException;
	
	/**
	 * Method to adapt the privacy and/or trust values for updating a profile
	 * @param profileUri
	 * @param adapt
	 * @return
	 */
	public TrustRecommendation processTrustForUpdateProfile(URI profileUri, String adapt);
	
	/**
	 * Method to adapt the privacy and/or trust values for updating a file
	 * @param fileUri
	 * @param adapt
	 * @return
	 */
	public TrustRecommendation processTrustForUpdateFile(URI fileUri, String adapt);
	
	/**
	 * Method to adapt the privacy and/or trust values for sharing a livestream
	 * @param liveStreamUri
	 * @param agentUri
	 * @param adapt
	 * @return
	 */
	public TrustRecommendation processTrustForShareLivestream(URI liveStreamUri, URI agentUri, String adapt);
	
	/**
	 * Method to adapt the privacy and/or trust values for sharing a livepost
	 * @param livePostUri
	 * @param agentUri
	 * @param adapt
	 * @return
	 */
	public TrustRecommendation processTrustForShareLivepost(URI livePostUri, URI agentUri, String adapt);
	
	/**
	 * Method to adapt the privacy and/or trust values for sharing a ressource
	 * @param resourceUri
	 * @param agentUri
	 * @param adapt
	 * @return
	 */
	public TrustRecommendation processTrustForShareResource(URI resourceUri, URI agentUri, String adapt);
	
	/**
	 * Method to adapt the privacy and/or trust values for updating a ressource
	 * @param resourceUri
	 * @param adapt
	 * @return
	 */
	public TrustRecommendation processTrustForUpdateResource(URI resourceUri, String adapt);
	
}
