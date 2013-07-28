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

package eu.dime.ps.controllers.placeprocessor;

import java.io.IOException;
import java.io.StringReader;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.dime.commons.dto.Place;
import eu.dime.ps.gateway.service.noauth.YMUtils;
import eu.dime.ps.semantic.model.nfo.Placemark;

//import eu.dime.ps.controllers.requestbroker.dto.Place;

public class PlaceParser {
	
	private Logger logger = Logger.getLogger(PlaceParser.class);
	private XPathFactory xpf = XPathFactory.newInstance();
	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder documentBuilder;
	
	private DecimalFormat latFormat = null;
	private DecimalFormat lonFormat = null;
	private DecimalFormatSymbols decimalFormat = new DecimalFormatSymbols();
	
	private String srXPathExpr = "//*[local-name() = 'parS']";
	protected static double NO_VOTE = -1;
        
    public PlaceParser() {
        // Just copied from PlaceProcessor since it's no more useful there but here
        decimalFormat.setDecimalSeparator('.');
        latFormat = new DecimalFormat("+00.0000;-00.0000",decimalFormat);
        lonFormat = new DecimalFormat("+000.0000;-000.0000",decimalFormat);
        
        try {
			documentBuilder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(),e);
		}
    }
	
    @Deprecated
	public Place parsePlacemark(Placemark pm)
	{
		Place p = new Place();
		String placeId = parseDescr(pm, "ID");
		p.setGuid(placeId);
		if (pm.getScore() != null) p.setYMRating(pm.getScore());
		else p.setYMRating(NO_VOTE);

		p.setName(pm.getPrefLabel()); 
		p.setDistance(Double.parseDouble(parseDescr(pm,"Distance")));
		p.setPosition(getFormattedPosition(pm.getLat(),pm.getLong()));
	
		eu.dime.commons.dto.PlaceAddress address = new eu.dime.commons.dto.PlaceAddress();
		address.setStreetAddress(parseDescr(pm, "StreetAddress"));
		address.setRegion(parseDescr(pm, "Region"));
		address.setLocality(parseDescr(pm, "Locality"));
		address.setPostalCode(parseDescr(pm, "PostalCode"));
		address.setCountry(parseDescr(pm, "Country"));
		address.setFormatted(parseDescr(pm, "StreetAddress")+", "+parseDescr(pm, "Locality")+", "+parseDescr(pm, "PostalCode")+" - "+parseDescr(pm, "Country"));
		p.setAddress(address);
		p.setUrl(parseDescr(pm, "URL"));
		p.setImageUrl(parseDescr(pm,"ImgUrl"));
		p.setInformation(parseDescr(pm, "Description"));
		
		if(parseDescr(pm, "Fav").equals("true"))
		{
			p.setFavorite(true);
		}
		else
		{
			p.setFavorite(false);
		}
		

		p.setUserRating(NO_VOTE);
		p.setSocialRecRating(NO_VOTE);
	
					
		
		return p;

	}
	
	public List<Place> parseYMServiceResponse(String xmlYMSAResponse) {
		try {
			return YMUtils.getPlaceListFromRespond(xmlYMSAResponse);
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	@Deprecated
	//Convert a list of Placemarks to a list of Places
	public List<Place> parseYMServiceResponse(Collection<Placemark> resp)
	{
		List<Place> PL = new ArrayList<Place>();  
		
		Iterator<Placemark> it = resp.iterator();
		while (it.hasNext()) 
		{
			PL.add(parsePlacemark(it.next()));
		}
		
		return PL;
	}
	
	@Deprecated
	//parses the Descriptions of a placemark to retrieve the necessary data. Ismael, don't look at this :)
	private String parseDescr(Placemark pm, String key)
	{
		Iterator<String> it =pm.getAllDescription();
		while (it.hasNext())
				{
					String[] temp = it.next().split("\\|");
					if(temp[0].equals(key))
					{
                                            if (temp.length == 2) {
                                                return temp[1];
                                            } else {
                                                return "";
                                            }
					}
				}
		return "";
	}
	
	
	public List<Place> parseSocialRecServiceResponse(String xml) {
		
		List<Place> places = new ArrayList<Place>();
		if (xml == null || xml.equalsIgnoreCase("")) return places;
		
		

		//InputSource is = new InputSource(new StringReader(xml));
		
		try {
			Document someXML = documentBuilder.parse(new InputSource(new StringReader(xml)));
			//Object o = xpf.newXPath().evaluate(srXPathExpr,is,XPathConstants.NODESET);
			Object o = xpf.newXPath().evaluate(srXPathExpr,someXML,XPathConstants.NODESET);
			NodeList parSNodes = (NodeList)o;
		
			for (int i=0; i<parSNodes.getLength(); i++) {
				String placeId = null;
				String topn = null;
				String cf = null;
				String vote = null;
				Node parS = parSNodes.item(i);
				NodeList parList = parS.getChildNodes();
				for (int j=0; j<parList.getLength(); j++) {
					Node par = parList.item(j);
					String content = par.getTextContent();
					if (content != null && !content.equalsIgnoreCase("\n")) {
						NamedNodeMap map = par.getAttributes();
						Node n = map.getNamedItem("n");
						String value = n.getNodeValue();
						if (value.equalsIgnoreCase("item_id")) placeId = content;
						else if (value.equalsIgnoreCase("recs:vote")) vote = content;
						else if (value.equalsIgnoreCase("recs:engine:topn")) topn = content;
						else if (value.equalsIgnoreCase("recs:engine:cf")) cf = content;
					}
				}
				Place p = createPlace(placeId,vote,topn,cf);
				if (p != null) places.add(p);
			}
		
		} catch (XPathExpressionException e) {
			logger.error(e.toString(),e);
		} catch (Exception e) {
			logger.error(e.toString(),e);
		}
		
		return places;
		
	}
	
	private Place createPlace(String placeId, String vote, String topn, String cf) {
		
		Place p = new Place();
		
		if (placeId != null) p.setGuid(placeId);
		else return null;
		
		if (vote == null && topn == null && cf == null) {
			logger.error("No votes came from SocRecService for item " + placeId);
			p.setUserRating(PlaceProcessor.NO_VOTE);
			p.setSocialRecRating(PlaceProcessor.NO_VOTE);
			return p;
		}
		
		if (vote != null) p.setUserRating(normalizeVoteToFloat(vote));
		if (cf != null) p.setSocialRecRating(normalizeVoteToFloat(cf));
		else if (topn != null) p.setSocialRecRating(normalizeVoteToFloat(topn));
		return p;
		
	}
	
	private Float normalizeVoteToFloat(String vote) {
		try {
			Float f = Float.parseFloat(vote);
			return new Float(f/100);
		} catch (NumberFormatException ex) {
			logger.error(ex.toString());
			return new Float(0);
		}
		
	}
	
	private String getFormattedPosition(Float lat, Float lon) {
		return latFormat.format(lat) +" "+ lonFormat.format(lon) + "/";
	}

	public Float[] parseFormattedPosition(String position) {
		StringTokenizer tok = new StringTokenizer(position, " ");
		if (tok.countTokens() != 2) {
			logger.error("Place position is not formatted correctly: " + position);
			return null;
		}
		String lat = tok.nextToken();
		String lon = tok.nextToken();
		lon = lon.substring(0,lon.length()-2);
		Float[] coords = new Float[2];
		try {
			coords[0] = Float.parseFloat(lat);
			coords[1] = Float.parseFloat(lon);
		} catch (NumberFormatException e) {
			logger.error(e.toString());
			return null;
		}
		return coords;
	}

}
