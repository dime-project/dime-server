package eu.dime.ps.semantic.exception;

/**
 * Thrown when a name assigned to a thing or tag is not unique,
 * when the name was used for another thing before.
 *
 * @author Ismael Rivera
 */
public class NameNotUniqueException extends Exception {
    
    private static final long serialVersionUID = 809134182365634885L;
    
    /**
     * The URI of the Resource that already uses the name
     */
    protected String existingResourceURI;

	public NameNotUniqueException() {
	}

	/**
	 * @param message
	 */
	public NameNotUniqueException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NameNotUniqueException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NameNotUniqueException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param message
	 */
	public NameNotUniqueException(String message, String existingResourceURI) {
	    super(message);
        this.existingResourceURI = existingResourceURI;
	}
	
	/**
	 * @param cause
	 */
	public NameNotUniqueException(Throwable cause, String existingResourceURI) {
	    super(cause);
        this.existingResourceURI = existingResourceURI;
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public NameNotUniqueException(String message, Throwable cause, String existingResourceURI) {
	    super(message, cause);
        this.existingResourceURI = existingResourceURI;
	}

    /**
     * The URI of the existing resource that has the same name as the 
     * name that was tried to set.
     * 
     * @return the URI of the existing resource, or null if the URI was not
     * set by the implementation
     */
    public String getExistingResourceURI() {
        return existingResourceURI;
    }

}
