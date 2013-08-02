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

package eu.dime.ps.gateway.transformer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.dime.ps.semantic.util.DateUtils;

/**
 *
 * @author Will Fleury
 */
public class FormatUtils {
	
	private static Logger logger = LoggerFactory.getLogger(FormatUtils.class);
	
	private static final String[] ameticDateKeys = new String[] {
			"dateCompleted", "dueDate", "startDate", "regDueDate", "paymentDueDate"};

	/**
	 * This method is used to convert the raw AMETIC JSON service data to XML
	 * data for processing by the XSparqlTransformer. A default encoding of
	 * UTF-8 is used.
	 * 
	 * @param rawJSON the raw json data to convert
	 * @return the converted json data in XML format
	 * 
	 * @throws JSONException if the conversion from JSON to XML can't be made for I/O reasons.
	 */
	public static String convertAmeticJSONToXML(String rawJSON) {
		return convertAmeticJSONToXML(rawJSON, "UTF-8");
	}
	
	/**
	 * This method is used to convert the raw AMETIC JSON service data to XML
	 * data for processing by the XSparqlTransformer. 
	 * 
	 * @param rawJSON the raw json data to convert
	 * @param encoding specifies the encoding to use for the XML output (e.g. UTF-8)
	 * @return the converted json data in XML format
	 * 
	 * @throws JSONException if the conversion from JSON to XML can't be made for I/O reasons.
	 */
	public static String convertAmeticJSONToXML(String rawJSON, String encoding) {
		String jsonData = fixDateFormats(rawJSON);
		
		XMLSerializer serializer = new XMLSerializer();

		JSON json = JSONSerializer.toJSON(jsonData);

		serializer.setRootName("AmeticEvent");
		
		//to get rid of type hints
		serializer.setTypeHintsEnabled(false); 

		//note default enconding is UTF-8 but lets make sure it never changes..
		String xml = serializer.write(json, encoding);

		return xml;
	} 
	
	/**
	 * This method is used to convert the raw JSON service data to XML
	 * data for processing by the XSparqlTransformer. A default encoding of
	 * UTF-8 is used.
	 * 
	 * @param rawJSON the raw json data to convert
	 * @return the converted json data in XML format
	 * 
	 * @throws JSONException if the conversion from JSON to XML can't be made for I/O reasons.
	 */
	public static String convertJSONToXML(String rawJSON, String rootName) {
		return convertJSONToXML(rawJSON, rootName, "UTF-8");
	}
	
	/**
	 * This method is used to convert the raw JSON service data to XML
	 * data for processing by the XSparqlTransformer. 
	 * 
	 * @param rawJSON the raw json data to convert
	 * @param encoding specifies the encoding to use for the XML output (e.g. UTF-8)
	 * @return the converted json data in XML format
	 * 
	 * @throws JSONException if the conversion from JSON to XML can't be made for I/O reasons.
	 */
	public static String convertJSONToXML(String rawJSON, String rootName, String encoding) {	

		XMLSerializer serializer = new XMLSerializer();
		JSON json = JSONSerializer.toJSON(rawJSON);
		
		serializer.setRootName(rootName);		
		// to get rid of type hints
		serializer.setTypeHintsEnabled(false);
		// note default encoding is UTF-8 but lets make sure it never changes..
		String xml = serializer.write(json, encoding);

		return xml;
	}
	
	/**
	 * Checks for specific properties in the json string which are dates in 
	 * unix time. It then converts these to ISO 8901 format if they are not 
	 * null.
	 */
	private static String fixDateFormats(String rawJSON) {
		JSON json = JSONSerializer.toJSON(rawJSON);
		
		//Note: no error checking really.
		//I dont really like this json-lib library.. Doesn't have nice way to
		//avoid unsafe casts and this appears to be the only way to check if
		//it is a json object or array.. Maybe i missed something in the API!
		if (json.isArray()) {
			JSONArray array = (JSONArray)json;
			
			for (int i = 0; i < array.size(); i++) {
				Object obj = array.get(i);

				if (obj instanceof JSONObject) {
					JSONObject bean = (JSONObject)obj;

					fixJSONObjectFormats(bean);
				}
			}
			
			return array.toString();
		}
		
		//if its not the array of JSONObjects it must be a JSONObject..
		else if (json instanceof JSONObject) {
			JSONObject bean = (JSONObject)json;
			
			fixJSONObjectFormats(bean);
			
			return bean.toString();
		}
		
		//dont touch if we dont recognise..
		return rawJSON;
	}
	
	
	private static JSONObject fixJSONObjectFormats(JSONObject bean) {
		for (String key : ameticDateKeys) {
			Object value = bean.get(key);

			//better check might be !(value instanceof JSONNull) 
			//although the check i use should be ok in the event they actually
			//fix the date format on the server side (as it will be string and
			//this wont peform anything..)
			if (value != null && value instanceof Long) {
				bean.put(key, DateUtils.dateTimeToString((Long) value));
			}
		}
		
		return bean;
	}
		
