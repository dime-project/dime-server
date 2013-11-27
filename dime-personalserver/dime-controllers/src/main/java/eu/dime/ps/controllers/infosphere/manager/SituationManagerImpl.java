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

package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.util.DateUtils;

/**
 * Implements {@link SituationManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * It only allows to store one situation for a given name (nao:prefLabel).
 *  
 * @author Ismael Rivera
 */
public class SituationManagerImpl extends InfoSphereManagerBase<Situation> implements SituationManager {

	@Override
	public Situation get(String situationId) throws InfosphereException {
		return get(situationId, new ArrayList<URI>(0));
	}

	@Override
	public boolean exist(String situationId) throws InfosphereException {
		URI situationUri = new URIImpl(situationId);
		return getTripleStore().containsStatements(situationUri, situationUri, RDF.type, DCON.Situation);
	}

	@Override
	public Situation get(String situationId, List<URI> properties)
			throws InfosphereException {
		if (exist(situationId)) {
			Situation situation = modelFactory.getDCONFactory().createSituation(situationId);
			situation.getModel().addAll(getTripleStore().getModel(new URIImpl(situationId)).iterator());

			// TODO filter properties with the parameter list

			return situation;
		} else {
			throw new InfosphereException("Cannot find situation "+situationId);
		}
	}
	
	@Override
	public Collection<Situation> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}

	@Override
	public Collection<Situation> getAll(List<URI> properties)
			throws InfosphereException {
			
			Collection<Resource> ids = getResourceStore().find(Situation.class).distinct().ids();
			Collection<Situation> results = new ArrayList<Situation>(ids.size());
			for (Resource id : ids) {
				results.add(get(id.toString(), properties));
			}
			
			return results;
	}
	
	@Override
	public Situation getByName(String name) throws InfosphereException {
		return getResourceStore().find(Situation.class).where(NAO.prefLabel).is(name).first();
	}
	
	@Override
	public void add(Situation situation) throws InfosphereException {
		validate(situation);
		
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		
		if (!situation.hasCreator()) {
			situation.setCreator(pimoService.getUserUri());
		}
		
		situation.setCreated(DateUtils.currentDateTimeAsLiteral());
		situation.setLastModified(DateUtils.currentDateTimeAsLiteral());

		try {
			resourceStore.create(situation.asURI(), situation);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("Could not add situation "+situation.asURI(), e);
		}
	}
	
	@Override
	public void update(Situation situation) throws InfosphereException {
		update(situation, false);
	}

	@Override
	public void update(Situation situation, boolean override) throws InfosphereException {
		validate(situation);
		
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		
		if (!situation.hasCreator()) {
			situation.setCreator(pimoService.getUserUri());
		}
		
		situation.setCreated(DateUtils.currentDateTimeAsLiteral());
		situation.setLastModified(DateUtils.currentDateTimeAsLiteral());

		try {
			resourceStore.update(situation.asURI(), situation, !override);
		} catch (NotFoundException e) {
			throw new InfosphereException("Could not update situation "+situation.asURI(), e);
		}
	}
	
	@Override
	public void activate(String situationId) throws InfosphereException {
		URI situationUri = new URIImpl(situationId);
		TripleStore tripleStore = getTripleStore();
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		try {
			float score = 0.3f;
			Node scoreLiteral = ModelUtils.findObject(resourceStore.getTripleStore(), situationUri, NAO.score);
			if (scoreLiteral != null) {
				score = Float.parseFloat(scoreLiteral.asDatatypeLiteral().getValue());
				score += score * 0.08;
			}
			tripleStore.addStatement(situationUri, pimoService.getUserUri(), DCON.hasSituation, situationUri);
			tripleStore.removeStatements(Variable.ANY, situationUri, NAO.score, Variable.ANY);
			tripleStore.addStatement(situationUri, situationUri, NAO.score, new DatatypeLiteralImpl(Float.toString(score), XSD._float));
			Resource spatem = ModelUtils.findSubject(tripleStore.getModel(situationUri), RDF.type, DCON.SpaTem);
			if (spatem == null) {
				spatem = new URIImpl("urn:uuid:" + UUID.randomUUID());
				tripleStore.addStatement(situationUri, spatem, RDF.type, DCON.SpaTem);
			}
			if (!tripleStore.containsStatements(situationUri, Variable.ANY, RDF.type, PIMO.Location)) {
				URI uuid = new URIImpl("urn:uuid:" + UUID.randomUUID());
				tripleStore.addStatement(situationUri, spatem, DCON.currentPlace, uuid);
				tripleStore.addStatement(situationUri, uuid, RDF.type, PIMO.Location);
				String hex = "4c7578656d626f757267";
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < hex.length() - 1; i += 2)
					sb.append((char) Integer.parseInt(hex.substring(i, (i + 2)), 16));
				tripleStore.addStatement(situationUri, uuid, NAO.prefLabel, new PlainLiteralImpl(sb.toString()));
				tripleStore.addStatement(situationUri, uuid, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
				uuid = new URIImpl("urn:uuid:" + UUID.randomUUID());
				hex = "4575726f666f72756d";
				tripleStore.addStatement(situationUri, spatem, DCON.currentPlace, uuid);
				tripleStore.addStatement(situationUri, uuid, RDF.type, PIMO.Location);
				sb = new StringBuilder();
				for (int i = 0; i < hex.length() - 1; i += 2)
					sb.append((char) Integer.parseInt(hex.substring(i, (i + 2)), 16));
				tripleStore.addStatement(situationUri, uuid, NAO.prefLabel, new PlainLiteralImpl(sb.toString()));
				tripleStore.addStatement(situationUri, uuid, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			}
			Situation situation = resourceStore.get(situationUri, Situation.class);
			BroadcastManager.getInstance().sendBroadcast(new Event(resourceStore.getName(), Event.ACTION_RESOURCE_MODIFY, situation));
		} catch (NotFoundException e) {
			throw new InfosphereException("Situation "+situationId+" couldn't be activated:"+e.getMessage(), e);
		}
	}

	@Override
	public void deactivate(String situationId) throws InfosphereException {
		URI situationUri = new URIImpl(situationId);
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		try {
			resourceStore.removeValue(situationUri, pimoService.getUserUri(), DCON.hasSituation, situationUri);
		} catch (NotFoundException e) {
			throw new InfosphereException("Situation "+situationId+" couldn't be deactivated:"+e.getMessage(), e);
		}
	}
	
	private void validate(Situation situation) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		String name = situation.getPrefLabel();
		
		if (name == null) {
			throw new InfosphereException("Name (nao:prefLabel) is undefined, it must be specified and it should contain the name of the Situation.");
		}
		
		Collection<Resource> ids = resourceStore.find(Situation.class).distinct().where(NAO.prefLabel).is(name).ids();
		if (ids.size() > 1) {
			throw new InfosphereException("There is more than 1 situation using the name (nao:prefLabel) '"+name+"'.");
		} else if (ids.size() == 1) {
			if (!ids.contains(situation.asResource())) {
				throw new InfosphereException("The name (nao:prefLabel) '"+name+"' is already in use.");
			}
		}
	}

}