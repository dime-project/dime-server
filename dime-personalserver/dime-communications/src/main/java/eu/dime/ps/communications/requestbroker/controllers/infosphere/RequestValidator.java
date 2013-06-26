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
