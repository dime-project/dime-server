package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NSO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import javax.activation.MimeType;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sun.jersey.multipart.FormDataParam;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SharedTo;
import eu.dime.commons.dto.TrustEntry;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;
import eu.dime.ps.dto.Include;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.proxy.BinaryFile;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.gateway.util.JSONLDUtils;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.NFOFactory;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.storage.entities.AccountCredentials;

/**
 * Dime REST API Controller about a Resources features
 * 
 * @author mplanaguma (BDCT)
 * 
 */
@Controller
@Path("/dime/rest/{said}/resource/")
public class PSResourcesController extends PSSharingControllerBase implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSResourcesController.class);

	private static final URI[] payload = new URI[] {
		NAO.prefSymbol, NFO.wordCount, NFO.pageNumber, NAO.privacyLevel,
		NIE.mimeType, NAO.created,NAO.creator, NAO.lastModified, NFO.fileOwner, NFO.fileSize,
		NFO.lineCount, NAO.privacyLevel, NAO.prefLabel, NAO.created, NSO.sharedBy, NSO.sharedWith };

	private CredentialStore credentialStore;
	private ServiceGateway serviceGateway;
	private AccountManager accountManager;
	private PersonManager personManager;
	private PersonGroupManager personGroupManager;
	private SharingManager sharingManager;
	private FileManager fileManager;

	private final NFOFactory nfoFactory = new ModelFactory().getNFOFactory();

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}

	public void setPersonGroupManager(PersonGroupManager personGroupManager) {
		this.personGroupManager = personGroupManager;
	}

	public void setSharingManager(SharingManager sharingManager) {
		this.sharingManager = sharingManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setCredentialStore(CredentialStore credentialStore) {
		this.credentialStore = credentialStore;
	}

	@Autowired
	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

	@Override
	public PersonManager getPersonManager() {
		return this.personManager;
	}

	@Override
	public AccountManager getAccountManager() {
		return this.accountManager;
	}

	@Override
	public PersonGroupManager getPersonGroupManager() {
		return this.personGroupManager;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@all")
	public Response<Resource> getAllResources(@PathParam("said") String said) {
		Data<Resource> data = null;
		List<URI> properties = new ArrayList<URI>();
		properties = Arrays.asList(payload);
		try {
			Collection<FileDataObject> files = fileManager.getAll(properties);

			data = new Data<Resource>(0, files.size(), files.size());
			for (FileDataObject file : files) {
				Resource fileResource = new Resource(file, said,fileManager.getMe().asURI());
				PrivacyPreference pp = sharingManager.findPrivacyPreference(file.asURI().toString(), PrivacyPreferenceType.FILE);
				writeIncludes(fileResource,pp);
				fileResource.remove("nao:creator");
				resolveImageUrl(file, said, fileResource);
				data.getEntries().add(fileResource);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Create resource
	 * 
	 * @param json
	 * @return
	 */
	@POST
	@Path("@me")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Resource> createResourceFromPersonById(@PathParam("said") String said,
			Request<Resource> request) {

		Data<Resource> data, returnData;

		try {
			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();

			Resource dto = data.getEntries().iterator().next();
			if (dto.containsKey("nao:privacyLevel")){
				try {
					double privacy = ((Integer)dto.get("nao:privacyLevel")).doubleValue();
					dto.put("nao:privacyLevel", privacy);	
				} catch (ClassCastException e){
					//value already double
				}	
			}

			FileDataObject fileDataObject = dto.asResource(FileDataObject.class,fileManager.getMe().asURI());
			if(!fileDataObject.hasPrivacyLevel()){
				fileDataObject.setPrivacyLevel(AdvisoryConstants.DEFAULT_PRIVACY_LEVEL);
			}
			fileManager.add(fileDataObject);
			readIncludes(dto,fileDataObject);
			Resource fileResource = new Resource(fileDataObject, said,fileManager.getMe().asURI());
			fileResource.remove("nao:creator");
			resolveImageUrl(fileDataObject, said, fileResource);
			returnData = new Data<Resource>(0, 1, fileResource);

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(returnData);
	}



	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@all")
	public Response<Resource> getAllMyResources(@PathParam("said") String said) {
		Data<Resource> data = null;	
		List<URI> properties = new ArrayList();
		properties = Arrays.asList(payload);

		try {
			Collection<FileDataObject> files = fileManager.getAll(properties);

			data = new Data<Resource>(0, files.size(), files.size());
			for (FileDataObject file : files) {
				Resource fileResource = new Resource(file, said,fileManager.getMe().asURI());
				PrivacyPreference pp = sharingManager.findPrivacyPreference(file.asURI().toString(), PrivacyPreferenceType.FILE);
				writeIncludes(fileResource,pp);
				fileResource.remove("nao:creator");
				resolveImageUrl(file, said, fileResource);
				data.getEntries().add(fileResource);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("{personID}/@all")
	public Response<Resource> getAllResourcesFromPersonById(@PathParam("said") String said,
			@PathParam("personID") String personID, @PathParam("profileID") String profileID) {

		Data<Resource> data = null;

		try {
			Collection<FileDataObject> files = fileManager.getAllSharedBy(personID);

			data = new Data<Resource>(0, files.size(), files.size());

			for (FileDataObject file : files) {
				Resource fileResource = new Resource(file, said,fileManager.getMe().asURI());
				PrivacyPreference pp = sharingManager.findPrivacyPreference(file.asURI().toString(), PrivacyPreferenceType.FILE);
				writeIncludes(fileResource,pp);
				fileResource.remove("nao:creator");
				resolveImageUrl(file, said, fileResource);
				data.getEntries().add(fileResource);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	// TODO personID is not used
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("{personID}/{resourceID}")
	public Response<Resource> getResourceFromPersonById(@PathParam("said") String said,
			@PathParam("personID") String personID, @PathParam("resourceID") String resourceID) {

		Data<Resource> data;
		try {

			data = getResource(said, resourceID);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	@POST
	@Path("{personID}/{resourceID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Resource> postResourceFromPersonById(@PathParam("said") String said,
			Request<Resource> request, @PathParam("personID") String personID,
			@PathParam("resourceID") String resourceID) {

		Data<Resource> data, returnData;

		try {
			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();
			//read includes for the sharing proccess
			Resource resource = data.getEntries().iterator().next();

			if (resource.containsKey("nao:privacyLevel")){
				try {
					double privacy = ((Integer)resource.get("nao:privacyLevel")).doubleValue();
					resource.put("nao:privacyLevel", privacy);	
				} catch (ClassCastException e){
					//value already double
				}	
			}


			URI resourceUri = new URIImpl(resourceID);
			FileDataObject fileDataObject = data.getEntries().iterator().next()
					.asResource(resourceUri, FileDataObject.class,fileManager.getMe().asURI());
			fileManager.update(fileDataObject);
			readIncludes(resource,fileDataObject);
			Resource fileResource = new Resource(fileDataObject, said,fileManager.getMe().asURI());
			fileResource.remove("nao:creator");

			returnData = new Data<Resource>(0, 1,fileResource);
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(returnData);
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("{personID}/{resourceID}")
	public Response deleteResourceFromPersonById(@PathParam("said") String said,
			@PathParam("personID") String personID, @PathParam("resourceID") String resourceID) {

		try {
			// TODO fix: it seems to be the wrong call
			// infosphereManager.removePersonGroup(resourceID);

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
			// } catch (InfosphereException e) {
			// return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();
	}



	////
	//Method for reading the privacyPreferences of a resource
	////
	@Override
	public List<Include> readIncludes(eu.dime.ps.dto.Resource resource,eu.dime.ps.semantic.model.RDFReactorThing file)
			throws InfosphereException {

		List<Include> includes = buildIncludesFromMap(resource);
		if (!includes.isEmpty()){		

			for(Include include: includes){
				for(String group : include.groups){	
					sharingManager.shareFile(file.asURI().toString(), include.getSaidSender(),  new String[]{group});
				}
				for(String service: include.services){	
					sharingManager.shareFile(file.asURI().toString(), include.getSaidSender(),  new String[]{service});						
				}
				for (HashMap<String, String> person : include.persons){
					if(person.get("saidReceiver") == null){						
						sharingManager.shareFile(file.asURI().toString(), include.getSaidSender(),  new String[]{person.get("personId")});	
					}
					else{	
						sharingManager.shareFile(file.asURI().toString(), include.getSaidSender(),  new String[]{person.get("saidReceiver")});	
					}					
				}

			}
		}
		return includes;
	}


	// FileManager

	@GET
	@Path("/@me/shared/{saidReceiver}/{resourceID}")
	public javax.ws.rs.core.Response getSharedBinaryFile(@PathParam("said") String said,
			@PathParam("resourceID") String resourceID,@PathParam("saidReceiver") String saidReceiver) {

		logger.info("Gettin binary file shared by: "+saidReceiver+" with URI= " + resourceID);		

		DimeServiceAdapter adapter = null;
		//obtain the saidFor the sender, this should be already in place since it is a shared resource		
		String saidNameReceiver = credentialStore.getNameSaid(saidReceiver);
		//TODO: specify sender (needs to come from UI or needs to be stored somewhere)
		//String saidNameSender = credentialStore.getNameSaid(saidURISender);  <-- uncomment when saidURISender is available 
		//FIXME: remove following "hack" -> might fail if there are be more than one connection between two persons
		AccountCredentials ac = AccountCredentials.findAllByTargetUri(saidReceiver);
		String saidURISender = ac.getSource().getAccountURI();

		try {
			adapter = (DimeServiceAdapter) serviceGateway.getDimeServiceAdapter(saidNameReceiver);				
		} catch (ServiceNotAvailableException e) {
			logger.error("Resource cannot be retreived for trouble with the serviceGateway: " + e.getMessage());
			return javax.ws.rs.core.Response.serverError().build();
		}

		BinaryFile binary = null;
		//get the binary from the other PS
		try {
			logger.info("retreaving the binary file "+resourceID+" with shared from: "+saidNameReceiver+" to "+said);	
			binary = adapter.getBinary(saidReceiver,saidURISender, URLEncoder.encode(resourceID));		
		} catch (ServiceNotAvailableException e) {
			logger.error("Resource cannot be retreived for trouble with the serviceAdapter: " + e.getMessage());
			return javax.ws.rs.core.Response.serverError().build();
		}
		String mimeType =  binary.getType().substring(13);
		InputStream is = binary.getByteStream();

		//before returning, save the file
		InputStream localIS;
		try {
			fileManager.update(resourceID, is);
			localIS = fileManager.getBinaryStream(resourceID);
		} catch (IOException e) {
			logger.error("Downloaded resource cannot be stored. " + e.getMessage());
			return javax.ws.rs.core.Response.serverError().build();
		} catch (InfosphereException e) {
			logger.error("Downloaded resource cannot be stored. " + e.getMessage());
			return javax.ws.rs.core.Response.serverError().build();
		}

		return javax.ws.rs.core.Response.ok(localIS, mimeType).build();		

	}


	// FileManager

	@GET
	@Path("filemanager/{resourceID}")
	public javax.ws.rs.core.Response getFile(@PathParam("said") String said,
			@PathParam("resourceID") String resourceID) {
		logger.info("Gettin file with URI= " + resourceID);

		try {
			String decodedId = "";
			try {
				decodedId = URLDecoder.decode(resourceID, "UTF-8");
				resourceID = UriUtil.encodeUri(decodedId);
			} catch (UnsupportedEncodingException e) {
				logger.error("Resource URI "+resourceID+" is not correct: "+e.getMessage(), e);
				return javax.ws.rs.core.Response.serverError().build();
			}

			// gets the mimetype
			FileDataObject fdo = fileManager.get(resourceID);
			Node mimetype = ModelUtils.findObject(fdo.getModel(), fdo, NIE.mimeType);
			String mimeString = null;
			if (mimetype == null) {
				logger.error("Error retrieving file "+resourceID+": mime type not found.");
				mimeString = "application"; // if no mimetype assume binary
			} else {
				 try {
					mimeString = mimetype.asLiteral().getValue();
				} catch (ClassCastException e) {
					logger.warn("Could not retrieve mimetype. assuming binary.",e);
					mimeString = "application";
				}
				 
			}

			// gets file contents
			InputStream fileStream = fileManager.getBinaryStream(resourceID);

			return javax.ws.rs.core.Response.ok(fileStream, mimeString).build();

		} catch (InfosphereException e) {
			logger.warn("Could not find resource with URI: " + resourceID);
			return javax.ws.rs.core.Response.serverError().build();
		}
	}

	@POST
	@Path("filemanager")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@PathParam("said") String said,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("jsonld") String jsonld) {

		logger.info("Adding a file as a resource [" + jsonld + "]");

		if (uploadedInputStream != null && jsonld != null) {

			try {

				FileDataObject file = JSONLDUtils.deserialize(jsonld, FileDataObject.class);
				if(!file.hasPrivacyLevel()){
					file.setPrivacyLevel(AdvisoryConstants.DEFAULT_PRIVACY_LEVEL);
				}
				fileManager.add(file, uploadedInputStream);
				Resource fileResource = new Resource(file, said,fileManager.getMe().asURI());
				readIncludes(fileResource,file);

				PrivacyPreference pp = sharingManager.findPrivacyPreference(file.asURI().toString(), PrivacyPreferenceType.FILE);
				writeIncludes(fileResource,pp);
				Data<Resource> data = new Data<Resource>(0, 1, fileResource);

				return Response.ok(data);

			} catch (ModelRuntimeException e) {
				return Response.serverError(e.getMessage(), e);
			} catch (IOException e) {
				return Response.serverError(e.getMessage(), e);
			} catch (InfosphereException e) {
				return Response.serverError(e.getMessage(), e);
			}

		} else {
			return Response.badRequest("No correct parameters", null);
		}

	}


	//@uploadFile
	@POST
	@Path("@me/@uploadFile")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)

	public Response<Resource> simpleFileUpload(
			@PathParam("said") final String said,
			@QueryParam("qqfile") final String filename,
			@HeaderParam("X-Mime-Type") String mimeType,
			final InputStream input) {

		logger.info("called API method: POST /dime/rest/" + said + "/resource/@me/@uploadFile");

		File myFile = new File(filename);
		String uri = "urn:uuid:" + UUID.randomUUID();

		String metaData = createMetaString(myFile, uri, mimeType, null);//FIXME provide mimetype - from http header and hash from ???


		try{
			FileDataObject fdo = createNewFile(input, uri, metaData, "application/x-turtle");
			Data<Resource> resource = getResource(said, fdo.asURI().toString());
			return Response.ok(resource);


		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

	}

	// Crawler resources upload
	@POST
	@Path("crawler")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public javax.ws.rs.core.Response addCrawledFile(@PathParam("said") String said,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("uri") String uri, @FormDataParam("hash") String hash,
			@FormDataParam("metadata") String metadata, @FormDataParam("syntax") String syntax)
					throws IOException {

		logger.info("Adding crawled resource [uri=" + uri + ", hash=" + hash + "]");

		try{
			createNewFile(uploadedInputStream, uri, metadata, syntax);
		} catch (ModelRuntimeException e) {
			logger.error(e.getMessage(),e);
			return javax.ws.rs.core.Response.serverError().build();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			return javax.ws.rs.core.Response.serverError().build();
		} catch (InfosphereException e) {
			logger.error(e.getMessage(),e);
			return javax.ws.rs.core.Response.serverError().build();
		}
		return javax.ws.rs.core.Response.ok().build();
	}

	private FileDataObject createNewFile(InputStream uploadedInputStream,
			String uri, String metadata, String syntax) throws ModelRuntimeException, IOException, InfosphereException  {

		FileDataObject fdo = nfoFactory.createFileDataObject(uri);
		fdo.getModel().readFrom(IOUtils.toInputStream(metadata, "UTF-8"), Syntax.forMimeType(syntax));
		if (!fdo.hasPrivacyLevel()) {
			fdo.setPrivacyLevel(AdvisoryConstants.DEFAULT_PRIVACY_LEVEL);
		}
		//set creator 
		fdo.setCreator(fileManager.getMe());

		return fileManager.add(fdo, uploadedInputStream);		   

	}



	@PUT
	@Path("crawler/{resourceID}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public javax.ws.rs.core.Response updateCrawledFile(@PathParam("said") String said,
			@PathParam("resourceId") @Encoded String resourceId,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("uri") String uri, @FormDataParam("hash") String hash,
			@FormDataParam("metadata") String metadata, @FormDataParam("syntax") String syntax)
					throws IOException {

		logger.info("Updating crawled resource [uri=" + uri + ", hash=" + hash + "]");

		try {
			FileDataObject fdo = nfoFactory.createFileDataObject(uri);
			fdo.getModel().readFrom(IOUtils.toInputStream(metadata, "UTF-8"),
					Syntax.forMimeType(syntax));
			fileManager.update(fdo, uploadedInputStream);
		} catch (ModelRuntimeException e) {
			return javax.ws.rs.core.Response.serverError().build();
		} catch (IOException e) {
			return javax.ws.rs.core.Response.serverError().build();
		} catch (InfosphereException e) {
			return javax.ws.rs.core.Response.serverError().build();
		}

		return javax.ws.rs.core.Response.ok().build();
	}

	@DELETE
	@Path("crawler/{resourceID}")
	public javax.ws.rs.core.Response deleteCrawledFile(@PathParam("said") String said,
			@PathParam("resourceID") String resourceID) throws IOException {

		logger.info("Deleting crawled resource [uri=" + resourceID + "]");

		try {
			fileManager.remove(resourceID);
		} catch (InfosphereException e) {
			return javax.ws.rs.core.Response.serverError().build();
		}

		return javax.ws.rs.core.Response.ok().build();
	}

	// SharedTo

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@sharedTo/{agentId}/@all")
	public Response<SharedTo> getAllSharedResources(@PathParam("said") String said,
			@PathParam("agentId") String agentId) {

		Data<SharedTo> data = null;

		try {
			Collection<FileDataObject> fileDataObjects = sharingManager.getSharedFiles(agentId);
			data = new Data<SharedTo>(0, fileDataObjects.size(), fileDataObjects.size());

			for (FileDataObject fileDataObject : fileDataObjects) {
				SharedTo sharedTo = new SharedTo();

				sharedTo.setGuid(UUID.randomUUID().toString());
				sharedTo.setAgentId(agentId);
				if (personManager.isPerson(agentId)) {
					sharedTo.setAgentType("person");
				}
				if (personGroupManager.isPersonGroup(agentId)) {
					sharedTo.setAgentType("group");
				}

				String itemUri = fileDataObject.asURI().toString();
				if (itemUri.contains("/")) {
					itemUri = StringUtils.substringAfterLast(itemUri, "/");
				}
				itemUri = URLDecoder.decode(itemUri, "UTF-8");

				sharedTo.setItemId(itemUri);

				sharedTo.setType("resource");

				data.getEntries().add(sharedTo);
			}

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path(value = "@me/@all/shared")
	public Response<SharedTo> getAllSharedResourcesByQuery(@PathParam("said") String said,
			@QueryParam("sharedWithAgent") String agentId,
			@QueryParam("sharedWithService") String serviceId) {

		Data<SharedTo> data = null;

		try {
			Collection<FileDataObject> fileDataObjects = sharingManager.getSharedFiles(serviceId==null? agentId: serviceId);
			data = new Data<SharedTo>(0, fileDataObjects.size(), fileDataObjects.size());

			for (FileDataObject fileDataObject : fileDataObjects) {
				SharedTo sharedTo = new SharedTo();

				sharedTo.setGuid(UUID.randomUUID().toString());
				sharedTo.setAgentId(serviceId==null? agentId: serviceId);
				if(serviceId==null){
					if (personManager.isPerson(agentId))
						sharedTo.setAgentType("person");
					if (personGroupManager.isPersonGroup(agentId))
						sharedTo.setAgentType("group");
				}
				else{sharedTo.setAgentType("service");}

				String itemUri = fileDataObject.asURI().toString();
				if (itemUri.contains("/")) {
					itemUri = StringUtils.substringAfterLast(itemUri, "/");
				}
				itemUri = URLDecoder.decode(itemUri, "UTF-8");

				sharedTo.setItemId(itemUri);

				sharedTo.setType("resource");

				data.getEntries().add(sharedTo);
			}

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@sharedTo/{agentId}/{resourceId}")
	public Response<Resource> hasAccessToResource(@PathParam("said") String said,
			@PathParam("agentId") String agentId, @PathParam("resourceId") String resourceId) {

		Boolean access;
		Data<LinkedHashMap<String, Boolean>> returnData;


		try {

			access = sharingManager.hasAccessToFile(resourceId, agentId);

			LinkedHashMap<String, Boolean> result = new LinkedHashMap<String, Boolean>();
			result.put("access", access);
			returnData = new Data<LinkedHashMap<String, Boolean>>(0, 1, result);

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();
	}

	@POST
	@Path("@me/@sharedTo/{agentId}/{resourceId}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<TrustEntry> postShareDatabox(
			@PathParam("said") String said,
			@PathParam("agentId") String agentId, 
			@PathParam("resourceId") String resourceId,
			@QueryParam("adapt") String adapt,
			Request<SharedTo> request) {

		// adapt values: privacy, trust, none or ""
		if (adapt == null) {
			adapt = "";
		}
		//
		//	TrustRecommendation trustRecomendation = trustEngineInterface.processTrustForFiles(
		//		resourceId, adapt, null);
		//
		//	trustRecomendation.getConflictMap();
		//	trustRecomendation.getMessage();
		//
		//	if (trustRecomendation.updateModel()) {

		Collection<SharedTo> entries = request.getMessage().getData().getEntries();

		for (SharedTo sharedTo : entries){
			try {
				sharingManager.shareFile(resourceId, sharedTo.getSaidSender(), new String[]{agentId});								
			} catch (IllegalArgumentException e) {
				return Response.badRequest(e.getMessage(), e);
			} catch (InfosphereException e) {
				return Response.badRequest(e.getMessage(), e);
			} catch (Exception e) {

				return Response.serverError(e.getMessage(), e);
			}
		}

		return Response.ok();

		//} else {

		//		Map<String, TrustConflict> conflictMap = trustRecomendation.getConflictMap();
		//
		//		Data<TrustEntry> data = new Data<TrustEntry>();
		//
		//		Set<String> keySet = conflictMap.keySet();
		//		for (String key : keySet) {
		//
		//		TrustEntry jsonTrustConflict = new TrustEntry();
		//		TrustConflict trustConflict = conflictMap.get(key);
		//		jsonTrustConflict.setAgent_guid(trustConflict.getAgentId());
		//		jsonTrustConflict.setThing_guid(trustConflict.getThingId());
		//		jsonTrustConflict.setMessage(trustConflict.getMessage());
		//		data.addEntry(jsonTrustConflict);
		//
		//		}
		//		
		//		return Response.ok(data);
		//	}


	}


	//-------------------------------------
	//------- META for file-upload ---------
	//-------------------------------------


	private static final String P_SPACE = "	  ";
	private static final String I_SPACE = "	   ";
	private static final String LP = "\n";
	private static final String METADATA_PART_NAME_TAG = "metadata";
	private static final String PREFIX = "@prefix nfo:	 <http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#> .\n@prefix nie:	 <http://www.semanticdesktop.org/ontologies/2007/01/19/nie#> .\n\n";
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss.SSS");
	private static final SimpleDateFormat timeZoneFormatter = new SimpleDateFormat("Z");

	/**
	 * Format a Java date as a XSD dateTime. Example:
	 * 2011-09-16T13:32:54.000+01:00
	 *
	 * @param date
	 * @return
	 */
	private static String formatDate(Date date) {

		String timezone = timeZoneFormatter.format(date); // : -0800

		// convert timezone format to -08:00
		timezone = timezone.substring(0, 3) + ":" + timezone.substring(3);

		return dateFormatter.format(date) + "T" + timeFormatter.format(date) + timezone;
	}

	private void addToPayload(StringBuilder payload, String name, String value, String schema, boolean useQuotes, boolean finalEntry) {

		String quotes = useQuotes ? "\"" : "";

		payload.append(P_SPACE).append(name).append(I_SPACE).append(quotes).append(value).append(quotes).append(schema).append(finalEntry ? " ." : " ;").append(LP);
	}

	private String createMetaString(File myFile, String uriString, String mimeType, String hash) {


		java.net.URI uRI;
		try {
			uRI = new java.net.URI(uriString);
		} catch (URISyntaxException e) {
			logger.error("Uri not correct.",e);
			return null;
		}


		String container = "";
		if (myFile.getParent() != null) {
			container = new File(myFile.getParent()).toURI().toString();
		}
		String lastModified = formatDate(new Date(myFile.lastModified()));

		StringBuilder payload = new StringBuilder();

		payload.append(PREFIX);
		payload.append("<").append(uRI.toString()).append(">").append(LP);

		addToPayload(payload, "a", "nfo:FileDataObject", "", false, false);
		if (mimeType != null && mimeType.length() > 0) {
			addToPayload(payload, "nie:mimeType", mimeType, "", true, false);
		}else{
			addToPayload(payload, "nie:mimeType", "application/octet-stream", "", true, false);
		}
		addToPayload(payload, "nfo:belongsToContainer", container, "", true, false);
		addToPayload(payload, "nfo:fileLastModified", lastModified, "^^<http://www.w3.org/2001/XMLSchema#dateTime>", true, false);
		addToPayload(payload, "nfo:fileName", myFile.getName(), "", true, false);
		addToPayload(payload, "nfo:fileSize", myFile.length() + "", "^^<http://www.w3.org/2001/XMLSchema#long>", true, false);
		if (hash != null && hash.length() > 0) {
			addToPayload(payload, "nfo:hashValue", hash, "", true, true);
		}else{
			addToPayload(payload, "nfo:hashValue", "", "", true, true);
		}

		return payload.toString();
	}


	//---------------------------------
	//------------RESOURCE-------------
	//---------------------------------
	private Data<Resource> getResource(String said, String resourceId) throws InfosphereException, UnsupportedEncodingException {

		Data<Resource> result;

		List<URI> properties = Arrays.asList(payload);

		FileDataObject file = fileManager.get(resourceId, properties);
		Resource fileResource = new Resource(file, said, fileManager.getMe().asURI());
		PrivacyPreference pp = sharingManager.findPrivacyPreference(file.asURI().toString(), PrivacyPreferenceType.FILE);
		writeIncludes(fileResource,pp);
		resolveImageUrl(file, said, fileResource);
		fileResource.remove("nao:creator");
		result = new Data<Resource>(0, 1, fileResource);
		return result;
	}


	private void resolveImageUrl(FileDataObject file,String said,Resource fileResource) {

		if(fileResource.containsKey("imageUrl") && 
				!fileResource.get("imageUrl").toString().equals("")){
			String resourceId = fileResource.get("imageUrl").toString();			
			FileDataObject image = null;
			try {
				image = fileManager.get(file.toString());
			}
			catch (InfosphereException e) {
				logger.warn("The URI from the imageUrl "+resourceId+"cannot be retrevied",e);
			}
			if (image == null){
				try {
					image = fileManager.get(resourceId);
				}
				catch (InfosphereException e) {
					logger.warn("The URI from the imageUrl "+resourceId+"cannot be retrevied",e);
				}
			}
			if(image != null){
				
				String guid = UriUtil.decodeUri(image.asURI().toString());
				String encodedGuid = null;
				try {
					encodedGuid = URLEncoder.encode(guid, "UTF-8");
					fileResource.put("imageUrl", "/dime-communications/api/dime/rest/" + said
							+ "/resource/filemanager/" + encodedGuid);
				} catch (UnsupportedEncodingException e) {
					logger.warn("The Encoding is not suported for "+resourceId,e);
				}
			}
		}
		else{logger.info("File "+file.asURI().toString()+"has no imageURL");}
	}

}
