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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.dime.commons.dto.Place;
import eu.dime.commons.dto.PlaceAddress;


public class YMUtils {

	public static String USR_PWD = "dime";
	
	static String CATEGORY_DIME = "dime";
	public static float NO_SCORE = -1;
	public static int MAX_SCORE = 5;
	
	public static String XML_PLACE = "Place";
	public static String XML_STREETADDRESS = "StreetAddress";
	public static String XML_POSTAL = "PostalCode";
	public static String XML_LOCALITY = "Locality";
	public static String XML_COUNTRY = "Country";
	public static String XML_URL = "URL";
	public static String XML_PHONE = "PhoneNumber";
	public static String XML_DESCR = "Description";
	public static String XML_DIST = "Distance";
	public static String XML_SCORE = "Score";
	public static String XML_LAT = "Lat";
	public static String XML_LONG = "Long";
	public static String XML_ID = "ID";
	public static String XML_FAV = "Fav";
	public static String XML_IMG_URL = "ImgUrl";
	public static String XML_SNAME = "SName";
	public static String XML_VAL_TRUE = "true";
	public static String XML_VAL_FALSE = "false";
	
	private static final Logger logger = LoggerFactory.getLogger(YMUtils.class);
	
	/**
	 * Returns a string for retrieving places from yellowmap.de
	 * @param longitude
	 * @param latitude
	 * @param radius
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	/*public static String getPlacesParameters(double longitude, double latitude, int radius) throws UnsupportedEncodingException {
		String parameter = "&BC=" + CATEGORY_DIME + "&LocX=" + longitude + "&LocY=" + latitude;
		
		if(radius > 0)
			parameter += "&Radius=" + radius;
		return parameter;
	}*/
	
	/**
	 * Returns a string for retrieving place details from yellowmap.de
	 * @param poiID The POI ID of the place
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	/*public static String getPlaceDetailsParameters(String poiID) throws UnsupportedEncodingException {
		return "&QT=10&DetailInfoView=1&Ebinr=" + poiID;
	}*/
	
