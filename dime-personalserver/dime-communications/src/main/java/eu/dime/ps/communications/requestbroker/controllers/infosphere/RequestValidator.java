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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import eu.dime.commons.dto.Request;

public class RequestValidator {

    /**
     * Validates that the request is well-formed: contains a message object, and
     * the message contains a non-empty data object.
     */
    public static void validateRequest(Request request) throws IllegalArgumentException {
	if (request.getMessage() == null) {
	    throw new IllegalArgumentException("Wrong Request JSON");
	} else if (request.getMessage().getData() == null) {
	    throw new IllegalArgumentException("Wrong Request JSON");
	} else if (request.getMessage().getData().getEntries().isEmpty()) {
	    throw new IllegalArgumentException("Wrong Request JSON - Entry field is empty");
	}
    }

}
