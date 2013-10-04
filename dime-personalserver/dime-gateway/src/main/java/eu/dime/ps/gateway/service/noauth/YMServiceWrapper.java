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

package eu.dime.ps.gateway.service.noauth;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.dime.commons.dto.Place;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.proxy.HttpRestProxy;

public class YMServiceWrapper {

	private String ymServiceURL;
	private String myYMServiceURL;
	private String myYMCommentsServiceURL;
	private Integer port;
	private String staticParameter;
	
	private String realm = "";
	private String proxyUsername = "";
	private String proxyPassword = "";
        
        private String xpathExpPoiDetail = "";
        private String xpathExpPoi = "";
        private String xpathExpUserId = "";
        private String xpathExpCategory = "";
        private String xpathExpFavorites = "";
        private String xpathExpErrors = "";
        
        private String ymNs = "";
        private String ymNsPrefix = "";
        private String myYmNs = "";
        private String myYmNsPrefix = "";
	
	// Strings used in the services.property file
	private static String YMSA = "YELLOWMAP";
	private static String PLACESURL = "PLACESURL";
	private static String MYYMURL = "MYYMURL";
	private static String COMMENTSURL = "COMMENTSURL";
	private static String PORT = "PORT";
	private static String STATICPARAMETERS = "STATICPARAMETERS";
	
	private static String XPATH_EXP_POI = "XPATH_EXP_POI";
	private static String XPATH_EXP_POI_DETAIL = "XPATH_EXP_POI_DETAIL";
	private static String XPATH_EXP_USER_ID = "XPATH_EXP_USER_ID";
	private static String XPATH_EXP_CATEGORY = "XPATH_EXP_CATEGORY";
	private static String XPATH_EXP_FAVORITES = "XPATH_EXP_FAVORITES";
	private static String XPATH_EXP_ERROR_YM = "XPATH_EXP_ERROR_YM";
        
        private static String YM_NS = "YM_NS";
	private static String YM_NS_PREFIX = "YM_NS_PREFIX";
	private static String MY_YM_NS = "MY_YM_NS";
	private static String MY_YM_NS_PREFIX = "MY_YM_NS_PREFIX";
        
        // Additional Parameter for My YM Service
	private static String MYYM_LOGIN_SELECT = "LoginSelect?";
	private static String MYYM_LOGIN_CREATE = "AccountCreate?";
	private static String MYYM_LOGIN_ACTIVATE = "AccountActivate?";
	private static String MYYM_LIST_CREATE = "CategorySave?";
	private static String MYYM_LIST_SELECT = "CategoriesSelect?";
	private static String MYYM_FAV_SELECT = "AddressesSelect?";
	private static String MYYM_FAV_CREATE = "AddressSave?";
	private static String MYYM_FAV_DELETE = "AddressDelete?";
//	private static String MYYM_RATING_SAVE = "WriteRating?";
	private static String MYYM_RATING_SAVE = "WriteComment?";

	private static String CATEGORY_DIME = "dime";


	// Proxy for YM POIs
	private HttpRestProxy ymProxy;
	
	// Proxy for MY YM
	private HttpRestProxy myymProxy;
	
	// Proxy for comments
	private HttpRestProxy myymCommentsProxy;
	
	private static final Logger logger = LoggerFactory.getLogger(YMServiceWrapper.class);

	private static YMServiceWrapper instance; 
	
	public static YMServiceWrapper getInstance(PolicyManager policyManager) throws ServiceNotAvailableException, MalformedURLException {
		if(instance == null) {
			instance = new YMServiceWrapper(policyManager);
		}
		return instance;
	}
	
