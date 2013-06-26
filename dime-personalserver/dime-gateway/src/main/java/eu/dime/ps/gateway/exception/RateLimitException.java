/**
 * 
 */
package eu.dime.ps.gateway.exception;

/**
 * @author Sophie.Wrobel
 *
 */
public class RateLimitException extends ServiceNotAvailableException {

	public RateLimitException() {
		super("Rate limit exceeded.", "SERV-002");
	}
	
	public RateLimitException(String message) {
		super(message, "SERV-002");
	}

}
