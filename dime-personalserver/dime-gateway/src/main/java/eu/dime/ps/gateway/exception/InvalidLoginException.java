/**
 * 
 */
package eu.dime.ps.gateway.exception;

/**
 * @author Sophie.Wrobel
 *
 */
public class InvalidLoginException extends ServiceException {

	public InvalidLoginException() {
		super("The login credentials are incorrect.", "SERV-003");
	}
	
	public InvalidLoginException(String message) {
		super(message, "SERV-003");
	}

}