	private YMServiceWrapper(PolicyManager policyManager) throws ServiceNotAvailableException, MalformedURLException {
		this.ymServiceURL = policyManager.getPolicyString(PLACESURL, YMSA);
		this.myYMServiceURL = policyManager.getPolicyString(MYYMURL, YMSA);
		this.myYMCommentsServiceURL = policyManager.getPolicyString(COMMENTSURL, YMSA);
		this.staticParameter = policyManager.getPolicyString(STATICPARAMETERS, YMSA);
                this.ymProxy = new HttpRestProxy(new URL(this.ymServiceURL),this.proxyUsername, this.proxyPassword);
		this.myymProxy = new HttpRestProxy(new URL(this.myYMServiceURL), this.proxyUsername, this.proxyPassword);
		this.myymCommentsProxy = new HttpRestProxy(new URL(this.myYMCommentsServiceURL), this.proxyUsername, this.proxyPassword);
                
                this.xpathExpPoiDetail = policyManager.getPolicyString(XPATH_EXP_POI_DETAIL, YMSA);
                this.xpathExpPoi = policyManager.getPolicyString(XPATH_EXP_POI, YMSA);
                this.xpathExpUserId = policyManager.getPolicyString(XPATH_EXP_USER_ID, YMSA);
                this.xpathExpCategory = policyManager.getPolicyString(XPATH_EXP_CATEGORY, YMSA);
                this.xpathExpFavorites = policyManager.getPolicyString(XPATH_EXP_FAVORITES, YMSA);
                this.xpathExpErrors = policyManager.getPolicyString(XPATH_EXP_ERROR_YM, YMSA);
                
                this.ymNs = policyManager.getPolicyString(YM_NS, YMSA);
                this.ymNsPrefix = policyManager.getPolicyString(YM_NS_PREFIX, YMSA);
                this.myYmNs = policyManager.getPolicyString(MY_YM_NS, YMSA);
                this.myYmNsPrefix = policyManager.getPolicyString(MY_YM_NS_PREFIX, YMSA);
	}
	
	public boolean isConnected() {
		if(this.ymProxy != null && this.myymProxy != null && this.myymCommentsProxy != null)
			return true;
		return false;
	}
	
	/**
	 * Retrieves the places in XML. This is self created to take into account the favorites which come from a different service
	 * @return
	 * @throws ServiceNotAvailableException 
	 * @throws InvalidLoginException 
	 * @throws AttributeNotSupportedException 
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws ParserConfigurationException 
	 */
	public String getPlaces(String parameters, String user, String passwd, String firstname, String lastname) throws ServiceNotAvailableException, AttributeNotSupportedException, InvalidLoginException, ParserConfigurationException, TransformerException, IOException, ServiceException {
		
		// Get the POIs for the given position
		String query = URLEncoder.encode(this.staticParameter + parameters, "UTF-8");
		String pois = callService(this.ymProxy, query);
		if(pois == null)
			return null;

		// get user
		String userID = getUserID(user, passwd);
		
		// If no user ID retrieved create new user
		if(userID == null) {
			userID = createUser(user, passwd,firstname,lastname);
		}

		// Get the favorites of the user
		List<String> favorites = getFavoritIDs(userID);

		NodeList places = null;
		if(parameters.contains("&QT=10&DetailInfoView=1&Ebinr="))
			places = findNodes(pois, xpathExpPoiDetail);
		else
			places = findNodes(pois, xpathExpPoi);
		if(places != null)
			return YMUtils.createXML(places, favorites);
		else return null;
	}

	
	/**
	 * Updates personal information of a POI
	 * @throws ServiceNotAvailableException 
	 * @throws UnsupportedEncodingException 
	 */
	public void updatePOI(Place place, String user, String passwd) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {
		String userID = getUserID(user, passwd);
		
		// The address list id
		String favPlaceListID = getFavPlaceListID(userID, place.getGuid());
		
		if (favPlaceListID != null) {// the place is already a favorite at yellowmap
			if (!place.isFavorite()) {// delete place if not a favorite in dime
				deleteFavorite(userID, favPlaceListID);
			}
		} else {// the place is not already a fav at yellowmap
			if (place.isFavorite()) { // set as favorite
				setFavorite(userID, place.getGuid());
			}

		}

		// is there a rating?
		if (place.getUserRating() != YMUtils.NO_SCORE) {
			setRating(userID, place.getGuid(),
					(int) (place.getUserRating() * YMUtils.MAX_SCORE));
		}

	}
	
	
	// save rating
	// userID= YM ID
	// rating = YM Rating, e.g. [1..5]
	private void setRating(String userID, String placeID, int rating) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {
		String tmpPlaceID = placeID;

		// Remove "ametic"-prefix
		if (placeID.toLowerCase().startsWith("ametic:")) {
			tmpPlaceID = placeID.substring("ametic:".length());
		}

		String params;
		params = "System" + this.staticParameter.substring(1) + "&AccountID="
				+ userID + "&CommentID=&Identifier="
				+ URLEncoder.encode(tmpPlaceID, "UTF-8") + "&Grade="
				+ rating
				+ "&CommentTitle=&CommentText=&Status=NOT_RELEASED";
		
		String query = MYYM_RATING_SAVE +   params;
		
		callService(this.myymCommentsProxy, query);
	}
	
