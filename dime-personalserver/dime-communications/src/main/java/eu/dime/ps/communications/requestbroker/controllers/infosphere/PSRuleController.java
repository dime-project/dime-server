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

import ie.deri.smile.rules.transformer.RuleInstance;

import java.util.Collection;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.RuleManager;
import eu.dime.ps.semantic.model.drmo.Rule;

/**
 * Dime REST API Controller for rules
 */
@Controller
@Path("/dime/rest/{said}/rule")
public class PSRuleController implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSRuleController.class);

	private RuleManager ruleManager;

	Model drmoModel = RDF2Go.getModelFactory().createModel().open();

	@Autowired
	public void setRuleManager(RuleManager ruleManager) {
		this.ruleManager = ruleManager;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response<RuleInstance> getAll(
			@PathParam("said") String said) {

		logger.info("GET /dime/rest" + said +"/rule/@me/@all");
		
		Data<RuleInstance> response;
		try {
			Collection<Rule> rules = ruleManager.getAll();
			response = new Data<RuleInstance>(0, rules.size(), rules.size());
			for (Rule rule : rules) {
				response.getEntries().add(new RuleInstance(rule.asURI(), rule.getModel(), drmoModel));
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(response);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{ruleId}")
	public Response<RuleInstance> getById(
			@PathParam("said") String said,
			@PathParam("ruleId") String ruleId) {

		logger.info("GET /dime/rest" + said +"/rule/"+ruleId);
		
		Data<RuleInstance> response;

		try {
			Rule rule = ruleManager.get(ruleId);
			RuleInstance dto = new RuleInstance(rule.asURI(), rule.getModel(), drmoModel);
			response = new Data<RuleInstance>(0, 1, dto);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}
		
		return Response.ok(response);
	}

	@POST
	@Path("/@me")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<RuleInstance> create(
			Request request,
			@PathParam("said") String said) {

		logger.info("POST /dime/rest" + said +"/rule/@me");

		Data<RuleInstance> data, response;

		try {
			RequestValidator.validateRequest(request);
			data = request.getMessage().getData();
			RuleInstance dto = data.getEntries().iterator().next();
			
			// replace guid with a new unique URI
			dto.setGuid(new URIImpl("urn:uuid:" + UUID.randomUUID()));
			
			Model ruleModel = dto.toRDF();
			Rule rule = new Rule(ruleModel, dto.getGuid(), false);
			ruleManager.add(rule);
			
			response = new Data<RuleInstance>(0, 1, dto);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}
		
		return Response.ok(response);
	}

}