	/**
	 * This is a hack provided by Keith as a temporary workaround for a bug
	 * in the xsparql library. The xpsarql developers are working on a fix 
	 * as we speak. 
	 * 
	 * Hack updated to include timestamp fix for linkedin liveposts. Need
	 * to convert from unix time to ISO 8901
	 * 
	 * REMOVE THIS ONCE THE XSPARQL ISSUE IS FIXED
	 */
	public static String fixLivePostXMLNodes(String xml) throws Exception {
	
		final String oldTag = "comment";
		final String newTag = "message";
		final String timeTag = "timestamp";
	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(
				new ByteArrayInputStream(xml.getBytes("UTF-8")));

		NodeList nodes = doc.getElementsByTagName(oldTag);
		for (int i = 0; i < nodes.getLength(); i++) {
			doc.renameNode(nodes.item(i), null, newTag);
		}
		
		//now fix unix time to ISO
		nodes = doc.getElementsByTagName(timeTag);
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList childNodes = nodes.item(i).getChildNodes();
			if (childNodes.getLength() <= 0)
				continue; 
			
			Node node = childNodes.item(0);
			String value = node.getNodeValue();
			
			if (value != null && !value.isEmpty()) {
				try {
					node.setNodeValue(DateUtils.dateTimeToString(Long.parseLong(value)));
				} catch (NumberFormatException e) { logger.error(timeTag+" value wasn't a long. : "+value); }
			}
		}
		
		StringWriter writer = new StringWriter();
		
		Source source = new DOMSource(doc); // Prepare the DOM document for writing	   
		Result result = new StreamResult(writer); // Prepare the output file

		javax.xml.transform.Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(source, result);  // Write the DOM document to the file
		
