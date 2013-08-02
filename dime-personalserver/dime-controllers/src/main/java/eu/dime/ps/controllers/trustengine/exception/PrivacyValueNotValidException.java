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

/** 
 * @author <a href="mailto:heupel@wiwi.uni-siegen.de">
 * 	Marcel Heupel (mheupel)</a>
 *
 */
public class PrivacyValueNotValidException extends TrustException {

	public PrivacyValueNotValidException(double privacyValue) {
		super("Privacy value has to be between 0-1. It is now: "+privacyValue);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4540577722963394207L;

}
