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

package eu.dime.commons.dto;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Entry {
	
	@javax.xml.bind.annotation.XmlElement(name="guid")
	public String guid;
	@javax.xml.bind.annotation.XmlElement(name="name")
	public String name = null;
	@javax.xml.bind.annotation.XmlElement(name="imageUrl")
	public String imageUrl = null ;
	@javax.xml.bind.annotation.XmlElement(name="type")
	public String type = null;
	@javax.xml.bind.annotation.XmlElement(name="items")
	public List<String> items = null;
	@javax.xml.bind.annotation.XmlElement(name="lastModified")
	public String lastModified = null;
	
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getItems() {
	    	if(items == null){
	    	    items = new Vector<String>();
	    	}
		return items;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	
//  {
//  "guid": <ID1>,
//  "name": <NAME>,
//  "imageUrl": <e.g.resources/...>,
//  "type": person|group|livestream|livestreamitem|databox|resource|profile|notification|serviceaccount|situation,
//  "items": [
//      <item1Id>,
//      <item2Id>,
//      ...
//  ],
//  "lastUpdate": <timedate>,
//  
//}


}
