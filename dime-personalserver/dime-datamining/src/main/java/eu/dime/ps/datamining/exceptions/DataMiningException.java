/*
 * Copyright (c) 2011 DERI, NUIG 
 */

package eu.dime.ps.datamining.exceptions;

/**
 *
 * @author Will Fleury
 */
public class DataMiningException extends Exception {
    private static final long serialVersionUID = -8644497556445317241L;
    
    public DataMiningException()
    {
	super();
    }

    public DataMiningException(final String message)
    {
	super(message);
    }

    public DataMiningException(final String message, final Throwable cause)
    {
	super(message, cause);
    }
}
