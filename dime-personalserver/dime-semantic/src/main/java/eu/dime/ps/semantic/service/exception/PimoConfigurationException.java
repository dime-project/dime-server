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

package eu.dime.ps.semantic.service.exception;

/**
 * Thrown when the PIMO Service cannot be configured.
 * 
 * @author Ismael Rivera
 */
public class PimoConfigurationException extends Exception {

	private static final long serialVersionUID = 2293776964332753598L;

	public PimoConfigurationException() {
    }

    public PimoConfigurationException(String message) {
        super(message);
    }

    public PimoConfigurationException(Throwable cause) {
        super(cause);
    }

    public PimoConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
