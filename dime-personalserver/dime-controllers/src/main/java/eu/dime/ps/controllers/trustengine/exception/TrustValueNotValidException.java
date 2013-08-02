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

package eu.dime.ps.controllers.trustengine.exception;

public class TrustValueNotValidException extends TrustException {

	public TrustValueNotValidException(double trustValue) {
		super("Trust value has to be in [0..1]. It is now: " + trustValue);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -825281557178824751L;

}
