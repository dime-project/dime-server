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

package eu.dime.ps.communications.requestbroker.controllers.context;

import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.rdf.impl.ResourceModelImpl;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DCON;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.openrdf.repository.RepositoryException;
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Entry;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dcon.State;
import eu.dime.ps.semantic.service.LiveContextService;

/**
 * TODO This is a temporary controller to be able to retrieve activities from the live
 * context to be shown in the Y2 review demo. For Y3 there will be a real API for the live
 * context and this will be refactored.
 * 
 * @author Ismael Rivera
 */
@Controller
@Path("/dime/rest/{said}/activity")
public class ActivityController {

	private ConnectionProvider connectionProvider;
	
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response<Activity> getMeAll() {

		try {
			Connection connection = connectionProvider.getConnection(TenantContextHolder.getTenant().toString());
			LiveContextService liveContextService = connection.getLiveContextService();
		
			List<Activity> entries = new ArrayList<Activity>();
		
			State state = liveContextService.get(State.class);
			ClosableIterator<Statement> currentIt = state.getModel().findStatements(state, DCON.currentActivity, Variable.ANY);
			while (currentIt.hasNext()) {
				Node object = currentIt.next().getObject();
				if (object instanceof URI) {
					Model activityModel = RDF2Go.getModelFactory().createModel().open();
					ModelUtils.fetch(liveContextService.getLiveContext(), activityModel, object.asURI());
					entries.add(new Activity(object.asURI(), activityModel));
				}
			}
			currentIt.close();
	
			return Response.ok(new Data<Activity>(0, entries.size(), entries));
	
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (RepositoryException e) {
			return Response.serverError("No access to data repository: "+e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

	}

	@javax.xml.bind.annotation.XmlRootElement
	@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
	class Activity extends Entry {

		@javax.xml.bind.annotation.XmlElement(name = "description")
		private String description;

		@javax.xml.bind.annotation.XmlElement(name = "caloriesExpended")
		private Integer caloriesExpended;
		
		@javax.xml.bind.annotation.XmlElement(name = "distanceCovered")
		private Integer distanceCovered;

		@javax.xml.bind.annotation.XmlElement(name = "duration")
		private Integer duration;

		public Activity(URI identifier, Model metadata) {
			super();
			this.guid = identifier.toString();
			this.type = "activity";
			this.imageUrl = null;
			this.items = new ArrayList<String>();
			
			ResourceModel rm = new ResourceModelImpl(metadata, identifier);
			this.name = rm.getString(NAO.prefLabel);
			this.imageUrl = rm.has(NAO.prefSymbol) ? rm.getURI(NAO.prefSymbol).toString() : null;
			this.lastModified = rm.has(DCON.recordedAt) ? Long.toString(rm.getDate(DCON.recordedAt).getTime()) : null;
			this.description = rm.getString(NAO.description);
			this.caloriesExpended = rm.getInteger(DCON.caloriesExpended);
			this.distanceCovered = rm.getInteger(DCON.distanceCovered);
			this.duration = rm.getInteger(DCON.duration);
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Integer getCaloriesExpended() {
			return caloriesExpended;
		}

		public void setCaloriesExpended(Integer caloriesExpended) {
			this.caloriesExpended = caloriesExpended;
		}

		public Integer getDistanceCovered() {
			return distanceCovered;
		}

		public void setDistanceCovered(Integer distanceCovered) {
			this.distanceCovered = distanceCovered;
		}

		public Integer getDuration() {
			return duration;
		}

		public void setDuration(Integer duration) {
			this.duration = duration;
		}
	
	}
	
}
