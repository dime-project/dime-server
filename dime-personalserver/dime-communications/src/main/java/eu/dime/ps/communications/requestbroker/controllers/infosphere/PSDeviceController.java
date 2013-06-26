package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DDO;
import ie.deri.smile.vocabulary.NAO;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Device;
import eu.dime.commons.dto.Entry;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.exception.DimeException;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.DeviceManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.DDOFactory;

/**
 * Dime REST API Controller for a context features
 * 
 * @author mplanaguma (BDCT)
 * 
 */
@Controller
@Path("/dime/rest/{said}/device")
public class PSDeviceController implements APIController {

    private DeviceManager deviceManager;

    @Autowired
    public void setDeviceManager(DeviceManager deviceManager) {
	this.deviceManager = deviceManager;
    }


    // /device/

    @POST
    @Path("/@me")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<Device> postNewDevice(@PathParam("said") String said, Request request) {

	Data<Device> data, returnData;

	try {
	    RequestValidator.validateRequest(request);
	    data = request.getMessage().getData();
	    Device dto = data.getEntries().iterator().next();
	    eu.dime.ps.semantic.model.ddo.Device semanticDevice = DTO2Semantic(dto);
	    deviceManager.add(semanticDevice);
	    dto = semantic2DTO(semanticDevice);
	    returnData = new Data<Device>(0, 1, dto);
	    return Response.ok(returnData);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}
    }

    @POST
    @Path("/@me/{deviceId}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<Device> updateDevice(@PathParam("said") String said,
	    @PathParam("deviceId") String deviceId, Request<Device> request) {

	Data<Resource> data;
	Data<Device> returnData;

	try {
	    RequestValidator.validateRequest(request);

	    Device device = request.getMessage().getData().getEntries().iterator().next();
	    eu.dime.ps.semantic.model.ddo.Device semanticDevice = DTO2Semantic(device);
	    deviceManager.update(semanticDevice);
	    device = semantic2DTO(semanticDevice);
	    returnData = new Data<Device>(0, 1, device);
	    return Response.ok(returnData);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/@me/@all")
    public Response<Entry> getAllCurrentUserDevices(@PathParam("said") String said)
	    throws DimeException {

	Data<Entry> returnData;
	try {
	    String personId = deviceManager.getMe().asURI().toString();
	    Collection<eu.dime.ps.semantic.model.ddo.Device> semanticDevices = deviceManager
		    .getAllOwnedBy(personId);
	    returnData = new Data<Entry>(0, semanticDevices.size(), semanticDevices.size());
	    for (eu.dime.ps.semantic.model.ddo.Device semanticDevice : semanticDevices) {
		returnData.getEntries().add(semantic2DTO(semanticDevice));
	    }
	    return Response.ok(returnData);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/@me/{deviceId}")
    public Response<Entry> getDevice(@PathParam("said") String said,
	    @PathParam("deviceId") String deviceId) throws DimeException {

	Data<Entry> returnData = new Data<Entry>(0, 0, 0);

	try {
	    eu.dime.ps.semantic.model.ddo.Device semanticDevice = deviceManager.get(deviceId);
	    Device dto = semantic2DTO(semanticDevice);
	    returnData.addEntry(dto);
	    return Response.ok(returnData);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/@me/{deviceId}")
    public Response deleteDevice(@PathParam("said") String said,
	    @PathParam("deviceId") String deviceId) {

	try {
	    deviceManager.remove(deviceId);
	    return Response.ok();
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

    }

    private Device semantic2DTO(eu.dime.ps.semantic.model.ddo.Device semanticDevice) throws ClassCastException, InfosphereException {
	Device device = new Device();
	if (semanticDevice != null) {
	    Resource deviceResource = new Resource(semanticDevice,deviceManager.getMe().asURI());
	    // guid
	    device.setGuid((String) deviceResource.get("guid"));
	    // lastmodified
	    Calendar calendar = semanticDevice.getLastModified();
	    if (calendar != null) {
		device.setLastModified(String.valueOf(semanticDevice.getLastModified()
			.getTimeInMillis()));
	    }
	    // userId
	    org.ontoware.rdf2go.model.node.Resource subject = ModelUtils.findSubject(
		    semanticDevice.getModel(), DDO.owns, semanticDevice.asResource());
	    if (subject != null) {
		device.setUserId(subject.asURI().toString());
	    }
	    // imageUrl
	    if (semanticDevice.getPrefSymbol() != null)
		device.setImageUrl(semanticDevice.getPrefSymbol().toString());

	    ClosableIterator<String> deviceNameIterator = semanticDevice.getAllDeviceName();
	    if (deviceNameIterator != null && deviceNameIterator.hasNext()) {
		device.setName(semanticDevice.getAllDeviceName().next());
	    }
	    // ddo:deviceIdentifier
	    Node node = ModelUtils.findObject(semanticDevice.getModel(),
		    semanticDevice.asResource(), DDO.deviceIdentifier);
	    if (node != null) {
		device.setDeviceIdentifier(node.asLiteral().getValue());
	    }

	    // TODO : how to obtain versionNumber from semantic object??
	    // device.setVersionNumber("");
	    // TODO: how to obtain clientType from semantic object??
	    // device.setClientType("");

	}
	return device;
    }

    private eu.dime.ps.semantic.model.ddo.Device DTO2Semantic(Device device) {
	DDOFactory ddoFactory = new DDOFactory();
	eu.dime.ps.semantic.model.ddo.Device semanticDevice = null;
	if (device.getGuid() != null) {
	    // updating an existing device
	    semanticDevice = ddoFactory.createDevice(device.getGuid());
	} else {
	    // creating a new device
	    semanticDevice = ddoFactory.createDevice();
	}

	Model model = semanticDevice.getModel();
	// semantic.devicename == dto.name
	if (device.getName() != null)
	    semanticDevice.setDeviceName(device.getName());
	// deviceIdentifier
	if (device.getDeviceIdentifier() != null)
	    model.addStatement(semanticDevice.getResource().asURI(), DDO.deviceIdentifier,
		    (String) device.getDeviceIdentifier());
	// imageUrl
	if (device.getImageUrl() != null)
	    model.addStatement(semanticDevice.getResource().asURI(), NAO.prefSymbol,
		    (String) device.getImageUrl());
	// userId
	if (device.getUserId() != null)
	    model.addStatement(new URIImpl(device.getUserId()), DDO.owns, semanticDevice
		    .getResource().asURI());
	// lastModified
	if (device.getLastModified() != null && device.getLastModified().trim().length() > 0) {
	    try {
		long milis = Long.valueOf(device.getLastModified());
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(milis);
		semanticDevice.setLastModified(calendar);
	    } catch (Exception e) {

	    }
	}

	// TODO : how to set clientType value in semantic object?
	// TODO : how to set versionNumber value in semantic object?

	return semanticDevice;
    }

}
