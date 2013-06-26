/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.communications.requestbroker.controllers.authentication;

/**
 *
 * @author simon
 */
public class AccessDeniedException extends Exception{

    public AccessDeniedException(String userName) {
        super("Access denied trying to access: "+userName);
    }
    

}
