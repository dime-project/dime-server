package eu.dime.commons.dto;

import java.util.LinkedHashSet;

import javax.xml.bind.annotation.XmlAccessType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Response<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(Response.class);
	
    public enum Status {
        OK(200, "OK"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error");
        
        private final int code;
        private final String reason;
        
        Status(final int code, final String reason) {
            this.code = code;
            this.reason = reason;
        }
        
        /**
         * Get the associated status code
         * @return the status code
         */
        public int getCode() {
            return code;
        }
        
        /**
         * Get the reason
         * @return the reason
         */
        public String getReason() {
            return toString();
        }
        
        /**
         * Get the code & reason
         * @return the code & reason
         */
        @Override
        public String toString() {
            return code+" - "+reason;
        }
    
    }
    
	@javax.xml.bind.annotation.XmlElement(name="response")
	private Message<T> message;

	public Response() {}

	public Response(Message<T> message) {
		this.message = message;
	}

	public Message<T> getMessage() {
		return message;
	}

	public void setMessage(Message<T> message) {
		this.message = message;
	}
	
	public static <T> Response<T> ok() {
		return ok(null);
	}
	
	public static <T> Response<T> okEmpty() {
		Data<T> data = new Data<T>(0, 0, 0);
		data.setEntry(new LinkedHashSet<T>(0));	    	
		return ok(data);
	}
	
	public static <T> Response<T> ok(Data<T> data) {
		Meta meta = new Meta();
		meta.setCode(Status.OK.getCode());
		meta.setStatus("OK");
		meta.setTimeRef(System.currentTimeMillis());
		meta.setVersion("0.9");
		meta.setMessage("");
		Message<T> message = new Message<T>();
		message.setMeta(meta);
		message.setData(data);
		
		return new Response<T>(message);
	}
	
	public static <T> Response<T> serverError() {
		return serverError(null, null);
	}

	public static <T> Response<T> serverError(String reason, Exception error) {
		Meta meta = new Meta();
		meta.setCode(Status.INTERNAL_SERVER_ERROR.getCode());
		meta.setStatus("ERROR");
		meta.setTimeRef(System.currentTimeMillis());
		meta.setVersion("0.9");
		meta.setMessage("");
		meta.setMessage(reason);

		Message<T> message = new Message<T>();
		message.setMeta(meta);
		
		if (error != null)
			logger.error(reason, error);

		return new Response<T>(message);
	}
	
	public static <T> Response<T> badRequest() {
		return badRequest(null, null);
	}
	
	public static <T> Response<T> badRequest(String reason) {
		return badRequest(reason, null);
	}
	
	public static <T> Response<T> badRequest(String reason, Exception error) {
		Meta meta = new Meta();
		meta.setCode(Status.BAD_REQUEST.getCode());
		meta.setStatus("ERROR");
		meta.setTimeRef(System.currentTimeMillis());
		meta.setVersion("0.9");
		meta.setMessage("");
		meta.setMessage(reason);
		
		Message<T> message = new Message<T>();
		message.setMeta(meta);
		
		if (error != null) {
                    logger.error(reason +": " + error.getMessage());
                }

		return new Response<T>(message);
	}
	
	public static <T> Response<T> status(Status status, String reason) {
		return status(status, reason, null);
	}

	public static <T> Response<T> status(Status status, String reason, Exception error) {
		Meta meta = new Meta();
		meta.setCode(status.getCode());
		meta.setStatus(status.equals(Status.OK) ? "OK" : "ERROR");
		meta.setTimeRef(System.currentTimeMillis());
		meta.setVersion("0.9");
		meta.setMessage("");
		meta.setMessage(reason);

		Message<T> message = new Message<T>();
		message.setMeta(meta);
		
		if (error != null)
			logger.error(reason, error);
		
		return new Response<T>(message);
	}

}