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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.util.ModelUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.github.jsonldjava.core.JSONLDProcessingError;

import eu.dime.ps.communications.utils.Base64encoding;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableDataboxManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableFileManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableLivePostManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableProfileManager;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.util.JSONLDUtils;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.storage.entities.ServiceAccount;

@Controller
@Path("/dime/rest/{said}/shared")
public class PSSharedController extends PSControllerBase implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSSharedController.class);

	private ShareableDataboxManager shareableDataboxManager;
	private ShareableLivePostManager shareableLivePostManager;
	private ShareableProfileManager shareableProfileManager;
	private ShareableFileManager shareableFileManager; 
	private FileManager fileManager;

	@Autowired
	public void setShareableDataboxManager(ShareableDataboxManager shareableDataboxManager) {
		this.shareableDataboxManager = shareableDataboxManager;
	}
	@Autowired
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Autowired
	public void setShareableLivePostManager(ShareableLivePostManager shareableLivePostManager) {
		this.shareableLivePostManager = shareableLivePostManager;
	}

	@Autowired
	public void setShareableProfileManager(ShareableProfileManager shareableProfileManager) {
		this.shareableProfileManager = shareableProfileManager;
	}

	@Autowired
	public void setShareableFileManager(ShareableFileManager shareableFileManager) {
		this.shareableFileManager = shareableFileManager;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSONLD)
	@Path("/databox/@all")
	public Object getAllDataboxJSONLD(@PathParam("said") String said) {
		return getAllResources(said, shareableDataboxManager);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSONLD)
	@Path("/databox/{dbId}")
	public Object getDataboxJSONLD(
			@PathParam("said") String said,
			@PathParam("dbId") String dbId) throws UnsupportedEncodingException {
		return getResource(said, Base64encoding.decode(dbId), shareableDataboxManager);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSONLD)
	@Path("/livepost/@all")
	public Object getAllLivepostJSONLD(@PathParam("said") String said) {
		return getAllResources(said, shareableLivePostManager);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSONLD)
	@Path("/livepost/{livepostId}")
	public Object getLivepostJSONLD(
			@PathParam("said") String said,
			@PathParam("livepostId") String livepostId) throws UnsupportedEncodingException {
		return getResource(said, Base64encoding.decode(livepostId), shareableLivePostManager);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSONLD)
	@Path("/resource/@all")
	public Object getAllResourceJSONLD(@PathParam("said") String said) {
		return getAllResources(said, shareableFileManager);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSONLD)
	@Path("/resource/{resourceId}")
	public Object getResourceJSONLD(
			@PathParam("said") String said,
			@PathParam("resourceId") String resourceId) throws UnsupportedEncodingException {
		return getResource(said, Base64encoding.decode(resourceId), shareableFileManager);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSONLD)
	@Path("/profile")
	public Object getProfileJSONLD(
			@PathParam("said") String said) throws UnsupportedEncodingException {
		return getProfile(said, shareableProfileManager);
	}
	
	
	@GET
	@Path("/resource/filemanager/{resourceID}")
	public javax.ws.rs.core.Response getFile(@PathParam("said") String said,
			@PathParam("resourceID") String resourceID) {		
		logger.info("Called API method: GET /dime/rest/"+said+"/shared/resource/filemanager/"+resourceID);

		try {
			try {
				String decodedId = URLDecoder.decode(resourceID, "UTF-8");
				resourceID = UriUtil.encodeUri(decodedId);
			} catch (UnsupportedEncodingException e) {
				logger.error("Resource URI "+resourceID+" is not correct: "+e.getMessage(), e);
				return javax.ws.rs.core.Response.serverError().build();
			}
			// gets the mimetype
			FileDataObject fdo = fileManager.get(resourceID);
			Node mimetype = ModelUtils.findObject(fdo.getModel(), fdo, NIE.mimeType);
			if (mimetype == null) {
				logger.error("Error retrieving file "+resourceID+": mime type not found.");
				return javax.ws.rs.core.Response.serverError().build();
			}

			// gets file contents
			InputStream fileStream = fileManager.getBinaryStream(resourceID);

			return javax.ws.rs.core.Response.ok(fileStream, mimetype.asLiteral().getValue()).build();

		} catch (InfosphereException e) {
			logger.warn("Could not find resource with URI: " + resourceID);
			return javax.ws.rs.core.Response.serverError().build();
		}
	}
	
	

	/**
	 * Returns the profile associated with a specific account.
	 * 
	 * @param said account identifier used to share the profile through
	 * @param manager infosphere manager to retrieve shared profiles
	 * @return the profile instance serialized in json+ld
	 */
	protected <T extends Resource> Object getProfile(String said, ShareableManager<T> manager) {
		Map<String, Object> error = new HashMap<String, Object>();

		// TODO check what resources this said can access!!! and filter by it :)

		ServiceAccount account = ServiceAccount.findByName(said);
		if (account == null || account.getAccountURI() == null) {
			error.put("error", "Account "+said+" does not exist or is corrupted!");
			return error;
		}

		T profile = null;
		try {
			// TODO the managers should ensure that only the "allowed" data is returned,
			// but also that the requester is able to access this element
			// for now, we just return the object without checking any resource

			String requester = getRequesterAccount();
			String accountUri = account.getAccountURI();
			
			Collection<T> profiles = manager.getAll(accountUri, requester);
			if (profiles.size() == 0) {
				error.put("error", "No profile was shared with " + requester + " through account " +
						accountUri + ", or no profile exists for that account.");
				return error;
			} else if (profiles.size() > 1) {
				logger.warn("There should be only one profile card associated with account " + 
						accountUri + ". Returning first profile card found, but this should not happen.");
			}
			profile = profiles.iterator().next();
			
			return JSONLDUtils.serialize(profile);
		} catch (InfosphereException e) {
			error.put("error", "Cannot retrieve resource: " + e.getMessage());
			return error;
		} catch (JSONLDProcessingError e) {
			error.put("error", "Cannot serialize profile to JSON-LD: " + e.getMessage() +
					"\nProfile's RDF: \n" + profile.getModel().serialize(Syntax.Turtle));
			return error;
		}
	}

}