	/**
	 * Having the XML generated from createXML this creates a list of {@link Place} from it
	 * @param xml The xml containing the retrieved POIs of the yellowmap.de service
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<Place> getPlaceListFromRespond(String xml) throws ParserConfigurationException, SAXException, IOException {
		List<Place> pList = new ArrayList<Place>();
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;

		InputSource is = new InputSource(new StringReader(xml));
		documentBuilder = builderFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse(is);

		NodeList placeNodes = doc.getElementsByTagName(XML_PLACE);
		
		for(int i = 0; i < placeNodes.getLength(); i++) {
			pList.add(getPlace(doc, (Element) placeNodes.item(i)));
		}
		
		return pList;
	}

	/**
	 * Creates a {@link Place} from the element
	 * @param doc
	 * @param element
	 * @return
	 */
	private static Place getPlace (Document doc, Element element) {
		Place p = new Place();

		String value = "ametic:" + element.getElementsByTagName(XML_ID).item(0).getTextContent();
		p.setGuid(value);

		value = element.getElementsByTagName(XML_SCORE).item(0).getTextContent();
		try {
			p.setYMRating(Double.parseDouble(value));
		} catch(NumberFormatException e) {
			logger.error("Could not extract rating from YMServiceAdapter response.", e);
		}

		value = element.getElementsByTagName(XML_SNAME).item(0).getTextContent();
		p.setName(value);
		
		value = element.getElementsByTagName(XML_DIST).item(0).getTextContent();
		try {
			p.setDistance(Double.parseDouble(value));
		} catch(NumberFormatException e) {
			logger.error("Could not extract distance from YMServiceAdapter response.", e);
		}
		
		DecimalFormatSymbols decimalFormat = new DecimalFormatSymbols();
		decimalFormat.setDecimalSeparator('.');
		DecimalFormat latFormat = new DecimalFormat("+00.0000;-00.0000",decimalFormat);
		DecimalFormat lonFormat = new DecimalFormat("+000.0000;-000.0000",decimalFormat);
		value = element.getElementsByTagName(XML_LAT).item(0).getTextContent();
		String value2 = element.getElementsByTagName(XML_LONG).item(0).getTextContent();
		try {
			p.setPosition(latFormat.format(Float.parseFloat(value)) +" "+ lonFormat.format(Float.parseFloat(value2)) + "/");
		} catch(NumberFormatException e) {
			logger.error("Could not extract position (longitude and latitide) from YMServiceAdapter response.", e);
		}
	
		PlaceAddress address = new eu.dime.commons.dto.PlaceAddress();
		
		value = element.getElementsByTagName(XML_STREETADDRESS).item(0).getTextContent();
		address.setStreetAddress(value);
		
//		address.setRegion(parseDescr(pm, "Region"));

		value = element.getElementsByTagName(XML_LOCALITY).item(0).getTextContent();
		address.setLocality(value);
		
		value = element.getElementsByTagName(XML_POSTAL).item(0).getTextContent();
		address.setPostalCode(value);
		
		value = element.getElementsByTagName(XML_COUNTRY).item(0).getTextContent();
		address.setCountry(value);
		
		address.setFormatted(address.getStreetAddress() + ", " + address.getLocality() + ", " + address.getPostalCode() + ", " + address.getCountry());
		p.setAddress(address);

		value = element.getElementsByTagName(XML_URL).item(0).getTextContent();
		p.setUrl(value);
		
		value = element.getElementsByTagName(XML_IMG_URL).item(0).getTextContent();
		p.setImageUrl(value);
		
		value = element.getElementsByTagName(XML_DESCR).item(0).getTextContent();
		p.setInformation(value);
		
		value = element.getElementsByTagName(XML_FAV).item(0).getTextContent();
		if(value.equals(XML_VAL_TRUE))
		{
			p.setFavorite(true);
		}
		else
		{
			p.setFavorite(false);
		}
		
		p.setUserRating(NO_SCORE);
		p.setSocialRecRating(NO_SCORE);
	
		return p;
	}
	