	/**
	 * Creates a new Favorite
	 * @param userID
	 * @param placeID
	 * @throws UnsupportedEncodingException
	 * @throws ServiceNotAvailableException
	 */
	protected void setFavorite(String userID, String placeID) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {
		// 1. get categoryID of categry dime
		// 2. save place as favorite of user in category di.me
		String catID = "";
		String tmpPlcID = placeID;

		// Remove "ametic"-prefix
		if (placeID.toLowerCase().startsWith("ametic:")) {
			tmpPlcID = placeID.substring("ametic:".length());
		}

		catID = getFavoriteListID(userID);

		// save place
		String params = this.staticParameter + "&UserID=" + URLEncoder.encode(userID, "UTF-8") + "&Ebinr=" + URLEncoder.encode(tmpPlcID, "UTF-8")
					+ "&CategoryID=" + URLEncoder.encode(catID, "UTF-8") + "&ListID=&Description=";
		
		String query = MYYM_FAV_CREATE + URLEncoder.encode(this.staticParameter, "UTF-8") + params;
		callService(this.myymProxy, query);
	}
	
	
	// delete an address from the fav. list
	private void deleteFavorite(String userID, String favPlaceListID) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {
		String params = this.staticParameter + "&UserID=" + userID;

		// delete place
		params = this.staticParameter + "&UserID=" + userID + "&ListID=" + favPlaceListID;
		
		String query = MYYM_FAV_DELETE + params;

		callService(this.myymProxy, query);
	}

	
	// returns address ListIDs of favorite places of this user
	private String getFavPlaceListID(String userID, String placeID) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {

		NodeList favList;
		String listID = "";
		String tmpPlcID = placeID;
		String result;

		// Remove "ametic"-prefix
		if (placeID.toLowerCase().startsWith("ametic:")) {
			tmpPlcID = placeID.substring("ametic:".length());
		}

		// 1. get Category ID
		listID = getFavoriteListID(userID);

		// 2.get all listItems
		if (listID != "") {
			favList = getFavoriteNodes(userID, listID);

			if (favList != null) {
				for (int i = 0; i < favList.getLength(); i++) {
					
					Element poiElm = (Element) favList.item(i);
					String tempPlaceID = poiElm
						.getElementsByTagName("Ebinr").item(0)
						.getTextContent();

					// Check if we found the requested place
					if (tempPlaceID.equals(tmpPlcID)) {
						NamedNodeMap poiAttrMap = poiElm.getAttributes();

						/*
						 * poiElm .getElementsByTagName("AddressData")
						 * .item(0).getAttributes();
						 */
						result = poiAttrMap.getNamedItem("ListID")
							.getNodeValue();
						return result;
					}
				}
			}

		}
		return null;
	}
	
