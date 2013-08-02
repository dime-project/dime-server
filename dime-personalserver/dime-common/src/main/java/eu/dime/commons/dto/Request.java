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

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Request<T> {
	
	@javax.xml.bind.annotation.XmlElement(name="request")
	private Message<T> message;

	public Request() {}

	public Request(Message<T> message) {
		this.message = message;
	}

	public Message<T> getMessage() {
		return message;
	}

	public void setMessage(Message<T> message) {
		this.message = message;
	}
	
}