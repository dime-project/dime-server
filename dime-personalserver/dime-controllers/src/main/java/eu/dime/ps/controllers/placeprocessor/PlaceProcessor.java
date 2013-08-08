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

package eu.dime.ps.controllers.placeprocessor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.dto.Place;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.ContextValueMap;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PlacemarkManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.noauth.PlaceServiceAdapter;
import eu.dime.ps.gateway.service.noauth.SocialRecommenderAdapter;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nfo.Placemark;
import eu.dime.ps.storage.entities.Tenant;
//import eu.dime.ps.gateway.service.noauth.YMServiceAdapter;
//import eu.dime.ps.gateway.service.noauth.YMUtils;

public class PlaceProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(PlaceProcessor.class);
	
	private ServiceGateway serviceGateway;
	private SocialRecommenderAdapter socialRecService;
	private PlaceParser placeParser = new PlaceParser();
	private ModelFactory modelFactory = new ModelFactory();
	
	private AccountManager accountManager;
	private PlacemarkManager placemarkManager;
	private NotifierManager notifierManager;
	
	private IContextProcessor contextProcessor = null;
	private IScope positionScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION);
	private IScope latitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LATITUDE);
	private IScope longitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LONGITUDE);
	
	//private YMServiceAdapter ymServiceAdapter;
	private PlaceServiceAdapter placeServiceAdapter;
	
	private String social_rec_CID = "DIME";
	private String social_rec_catalog = "YM_FOOD";
	protected static double NO_VOTE = -1;
	
	public HashMap<PlaceKey,String> RDFPlaceReferences = new HashMap<PlaceKey, String>();
	
	public void setPlacemarkManager(PlacemarkManager placemarkManager) {
		this.placemarkManager = placemarkManager;
	}
	
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
	
	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}
	
	public void setContextProcessor(IContextProcessor contextProcessor) {
		this.contextProcessor = contextProcessor;
	}
	
	private Account retrieveAccount(String serviceName) {
		if (this.accountManager != null && this.serviceGateway != null) {
			if (serviceName.equalsIgnoreCase(SocialRecommenderAdapter.adapterName)) {
				Collection<Account> socRecAccts = null;
				try {
					socRecAccts = this.accountManager.getAllByType(serviceName);
				} catch (InfosphereException e) {
					logger.error(e.getMessage(),e);
					return null;
				}
				if (socRecAccts != null && socRecAccts.size() > 0) {
					Iterator<Account> it = socRecAccts.iterator();
					return it.next();
				}
			}
			// TODO @YM add here retrieval of proper YM account
			
			// TODO @Roman: correct? 
			else if (serviceName.equalsIgnoreCase(PlaceServiceAdapter.adapterName)) {
				Collection<Account> ymAccounts = null;
				try {
					ymAccounts = this.accountManager.getAllByType(serviceName);
				} catch (InfosphereException e) {
					logger.error(e.getMessage(),e);
					return null;
				}
				if (ymAccounts != null && ymAccounts.size() > 0) {
					Iterator<Account> it = ymAccounts.iterator();
					return it.next();
				}
				
			}
		} 
		return null;
	}
	
	private void setServiceReference(Account account, String serviceName, Tenant localTenant) {
		if (serviceName.equalsIgnoreCase(SocialRecommenderAdapter.adapterName)) {
			try {
				this.socialRecService = (SocialRecommenderAdapter) this.serviceGateway.getServiceAdapter(account.asURI().toString(), localTenant);
			} catch (ServiceNotAvailableException e) {
				logger.error(e.getMessage(),e);
				this.socialRecService = null;
			} catch (ServiceAdapterNotSupportedException e) {
				logger.error(e.getMessage(),e);
				this.socialRecService = null;
			} catch (ClassCastException e) {
				logger.error(e.getMessage(),e);
				this.socialRecService = null;
			} 
			
		} 
		// TODO @YM add here retrieval of proper YM account
                
                
                
		// TODO @Roman: correct? 
		else if (serviceName.equalsIgnoreCase(PlaceServiceAdapter.adapterName)) {
			try {
				this.placeServiceAdapter = (PlaceServiceAdapter) this.serviceGateway.getServiceAdapter(account.asURI().toString(), localTenant);
				if(placeServiceAdapter == null) {
					throw new ServiceNotAvailableException("Check if the service adapter YellowMap has been registered.");
				}
			} catch (ServiceNotAvailableException e) {
				logger.error(e.getMessage(),e);
				this.placeServiceAdapter = null;
			} catch (ServiceAdapterNotSupportedException e) {
				logger.error(e.getMessage(),e);
				this.placeServiceAdapter = null;
			} catch (ClassCastException e) {
				logger.error(e.getMessage(),e);
				this.placeServiceAdapter = null;
			}
		}

	}
	
	
	public List<Place> getPlaces(String mainSaid, double latitude, double longitude,
			double radius, List<String> categories) throws ServiceNotAvailableException {
		
		logger.debug("Get places request received");

        Tenant tenant = TenantHelper.getCurrentTenant();

		List<Place> places = new ArrayList<Place>();
                
		
		Account srAccount = retrieveAccount(SocialRecommenderAdapter.adapterName);
		if (srAccount != null) {
			setServiceReference(srAccount, SocialRecommenderAdapter.adapterName, tenant);
		}
		
		// TODO @YM retrieve account and set service reference of YMAdapter
		// TODO from Roman: is this correct?
		Account ymAccount = retrieveAccount(PlaceServiceAdapter.adapterName);
		if (ymAccount != null) {
			setServiceReference(ymAccount, PlaceServiceAdapter.adapterName, tenant);
		}
		
		// Note: [TI] has been decided that no default position will be used if the API is called without 
		// coordinates and if user's position is not available in raw context cache
		
		// Stuttgart coordinates (Nobelstrasse 12)
//		double defaultLat = 48.740035;
//		double defaultLon = 9.097087;
		double defaultRad = 30000;
		
		if ((latitude == 0) && (longitude == 0)) {
			// If called without parameters:
			// 1. try to retrieve user's position from raw context
			
			IEntity entity = Factory.createEntity(mainSaid);
			IContextDataset position = null;
			
			try {
				position = contextProcessor.getContext(tenant, entity, positionScope);
                                
			} catch (ContextException e) {
				logger.error(e.toString(),e);
			}
			
			if (position != null && !position.equals(IContextDataset.EMPTY_CONTEXT_DATASET)) {
				
				IContextElement[] ces = position.getContextElements(positionScope);
				if (ces != null && ces.length > 0) {
					IContextElement ce = ces[0];
					latitude = (Double)ce.getContextData().getContextValue(latitudeScope).getValue().getValue();
					longitude = (Double)ce.getContextData().getContextValue(longitudeScope).getValue().getValue();
                                        radius = defaultRad;
                                        //categories.add("DIME");
				} 
//                                else {
//					// 2. set default location ???
//					if (latitude==0) latitude = defaultLat;
//					if (longitude==0) longitude = defaultLon;
//					if (radius==0) radius = defaultRad;
//					if (categories.isEmpty()) categories.add("DIME");
//				}
			} 
//                        else {
//				// 2. set default location ???
//				if (latitude==0) latitude = defaultLat;
//				if (longitude==0) longitude = defaultLon;
//				if (radius==0) radius = defaultRad;
//				if (categories.isEmpty()) categories.add("DIME");
//			}
		}
                  
                
                
		String placeList = "";
		
		// Step 1: invoke YM service adapter to retrieve places info 
		ServiceResponse[] raw = null;
		try {
			if(placeServiceAdapter == null) {
				throw new ServiceNotAvailableException("Check if the service adapter YellowMap has been registered.");
			}
			// TODO @YM, tenant should be removed from the signature after proper Adapter connection. Proper service account URI should be used..
			// TODO @Roman proper account: pending due to missing UI elements
			// TODO @Roman categories
			//this.placeServiceAdapter.setCredentials(mainSaid);
			this.placeServiceAdapter.setCredentials();

			raw = this.placeServiceAdapter.getRaw(placeServiceAdapter.getPlacesParameters(longitude, latitude, new Double(radius).intValue()));
		} catch (AttributeNotSupportedException e) {
			logger.error(e.getMessage(), e);		
		} catch (InvalidLoginException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		
		if(raw == null || raw.length < 1 || raw[0].getResponse() == null) {
			return new ArrayList<Place>();
		}
		
		List<Place> ymPlaces = placeParser.parseYMServiceResponse(raw[0].getResponse());
		
		
		// Step 2: retrieve from previous results a list of place ids
		if (ymPlaces != null && ymPlaces.size() != 0) placeList = getYMPlaceIds(ymPlaces);
		else return places;
		
		// Step 3: invoke SocialRecommenderServiceAdapter to get social rating on these items
		List<Place> srPlaces = new ArrayList<Place>();
		if (this.socialRecService != null) {
			
			String encodedPlaceList = "";
			String srUser = "";
			
			try {
				encodedPlaceList = URLEncoder.encode(placeList,"UTF-8");
				srUser = URLEncoder.encode(srAccount.asURI().toString(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.toString());
			}
			
			if (!encodedPlaceList.equalsIgnoreCase("")) {
				String query = "user2item.php?cid=" + social_rec_CID + "&cat_key=" + social_rec_catalog + "&user=" + srUser + "&engines=topn,cf&item_list=" + encodedPlaceList;		
				ServiceResponse[] srResponses = null;
				try {
					srResponses = this.socialRecService.getRaw(query);
				} catch (AttributeNotSupportedException e) {
					logger.error(e.toString());
				} catch (ServiceNotAvailableException e) {
					logger.error(e.toString());
				} catch (InvalidLoginException e) {
					logger.error(e.toString());
				} catch (ServiceException e) {
					logger.error(e.getMessage(), e);
				}
				if (srResponses != null && srResponses.length > 0)
					srPlaces = placeParser.parseSocialRecServiceResponse(srResponses[0].getResponse());
			} 
		}
		
		
		// Step 4: create list to be returned 
		Iterator<Place> it = ymPlaces.iterator();
		while (it.hasNext()) {
			Place ymp = it.next();
			Place srp = getSocialItem(ymp.getGuid(),srPlaces);
			Place p = mergePlaceInfo(ymp,srp);
			if (p != null) places.add(p);
		}
		
		// For each place detected nearby, a new RDF Placemark Resource is created
		// PlaceProcessor.RDFPlaceReferences contains mapping between <Tenant,YM place ID> and related Placemark URI
		// to be used in publication of current place in Live Context Graph
		// Temporary removed to avoid multiple notifications on the UI
		// createPlacemarkResources(places);
		
		return places;
	}

	public void createPlacemarkResources(List<Place> places) {
        Long tenantId = TenantHelper.getCurrentTenantId();
		Iterator<Place> it = places.iterator();
		while (it.hasNext()) {
			Place p = it.next();
			Placemark pmk = createPlacemarkFromPlace(p);
			if (pmk != null) {
                try {
                    this.placemarkManager.add(pmk);
                    RDFPlaceReferences.put(new PlaceKey(tenantId, p.getGuid()), pmk.asURI().toString());
                } catch (InfosphereException e) {
                    logger.error(e.toString());
                }
            }
		}
	}

	private Placemark createPlacemarkFromPlace(Place p) {
		Placemark pmk = this.modelFactory.getNAOFactory().createResource(Placemark.class);
		pmk.setPrefLabel(p.getGuid());
		Float[] coords = placeParser.parseFormattedPosition(p.getPosition());
		if (coords != null) {
			pmk.setLat(coords[0]);
			pmk.setLong(coords[1]);
		}
		return pmk;
	}

	// TODO @YM, tenant should be removed from the signature after proper Adapter connection. Proper service account URI should be used..
	public Place updatePlace(String mainSaid, String placeId, Place place) throws ServiceNotAvailableException {
		
		Account srAccount = retrieveAccount(SocialRecommenderAdapter.adapterName);
		if (srAccount != null) {
			setServiceReference(srAccount, SocialRecommenderAdapter.adapterName, TenantHelper.getCurrentTenant());
		}
		// TODO @YM retrieve account and set service reference of YMAdapter
		// TODO from Roman: is this correct?
		Account ymAccount = retrieveAccount(PlaceServiceAdapter.adapterName);
		if (ymAccount != null) {
			setServiceReference(ymAccount, PlaceServiceAdapter.adapterName, TenantHelper.getCurrentTenant());
		}
		
		if (place.userRating != NO_VOTE) {
			// Step 1: invoke SocRec Adapter to vote item
			// Normalize vote in input (0..1) to Social rec range (0..100)
			int normVote = (int) (place.userRating * 100);
			
			if (this.socialRecService != null) {
				
				String encodedPlace = "";
				String srUser = "";
				
				try {
					encodedPlace = URLEncoder.encode(placeId,"UTF-8");
					srUser = URLEncoder.encode(srAccount.asURI().toString(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(),e);
					return place;
				}
				
				String query = "vote.php?cid=" + social_rec_CID + "&cat_key=" + social_rec_catalog 
						+ "&user=" + srUser + "&item_id=" + encodedPlace + "&rating=" + normVote;
				
				try {
					this.socialRecService.getRaw(query);
				} catch (AttributeNotSupportedException e) {
					logger.error(e.toString());
				} catch (ServiceNotAvailableException e) {
					logger.error(e.toString());
				} catch (InvalidLoginException e) {
					logger.error(e.toString());
				} catch (ServiceException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
			// Step 2: forward vote and favorite flag to YM using YM Adapter 
			// @YM: Maybe also this should be checked according to gateway refactoring
			try {
				if(placeServiceAdapter == null) {
					throw new ServiceNotAvailableException("Check if the service adapter YellowMap has been registered.");
				}
				// TODO @Roman proper account: pending due to missing UI elements
				// this.placeServiceAdapter.setCredentials(mainSaid);
				this.placeServiceAdapter.setCredentials();

				this.placeServiceAdapter.updatePlace(place);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			} catch (ServiceNotAvailableException e) {
				logger.error(e.getMessage(), e);
			} catch (ServiceException e) {
				logger.error(e.getMessage(), e);
			} 
			
		} else {
			// Step 1: update YM favorite flag
			// @YM: Maybe also this should be checked according to gateway refactoring
			try {
				if(placeServiceAdapter == null) {
					throw new ServiceNotAvailableException("Check if the service adapter YellowMap has been registered.");
				}
				// TODO @Roman proper account: pending due to missing UI elements
				// this.placeServiceAdapter.setCredentials(mainSaid);
                                this.placeServiceAdapter.setCredentials();

				this.placeServiceAdapter.updatePlace(place);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			} catch (ServiceNotAvailableException e) {
				logger.error(e.getMessage(), e);
			} catch (ServiceException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		notifyPlaceUpdate(place);
		
		return place;
	}
	
	private void notifyPlaceUpdate(Place place) {
		Long t = TenantHelper.getCurrentTenantId();
		SystemNotification notification = 
				new SystemNotification(t, DimeInternalNotification.OP_UPDATE, 
						place.guid, DimeInternalNotification.ITEM_TYPE_PLACE, null);
		/*DimeInternalNotification notification = new DimeInternalNotification(t);
		notification.setItemID(place.guid);
		notification.setItemType(DimeInternalNotification.ITEM_TYPE_PLACE);
		notification.setOperation(DimeInternalNotification.OP_UPDATE);
		notification.setName(place.name + " updated.");*/
		try {
			notifierManager.pushInternalNotification(notification);
		} catch (NotifierException e) {
			logger.error(e.getMessage(),e);
		}
	}

	public Place getPlace(String tenant, String placeId) {
		
		// TODO @YM, tenant should be removed from the signature after proper Adapter connection. Proper service account URI should be used..
		
		logger.debug("Get place request received (" + placeId + ")");
		
		Account srAccount = retrieveAccount(SocialRecommenderAdapter.adapterName);
		if (srAccount != null) {
			setServiceReference(srAccount, SocialRecommenderAdapter.adapterName, TenantHelper.getCurrentTenant());
		}
		
		// TODO @YM retrieve account and set service reference of YMAdapter
		// TODO from Roman: is this correct?
		Account ymAccount = retrieveAccount(PlaceServiceAdapter.adapterName);
		if (ymAccount != null) {
			setServiceReference(ymAccount, PlaceServiceAdapter.adapterName, TenantHelper.getCurrentTenant());
		}

		// Step 1: invoke YM service adapter to retrieve places info 
		// TODO @YM to be changed as in getPlaces method
		ServiceResponse[] raw = null;
		
		try {
			if(placeServiceAdapter == null) {
				throw new ServiceNotAvailableException("Check if the YellowMapService has been registered.");
			}

			// TODO @Roman proper account: pending due to missing UI elements
			//this.placeServiceAdapter.setCredentials(tenant);
			this.placeServiceAdapter.setCredentials();
			//raw = this.ymServiceAdapter.getRaw(YMUtils.getPlaceDetailsParameters(placeId));
			raw = this.placeServiceAdapter.getRaw(placeServiceAdapter.getPlaceDetailsParameters(placeId));
			
		} catch (AttributeNotSupportedException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (ServiceNotAvailableException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (InvalidLoginException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (UnsupportedEncodingException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		if(raw == null || raw.length < 1 || raw[0].getResponse() == null) {
			return null;
		}
		
		List<Place> ymPlaces = placeParser.parseYMServiceResponse(raw[0].getResponse());
		Place ymPlace = ymPlaces.get(0);
		
		// Step 3: invoke SocialRecommenderServiceAdapter to get social rating on requested item
		List<Place> srPlace = null;
		if (this.socialRecService != null) {
			
			String encodedPlace = "";
			String srUser = "";
			
			try {
				encodedPlace = URLEncoder.encode(placeId,"UTF-8");
				srUser = URLEncoder.encode(srAccount.asURI().toString(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(),e);
			}
			
			String query = "user2item.php?cid=" + social_rec_CID + "&cat_key=" + social_rec_catalog + "&user=" + srUser + "&engines=topn,cf&item_list=" + placeId;
			ServiceResponse[] srResponses = null;
			
			try {
				srResponses = this.socialRecService.getRaw(query);
			} catch (AttributeNotSupportedException e) {
				logger.error(e.toString());
			} catch (ServiceNotAvailableException e) {
				logger.error(e.toString());
			} catch (InvalidLoginException e) {
				logger.error(e.toString());
			} catch (ServiceException e) {
				logger.error(e.getMessage(), e);
			}
			if (srResponses != null && srResponses.length > 0)
				srPlace = placeParser.parseSocialRecServiceResponse(srResponses[0].getResponse());
		}
		
		
		// Step 4: create place to be returned 
		Place srp = getSocialItem(ymPlace.getGuid(),srPlace);
		Place p = mergePlaceInfo(ymPlace, srp);
		if (p != null) {
			List<Place> pl = new ArrayList<Place>();
			pl.add(p);
			createPlacemarkResources(pl);
			return p;
		}
		else return null;
		
	}
	
	private Place getSocialItem(String placeId, List<Place> srPlaces) {
		if (srPlaces == null) return null;
		Iterator<Place> it = srPlaces.iterator();
		while (it.hasNext()) {
			Place p = it.next();
			if (p.getGuid().equalsIgnoreCase(placeId)) return p;
		}
		return null;
	}
	
	private Place mergePlaceInfo(Place ymp, Place srp) {
		if (srp == null) return ymp;
		ymp.setUserRating(srp.getUserRating());
		ymp.setSocialRecRating(srp.getSocialRecRating());
		return ymp;
	}
	
	private String getYMPlaceIds(List<Place> ymPlaces) {
		String placeIds = "";
		Iterator<Place> it = ymPlaces.iterator();
		while (it.hasNext()) {
			Place p = it.next();
			if (!placeIds.equalsIgnoreCase("")) placeIds += ",";
			placeIds += p.getGuid();
		}
		return placeIds;
	}
	
}

	