	private String callService(HttpRestProxy proxy, String query) throws ServiceNotAvailableException, ServiceException {
		String result = proxy.get(query);
		
		// If the response is an error message  
		String message = getMessage(result);
		if(message != null) {
			logger.error(message);
			return null;
		}
		return result;
	}
	
	/**
	 * Gets the userID for the given user. if not exist returns <code>null</code>
	 * @param eMail
	 * @param password
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws ServiceNotAvailableException
	 */
	private String getUserID(String eMail, String password) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {
		String params = "&User=" + eMail
			+ "&Password=" + password + "&Password2=" + password;

		String query = MYYM_LOGIN_SELECT + this.staticParameter + params;
		String userXml = callService(this.myymProxy, query);

		if(userXml == null) {
			logger.info("No user for exist for " + eMail);
			return null;
		}

		// Retrieves the userID
		NodeList nodes = findNodes(userXml, xpathExpUserId);

		// return userID
		if (nodes.getLength() > 0)
			return nodes.item(0).getTextContent();
		else
			return null;
	}
	
	
	
	/**
	 * Creates a new user with the given name and password. Additionally it creates a favorite list with the name CATEGORY_DIME.
	 * @param myymAdapter The adapter to myYM service to be called for retrieving the UserID
	 * @param eMail The myYM user name
	 * @param password The myYM passwd
	 * @return The myYM userID
	 * @throws UnsupportedEncodingException 
	 */
	private String createUser(String eMail, String password, String firstname, String lastname) throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException, UnsupportedEncodingException, ServiceException {
		// Create the account
		String params = "&User=" + eMail
			+ "&Password=" + password + "&Password2=" + password + "&eMail=" + eMail +"&Title=Mr&Firstname="+firstname+"&Surname="+lastname+"&MobilPhoneNumber=0&PreferredActivationMethod=0";
		
		
		String query = MYYM_LOGIN_CREATE + this.staticParameter + params;
		String userXml = callService(this.myymProxy, query);

		// Retrieves the userID
		NodeList nodes = findNodes(userXml, xpathExpUserId);


		if (nodes.getLength() > 0) {
			String userID = nodes.item(0).getTextContent();
			
			if(userID != null && userID != "") {
			// 2. Activate the account
				params = "&UserID=" + userID;
				
				query = MYYM_LOGIN_ACTIVATE + this.staticParameter + params;
				callService(this.myymProxy, query);

			// 3. Create new list named like CATEGORY_DIME
				params = "&UserID=" + userID + "&Category=" + CATEGORY_DIME;
				
				query = MYYM_LIST_CREATE + this.staticParameter + params;
				callService(this.myymProxy, query);
				
				// TODO check if it worked
				return userID;
			}
		}
		return null;
	}
	
	/**
	 * Get the favorites of the user
	 * @param userID the userID of the user. This is not the user name
	 * @return
	 * @throws AttributeNotSupportedException
	 * @throws ServiceNotAvailableException
	 * @throws InvalidLoginException
	 * @throws UnsupportedEncodingException
	 */
	private List<String> getFavoritIDs(String userID) throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException, UnsupportedEncodingException, ServiceException {

		String listID = getFavoriteListID(userID);
		
		if(listID != null) {
			// Select the favorit list for dime and get the favorits
			NodeList favorits = getFavoriteNodes(userID, listID);
				
			ArrayList<String> result = new ArrayList<String>();

			// Parse the list for the IDs
			if (favorits != null) {
				for (int i = 0; i < favorits.getLength(); i++) {
					Element poiElm = (Element) favorits.item(i);
					result.add(poiElm.getElementsByTagName("Ebinr").item(0)
							.getTextContent());
				}
			}
			return result;
		}
		return null;
	}
	
	/**
	 * Retrieve the favorites
	 * @param userID
	 * @param listID Favorite list ID
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws ServiceNotAvailableException 
	 */
	private NodeList getFavoriteNodes(String userID, String listID) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {
		// Select the favorit list for dime
		String params = "&UserID=" + userID + "&CategoryID=" + listID;
		String query = MYYM_FAV_SELECT + this.staticParameter + params;
		String favoritesXML = callService(this.myymProxy, query);

		return findNodes(favoritesXML, xpathExpFavorites);
	}
	
