package eu.dime.ps.controllers.context;

import ie.deri.smile.vocabulary.DCON;



import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.context.IContextListener;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.contextprocessor.impl.RawContextNotification;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.LocationManager;
import eu.dime.ps.controllers.infosphere.manager.PlacemarkManager;
import eu.dime.ps.controllers.placeprocessor.PlaceKey;
import eu.dime.ps.controllers.placeprocessor.PlaceProcessor;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.model.pimo.Location;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;
import eu.dime.ps.storage.entities.Tenant;

public class LocationContextUpdater implements LiveContextUpdater, IContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(LocationContextUpdater.class);
	
	private URI dataSource = new URIImpl("urn:LocationContextUpdater");
	
	private IContextProcessor contextProcessor;
	private ConnectionProvider connectionProvider;
	private TenantManager tenantManager;
	private LocationManager locationManager;
	private PlaceProcessor placeProcessor;
	
	private Connection connection = null;
	private LiveContextService liveContextService = null;
	
	private List<IContextElement> rawContextQueue = new ArrayList<IContextElement>();
	
	public void setContextProcessor(IContextProcessor contextProcessor) {
		this.contextProcessor = contextProcessor;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}
	
	public void setPlaceProcessor(PlaceProcessor placeProcessor) {
		this.placeProcessor = placeProcessor;
	}
	
	public void init() {
		logger.debug("INIT Location Context Updater");
		try {
			this.contextProcessor.subscribeContext(Constants.ENTITY_ALL_USERS, Factory.createScope(Constants.SCOPE_CURRENT_PLACE),this);
			//dataSource = new URIImpl("urn:LocationContextUpdater");
		} catch (ContextException e) {
			logger.error(e.toString(),e);
		} 
	}

	@Override
	public void contextChanged(RawContextNotification notification) throws Exception {
		
		String name = notification.getName(); 
		logger.debug("Current Place Raw Context notification received: " + name);
		
		StringTokenizer tok = new StringTokenizer(name,",");
		IEntity entity = Factory.createEntity(tok.nextToken());
		IScope scope = Factory.createScope(tok.nextToken());
		
		Tenant t = new Tenant();
		t.setId(notification.getTenant());
		
		IContextDataset dataset;
		try {
			dataset = this.contextProcessor.getContext(t,entity, scope);
			if (!dataset.equals(IContextDataset.EMPTY_CONTEXT_DATASET)) {
				IContextElement[] ces = dataset.getContextElements(scope);
				if (ces != null && ces.length == 1) {
					this.rawContextQueue.add(ces[0]);
					update();
				}
			}
		} catch (ContextException e) {
			logger.error(e.toString());
		}
		
	}

	@Override
	public void update() {
		Iterator<IContextElement> it = this.rawContextQueue.iterator();
		while (it.hasNext()) {
			try {
				IContextElement ce = it.next();
				Tenant t = tenantManager.getByAccountName(ce.getEntity().getEntityIDAsString());
				TenantContextHolder.setTenant(t.getId());
				connection = connectionProvider.getConnection(t.getId().toString());
				liveContextService = connection.getLiveContextService();
				if (ce.getScope().getScopeAsString().equalsIgnoreCase(Constants.SCOPE_CURRENT_PLACE)) {
					String placeId = (String)ce.getContextData().getContextValue(Factory.createScope(Constants.SCOPE_CURRENT_PLACE_ID)).getValue().getValue();
					PlaceKey pk = new PlaceKey(t.getId(),placeId);
					String pmkId = placeProcessor.RDFPlaceReferences.get(pk);
					if (pmkId == null) {
						placeProcessor.getPlace(ce.getEntity().getEntityIDAsString(),placeId);
						pmkId = placeProcessor.RDFPlaceReferences.get(pk);
					}
					if (pmkId != null) {
						Location location = this.locationManager.getByPlacemarkId(pmkId);
						if (location != null) {
							LiveContextSession session = liveContextService.getSession(dataSource);
							session.set(SpaTem.class, DCON.currentPlace, location.asURI());
							// add place if I know category
						}
					}
				}
				TenantContextHolder.unset();
			} catch (RepositoryException e) {
				logger.error(e.toString(),e);
			} catch (ClassCastException e) {
				logger.error(e.toString(),e);
			} catch (LiveContextException e) {
				logger.error(e.toString(),e);
			} catch (InfosphereException e) {
				logger.error(e.toString(),e);
			}
			it.remove();
		}
	}

}
