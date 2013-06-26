/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.datamining.service.api.exception;

/**
 *
 * @author Will Fleury
 */
public class ExternalServiceException extends RuntimeException {
    
    public ExternalServiceException() { }
    public ExternalServiceException(String msg) { super(msg); }
    public ExternalServiceException(Throwable cause) { super (cause); }
    public ExternalServiceException(String msg, Throwable cause) { super(msg, cause); }
}
