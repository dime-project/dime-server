package eu.dime.ps.controllers.context;

import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DDO;
import ie.deri.smile.vocabulary.NAO;

import java.util.List;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.ConnectionBase;
import eu.dime.ps.controllers.infosphere.manager.SituationManager;
import eu.dime.ps.gateway.service.external.oauth.FitbitServiceAdapter;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dcon.Aspect;
import eu.dime.ps.semantic.model.dcon.Connectivity;
import eu.dime.ps.semantic.model.dcon.Peers;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.model.dcon.State;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.exception.LiveContextException;
import eu.dime.ps.semantic.service.impl.PimoService;

public class LiveContextManager extends ConnectionBase {

	private SituationManager situationManager;
	private final ModelFactory modelFactory = new ModelFactory();
	
	private static final DatatypeLiteral DEFAULT_ASPECT_WEIGHT = new DatatypeLiteralImpl("0.7", XSD._double);
	private static final DatatypeLiteral DEFAULT_ELEMENT_WEIGHT = new DatatypeLiteralImpl("0.7", XSD._double);
	
	public void setSituationManager(SituationManager situationManager) {
		this.situationManager = situationManager;
	}
	
	/**
	 * Proxy method for {@link LiveContextService#get(URI, Class)}.
	 */
	public <T extends Aspect> T get(URI resourceUri, Class<T> returnType)
			throws NotFoundException {
		return null;
	}

	/**
	 * Proxy method for {@link LiveContextService#get(Class)}.
	 */
	public <T extends Aspect> T get(Class<T> returnType) {
		return null;
	}

	/**
	 * Proxy method for {@link LiveContextService#getAspects()}.
	 */
	public List<URI> getAspects() {
		return null;
	}
	
	/**
	 * Proxy method for {@link LiveContextService#getElements()}.
	 */
	public List<URI> getElements() {
		return null;
	}

	/**
	 * Exports the current live context as an instance of Situation
	 * 
	 * @param name or label to assign to the situation
	 * @return situation instance containing all the information of
	 *         the live context graph
	 */
	public Situation saveAsSituation(String name) throws LiveContextException, InfosphereException {
		LiveContextService lcs = getLiveContextService();
		
		Situation situation = modelFactory.getDCONFactory().createSituation();
		situation.setPrefLabel(name);
	
		if ("work meeting".equals(name)
				|| "exercising".equals(name)) {
			try {
				predefine(name, situation);
			} catch (InfosphereException e) {
				throw new LiveContextException("Could not save situation '"+name+"'", e);
			}
		} else {
			// add all resources from live context model to the situation model
			Model lcModel = lcs.getLiveContext();
			situation.getModel().addAll(lcModel.iterator());
			
			// add default weights to aspects and elements
			for (URI aspect : lcs.getAspects())
				situation.getModel().addStatement(aspect, DCON.weight, DEFAULT_ASPECT_WEIGHT);
			for (URI element : lcs.getElements())
				situation.getModel().addStatement(element, DCON.weight, DEFAULT_ELEMENT_WEIGHT);
		}
		
		try {
			situationManager.add(situation);
		} catch (InfosphereException e) {
			throw new LiveContextException("Cannot save live context as situation '"+name+"': "+e.getMessage(), e);
		}
		
		return situation;
	}
	
