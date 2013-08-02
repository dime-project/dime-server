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


package eu.dime.ps.communications.ui;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
@Path("/ui")
public class StaticResourcesController {
    
    private static final Logger logger = LoggerFactory.getLogger(StaticResourcesController.class);
    
    private Tika tika;
    
    public StaticResourcesController(){
    	tika = new Tika();
    } 
    
    @GET
    @Path("/{subResources:.*}")
    public Response getGeneral(
    		@Context UriInfo uriInfo, 
    		@Context ServletContext sc) {
	
    	String path = uriInfo.getPath();
    	path = path.replaceFirst("ui/", "");
    	logger.debug("Called resource: " + path);
    	
    	InputStream file = sc.getResourceAsStream("/static/" + path);

    	if (file != null) {
        	String type;
    		try {
    			type = tika.detect(file);
    		} catch (IOException e) {
    			return Response.serverError().build();
    		}
    		return Response.ok(file, type).build();
		} else {
			file = sc.getResourceAsStream("/static/errors/404.html");
			return Response.status(Response.Status.NOT_FOUND).entity(file)
					.type(MediaType.TEXT_HTML).build();
		}	
    }
    
    @GET
    @Path("/{subResources:.*\\.css}")
    public Response getCss(
    		@Context UriInfo uriInfo, 
    		@Context ServletContext sc) {
    	
    	String path = uriInfo.getPath();
    	path = path.replaceFirst("ui/", "");
    	
    	InputStream file = sc.getResourceAsStream("/static/" + path);

    	if (file != null) {
        	String type = "text/css";
    		return Response.ok(file, type).build();
		}else {
			file = sc.getResourceAsStream("/static/errors/404.html");
			return Response.status(Response.Status.NOT_FOUND).entity(file)
			.type(MediaType.TEXT_HTML).build();
		}	 	
    }
    
    @GET
    @Path("/{subResources:.*\\.js}")
    public Response getJs(
    		@Context UriInfo uriInfo, 
    		@Context ServletContext sc) {
    	
    	String path = uriInfo.getPath();
    	path = path.replaceFirst("ui/", "");
    	
    	InputStream file = sc.getResourceAsStream("/static/" + path);

    	if (file != null) {
        	String type = "text/javascript";
    		return Response.ok(file, type).build();
		}else {
			file = sc.getResourceAsStream("/static/errors/404.html");
			return Response.status(Response.Status.NOT_FOUND).entity(file)
			.type(MediaType.TEXT_HTML).build();
		}	 	
    }
   
}