	/**
	 * Creates a dom tree from the places and adds a tag to the place if it is favorite.
	 * This is used as service adapter result since the process involves more than one YM service
	 * which is combined to one result
	 * @param places
	 * @param favorites
	 * @return
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 */
 	public static String createXML(NodeList places, List<String> favorites) throws ParserConfigurationException, TransformerException, IOException {
		if(places != null) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("Places");
				doc.appendChild(rootElement);
	
				// adds each place
				for(int i = 0; i < places.getLength(); i++) {
					addPlace(doc, rootElement, (Element)places.item(i), favorites);
				}
				
				// generates the string representation (XML)
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer();
				StringWriter writer = new StringWriter();
				Result result = new StreamResult(writer);
				Source source = new DOMSource(doc);
				transformer.transform(source, result);
				writer.close();
				String xml = writer.toString();
				return xml;
			} catch (ParserConfigurationException e) {
				logger.error(e.getMessage(), e);
				throw e;
			} catch (TransformerException e) {
				logger.error(e.getMessage(), e);
				throw e;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			
		}
		return null;
	}
	
 	/**
 	 * Adds a place to the dom tree. if the place 
 	 * @param doc The dom document
 	 * @param rootElement the element where the place is added to as child 
 	 * @param poiElm the retrieved POI from the yellowmap.de service
 	 * @param favorites the list of POI IDs that are favorites
 	 */
	private static void addPlace(Document doc, Element rootElement, Element poiElm, List<String> favorites) {
		Element placeElm = doc.createElement(XML_PLACE);
		rootElement.appendChild(placeElm);
		
		addElement(doc, placeElm, XML_SNAME, poiElm.getElementsByTagName("SName").item(0)
				.getTextContent());
		
		String street = "";
		String hNo = "";
		String zip = "";
		String twn = "";
		String ct = "";
		String www = "";
		String phone = "";
		String txtDyn = "";
		String dist = "0";
		String rating = "";

		NamedNodeMap poiAttrMap = null;

		// read all values
		try {

			street = poiElm.getElementsByTagName("Str").item(0)
					.getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {

			hNo = poiElm.getElementsByTagName("HNo").item(0).getTextContent();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {

			zip = poiElm.getElementsByTagName("Zip").item(0).getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {

			twn = poiElm.getElementsByTagName("Twn").item(0).getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {

			ct = poiElm.getElementsByTagName("Ct").item(0).getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			www = poiElm.getElementsByTagName("Www").item(0).getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			phone = poiElm.getElementsByTagName("Phone").item(0)
					.getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			txtDyn = poiElm.getElementsByTagName("TxtDyn").item(0)
					.getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			dist = poiElm.getElementsByTagName("Dist").item(0)
							.getTextContent().trim();
		} catch (Exception e) {
			dist = "0";
			logger.error(e.getMessage(), e);
		}

		try {
			rating = poiElm.getElementsByTagName("Rating").item(0)
					.getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			poiAttrMap = poiElm.getElementsByTagName("AdrV21").item(0)
					.getAttributes();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		
		// assign values
		addElement(doc, placeElm, XML_STREETADDRESS, street + " " + hNo);

		addElement(doc, placeElm, XML_POSTAL, zip);

		addElement(doc, placeElm, XML_LOCALITY, twn);

		addElement(doc, placeElm, XML_COUNTRY, ct);

		addElement(doc, placeElm, XML_URL, www);

		addElement(doc, placeElm, XML_PHONE, phone);

		addElement(doc, placeElm, XML_DESCR, txtDyn);

		addElement(doc, placeElm, XML_DIST, dist);
		
		// if there is no score saved at YM, the tag is missing in the result
		// otherwise its avr_rating|No_of_ratings
		try {
			String[] temp = rating.trim().split("\\|");
			addElement(doc, placeElm, XML_SCORE, Float.valueOf(temp[0]).floatValue() / 5 + "");
		} catch (Exception e) {
			addElement(doc, placeElm, XML_SCORE, NO_SCORE + "");
			logger.error(e.getMessage(), e);
		}

		try {
			// convert to WGS84
			addElement(doc, placeElm, XML_LAT, Float
					.valueOf(poiAttrMap.getNamedItem("Y").getNodeValue())
					.floatValue() / 100000 + "");
			addElement(doc, placeElm, XML_LONG, Float.valueOf(
					poiAttrMap.getNamedItem("X").getNodeValue()).floatValue() / 100000 + "");
		} catch (Exception e) {
			addElement(doc, placeElm, XML_LAT, (float) 0 + "");
			addElement(doc, placeElm, XML_LONG, (float) 0 + "");
			logger.error(e.getMessage(), e);
		}

		try {
			String id = poiAttrMap.getNamedItem("ID").getNodeValue();
			addElement(doc, placeElm, XML_ID, id);

			if(favorites != null && favorites.contains(id))
				addElement(doc, placeElm, XML_FAV, XML_VAL_TRUE);
			else
				addElement(doc, placeElm, XML_FAV, XML_VAL_FALSE);
				
			// add ImageUrl, if Image exists
			int CustID = Integer.parseInt(poiAttrMap.getNamedItem("CustID")
					.getNodeValue());

			switch (CustID) {
			case 8:
			case 12:
			case 13: {
				break;
			}
			default: {
				addElement(doc, placeElm, XML_IMG_URL, "http://www.uni-siegen.de/fb5/itsec/projekte/dime/ym-pictures/"
						+ poiAttrMap.getNamedItem("CustID").getNodeValue()
						+ ".jpg");
				break;
			}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Adds an element to the parent in the dom tree
	 * @param doc 
	 * @param parentElement Parent element
	 * @param name element name
	 * @param value element value
	 */
	private static void addElement(Document doc, Element parentElement, String name, String value) {
		Element element = doc.createElement(name);
		element.appendChild(doc.createTextNode(value));
		parentElement.appendChild(element);
	}
}