	/**
	 * Retrieves the ID of the favorite list.
	 * @param userID
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws ServiceNotAvailableException
	 */
	private String getFavoriteListID(String userID) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {
		String params = "&UserID=" + userID;
		String query = MYYM_LIST_SELECT + this.staticParameter + params;
		String favoritesCatXML = callService(this.myymProxy, query);

		// extract the categories
		NodeList favLists = findNodes(favoritesCatXML, xpathExpCategory);
		
		// check if category is dime
		String listID = null;
		if (favLists != null) {
			for (int i = 0; i < favLists.getLength(); i++) {
				Element poiElm = (Element) favLists.item(i);

				if (poiElm.getElementsByTagName("Category").item(0)
						.getTextContent().equals(CATEGORY_DIME)) {
					listID = poiElm.getElementsByTagName("CategoryID").item(0)
							.getTextContent();
					break;
				}
			}
		}
		return listID;
	}
	
	/**
	 * Returns the nodes within the XML rawData according to the xPath expression
	 * @param poiXML The XML coming from the YMServiceAdapter
	 * @param xPathExp The xPath expression
	 * @return List of nodes of the XML raw Data according to the xPath expression
	 */
	private NodeList findNodes(String poiXML, String xPathExp) {
		XPath xP = XPathFactory.newInstance().newXPath();
		xP.setNamespaceContext(new FixedNSResolver());

		NodeList resultList;

		if (!poiXML.isEmpty()) {

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder;

			try {

				InputSource is = new InputSource(new StringReader(poiXML));
//				is.setEncoding("UTF-8");
				documentBuilder = builderFactory.newDocumentBuilder();
				Document xmlResult = documentBuilder.parse(is);

				resultList = (NodeList) xP.evaluate(xPathExp, xmlResult,
						XPathConstants.NODESET);

				return resultList;

			} catch (SAXException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (ParserConfigurationException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (XPathExpressionException ex) {
				logger.error(ex.getMessage(), ex);
			}

		}
		return null;
	}


	
	/**
	 * If the message from the service contains a error message the message is extracted.
	 * @param xmlMessage
	 * @return The message or <code>null</code>
	 */
	private String getMessage(String xmlMessage) {
		NodeList nodes = findNodes(xmlMessage, xpathExpErrors);
		
		if (nodes != null && nodes.getLength() > 0) {
			Element elm = (Element) nodes.item(0);
			String level = elm.getAttribute("Level");

			Node messageNode = elm.getElementsByTagName("Msg").item(0);
			String textContent = messageNode.getTextContent();
			
			if(elm != null && level != null)
				return level + ": " + textContent;
		}
		return null;
	}
	
	// Necessary for the XML parsing in YellowMap-Namespace
	private class FixedNSResolver implements NamespaceContext {

		/**
		 * This method returns the uri for all prefixes needed.
		 * 
		 * @param prefix
		 * @return uri
		 */
		public String getNamespaceURI(String prefix) {
			if (prefix == null) {
				throw new IllegalArgumentException("No prefix provided!");
			} else if (prefix.equals(ymNsPrefix)) {
				return ymNs;
			} else if (prefix.equals(myYmNsPrefix)) {
				return myYmNs;
			} else {
				return XMLConstants.NULL_NS_URI;
			}
		}

		public String getPrefix(String namespaceURI) {
			if (namespaceURI.equals(ymNs)) {
				return ymNsPrefix;
			}
			else if (namespaceURI.equals(myYmNs)) {
				return myYmNsPrefix;
			}
			else
				return null;
		}

		public Iterator getPrefixes(String namespaceURI) {
			// Not needed in this context.
			return null;
		}

	}
	
}
