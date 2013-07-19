package eu.dime.ps.gateway.userresolver.client.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.zurich.idmx.dm.Values;
import com.ibm.zurich.idmx.issuance.IssuanceSpec;
import com.ibm.zurich.idmx.utils.Constants;

import eu.dime.ps.gateway.userresolver.client.utils.IdemixUtils.CredentialAttribute.Type;


public class IdemixUtils {
	
	public static class CredentialAttribute {
		public enum Type {
			INT, STRING, ENUM, UNKNOWN
		}

		private String name;
		private Type type;
		private boolean revealed;
		private List<String> enumValues;
		private String value;

		
		public CredentialAttribute(String name, Type type, boolean revealed) {
			this.name = name;
			this.type = type;
			this.revealed = revealed;
		}
		
		public CredentialAttribute(String name, Type type, boolean revealed,
				List<String> enumValues) {

			if(type != Type.ENUM)
				throw new IllegalArgumentException();
			if(enumValues == null)
				throw new IllegalArgumentException();
			
			this.name = name;
			this.type = type;
			this.revealed = revealed;
			this.enumValues = enumValues;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public Type getType() {
			return type;
		}

		public boolean isRevealed() {
			return revealed;
		}

		public List<String> getEnumValues() {
			return enumValues;
		}

		@Override
		public String toString() {
			return "IdemixAttribute [name=" + name + ", type=" + type
					+ ", revealed=" + revealed + ", enumValues=" + enumValues
					+ ", value=" + value + "]";
		}
	}
	
	public static class IssuanceValue {
		private String name;
		private String value;

		public String getName() { return name; }
		public void setName(String name) { this.name = name; 	}
		
		public String getValue() { return value; }
		public void setValue(String value) { this.value = value; }
		
		public IssuanceValue() {}
		
		public IssuanceValue(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////	

	public static Values generateIdemixValues(
			IssuanceSpec issuanceSpec, List<CredentialAttribute> attributes) {
		Values idemixValues = new Values(
				issuanceSpec.getPublicKey().getGroupParams().getSystemParams());
		for(CredentialAttribute attribute : attributes) {
			switch(attribute.getType()) {
			case INT:
				idemixValues.add(attribute.getName(), 
						new BigInteger(attribute.getValue()));
				break;
			case STRING:
				idemixValues.add(attribute.getName(), attribute.getValue());
				break;
			case ENUM:
				HashSet<String> primeEncoding= new HashSet<String>();
				for(String enumValue : 
					StringUtils.split(attribute.getValue(),';')) {
					primeEncoding.add(attribute.getName() 
							+ Constants.DELIMITER + enumValue);
				}
				BigInteger primeEncodedProduct = 
						Values.getPrimeEncodedProduct(
								issuanceSpec.getCredentialStructure()
									.getAttributeStructure(attribute.getName()), 
								primeEncoding);
				idemixValues.add(
						attribute.getName(), primeEncodedProduct, primeEncoding);
				break;
			default:
				throw new RuntimeException("Unsupported type");
			}
		}
		return idemixValues;
	}
	
	public static Values generateIdemixValues(String credentialStructure,
			IssuanceSpec issuanceSpec, Map<String, String> issuanceValues) {
		
		List<CredentialAttribute> credentialAttributes = 
				generateCredentialAttributes(credentialStructure);
		Iterator<CredentialAttribute> iterator = 
				credentialAttributes.iterator();
		while(iterator.hasNext()) {
			CredentialAttribute attribute = iterator.next();
			if(attribute.isRevealed()) {
				attribute.setValue(issuanceValues.get(attribute.getName()));
			} else {
				iterator.remove();
			}
		}
						
		return generateIdemixValues(issuanceSpec, credentialAttributes);
	}
	
	public static List<IssuanceValue> generateIssuanceValues(
			List<CredentialAttribute> attributes) {
		List<IssuanceValue> issuanceValues = new ArrayList<IssuanceValue>();
		for(CredentialAttribute attribute : attributes) {
			if(attribute.isRevealed()) {
				IssuanceValue value = new IssuanceValue(
						attribute.getName(), attribute.getValue());
				issuanceValues.add(value);
			}
		}
		return issuanceValues;
	}
	
	public static List<CredentialAttribute> generateCredentialAttributes(
			String credentialStrucuture) {
		List<CredentialAttribute> attributesList = 
				new ArrayList<CredentialAttribute>();
    	
        Document document = null;
        try {
        	DocumentBuilderFactory builderFactory = 
        			DocumentBuilderFactory.newInstance();
        	DocumentBuilder documentBuilder = 
        			builderFactory.newDocumentBuilder();
        	document = documentBuilder.parse(
        			new ByteArrayInputStream(
        					credentialStrucuture.getBytes("UTF-8")));
        } catch(ParserConfigurationException e) {
        	throw new RuntimeException("Unable to create XML parser", e);
        } catch(SAXException e) {
        	throw new IllegalArgumentException(
        			"Unable to parse credential strucuture", e);
        } catch(IOException e) {
        	throw new RuntimeException(
        			"IOException while reading string (WTF)", e);
        }
    	
    	Element attributesElement = 
    			(Element) document.getElementsByTagName("Attributes").item(0);
    	NodeList nodeList = attributesElement.getElementsByTagName("Attribute");
    	for(int i = 0 ; i < nodeList.getLength() ; i++) {
    		NamedNodeMap attributes = nodeList.item(i).getAttributes();
    	    
    	    String name = attributes.getNamedItem("name").getNodeValue();
    	    String mode = attributes.getNamedItem("issuanceMode").getNodeValue();
    		String typeString = attributes.getNamedItem("type").getNodeValue();
    	    CredentialAttribute.Type type;
    	    
    	    if(typeString.equalsIgnoreCase("int"))
    	    	type = Type.INT;
    	    else if(typeString.equalsIgnoreCase("enum"))
    	    	type = Type.ENUM;
    	    else if(typeString.equalsIgnoreCase("string"))
    	    	type = Type.STRING;
    	    else
    	    	type = Type.UNKNOWN; //later throw exception
    	    
    	    List<String> enumValues;
    	    if(type == Type.ENUM) {
    	    	enumValues = new ArrayList<String>();
    	    	
    	    	NodeList enumList = ((Element)nodeList.item(i)).
    	    			getElementsByTagName("EnumValue");
    	    	for(int j = 0 ; j < enumList.getLength() ; j++) {
    	    		Element enumValue = (Element) enumList.item(j);
    	    		enumValues.add(enumValue.getTextContent());
    	    	}
    	    	
    	    	attributesList.add(new CredentialAttribute(
    	    			name,
    	    			type,
    	    			mode.equals("known") ? true : false, 
    	    			enumValues));
    	    } else {
    	    	attributesList.add(new CredentialAttribute(
    	    			name, 
    	    			type, 
    	    			mode.equals("known") ? true : false));
    	    }
    	}
    	return attributesList;
	}
}