	// this method generates pre-define situations to be used on the 2nd year review demo
	private void predefine(String name, Situation situation) throws LiveContextException, InfosphereException {
		PimoService pimo = getPimoService();
		LiveContextService lcs = getLiveContextService();
		Model sModel = situation.getModel();
		
		if ("work meeting".equals(name)) {
			
			// running
			URI running = new URIImpl("http://www.semanticdesktop.org/ontologies/2011/10/05/dpo#Running");
			State state = lcs.get(State.class);
			sModel.addStatement(state, RDF.type, DCON.State);
			sModel.addStatement(state, DCON.weight, new DatatypeLiteralImpl("0.7", XSD._double));
			sModel.addStatement(state, DCON.currentActivity, running);
			sModel.addStatement(running, DCON.isExcluder, new DatatypeLiteralImpl("true", XSD._boolean));

			// wifi access point of the meeting room
			URI conraduser = new URIImpl("urn:ssid:conraduser");
			sModel.addStatement(conraduser, RDF.type, DDO.WiFi);
			sModel.addStatement(conraduser, DCON.recordedBy, new URIImpl("urn:WiFiContextUpdater"));
			sModel.addStatement(conraduser, DDO.macAddress, new PlainLiteralImpl("0022CF13EE4D"));
			sModel.addStatement(conraduser, DCON.weight, new DatatypeLiteralImpl("0.9", XSD._double));
			sModel.addStatement(conraduser, DCON.isRequired, new DatatypeLiteralImpl("true", XSD._boolean));

			Connectivity connectivity = lcs.get(Connectivity.class);
			sModel.addStatement(connectivity, RDF.type, DCON.Connectivity);
			sModel.addStatement(connectivity, DCON.weight, new DatatypeLiteralImpl("0.99", XSD._double));
			sModel.addStatement(connectivity, DCON.connection, conraduser);
			
			// during working hours
			URI workingHours = new URIImpl(pimo.getPimoUri() + ":WorkingHours");
			SpaTem staTem = lcs.get(SpaTem.class);
			sModel.addStatement(staTem, RDF.type, DCON.SpaTem);
			sModel.addStatement(staTem, DCON.weight, new DatatypeLiteralImpl("0.8", XSD._double));
			sModel.addStatement(staTem, DCON.currentTime, workingHours);
			sModel.addStatement(workingHours, DCON.weight, new DatatypeLiteralImpl("0.99", XSD._double));

			// Simon, Ismael, Andreas, Cristina, Rafa, Sophie, Marcel should be around
			Person simsce = pimo.find(Person.class).where(NAO.prefLabel).eq("Simon Scerri").first();
			Person ismriv = pimo.find(Person.class).where(NAO.prefLabel).eq("Ismael Rivera").first();
			Person andsch = pimo.find(Person.class).where(NAO.prefLabel).eq("Andreas Schuller").first();
			Person rafgim = pimo.find(Person.class).where(NAO.prefLabel).eq("Rafa Gimenez").first();
			Person crifra = pimo.find(Person.class).where(NAO.prefLabel).eq("Cristina Fra").first();
			Person simthi = pimo.find(Person.class).where(NAO.prefLabel).eq("Simon Thiel").first();
			Person sopwro = pimo.find(Person.class).where(NAO.prefLabel).eq("Sophie Wrobel").first();
			Person marheu = pimo.find(Person.class).where(NAO.prefLabel).eq("Marcel Heupel").first();
			Peers peers = lcs.get(Peers.class);
			sModel.addStatement(peers, RDF.type, DCON.Peers);
			sModel.addStatement(peers, DCON.weight, new DatatypeLiteralImpl("0.88", XSD._double));
			if (simsce != null && !simsce.equals(pimo.getUserUri())) {
				sModel.addStatement(peers, DCON.nearbyPerson, simsce);
				sModel.addStatement(simsce, DCON.weight, new DatatypeLiteralImpl("0.92", XSD._double));
				sModel.addStatement(simsce, DCON.recordedBy, new URIImpl("urn:PeersContextUpdater"));
			}
			if (ismriv != null && !ismriv.equals(pimo.getUserUri())) {
				sModel.addStatement(peers, DCON.nearbyPerson, ismriv);
				sModel.addStatement(ismriv, DCON.weight, new DatatypeLiteralImpl("0.91", XSD._double));
				sModel.addStatement(ismriv, DCON.recordedBy, new URIImpl("urn:PeersContextUpdater"));
			}
			if (andsch != null && !andsch.equals(pimo.getUserUri())) {
				sModel.addStatement(peers, DCON.nearbyPerson, andsch);
				sModel.addStatement(andsch, DCON.weight, new DatatypeLiteralImpl("0.78", XSD._double));
				sModel.addStatement(andsch, DCON.recordedBy, new URIImpl("urn:PeersContextUpdater"));
			}
			if (rafgim != null && !rafgim.equals(pimo.getUserUri())) {
				sModel.addStatement(peers, DCON.nearbyPerson, rafgim);
				sModel.addStatement(rafgim, DCON.weight, new DatatypeLiteralImpl("0.63", XSD._double));
				sModel.addStatement(rafgim, DCON.recordedBy, new URIImpl("urn:PeersContextUpdater"));
			}
			if (crifra != null && !crifra.equals(pimo.getUserUri())) {
				sModel.addStatement(peers, DCON.nearbyPerson, crifra);
				sModel.addStatement(crifra, DCON.weight, new DatatypeLiteralImpl("0.67", XSD._double));
				sModel.addStatement(crifra, DCON.recordedBy, new URIImpl("urn:PeersContextUpdater"));
			}
			if (simthi != null && !simthi.equals(pimo.getUserUri())) {
				sModel.addStatement(peers, DCON.nearbyPerson, simthi);
				sModel.addStatement(simthi, DCON.weight, new DatatypeLiteralImpl("0.81", XSD._double));
				sModel.addStatement(simthi, DCON.recordedBy, new URIImpl("urn:PeersContextUpdater"));
			}
			if (sopwro != null && !sopwro.equals(pimo.getUserUri())) {
				sModel.addStatement(peers, DCON.nearbyPerson, sopwro);
				sModel.addStatement(sopwro, DCON.weight, new DatatypeLiteralImpl("0.74", XSD._double));
				sModel.addStatement(sopwro, DCON.recordedBy, new URIImpl("urn:PeersContextUpdater"));
			}
			if (marheu != null && !marheu.equals(pimo.getUserUri())) {
				sModel.addStatement(peers, DCON.nearbyPerson, marheu);
				sModel.addStatement(marheu, DCON.weight, new DatatypeLiteralImpl("0.72", XSD._double));
				sModel.addStatement(marheu, DCON.recordedBy, new URIImpl("urn:PeersContextUpdater"));
			}

		} else if ("office break".equals(name)) {

			// during working hours
			URI workingHours = new URIImpl(pimo.getPimoUri() + ":WorkingHours");
			SpaTem staTem = lcs.get(SpaTem.class);
			sModel.addStatement(staTem, RDF.type, DCON.SpaTem);
			sModel.addStatement(staTem, DCON.weight, new DatatypeLiteralImpl("0.8", XSD._double));
			sModel.addStatement(staTem, DCON.currentTime, workingHours);
			sModel.addStatement(workingHours, DCON.weight, new DatatypeLiteralImpl("0.99", XSD._double));

			// wifi access point of the cafeteria
			URI epiknet = new URIImpl("urn:ssid:epik-net");
			sModel.addStatement(epiknet, RDF.type, DDO.WiFi);
			sModel.addStatement(epiknet, DCON.recordedBy, new URIImpl("urn:WiFiContextUpdater"));
			sModel.addStatement(epiknet, DDO.macAddress, new PlainLiteralImpl("0021910B4B10"));
			sModel.addStatement(epiknet, DCON.weight, new DatatypeLiteralImpl("0.8", XSD._double));
			sModel.addStatement(epiknet, DCON.isRequired, new DatatypeLiteralImpl("true", XSD._boolean));

			Connectivity connectivity = lcs.get(Connectivity.class);
			sModel.addStatement(connectivity, RDF.type, DCON.Connectivity);
			sModel.addStatement(connectivity, DCON.weight, new DatatypeLiteralImpl("0.99", XSD._double));
			sModel.addStatement(connectivity, DCON.connection, epiknet);
			
		} else if ("exercising".equals(name)) {
			Account fitbit = pimo.find(Account.class).where(DAO.accountType).eq(FitbitServiceAdapter.NAME).first();

			// running
			URI running = new URIImpl("http://www.semanticdesktop.org/ontologies/2011/10/05/dpo#Running");
			State state = lcs.get(State.class);
			sModel.addStatement(state, RDF.type, DCON.State);
			sModel.addStatement(state, DCON.weight, new DatatypeLiteralImpl("0.92", XSD._double));
			sModel.addStatement(state, DCON.currentActivity, running);
			sModel.addStatement(running, DCON.weight, new DatatypeLiteralImpl("0.95", XSD._double));
			if (fitbit != null)
				sModel.addStatement(running, DCON.recordedBy, fitbit.asURI());
			
			// on a weekday
			URI weekday = new URIImpl(pimo.getPimoUri() + ":Weekday");
			SpaTem staTem = lcs.get(SpaTem.class);
			sModel.addStatement(staTem, RDF.type, DCON.SpaTem);
			sModel.addStatement(staTem, DCON.weight, new DatatypeLiteralImpl("0.8", XSD._double));
			sModel.addStatement(staTem, DCON.currentTime, weekday);
			sModel.addStatement(weekday, DCON.weight, new DatatypeLiteralImpl("0.99", XSD._double));
		}
	}
	
}
