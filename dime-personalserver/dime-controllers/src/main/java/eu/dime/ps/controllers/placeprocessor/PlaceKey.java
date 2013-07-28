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

public class PlaceKey {
	
	private Long tenant;
	private String guid;
	
	public PlaceKey(Long tenantId, String guid) {
		this.tenant = tenantId;
		this.guid = guid;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PlaceKey) {
			PlaceKey p = (PlaceKey)o;
			return p.guid.equalsIgnoreCase(this.guid) && p.tenant.doubleValue() == this.tenant.doubleValue();
		} else return false;
	}
	
	@Override
	public int hashCode() {
		return tenant.intValue() + guid.hashCode();
	}

}
