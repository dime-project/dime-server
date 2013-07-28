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

package eu.dime.ps.gateway.exception;

import eu.dime.ps.gateway.service.ServiceAdapter;

/**
 * @author Sophie.Wrobel
 */
public class AttributeNotSupportedException extends ServiceException {

	private static final long serialVersionUID = 1L;

	private String attribute;
	private ServiceAdapter adapter;
	
	public AttributeNotSupportedException(String attribute, ServiceAdapter adapter) {
		super("Attribute '"+attribute+"' is not supported.", "SERV-002");
		this.attribute = attribute;
		this.adapter = adapter;
	}

	public AttributeNotSupportedException(String attribute, String reason, ServiceAdapter adapter) {
		super("Attribute '"+attribute+"' is not supported: "+reason, "SERV-002");
		this.attribute = attribute;
		this.adapter = adapter;
	}

	public String getAttribute() {
		return attribute;
	}

	public ServiceAdapter getAdapter() {
		return adapter;
	}
	
}
