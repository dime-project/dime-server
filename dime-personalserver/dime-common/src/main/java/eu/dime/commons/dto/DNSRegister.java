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

package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class DNSRegister {

	@javax.xml.bind.annotation.XmlElement(name="changeDate")
	public String changeDate;
	@javax.xml.bind.annotation.XmlElement(name="content")
	public String content;
	@javax.xml.bind.annotation.XmlElement(name="name")
	public String name;
	@javax.xml.bind.annotation.XmlElement(name="publickey")
    public String publickey;

	public DNSRegister(){		
	}

	public DNSRegister(String date, String ip, String said, String pubKey) {
		this.changeDate = date;
		this.content = ip;
		this.name = said;
		this.publickey = pubKey;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublickey() {
		return publickey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
		
	
}
