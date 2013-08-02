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

package eu.dime.ps.gateway.exception;

/**
 * @author Sophie.Wrobel
 */
public class ServiceNotAvailableException extends ServiceException {

	private static final long serialVersionUID = 1L;

	public static final String CODE = "SERV-001";
	
	public ServiceNotAvailableException() {
		super("The service could not be reached.", CODE);
	}
	
	public ServiceNotAvailableException(Exception e) {
		super("The service could not be reached. Details: " + e.getMessage(), CODE, e);
	}

	public ServiceNotAvailableException(String exceptionMessage) {
		super(exceptionMessage, CODE);
	}
	
	public ServiceNotAvailableException(String exceptionMessage, String errorCode) {
		super(exceptionMessage, errorCode);
	}

	public ServiceNotAvailableException(String exceptionMessage, Throwable cause) {
		super(exceptionMessage, CODE, cause);
	}

	public ServiceNotAvailableException(int httpStatus) {
		super("The service returned an error response. HTTP Error code: " + httpStatus, CODE);
	}
	
}