		return writer.toString();
	}
		
	/**
	 * This is a hack provided by Keith as a temporary workaround for a bug
	 * in the xsparql library. The xpsarql developers are working on a fix 
	 * as we speak. 
	 * 
	 * 
	 * REMOVE THIS ONCE THE XSPARQL ISSUE IS FIXED
	 */
	public static String changeXMLStringNodeName(String xml, String oldTag, String newTag) 
			throws Exception {
					
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();		
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		
		doc = changeXMLElementName(doc,oldTag,newTag);
		
		StringWriter outputString = new StringWriter();
		
		Source source = new DOMSource(doc);	   
		Result result = new StreamResult(outputString);	   
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(source, result);  // Write the DOM document to the file			
		
		return outputString.toString();
	}
		
	/**
	 * Facebook /livepost
	 * the following JSON:
	 * {"data":[{"id":"12_34","from":{"name":"xx","id":"111"},"story":"xxx likes a photo.","story_tags":{"0":[{"id":"113","name":"xxx","offset":0,"length":13,"type":"user"}]}}]}
	 * can't be converted to XML, and an exception is thrown:
	 * nu.xom.IllegalNameException: NCNames cannot start with the character 30
	 */
	public static String removeStoryTags(String rawJSON) {
		JSON json = JSONSerializer.toJSON(rawJSON);
		if (json instanceof JSONObject) {
			JSONObject o = (JSONObject) json;
			JSONArray entries = (JSONArray) o.get("data");
			for (Object entry : entries) {
				JSONObject oEntry = (JSONObject) entry;
				oEntry.remove("story_tags");
			}
		}
		return json.toString();
	}
	
	/**
	 * This method is used to delete two JSON fields that are not required/not 
	 * compliant to the XML standard before converting the data to XML data for 
	 * processing by the XSparqlTransformer.
	 * 
	 * @param is
	 *            the url of the raw json data 
	 * @param jsonArrayField
	 * 			  specifies the name of the json array field to traverse           
	 * @param jsonField1
	 *            specifies the first json field to be deleted
	 * @param jsonField2
	 *            specifies the second json field to be deleted  
	 * @return the json data after both json fields are deleted
	 * 
	 * @throws JSONException
	 *             if the conversion from JSON to XML can't be made for I/O
	 *             reasons.
	 */
	public static String removeJSONField(String jsonData, String jsonArrayField, String jsonField1, String jsonField2) {
		String jsonOutput = new String();
		try {    		
	     	JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonData); 
			JSONArray jsonArray  = json.getJSONArray(jsonArrayField);		
			
			for (int i=0; i<jsonArray.size(); i++) {						
				jsonArray.getJSONObject(i).discard(jsonField1);  //to delete a JSON field 
				jsonArray.getJSONObject(i).discard(jsonField2);  //to delete a JSON field 
			}			
			jsonOutput = json.toString();		
		} catch (Exception e) {			
			e.printStackTrace();		
		}		
		return jsonOutput;
	}
	
	/**
	 * Checks for specific properties in the xml string which are dates and times in 
	 * Twitter format. It then converts these to ISO 8901 format if they are not 
	 * null.
	 */
	public static String fixTwitterDateFormats(String xml) throws Exception {
		
		final String timeTag = "created_at";
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		
		NodeList nodes = doc.getElementsByTagName(timeTag);
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList childNodes = nodes.item(i).getChildNodes();
			if (childNodes.getLength() <= 0)
				continue; 
			
			Node node = childNodes.item(0);
			String value = node.getNodeValue();
			
			if (value != null && !value.isEmpty()) {
			   	node.setNodeValue(convertTwitterDateToISO8601(value));
			}
		}
		
		StringWriter writer = new StringWriter();
		
		Source source = new DOMSource(doc); // Prepare the DOM document for writing	   
		Result result = new StreamResult(writer); // Prepare the output file
		
		javax.xml.transform.Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(source, result);  // Write the DOM document to the file
		
		return writer.toString();
	}
				
	/**
	 * Converts Twitter Date format to ISO 8601 format
	 */
	private static String convertTwitterDateToISO8601(String oldDate) throws ParseException {
		TimeZone timeZone = TimeZone.getTimeZone("Europe/Dublin");
		
		SimpleDateFormat twitterFormat;
		twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
		Date date = twitterFormat.parse(oldDate.substring(0, 30));
		
		return DateUtils.dateTimeToString(date.getTime());   	
	}
	
	/**
	 * This is a hack changes the xml schema of Google Plus (after being converted from JSON)
	 * in-order to be compliant with the OpenSocial standard, since Google Plus will be the
	 * service adopted as a PoC for OpenSocial (partially-compliant)
	 */
	public static String changeGooglePlusXMLNodeNames(String xml, String[][][] element) {
		StringWriter outputString = new StringWriter();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();		
			Document doc = builder.parse(new InputSource(new StringReader(xml)));
						 
			//doc.getDocumentElement().setAttribute("xmlns", "http://ns.opensocial.org/2008/opensocial");				   					
			doc = changeXMLElementChildName(doc,element[0][0][0],element[0][0][1],element[0][0][2]);
			doc = changeXMLElementChildName(doc,element[0][1][0],element[0][1][1],element[0][1][2]);
			doc = changeXMLElementName(doc,element[0][2][1],element[0][2][2]);		   
			doc = changeXMLElementChildName(doc,element[0][3][0],element[0][3][1],element[0][3][2]);
			doc.getDocumentElement().removeAttribute("xmlns");
			
			Source source = new DOMSource(doc);	   
			Result result = new StreamResult(outputString);	   
			
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);  // Write the DOM document to the file				
		} catch (Exception e) {
			e.printStackTrace();
		} 
		 return outputString.getBuffer().toString().replaceAll("</?(?i:image)(.|\n)*?>","");		
	}
	
	/**
	 * This method is used for changing the name of a child of an XML Element
	 *
	 */
	private static Document changeXMLElementChildName(Document doc, String primaryElement, String oldTag, String newTag) {
		NodeList nodes = doc.getElementsByTagName(primaryElement);
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element elem = (Element)nodes.item(i);
				NodeList nodes2 = elem.getElementsByTagName(oldTag);
				
				for (int j = 0; j < nodes2.getLength(); j++) {
					Element elem2 = (Element)nodes2.item(j);
					doc.renameNode(elem2, elem2.getNamespaceURI(), newTag);						
				}					
			} 
		}		  
		return doc;
	}
	
	/**
	 * This method is used for changing the name of an XML Element
	 *
	 */
	private static Document changeXMLElementName(Document doc, String oldTag, String newTag) {
		NodeList nodes = doc.getElementsByTagName(oldTag);
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element elem = (Element)nodes.item(i);
				doc.renameNode(elem, elem.getNamespaceURI(), newTag);
			} 
		}
		return doc;	
	}
		
}
